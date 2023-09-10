package com.tt.common.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.tt.common.RequestDataHelper;
import com.tt.common.model.Entity;
import lombok.var;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Base2EntityMapper
 *
 * @author Shuang Yu
 */
public interface Base2EntityMapper<P extends Serializable, T extends Entity<P>> extends Base1EntityMapper<P, T> {

    /**
     * 根据 ID 查询
     *
     * @param id id
     * @return T
     */
    @Override
    default T selectById(Serializable id) {
        var qw = new QueryWrapper<T>();
        qw.eq("t.id", id);
        var list = selectList(qw);
        return list.size() > 0 ? list.get(0) : null;
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
        qw.in("t.id", ids);
        return selectList(qw);
    }

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper   实体对象封装操作类
     * @param includeDeleted 是否包括已删除的
     * @return Long
     */
    @Override
    default Long selectCount(Wrapper<T> queryWrapper, boolean includeDeleted) {
        var qw = (QueryWrapper<T>) queryWrapper;
        qw.eq(!includeDeleted, "t.row_deleted", 0);
        return selectCountByWrapper(qw, RequestDataHelper.getLanguage());
    }

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper   实体对象封装操作类
     * @param includeDeleted 是否包括已删除的
     * @return List<T>
     */
    @Override
    default List<T> selectList(Wrapper<T> queryWrapper, boolean includeDeleted) {
        var qw = (QueryWrapper<T>) queryWrapper;
        qw.eq(!includeDeleted, "t.row_deleted", 0);
        return selectListByWrapper(qw, RequestDataHelper.getLanguage());
    }

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     *
     * @param <Page>       IPage<OrgTagEntity>
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类
     * @return <P extends IPage<T>>
     */
    @Override
    default <Page extends IPage<T>> Page selectPage(Page page, Wrapper<T> queryWrapper, boolean includeDeleted) {
        var qw = (QueryWrapper<T>) queryWrapper;
        qw.eq(!includeDeleted, "t.row_deleted", 0);
        return selectPageByWrapper(page, qw, RequestDataHelper.getLanguage());
    }

    /**
     * 根据 Wrapper 条件，查询总记录数
     *
     * @param queryWrapper queryWrapper
     * @param language     language
     * @return Long
     */
    Long selectCountByWrapper(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper, @Param("language") String language);

    /**
     * 根据 Wrapper 条件，查询全部记录
     *
     * @param queryWrapper queryWrapper
     * @param language     language
     * @return List<T>
     */
    List<T> selectListByWrapper(@Param(Constants.WRAPPER) Wrapper<T> queryWrapper, @Param("language") String language);

    /**
     * 根据 entity 条件，查询全部记录（并翻页）
     *
     * @param <P>          IPage<OrgTagEntity>
     * @param page         分页查询条件
     * @param queryWrapper 实体对象封装操作类
     * @param language     language
     * @return <P extends IPage<T>>
     */
    <P extends IPage<T>> P selectPageByWrapper(P page, @Param(Constants.WRAPPER) Wrapper<T> queryWrapper, @Param("language") String language);

}
