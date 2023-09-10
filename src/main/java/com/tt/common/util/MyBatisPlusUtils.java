package com.tt.common.util;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.tt.common.PageData;
import com.tt.common.RequestDataHelper;
import com.tt.common.User;
import com.tt.common.annotation.TableAlias;
import com.tt.common.config.AutofillHandler;
import com.tt.common.config.LogicDeleteInterceptor;
import com.tt.common.config.MySqlInjector;
import com.tt.common.model.SqlStatement;
import com.tt.common.model.Entity;
import com.tt.common.model.QueryDto;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.type.JdbcType;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * MyBatisPlusUtils
 *
 * @author Shuang Yu
 */
public class MyBatisPlusUtils {

    public final static String VALUE_NOW = "{now}";
    public final static String VALUE_UID = "{uid}";
    public static final int MAX_IN = 1000;

    private MyBatisPlusUtils() {
    }

    /**
     * 默认拦截器
     * 分页至少支持mysql、oracle、sqlserver，此版本为mp的3.2.0，以上版本则需要指定数据库类型
     *
     * @param appName appName
     * @return MybatisPlusInterceptor
     */
    public static MybatisPlusInterceptor getDefaultInterceptor(String appName) {
        var interceptor = new MybatisPlusInterceptor();
        // 动态表名拦截器
        var innerInterceptor = new DynamicTableNameInnerInterceptor();
        innerInterceptor.setTableNameHandler((sql, tableName) -> {
            // 获取参数方法
            var suffix = RequestDataHelper.getTableSuffix();
            if (StringUtils.isNotEmpty(suffix)) {
                return tableName + "_" + suffix;
            } else {
                return tableName;
            }
        });
        interceptor.addInnerInterceptor(innerInterceptor);
        // 添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 添加乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        // 添加逻辑删除
        interceptor.addInnerInterceptor(new LogicDeleteInterceptor(appName));
        return interceptor;
    }

    /**
     * getGlobalConfig
     *
     * @param appName appName
     * @return GlobalConfig
     */
    public static GlobalConfig getGlobalConfig(String appName) {
        // 全局配置
        var globalConfig = GlobalConfigUtils.defaults();
        // 设置填充器
        globalConfig.setMetaObjectHandler(new AutofillHandler(appName));
        // 设置SQL注入器
        globalConfig.setSqlInjector(new MySqlInjector(true));
        if (globalConfig.getDbConfig() == null) {
            globalConfig.setDbConfig(new GlobalConfig.DbConfig());
        }
        return globalConfig;
    }

    /**
     * getSqlSessionFactoryBean
     *
     * @param dataSource 数据源
     * @param appName    appName
     * @return SqlSessionFactoryBean
     */
    public static SqlSessionFactoryBean getSqlSessionFactoryBean(DataSource dataSource, String appName) {
        var globalConfig = getGlobalConfig(appName);
        return getSqlSessionFactoryBean(dataSource, globalConfig);
    }

    /**
     * getSqlSessionFactoryBean
     *
     * @param dataSource   数据源
     * @param globalConfig 全局配置
     * @return SqlSessionFactoryBean
     */
    public static SqlSessionFactoryBean getSqlSessionFactoryBean(DataSource dataSource, GlobalConfig globalConfig) {
        var bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        //sqlSessionFactory.setMapperLocations(mapperLocations);
        var configuration = new MybatisConfiguration();
        configuration.setJdbcTypeForNull(JdbcType.NULL);
        configuration.setMapUnderscoreToCamelCase(true);
        configuration.setCacheEnabled(false);
        bean.setConfiguration(configuration);
        // 全局配置
        GlobalConfigUtils.setGlobalConfig(configuration, globalConfig);
        return bean;
    }

    /**
     * 设置修改信息
     *
     * @param <P>           P 主键类型
     * @param <T>           T extends com.tt.common.model.v0.BaseEntity
     * @param updateWrapper updateWrapper
     * @param user          用户
     */
    public static <P, T extends Entity<P>> void setUpdateInfo(UpdateWrapper<T> updateWrapper, User user) {
        setUpdateInfo(updateWrapper, user, Instant.now());
    }

