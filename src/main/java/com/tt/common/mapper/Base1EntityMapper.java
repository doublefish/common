package com.tt.common.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.tt.common.model.Entity;
import lombok.var;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base1EntityMapper
 * 需要在 mapper.xml 里声明三个方法：selectCountByWrapper/selectListByWrapper/selectPageByWrapper
 *
 * @author Shuang Yu
 */
public interface Base1EntityMapper<P extends Serializable, T extends Entity<P>> extends BaseEntityMapper<P, T> {

    /**
     * 根据 ID 查询
     *
     * @param id id
     * @return T
     */
    @Override
    default T selectById(Serializable id) {
        var qw = new QueryWrapper<T>();
        qw.eq("id", id);
        var list = selectList(qw);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    /**
     * 根据 ID 查询全部记录
     *
     * @param ids ids
     * @return List<T>
     */
    @Override
    default List<T> selectByIds(Collection<P> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        var qw = new QueryWrapper<T>();
        qw.in("id", ids);
        return selectList(qw);
    }

    /**
     * 根据指定列的值查询全部记录
     *
     * @param column column
     * @param value  value
     * @return List<T>
     */
    @Override
    default List<T> selectByValue(String column, Object value) {
        var qw = new QueryWrapper<T>();
        qw.eq(column, value);
        return selectList(qw);
    }

    /**
     * 根据指定列的值查询全部记录
     *
     * @param column column
     * @param values values
     * @return List<T>
     */
    @Override
    default List<T> selectByValues(String column, Collection<?> values) {
        if (CollectionUtils.isEmpty(values)) {
            return new ArrayList<>();
        }
        var qw = new QueryWrapper<T>();
        qw.in(column, values);
        return selectList(qw);
    }

    /**
     * 根据 entity 条件，查询一条记录
     *
     * @param queryWrapper 实体对象封装操作类
     * @return T
     */
    @Override
    default T selectOne(Wrapper<T> queryWrapper) {
        var list = selectList(queryWrapper);
        if (CollectionUtils.isNotEmpty(list)) {
            if (list.size() != 1) {
                throw ExceptionUtils.mpe("One record is expected, but the query result is multiple records");
            }
            return list.get(0);
        }
        return null;
    }

    /**
     * 根据 Wrapper 条件，判断是否存在记录
     *
     * @param queryWrapper 实体对象封装操作类
     * @return boolean
     */
    @Override
    default boolean exists(Wrapper<T> queryWrapper) {
        var count = selectCount(queryWrapper);
        return count != null && count > 0;
    }

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类
     * @return Long
     */
    @Override
    default Long selectCount(Wrapper<T> queryWrapper) {
        return selectCount(queryWrapper, false);
    }

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper   实体对象封装操作类
     * @param includeDeleted 是否包括已删除的
     * @return Long
     */
    default Long selectCount(Wrapper<T> queryWrapper, boolean includeDeleted) {
        var qw = (QueryWrapper<T>) queryWrapper;
        qw.eq(!includeDeleted, "row_deleted", 0);
        return selectCountByWrapper(qw);
    }

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper 实体对象封装操作类
     * @return list
     */
    @Override
    default List<T> selectList(Wrapper<T> queryWrapper) {
        return selectList(queryWrapper, false);
    }

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper   实体对象封装操作类
     * @param includeDeleted 是否包括已删除的
     * @return List<T>
     */
    default List<T> selectList(Wrapper<T> queryWrapper, boolean includeDeleted) {
        var qw = (QueryWrapper<T>) queryWrapper;
        qw.eq(!includeDeleted, "row_deleted", 0);
        return selectListByWrapper(qw);
    }

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     *
     * @param <Page>       IPage<T>
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类
     * @return <P extends IPage<T>>
     */
    @Override
    default <Page extends IPage<T>> Page selectPage(Page page, Wrapper<T> queryWrapper) {
        return selectPage(page, queryWrapper, false);
    }

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     *
     * @param <Page>         IPage<T>
     * @param page           分页查询条件
     * @param queryWrapper   实体对象封装操作类
     * @param includeDeleted 是否包括已删除的
     * @return <P extends IPage<T>>
     */
    default <Page extends IPage<T>> Page selectPage(Page page, Wrapper<T> queryWrapper, boolean includeDeleted) {
        var qw = (QueryWrapper<T>) queryWrapper;
        qw.eq(!includeDeleted, "row_deleted", 0);
        return selectPageByWrapper(page, qw);
    }

    /**
     * 根据 Wrapper 条件，查询总记录数
     * 需要在 mapper.xml 里声明此方法
     *
     * @param queryWrapper queryWrapper
     * @return Long
     */
    Long selectCountByWrapper(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 Wrapper 条件，查询全部记录
     * 需要在 mapper.xml 里声明此方法
     *
     * @param queryWrapper queryWrapper
     * @return List<T>
     */
    List<T> selectListByWrapper(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     * 需要在 mapper.xml 里声明此方法
     *
     * @param <P>          IPage<T>
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类
     * @return <P extends IPage<T>>
     */
    <P extends IPage<T>> P selectPageByWrapper(P page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper);

}
