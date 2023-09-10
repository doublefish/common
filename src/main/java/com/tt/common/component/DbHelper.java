package com.tt.common.component;

import com.tt.common.Constants;
import com.tt.common.annotation.Table;
import com.tt.common.annotation.TableColumn;
import com.tt.common.util.ReflectUtils;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.*;

/**
 * DbHelper
 * <br>自动生成SQL语句，未来目标：对增删改查操作简单封装，实现接近原生操作数据库的性能；
 * <br>
 * <br/>支持类型：String，继承Number类的数字类型，实现Temporal接口的时间类型；
 * <br>
 * <br/>实现Iterable或Map接口的类型的属性自动忽略；
 * <br>
 * <br/>生成更新语句或调用更新方法，会自动把传入的实体的rowVersion+1
 *
 * @param <T> T
 * @author Shuang Yu
 */
@Slf4j
public class DbHelper<T> {

    private final ThreadLocal<Map<String, Object>> thread_data = new ThreadLocal<>();
    protected final Class<T> clazz;
    protected final boolean includeId;
    protected String table;
    protected String id;
    protected Map<String, Field> fields = new LinkedHashMap<>(0);
    protected Map<String, String> mappings = new LinkedHashMap<>(0);
    protected String version;
    protected String logic;
    protected HashSet<String> includeFields = new HashSet<>();
    protected HashSet<String> excludeFields = new HashSet<>();
    protected DateTimeFormatter dateTimeFormatter;
    protected String columnsSql;
    protected String valuesSql;

    protected List<String> createInfoColumns = Arrays.asList(Constants.Column.ROW_CREATE_DATE, Constants.Column.ROW_CREATE_USER, Constants.Column.ROW_CREATE_USERNAME);
    protected List<String> updateInfoColumns = Arrays.asList(Constants.Column.ROW_UPDATE_DATE, Constants.Column.ROW_UPDATE_USER, Constants.Column.ROW_UPDATE_USERNAME);
    protected List<String> deleteInfoColumns = Arrays.asList(Constants.Column.ROW_DELETED_DATE, Constants.Column.ROW_DELETED_USER);

    /**
     * 构造函数
     *
     * @param clazz     类型
     * @param includeId 包含Id字段：生成的插入语句中包含Id字段
     */
    protected DbHelper(Class<T> clazz, @Nullable Boolean includeId) {
        this(clazz, null, null, includeId, null, null, null);
    }

    /**
     * 构造函数
     *
     * @param clazz             类型
     * @param table             表名：未声明时，须为实体类添加@com.tt.common.annotation.Table注解声明表名
     * @param mappings          属性和列名映射关系：未声明时，须为每个字段添加@com.baomidou.mybatisplus.annotation.TableField注解声明列名；<br/>如果每个字段都有对应的列，且符合驼峰规范，也可忽略此参数
     * @param includeId         包含Id字段：生成的插入语句中包含Id字段
     * @param includeFields     包含的字段：未声明mappings参数时，可通过此参数声明需要包含的列
     * @param excludeFields     排除的字段：未声明mappings参数时，可通过此参数声明需要排除的列
     * @param dateTimeFormatter 时间格式化工具：时间格式化工具默认格式：yyyy-MM-dd HH:mm:ss.SSS
     */
    protected DbHelper(Class<T> clazz, @Nullable String table, @Nullable Map<String, String> mappings
            , @Nullable Boolean includeId
            , @Nullable Collection<String> includeFields
            , @Nullable Collection<String> excludeFields
            , @Nullable DateTimeFormatter dateTimeFormatter) {
        Assert.notNull(clazz, "clazz must not be null");
        this.clazz = clazz;
        this.table = table;
        this.includeId = Boolean.TRUE.equals(includeId);
        if (CollectionUtils.isNotEmpty(includeFields)) {
            this.includeFields.addAll(includeFields);
        }
        if (CollectionUtils.isNotEmpty(excludeFields)) {
            this.excludeFields.addAll(excludeFields);
        }
        if (MapUtils.isNotEmpty(mappings)) {
            this.mappings.putAll(mappings);
            var fields = ReflectUtils.getDeclaredFields(this.clazz);
            for (var field : fields) {
                if (mappings.containsKey(field.getName())) {
                    this.fields.put(field.getName(), field);
                }
            }
        }
        this.dateTimeFormatter = dateTimeFormatter;
        init();
    }