    /**
     * 设置修改信息
     *
     * @param <P>           P 主键类型
     * @param <T>           T extends com.tt.common.model.v0.BaseEntity
     * @param updateWrapper updateWrapper
     * @param user          用户
     * @param date          时间
     */
    public static <P, T extends Entity<P>> void setUpdateInfo(UpdateWrapper<T> updateWrapper, User user, Instant date) {
        updateWrapper.set("row_update_date", date);
        updateWrapper.set("row_update_user", user.getId());
        updateWrapper.set("row_update_username", user.getName());
    }

    /**
     * in
     *
     * @param <T>          T
     * @param queryWrapper queryWrapper
     * @param column       column
     * @param values       values
     */
    public static <T> void in(QueryWrapper<T> queryWrapper, String column, Collection<?> values) {
        if (values.size() > MAX_IN) {
            queryWrapper.and(o -> {
                var skip = 0;
                while (skip < values.size()) {
                    var list = values.stream().skip(skip).limit(MAX_IN).toArray();
                    o.or().in(column, list);
                    skip += MAX_IN;
                }
            });
        } else {
            queryWrapper.in(column, values);
        }
    }

    /**
     * notIn
     *
     * @param <T>          T
     * @param queryWrapper queryWrapper
     * @param column       column
     * @param values       values
     */
    public static <T> void notIn(QueryWrapper<T> queryWrapper, String column, Collection<?> values) {
        if (values.size() > MAX_IN) {
            queryWrapper.and(o -> {
                var skip = 0;
                while (skip < values.size()) {
                    var list = values.stream().skip(skip).limit(MAX_IN).toArray();
                    o.notIn(column, list);
                    skip += MAX_IN;
                }
            });
        } else {
            queryWrapper.notIn(column, values);
        }
    }


    /**
     * 生成查询条件
     *
     * @param <P>      P 主键类型
     * @param <T>      T extends com.tt.common.model.v0.BaseEntity
     * @param <D>      D extends com.tt.common.model.v0.BaseQueryDto
     * @param tClass   实体类型
     * @param queryDto 查询条件
     * @param isPage   是否分页
     * @return QueryWrapper<T>
     */
    public static <P, T extends Entity<P>, D extends QueryDto<P>> QueryWrapper<T> generateQueryWrapper(Class<T> tClass, D queryDto, boolean isPage) {
        var qw = new QueryWrapper<T>();
        var tableAlias = tClass.getAnnotation(TableAlias.class);
        var fieldMap = ReflectUtils.getDeclaredFieldMap(tClass);
        // 默认不带已删除数据
        if (!Boolean.TRUE.equals(queryDto.getIncludeDeleted())) {
            var column = getColumn(fieldMap.get("rowDeleted"), tableAlias);
            qw.eq(column, 0);
        }
        var columnId = getColumn(fieldMap.get("id"), tableAlias);
        if (CollectionUtils.isNotEmpty(queryDto.getIds())) {
            qw.in(columnId, queryDto.getIds());
        }
        if (CollectionUtils.isNotEmpty(queryDto.getExcludeIds())) {
            qw.notIn(columnId, queryDto.getExcludeIds());
        }
        if (StringUtils.isNotEmpty(queryDto.getRowCreateUser())) {
            var column = getColumn(fieldMap.get("rowCreateUser"), tableAlias);
            qw.eq(column, queryDto.getRowCreateUser().trim());
        }
        if (StringUtils.isNotEmpty(queryDto.getRowCreateUsername())) {
            var column = getColumn(fieldMap.get("rowCreateUsername"), tableAlias);
            var value = SQLUtils.escapeSpecialSymbols(queryDto.getRowCreateUsername().trim());
            qw.like(column, value);
        }
        // 创建时间
        if (queryDto.getRowCreateDateFrom() != null) {
            var column = getColumn(fieldMap.get("rowCreateDate"), tableAlias);
            qw.ge(column, queryDto.getRowCreateDateFrom());
        }
        if (queryDto.getRowCreateDateTo() != null) {
            var column = getColumn(fieldMap.get("rowCreateDate"), tableAlias);
            qw.le(column, queryDto.getRowCreateDateTo());
        }
        // 修改时间
        if (queryDto.getRowUpdateDateFrom() != null) {
            var column = getColumn(fieldMap.get("rowUpdateDate"), tableAlias);
            qw.ge(column, queryDto.getRowUpdateDateFrom());
        }
        if (queryDto.getRowUpdateDateTo() != null) {
            var column = getColumn(fieldMap.get("rowUpdateDate"), tableAlias);
            qw.le(column, queryDto.getRowUpdateDateTo());
        }

        if (!isPage) {
            // 排序
            var isAsc = "asc".equalsIgnoreCase(queryDto.getSortType());
            if (StringUtils.isNotEmpty(queryDto.getSortName())) {
                var field = fieldMap.get(queryDto.getSortName());
                Assert.notNull(field, "无法识别的排序字段：%s", queryDto.getSortName());
                qw.orderBy(true, isAsc, getColumn(field, tableAlias));
            }
            if (queryDto.getScrollId() != null) {
                if (isAsc) {
                    qw.gt(columnId, queryDto.getScrollId());
                } else {
                    qw.lt(columnId, queryDto.getScrollId());
                }
            }
            if (queryDto.getSize() != null) {
                qw.last("LIMIT " + queryDto.getSize());
            }
        }

        return qw;
    }

