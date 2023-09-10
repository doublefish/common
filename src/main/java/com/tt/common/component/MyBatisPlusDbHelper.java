package com.tt.common.component;

import com.baomidou.mybatisplus.annotation.*;
import com.tt.common.util.ReflectUtils;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

/**
 * MyBatisPlusDbHelper
 * 继承DbHelper，主要为兼容MyBatis的注解
 *
 * @param <T> T
 * @author Shuang Yu
 */
public class MyBatisPlusDbHelper<T> extends DbHelper<T> {

    /**
     * 构造函数
     *
     * @param clazz     类型
     * @param includeId 包含Id字段：生成的插入语句中包含Id字段
     */
    protected MyBatisPlusDbHelper(Class<T> clazz, @Nullable Boolean includeId) {
        this(clazz, null, null, includeId, null, null, null);
    }

    /**
     * 构造函数
     *
     * @param clazz             类型：须为实体类添加@com.baomidou.mybatisplus.annotation.TableName注解声明表名
     * @param table             表名：未声明时，须为实体类添加@com.baomidou.mybatisplus.annotation.TableName注解声明表名
     * @param mappings          属性和列名映射关系：未声明时，须为每个字段添加@com.baomidou.mybatisplus.annotation.TableField注解声明列名；<br/>如果每个字段都有对应的列，且符合驼峰规范，也可忽略此参数
     * @param includeId         包含Id字段：生成的插入语句中包含Id字段
     * @param includeFields     包含的字段：未声明mappings参数时，可通过此参数声明需要包含的列
     * @param excludeFields     排除的字段：未声明mappings参数时，可通过此参数声明需要排除的列
     * @param dateTimeFormatter 时间格式化工具：时间格式化工具默认格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    public MyBatisPlusDbHelper(Class<T> clazz
            , @Nullable String table
            , @Nullable Map<String, String> mappings
            , @Nullable Boolean includeId
            , @Nullable Collection<String> includeFields
            , @Nullable Collection<String> excludeFields
            , @Nullable DateTimeFormatter dateTimeFormatter) {
        super(clazz, table, mappings, includeId, includeFields, excludeFields, dateTimeFormatter);
    }

    @Override
    protected void init() {
        if (StringUtils.isEmpty(this.table)) {
            var table = clazz.getAnnotation(TableName.class);
            if (table != null) {
                this.table = table.value();
                if (ArrayUtils.isNotEmpty(table.excludeProperty())) {
                    CollectionUtils.addAll(this.excludeFields, table.excludeProperty());
                }
            }
        }
        Assert.hasLength(this.table, "实体未添加@TableName注解，或注解的value为空");
        var fields = ReflectUtils.getDeclaredFields(clazz);
        for (var field : fields) {
            if (!contains(field, this.includeFields, this.excludeFields)) {
                continue;
            }
            var column = "";
            var tableColumn = field.getAnnotation(TableField.class);
            if (tableColumn != null && tableColumn.exist()) {
                column = StringUtils.isEmpty(tableColumn.value()) ? toCamelCaseString(field.getName()) : tableColumn.value();
            }
            if (StringUtils.isEmpty(column)) {
                var tablePrimary = field.getAnnotation(TableId.class);
                if (tablePrimary != null) {
                    column = StringUtils.isEmpty(tablePrimary.value()) ? "id" : tablePrimary.value();
                    this.id = column;
                }
            }
            if (StringUtils.isEmpty(column)) {
                continue;
            }
            if (StringUtils.isEmpty(this.version)) {
                var version = field.getAnnotation(Version.class);
                if (version != null) {
                    this.version = column;
                }
            }
            if (StringUtils.isEmpty(this.logic)) {
                var tableLogic = field.getAnnotation(TableLogic.class);
                if (tableLogic != null) {
                    this.logic = column;
                }
            }
            this.fields.put(field.getName(), field);
            this.mappings.put(field.getName(), column);
        }
        Assert.notNull(this.id, "实体未添加@TablePrimary注解，或注解的value为空");
        super.init();
    }

    private TableName getTableName(Class<?> clazz) {
        var tableName = clazz.getAnnotation(TableName.class);
        if (tableName == null || StringUtils.isEmpty(tableName.value())) {
            throw new IllegalArgumentException("实体未添加@TableName注解，或注解的value为空");
        }
        return tableName;
    }
}
