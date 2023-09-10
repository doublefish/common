package com.tt.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tt.common.model.BaseLanguageEntity;
import lombok.var;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * BaseLanguageEntityMapper
 *
 * @author Shuang Yu
 */
public interface BaseLanguageEntityMapper<T extends BaseLanguageEntity> extends BaseEntityMapper<Long, T> {

    /**
     * 根据 ID 查询
     *
     * @param id       id
     * @param language language
     * @return T
     */
    default T selectById(Serializable id, String language) {
        var queryWrapper = new QueryWrapper<T>();
        queryWrapper.eq("id", id);
        queryWrapper.eq("language", language);
        return selectOne(queryWrapper);
    }

    /**
     * getMainIdField
     *
     * @return getMainIdField
     */
    default String getMainIdField() {
        throw new RuntimeException("没有匹配的字段");
    }

    /**
     * generateQueryWrapper
     *
     * @param mainId   mainId
     * @param language language
     * @return QueryWrapper
     */
    default QueryWrapper<T> generateQueryWrapper(Serializable mainId, String language) {
        var queryWrapper = new QueryWrapper<T>();
        queryWrapper.eq(getMainIdField(), mainId);
        queryWrapper.eq(language != null, "language", language);
        return queryWrapper;
    }

    /**
     * generateQueryWrapper
     *
     * @param mainIds  orgIds
     * @param language language
     * @return QueryWrapper
     */
    default QueryWrapper<T> generateQueryWrapper(Collection<? extends Serializable> mainIds, String language) {
        var queryWrapper = new QueryWrapper<T>();
        queryWrapper.in(getMainIdField(), mainIds);
        queryWrapper.eq(language != null, "language", language);
        return queryWrapper;
    }

    /**
     * selectByMainId
     *
     * @param mainId   mainId
     * @param language language
     * @return T
     */
    default T selectByMainId(Serializable mainId, String language) {
        var queryWrapper = generateQueryWrapper(mainId, language);
        return selectOne(queryWrapper);
    }

    /**
     * selectListByMainId
     *
     * @param mainId mainId
     * @return List<T>
     */
    default List<T> selectListByMainId(Serializable mainId) {
        var queryWrapper = generateQueryWrapper(mainId, null);
        return selectList(queryWrapper);
    }

    /**
     * selectListByMainIds
     *
     * @param mainIds mainIds
     * @return List<T>
     */
    default List<T> selectListByMainIds(Collection<? extends Serializable> mainIds) {
        var queryWrapper = generateQueryWrapper(mainIds, null);
        return selectList(queryWrapper);
    }

    /**
     * deleteByMainId
     *
     * @param mainId mainId
     * @return affected rows
     */
    default int deleteByMainId(Serializable mainId) {
        var queryWrapper = generateQueryWrapper(mainId, null);
        return delete(queryWrapper);
    }

    /**
     * deleteByMainIds
     *
     * @param mainIds mainIds
     * @return affected rows
     */
    default int deleteByMainIds(Collection<? extends Serializable> mainIds) {
        var queryWrapper = generateQueryWrapper(mainIds, null);
        return delete(queryWrapper);
    }

}
