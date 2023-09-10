package com.tt.common.model;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.var;
import org.apache.ibatis.binding.MapperMethod;

/**
 * SqlStatement
 *
 * @author Shuang Yu
 */
@Data
@AllArgsConstructor
public class SqlStatement {
    private Class<?> mapper;
    private SqlMethod sqlMethod;
    private Object parameter;

    /**
     * 创建对象
     *
     * @param mapper    mapper
     * @param sqlMethod sqlMethod
     * @param parameter parameter
     */
    public static SqlStatement of(Class<?> mapper, SqlMethod sqlMethod, Object parameter) {
        return new SqlStatement(mapper, sqlMethod, parameter);
    }

    /**
     * 创建（插入一条数据的）对象
     *
     * @param <T>    T
     * @param mapper mapper
     * @param entity 插入对象
     */
    public static <T> SqlStatement ofInsertOne(Class<? extends BaseMapper<T>> mapper, T entity) {
        return of(mapper, SqlMethod.INSERT_ONE, entity);
    }

    /**
     * 创建（根据ID选择修改数据的）对象
     *
     * @param <T>    T
     * @param mapper mapper
     * @param entity 更新对象
     */
    public static <T> SqlStatement ofUpdateById(Class<? extends BaseMapper<T>> mapper, T entity) {
        var paramMap = new MapperMethod.ParamMap<>();
        paramMap.put("et", entity);
        return of(mapper, SqlMethod.UPDATE_BY_ID, paramMap);
    }

    /**
     * 创建（根据whereEntity条件，更新记录的）对象
     *
     * @param <T>           T
     * @param mapper        mapper
     * @param entity        更新对象
     * @param updateWrapper 更新条件
     */
    public static <T> SqlStatement ofUpdate(Class<? extends BaseMapper<T>> mapper, T entity, UpdateWrapper<T> updateWrapper) {
        var paramMap = new MapperMethod.ParamMap<>();
        paramMap.put("et", entity);
        paramMap.put("ew", updateWrapper);
        return of(mapper, SqlMethod.UPDATE_BY_ID, paramMap);
    }

}
