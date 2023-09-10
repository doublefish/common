package com.tt.common.config;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.tt.common.Constants;
import com.tt.common.RequestDataHelper;
import com.tt.common.util.DateTimeUtils;
import com.tt.common.util.MyBatisPlusUtils;
import lombok.var;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * 逻辑删除拦截器
 *
 * @author Shuang Yu
 */
public class LogicDeleteInterceptor implements InnerInterceptor {

    final static DateTimeFormatter dateTimeFormatter = DateTimeUtils.getDateTimeFormatter(Constants.DATE_TIME_FORMAT_MILLS);

    private final String appName;

    public LogicDeleteInterceptor(String appName) {
        this.appName = appName;
    }

    @Override
    public void beforeUpdate(Executor executor, MappedStatement ms, Object parameter) throws SQLException {
        InnerInterceptor.super.beforeUpdate(executor, ms, parameter);
    }

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        //InnerInterceptor.super.beforePrepare(sh, connection, transactionTimeout);
        var handler = PluginUtils.mpStatementHandler(sh);
        var ms = handler.mappedStatement();
        if (ms.getSqlCommandType() != SqlCommandType.UPDATE) {
            return;
        }

        var boundSql = handler.boundSql();
        var sql = boundSql.getSql();
        if (sql.contains(MyBatisPlusUtils.VALUE_NOW)) {
            var now = dateTimeFormatter.format(Instant.now());
            sql = sql.replace(MyBatisPlusUtils.VALUE_NOW, "'" + now + "'");
        }
        if (sql.contains(MyBatisPlusUtils.VALUE_UID)) {
            var user = RequestDataHelper.getUser();
            var uid = user != null ? user.getId() : appName;
            sql = sql.replace(MyBatisPlusUtils.VALUE_UID, "'" + uid + "'");
        }
        var mpBoundSql = PluginUtils.mpBoundSql(boundSql);
        mpBoundSql.sql(sql);
    }
}
