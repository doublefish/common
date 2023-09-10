package com.tt.common.mapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.tt.common.model.Entity;
import lombok.var;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * BaseEntityMapper
 *
 * @author Shuang Yu
 */
public interface BaseEntityMapper<P extends Serializable, T extends Entity<P>> extends BaseMapper<T> {

    /**
     * 插入（批量）
     *
     * @param entities entities
     * @return affected rows
     */
    default int insertBatch(Collection<T> entities) {
        throw new RuntimeException("方法未实现");
    }

    /**
     * 插入（批量）
     *
     * @param entities entities
     * @param size     size
     * @return affected rows
     */
    default int insertBatch(Collection<T> entities, int size) {
        var rows = 0;
        if (entities.size() > size) {
            var skip = 0;
            while (true) {
                var list = entities.stream().skip(skip).limit(size).collect(Collectors.toList());
                if (list.size() > 0) {
                    rows += insertBatch(list);
                    skip += size;
                    continue;
                }
                break;
            }
        } else {
            rows += insertBatch(entities);
        }
        return rows;
    }

    /**
     * 删除（根据ID或实体 批量删除）
     *
     * @param entities entities
     * @return affected rows
     */
    default int deleteByIds(Collection<?> entities) {
        return deleteBatchIds(entities);
    }

    /**
     * 根据 ID 查询全部记录
     *
     * @param ids ids
     * @return list
     */
    default List<T> selectByIds(Collection<P> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return new ArrayList<>();
        }
        return selectBatchIds(ids);
    }

    /**
     * 根据指定列的值查询全部记录（单表）
     *
     * @param column column
     * @param value  value
     * @return list
     */
    default List<T> selectByValue(String column, Object value) {
        var qw = new QueryWrapper<T>();
        qw.eq(column, value);
        return selectList(qw);
    }

    /**
     * 根据指定列的值查询全部记录（单表）
     *
     * @param column column
     * @param values values
     * @return list
     */
    default List<T> selectByValues(String column, Collection<?> values) {
        if (CollectionUtils.isEmpty(values)) {
            return new ArrayList<>();
        }
        var qw = new QueryWrapper<T>();
        qw.in(column, values);
        return selectList(qw);
    }


}