    /**
     * 生成分页查询对象
     *
     * @param <P>      P 主键类型
     * @param <T>      T extends com.tt.common.model.v0.BaseEntity
     * @param <D>      D extends com.tt.common.model.v0.BaseQueryDto
     * @param tClass   实体类型
     * @param queryDto 查询条件
     * @return IPage<T>
     */
    public static <P, T, D extends QueryDto<P>> IPage<T> generatePage(Class<T> tClass, D queryDto) {
        var page = new Page<T>(queryDto.getCurrent(), queryDto.getSize());
        if (MapUtils.isNotEmpty(queryDto.getSorts())) {
            for (var sort : queryDto.getSorts().entrySet()) {
                var column = getColumn(tClass, sort.getKey());
                var orderItem = new OrderItem();
                orderItem.setColumn(column);
                orderItem.setAsc(sort.getValue());
                page.orders().add(orderItem);
            }
        } else if (StringUtils.isNotEmpty(queryDto.getSortName())) {
            var column = getColumn(tClass, queryDto.getSortName());
            var orderItem = new OrderItem();
            orderItem.setColumn(column);
            orderItem.setAsc("asc".equalsIgnoreCase(queryDto.getSortType()));
            page.orders().add(orderItem);
        }
        return page;
    }

    /**
     * getColumn
     *
     * @param tClass tClass
     * @param column column
     * @return String
     */
    public static String getColumn(Class<?> tClass, String column) {
        var field = ReflectUtils.getDeclaredField(tClass, column);
        if (field == null) {
            throw new RuntimeException(String.format("无法识别的属性：%s", column));
        }
        var tableAlias = tClass.getAnnotation(TableAlias.class);
        return getColumn(field, tableAlias);
    }

    /**
     * getColumn
     *
     * @param field      field
     * @param tableAlias tableAlias
     * @return String
     */
    public static String getColumn(Field field, TableAlias tableAlias) {
        var value = "";
        if ("id".equalsIgnoreCase(field.getName())) {
            var id = field.getAnnotation(TableId.class);
            if (id == null) {
                throw new RuntimeException(String.format("属性：%s，未声明：@TableId", field.getName()));
            }
            value = id.value();
            if (tableAlias != null && StringUtils.isNotEmpty(tableAlias.value())) {
                value = tableAlias.value() + "." + value;
            }
        } else {
            var f = field.getAnnotation(TableField.class);
            if (f == null) {
                throw new RuntimeException(String.format("属性：%s，未声明：@TableField", field.getName()));
            }
            value = f.value();
            if (tableAlias != null && StringUtils.isNotEmpty(tableAlias.value())) {
                var alias = f.exist() ? tableAlias.value() : tableAlias.language();
                value = alias + "." + value;
            }
        }
        return value;
    }

    /**
     * convert
     *
     * @param <V>    V
     * @param vClass vClass
     * @param page   page
     */
    public static <V> PageData<V> convert(IPage<?> page, Class<V> vClass) {
        var res = new PageData<V>();
        var records = ReflectUtils.convert(page.getRecords(), vClass);
        res.setList(records);
        res.setTotal(page.getTotal());
        res.setSize(page.getSize());
        res.setCurrent(page.getCurrent());
        return res;
    }