    protected void init() {
        if (StringUtils.isEmpty(this.table)) {
            var table = clazz.getAnnotation(Table.class);
            if (table != null) {
                this.table = table.value();
                if (ArrayUtils.isNotEmpty(table.excludeProperty())) {
                    CollectionUtils.addAll(this.excludeFields, table.excludeProperty());
                }
            }
        }
        Assert.hasLength(this.table, "实体未添加@Table注解，或注解的value为空");
        if (MapUtils.isEmpty(this.mappings)) {
            var fields = ReflectUtils.getDeclaredFields(this.clazz);
            for (var field : fields) {
                if (!contains(field, this.includeFields, this.excludeFields)) {
                    continue;
                }
                var tableColumn = field.getAnnotation(TableColumn.class);
                if (tableColumn == null || !tableColumn.exist()) {
                    continue;
                }
                var column = StringUtils.isEmpty(tableColumn.value()) ? toCamelCaseString(field.getName()) : tableColumn.value();
                if (tableColumn.id() && StringUtils.isEmpty(this.id)) {
                    this.id = tableColumn.value();
                }
                if (tableColumn.version() && StringUtils.isEmpty(this.version)) {
                    this.version = tableColumn.value();
                }
                if (tableColumn.logic() && StringUtils.isEmpty(this.logic)) {
                    this.logic = tableColumn.value();
                }
                this.fields.put(field.getName(), field);
                this.mappings.put(field.getName(), column);
            }
        }
        Assert.notNull(this.id, "实体未添加@TablePrimary注解，或注解的value为空");
        if (this.dateTimeFormatter == null) {
            this.dateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DATE_TIME_FORMAT_MILLS).withZone(ZoneId.systemDefault());
        }
        var columns = new StringBuilder();
        var values = new StringBuilder();
        for (var entry : this.mappings.entrySet()) {
            var fieldName = entry.getKey();
            if (!this.includeId && fieldName.equals(this.id)) {
                continue;
            }
            var column = entry.getValue();
            columns.append(", `").append(column).append("`");
            values.append(", ?");
        }
        this.columnsSql = columns.substring(2);
        this.valuesSql = values.substring(2);
    }

    /**
     * 是否包含
     *
     * @param field field
     * @return contains
     */
    protected boolean contains(Field field, Collection<String> includeFields, Collection<String> excludeFields) {
        if (CollectionUtils.isNotEmpty(includeFields) && !includeFields.contains(field.getName())) {
            return false;
        }
        return !excludeFields.contains(field.getName());
    }

    /**
     * 根据驼峰规则转换
     *
     * @param field field
     */
    protected String toCamelCaseString(String field) {
        var array = StringUtils.splitByCharacterTypeCamelCase(field);
        return String.join("_", array).toLowerCase(Locale.ROOT);
    }

    /**
     * 获取线程池变量
     *
     * @param key   key
     * @param value value
     */
    protected void setThreadData(String key, Object value) {
        var map = thread_data.get();
        if (map == null) {
            map = new HashMap<>();
            thread_data.set(map);
        }
        map.put(key, value);
    }

    /**
     * 获取线程池变量
     *
     * @param key key
     * @return value
     */
    protected Object getThreadData(String key) {
        var map = thread_data.get();
        if (map != null) {
            return map.get(key);
        }
        return null;
    }


    /**
     * 获取表名
     *
     * @return String
     */
    public String getTableName() {
        var tableSuffix = getTableSuffix();
        return StringUtils.isEmpty(tableSuffix) ? this.table : this.table + "_" + tableSuffix;
    }

    /**
     * 设置表名后缀
     *
     * @return String
     */
    public String getTableSuffix() {
        var tableSuffix = getThreadData("tableSuffix");
        return tableSuffix != null ? tableSuffix.toString() : "";
    }

    /**
     * 设置表名后缀
     */
    public void setTableSuffix(String tableSuffix) {
        setThreadData("tableSuffix", tableSuffix);
    }

    /**
     * 清空表名后缀
     */
    public void clearTableSuffix() {
        setTableSuffix(null);
    }

    /**
     * resultSet转换为实体：适用查询所有字段
     *
     * @param rs     rs
     * @param rowNum rowNum
     */
    public T convert(ResultSet rs, int rowNum) {
        T entity;
        try {
            entity = clazz.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("创建实例发生异常：" + e.getMessage(), e);
        }
        for (var entry : this.mappings.entrySet()) {
            var fieldName = entry.getKey();
            var columnName = entry.getValue();
            var writer = ReflectUtils.getWriteMethod(clazz, fieldName);
            var field = this.fields.get(fieldName);
            var type = field.getType();
            try {
                if (Long.class.isAssignableFrom(type)) {
                    var value = rs.getLong(columnName);
                    writer.invoke(entity, value);
                } else if (Integer.class.isAssignableFrom(type)) {
                    var value = rs.getInt(columnName);
                    writer.invoke(entity, value);
                } else if (Short.class.isAssignableFrom(type)) {
                    var value = rs.getShort(columnName);
                    writer.invoke(entity, value);
                } else if (Float.class.isAssignableFrom(type)) {
                    var value = rs.getFloat(columnName);
                    writer.invoke(entity, value);
                } else if (Double.class.isAssignableFrom(type)) {
                    var value = rs.getDouble(columnName);
                    writer.invoke(entity, value);
                } else if (Boolean.class.isAssignableFrom(type)) {
                    var value = rs.getBoolean(columnName);
                    writer.invoke(entity, value);
                } else if (LocalDate.class.isAssignableFrom(type)) {
                    var value = rs.getDate(columnName);
                    writer.invoke(entity, value.toLocalDate());
                } else if (LocalTime.class.isAssignableFrom(type)) {
                    var value = rs.getTime(columnName);
                    writer.invoke(entity, value.toLocalTime());
                } else if (LocalDateTime.class.isAssignableFrom(type)) {
                    var value = rs.getTimestamp(columnName);
                    writer.invoke(entity, value.toLocalDateTime());
                } else if (Instant.class.isAssignableFrom(type)) {
                    var value = rs.getTimestamp(columnName);
                    writer.invoke(entity, value.toInstant());
                } else if (String.class.isAssignableFrom(type)) {
                    var value = rs.getString(columnName);
                    writer.invoke(entity, value);
                } else {
                    var value = rs.getObject(columnName);
                    writer.invoke(entity, value);
                }
            } catch (Exception e) {
                throw new RuntimeException(String.format("设置属性：%s的值发生异常：%s", fieldName, e.getMessage()), e);
            }
        }
        return entity;
    }

    /**
     * 生成插入语句
     *
     * @param entity entity
     * @return SQL
     */
    public String getInsertSQL(T entity) {
        Assert.notNull(entity, "entity must not be null");
        return getInsertSQL(Collections.singletonList(entity));
    }

    /**
     * 生成插入语句
     *
     * @param entities entities
     * @return SQL
     */
    public String getInsertSQL(Collection<T> entities) {
        Assert.notEmpty(entities, "entities must not be empty");
        var values = new StringBuilder();
        for (var entity : entities) {
            var temps = new StringBuilder();
            for (var entry : this.mappings.entrySet()) {
                var fieldName = entry.getKey();
                if (!this.includeId && fieldName.equals(this.id)) {
                    continue;
                }
                var field = this.fields.get(fieldName);
                var value = ReflectUtils.getValue(entity, fieldName);
                var sqlValue = toSQLString(value, field.getType());
                temps.append(", ").append(sqlValue);
            }
            values.append(",\n(").append(temps.substring(2)).append(")");
        }
        var valuesSql = values.substring(1);
        return String.format("INSERT INTO %s (%s) VALUES %s", getTableName(), this.columnsSql, valuesSql);
    }

    /**
     * 生成修改语句
     *
     * @param entity entity
     * @return UPDATE {table} SET column0 = {value0}, ... WHERE id = {id};
     */
    public String getUpdateByIdSQL(T entity) {
        return getUpdateByIdSQL(entity, null, null);
    }

    /**
     * 生成修改语句
     *
     * @param entity            entity
     * @param includeProperties 包含的属性
     * @param excludeProperties 排除的属性
     * @return UPDATE {table} SET column0 = {value0}, ... WHERE id = {id};
     */
    public String getUpdateByIdSQL(T entity, @Nullable Collection<String> includeProperties, @Nullable Collection<String> excludeProperties) {
        Assert.notNull(entity, "entity must not be null");
        Assert.hasLength(this.id, "未声明主键");
        var set = new StringBuilder();
        var where = new StringBuilder();
        for (var entry : this.mappings.entrySet()) {
            var fieldName = entry.getKey();
            var columnName = entry.getValue();
            var field = this.fields.get(fieldName);
            var value = ReflectUtils.getValue(entity, fieldName);
            if (columnName.equals(this.version)) {
                var oldVersion = NumberUtils.toInt(value.toString());
                var newVersion = oldVersion + 1;
                ReflectUtils.setValue(entity, fieldName, newVersion);
                set.append(", ").append(this.version).append(" = ").append(newVersion);
                where.append(" AND ").append(this.version).append(" = ").append(oldVersion);
                continue;
            }
            if (columnName.equals(this.id)) {
                var sqlValue = toSQLString(value, field.getType());
                where.append(" AND `").append(columnName).append("` = ").append(sqlValue);
                continue;
            }
            if (!canUpdate(fieldName, columnName, includeProperties, excludeProperties)) {
                continue;
            }
            var sqlValue = toSQLString(value, field.getType());
            set.append(", `").append(columnName).append("` = ").append(sqlValue);
        }
        if (where.length() == 0) {
            throw new IllegalArgumentException("未找到主键");
        }
        return String.format("UPDATE %s SET %s WHERE %s%s", getTableName(), set.substring(2), where.substring(5), getLogicWhere());
    }

    /**
     * 生成删除语句
     *
     * @param id   id
     * @param user user：逻辑删除时需要
     * @return SQL
     */
    public String getDeleteSQL(Serializable id, @Nullable String user) {
        Assert.notNull(id, "id must not be null");
        return getDeleteSQL(Collections.singletonList(id), user);
    }

    /**
     * 生成删除语句
     *
     * @param ids  ids
     * @param user user：逻辑删除时需要
     * @return SQL
     */
    public String getDeleteSQL(Collection<? extends Serializable> ids, @Nullable String user) {
        Assert.notEmpty(ids, "ids must not be empty");
        if (StringUtils.isEmpty(this.logic)) {
            return String.format("DELETE FROM %s WHERE %s", getTableName(), getWhere(ids));
        }
        Assert.hasLength(user, "user must not be empty");
        var set = new StringBuilder();
        set.append(this.logic).append("=1");
        if (this.mappings.containsKey(Constants.Field.ROW_DELETED_DATE)) {
            var column = this.mappings.get(Constants.Field.ROW_DELETED_DATE);
            var now = this.dateTimeFormatter.format(Instant.now());
            set.append(", ").append(column).append("=").append("'").append(now).append("'");
        }
        if (this.mappings.containsKey(Constants.Field.ROW_DELETED_USER)) {
            var column = this.mappings.get(Constants.Field.ROW_DELETED_USER);
            set.append(", ").append(column).append("=").append("'").append(user).append("'");
        }
        return String.format("UPDATE %s SET %s WHERE %s", getTableName(), set, getWhere(ids));
    }

    /**
     * 生成插入预编译对象
     *
     * @param connection connection
     * @param entity     entity
     * @return PreparedStatement
     */
    public PreparedStatement getInsertPreparedStatement(Connection connection, T entity) {
        Assert.notNull(entity, "entity must not be null");
        return getInsertPreparedStatement(connection, Collections.singletonList(entity));
    }

    /**
     * 生成插入预编译对象
     *
     * @param connection connection
     * @param entities   entities
     * @return PreparedStatement
     */
    public PreparedStatement getInsertPreparedStatement(Connection connection, Collection<T> entities) {
        Assert.notEmpty(entities, "entities must not be empty");
        var sql = getInsertSQL(entities);
        return getPreparedStatement(connection, sql);
    }

    /**
     * 生成修改预编译对象
     *
     * @param connection connection
     * @param entity     entity
     * @return PreparedStatement
     */
    public PreparedStatement getUpdatePreparedStatement(Connection connection, T entity) {
        return getUpdatePreparedStatement(connection, entity, null, null);
    }

    /**
     * 生成修改预编译对象
     *
     * @param connection        connection
     * @param entity            entity
     * @param includeProperties 包含的属性
     * @param excludeProperties 排除的属性
     * @return PreparedStatement
     */
    public PreparedStatement getUpdatePreparedStatement(Connection connection, T entity, @Nullable Collection<String> includeProperties, @Nullable Collection<String> excludeProperties) {
        Assert.notNull(entity, "entity must not be null");
        Assert.hasLength(this.id, "未声明主键");
        var sql = getUpdateByIdSQL(entity, includeProperties, excludeProperties);
        return getPreparedStatement(connection, sql);
    }

    /**
     * 生成插入预编译对象
     *
     * @param connection connection
     * @param entity     entity
     * @return affected rows
     */
    public int insert(Connection connection, T entity) {
        Assert.notNull(entity, "entity must not be null");
        return insert(connection, Collections.singletonList(entity));
    }

    /**
     * 生成插入预编译对象
     *
     * @param connection connection
     * @param entities   entities
     * @return affected rows
     */
    public int insert(Connection connection, Collection<T> entities) {
        try {
            var prepared = getInsertPreparedStatement(connection, entities);
            return executeBatch(connection, prepared);
        } catch (Exception e) {
            rollback(connection);
            throw new RuntimeException("执行插入发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 根据Id修改
     *
     * @param connection connection
     * @param entity     entity
     * @return affected rows
     */
    public int updateById(Connection connection, T entity) {
        return updateById(connection, entity, null, null);
    }

    /**
     * 根据Id修改
     *
     * @param connection        connection
     * @param entity            entity
     * @param includeProperties 包含的属性
     * @param excludeProperties 排除的属性
     * @return affected rows
     */
    public int updateById(Connection connection, T entity, @Nullable Collection<String> includeProperties, @Nullable Collection<String> excludeProperties) {
        try {
            var prepared = getUpdatePreparedStatement(connection, entity, includeProperties, excludeProperties);
            return executeUpdate(connection, prepared);
        } catch (Exception e) {
            throw new RuntimeException("执行更新语句发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * executeUpdate
     *
     * @param connection        connection
     * @param preparedStatement preparedStatement
     * @return affected rows
     */
    protected int executeUpdate(Connection connection, PreparedStatement preparedStatement) throws Exception {
        try {
            connection.setAutoCommit(false);
            var millis = System.currentTimeMillis();
            try {
                var res = preparedStatement.executeUpdate();
                connection.commit();
                return res;
            } finally {
                log(millis, preparedStatement);
            }
        } catch (Exception e) {
            rollback(connection);
            close(preparedStatement);
            throw e;
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    /**
     * executeBatch
     *
     * @param connection        connection
     * @param preparedStatement preparedStatement
     * @return affected rows
     */
    protected int executeBatch(Connection connection, PreparedStatement preparedStatement) throws Exception {
        try {
            connection.setAutoCommit(false);
            preparedStatement.addBatch();
            var millis = System.currentTimeMillis();
            try {
                var res = preparedStatement.executeBatch();
                connection.commit();
                return Arrays.stream(res).sum();
            } finally {
                log(millis, preparedStatement);
            }
        } catch (Exception e) {
            rollback(connection);
            close(preparedStatement);
            throw e;
        } finally {
            close(preparedStatement);
            close(connection);
        }
    }

    /**
     * 设置参数值
     *
     * @param prepared prepared
     * @param params   params
     * @param index    index：从零开始
     */
    protected void setParameterValue(PreparedStatement prepared, Collection<?> params, int index) throws SQLException {
        var i = index;
        for (var value : params) {
            i++;
            setParameterValue(prepared, value, i);
        }
    }

    /**
     * 设置参数值
     *
     * @param prepared prepared
     * @param value    value
     * @param i        i：从零开始
     */
    protected void setParameterValue(PreparedStatement prepared, Object value, int i) throws SQLException {
        if (value instanceof Instant) {
            var temp = (Instant) value;
            var timestamp = new Timestamp(temp.toEpochMilli());
            prepared.setTimestamp(i, timestamp);
        } else {
            prepared.setObject(i, value);
        }
    }


    /**
     * 获取查询条件
     *
     * @param ids ids
     * @return String
     */
    protected String getWhere(Collection<? extends Serializable> ids) {
        Assert.notEmpty(ids, "ids must not be empty");
        var builder = new StringBuilder(this.id);
        var element = ids.iterator().next();
        if (Number.class.isAssignableFrom(element.getClass())) {
            if (ids.size() == 1) {
                builder.append("=").append(element);
            } else {
                builder.append(" IN (").append(StringUtils.join(ids, ",")).append(")");
            }
        } else {
            if (ids.size() == 1) {
                builder.append("='").append(element).append("'");
            } else {
                builder.append(" IN ('").append(StringUtils.join(ids, "','")).append("')");
            }
        }
        builder.append(getLogicWhere());
        return builder.toString();
    }

    /**
     * 获取逻辑删除查询条件
     *
     * @return String
     */
    protected String getLogicWhere() {
        return StringUtils.isEmpty(this.logic) ? "" : String.format(" AND %s = 0", this.logic);
    }

    /**
     * 转换为SQL语句使用的value
     *
     * @param value value
     * @param type  type
     * @return 根据类型返回{value}获取'{value}'
     */
    protected String toSQLString(Object value, Class<?> type) {
        if (value == null) {
            return "NULL";
        }
        if (Number.class.isAssignableFrom(type)) {
            return value.toString();
        } else if (Boolean.class.isAssignableFrom(type)) {
            var temp = (Boolean) value;
            return temp ? "1" : "0";
        } else if (Temporal.class.isAssignableFrom(type)) {
            var temp = (Temporal) value;
            return "'" + this.dateTimeFormatter.format(temp) + "'";
        } else {
            return "'" + value.toString().replace("'", "''") + "'";
        }
    }

    /**
     * 获取预编译对象
     * 发生异常时，自动关闭connection
     *
     * @param connection connection
     * @param sql        sql
     * @return PreparedStatement
     */
    protected PreparedStatement getPreparedStatement(Connection connection, String sql) {
        try {
            return connection.prepareStatement(sql);
        } catch (Exception e) {
            close(connection);
            throw new RuntimeException("创建预编译对象发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 关闭连接
     *
     * @param connection connection
     */
    protected void close(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException("关闭连接发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 回滚
     *
     * @param connection connection
     */
    protected void rollback(Connection connection) {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (Exception e) {
            throw new RuntimeException("回滚发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 关闭编译对象
     *
     * @param statement statement
     */
    protected void close(Statement statement) {
        if (statement == null) {
            return;
        }
        try {
            statement.close();
        } catch (Exception e) {
            throw new RuntimeException("关闭编译对象发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 关闭预编译对象
     *
     * @param preparedStatement preparedStatement
     */
    protected void close(PreparedStatement preparedStatement) {
        if (preparedStatement == null) {
            return;
        }
        try {
            preparedStatement.close();
        } catch (Exception e) {
            throw new RuntimeException("关闭预编译对象发生异常：" + e.getMessage(), e);
        }
    }

    protected <SQL> void log(long mills, SQL sql) {
        var ts = System.currentTimeMillis() - mills;
        var str = sql.getClass().isArray() ? StringUtils.join((String[]) sql, ";") : sql;
        log.info("执行SQL=>耗时：{}ms，SQL：{}", ts, str);
    }

    /**
     * 是否可以更新
     *
     * @param propertyName      属性名
     * @param columnName        列名
     * @param includeProperties 包含的属性
     * @param excludeProperties 排除的属性
     * @return boolean
     */
    protected boolean canUpdate(String propertyName, String columnName, @Nullable Collection<String> includeProperties, @Nullable Collection<String> excludeProperties) {
        if (columnName.equals(this.logic) || columnName.equals(this.id) || columnName.equals(this.version)
                || this.createInfoColumns.contains(columnName) || this.deleteInfoColumns.contains(columnName)) {
            return false;
        }
        var canUpdate = CollectionUtils.isEmpty(includeProperties) || includeProperties.contains(propertyName);
        if (canUpdate && CollectionUtils.isNotEmpty(excludeProperties) && excludeProperties.contains(propertyName)) {
            canUpdate = false;
        }
        return canUpdate;
    }

}
