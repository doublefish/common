package com.tt.common.config;

import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.injector.methods.DeleteById;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import lombok.var;
import org.apache.ibatis.mapping.MappedStatement;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * DeleteById
 *
 * @author Shuang Yu
 */
public class LogicDeleteById extends DeleteById {

    public LogicDeleteById() {
        super();
    }

    public LogicDeleteById(String name) {
        super(name);
    }

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo) {
        var sql = "";
        var sqlMethod = SqlMethod.LOGIC_DELETE_BY_ID;
        if (tableInfo.isWithLogicDelete()) {
            var fieldInfos = tableInfo.getFieldList().stream()
                    .filter(TableFieldInfo::isWithUpdateFill)
                    .filter(f -> !f.isLogicDelete())
                    .collect(toList());
            if (CollectionUtils.isNotEmpty(fieldInfos)) {
                var sqlSet = "SET " + SqlScriptUtils.convertIf(fieldInfos.stream()
                        .map(i -> i.getSqlSet(EMPTY)).collect(joining(EMPTY)), "!@org.apache.ibatis.type.SimpleTypeRegistry@isSimpleType(_parameter.getClass())", true)
                        + tableInfo.getLogicDeleteSql(false, false);
                sqlSet += LogicDeleteMethod.sqlAuditSet(tableInfo);
                sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlSet, tableInfo.getKeyColumn(),
                        tableInfo.getKeyProperty(), tableInfo.getLogicDeleteSql(true, true));
            } else {
                var sqlSet = sqlLogicSet(tableInfo);
                sqlSet += LogicDeleteMethod.sqlAuditSet(tableInfo);
                sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), sqlSet,
                        tableInfo.getKeyColumn(), tableInfo.getKeyProperty(),
                        tableInfo.getLogicDeleteSql(true, true));
            }
            var sqlSource = languageDriver.createSqlSource(configuration, sql, Object.class);
            return addUpdateMappedStatement(mapperClass, modelClass, sqlMethod.getMethod(), sqlSource);
        } else {
            sqlMethod = SqlMethod.DELETE_BY_ID;
            sql = String.format(sqlMethod.getSql(), tableInfo.getTableName(), tableInfo.getKeyColumn(),
                    tableInfo.getKeyProperty());
            var sqlSource = languageDriver.createSqlSource(configuration, sql, Object.class);
            return this.addDeleteMappedStatement(mapperClass, sqlMethod.getMethod(), sqlSource);
        }
    }
}