    /**
     * 批量保存
     *
     * @param sqlSessionTemplate sqlSessionTemplate
     * @param sqlStatements      sqlStatements
     */
    public static void batchSave(SqlSessionTemplate sqlSessionTemplate, Collection<SqlStatement> sqlStatements) {
        batchSave(sqlSessionTemplate, sqlStatements, 1000);
    }

    /**
     * 批量保存
     *
     * @param sqlSessionTemplate sqlSessionTemplate
     * @param sqlStatements      sqlStatements
     * @param size               size
     */
    public static void batchSave(SqlSessionTemplate sqlSessionTemplate, Collection<SqlStatement> sqlStatements, int size) {
        var session = SqlSessionUtils.getSqlSession(sqlSessionTemplate.getSqlSessionFactory(),
                ExecutorType.BATCH, sqlSessionTemplate.getPersistenceExceptionTranslator());
        try {
            var i = 0;
            for (var sqlStatement : sqlStatements) {
                var statement = SqlHelper.getSqlStatement(sqlStatement.getMapper(), sqlStatement.getSqlMethod());
                if (sqlStatement.getSqlMethod() == SqlMethod.INSERT_ONE) {
                    session.insert(statement, sqlStatement.getParameter());
                } else if (sqlStatement.getSqlMethod() == SqlMethod.UPDATE_BY_ID) {
                    session.update(statement, sqlStatement.getParameter());
                } else if (sqlStatement.getSqlMethod() == SqlMethod.UPDATE) {
                    session.update(statement, sqlStatement.getParameter());
                }
                i++;
                if (i % size == 0 || i == sqlStatements.size()) {
                    session.flushStatements();
                }
            }
            session.commit();
        } finally {
            SqlSessionUtils.closeSqlSession(session, sqlSessionTemplate.getSqlSessionFactory());
        }
    }

    /**
     * 使用事务
     *
     * @param <R>                          R
     * @param dataSourceTransactionManager dataSourceTransactionManager
     * @param function                     function
     * @return R
     */
    public static <R> R useTransaction(DataSourceTransactionManager dataSourceTransactionManager, BiFunction<DataSourceTransactionManager, TransactionStatus, R> function) {
        var df = new DefaultTransactionDefinition();
        // 第三步，设置事务隔离级别，开启新的数据
        df.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        // 第四步，开启事务
        var transaction = dataSourceTransactionManager.getTransaction(df);
        try {
            return function.apply(dataSourceTransactionManager, transaction);
        } catch (Exception e) {
            e.printStackTrace();
            // 异常回滚事务
            dataSourceTransactionManager.rollback(transaction);
            throw e;
        }
    }

    /**
     * 生成插入SQL陈述式
     *
     * @param mapper   mapper
     * @param entities entities
     * @return List<SqlStatement>
     */
    public static List<SqlStatement> generateInsertSqlStatements(Class<?> mapper, Collection<?> entities) {
        return generateInsertSqlStatements(mapper, entities, false);
    }

    /**
     * 生成插入SQL陈述式
     *
     * @param mapper         mapper
     * @param entities       entities
     * @param useMultiThread 使用多线程
     * @return List<SqlStatement>
     */
    public static List<SqlStatement> generateInsertSqlStatements(Class<?> mapper, Collection<?> entities, boolean useMultiThread) {
        var sqlStatements = new ArrayList<SqlStatement>();
        if (useMultiThread) {
            var tasks = new ArrayList<Callable<List<SqlStatement>>>();
            var skip = 0;
            var size = 1000;
            while (true) {
                var part = entities.stream().skip(skip).limit(size).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(part)) {
                    break;
                }
                tasks.add(() -> {
                    var temps = new ArrayList<SqlStatement>();
                    for (var entity : part) {
                        temps.add(SqlStatement.of(mapper, SqlMethod.INSERT_ONE, entity));
                    }
                    return temps;
                });
                skip += size;
            }
            var res = ThreadUtils.submit(tasks, 1000);
            for (var r : res) {
                sqlStatements.addAll(r);
            }
        } else {
            for (var entity : entities) {
                var s = SqlStatement.of(mapper, SqlMethod.INSERT_ONE, entity);
                sqlStatements.add(s);
            }
        }
        return sqlStatements;
    }

}
