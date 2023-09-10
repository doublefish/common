package com.tt.common.util;

import com.tt.common.model.QueryDto;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * ElasticsearchUtils
 *
 * @author Shuang Yu
 */
public class ElasticsearchUtils {

    private ElasticsearchUtils() {
    }

    /**
     * toIdMap
     *
     * @param <T>    T
     * @param entity entity
     * @param clazz  clazz
     */
    public static <T> Object getId(T entity, Class<T> clazz) {
        Assert.notNull(entity, "entity must not be null");
        var idReader = getIdReadMethod(clazz);
        if (idReader == null) {
            throw new RuntimeException("主键没有读取方法");
        }
        try {
            return idReader.invoke(entity);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("读取Id的值发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * toIdMap
     * 性能：10W=35ms，100W=200ms
     *
     * @param entities entities
     * @param clazz    clazz
     * @return Map<String, Object>
     */
    public static Map<String, Object> toIdMap(Collection<?> entities, Class<?> clazz) {
        Assert.notNull(entities, "entities must not be null");
        var idReader = getIdReadMethod(clazz);
        if (idReader == null) {
            throw new RuntimeException("主键没有读取方法");
        }
        var map = new LinkedHashMap<String, Object>(0);
        try {
            for (var obj : entities) {
                var id = idReader.invoke(obj);
                map.put(id.toString(), obj);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException("读取Id的值发生异常：" + e.getMessage(), e);
        }
        return map;
    }

    private static Method getIdReadMethod(Class<?> clazz) {
        var field = ReflectUtils.getDeclaredFieldByAnnotation(clazz, Id.class);
        if (field == null) {
            throw new RuntimeException("没有注解为主键的字段");
        }
        return ReflectUtils.getReadMethod(clazz, field.getName());
    }

    /**
     * getContents
     *
     * @param <T>        T
     * @param searchHits searchHits
     * @return List<T>
     */
    public static <T> List<T> getContents(SearchHits<T> searchHits) {
        var list = new ArrayList<T>();
        if (searchHits.getTotalHits() > 0) {
            for (var searchHit : searchHits.getSearchHits()) {
                list.add(searchHit.getContent());
            }
        }
        return list;
    }

    /**
     * 生成更新文档脚本
     *
     * @param fields fields
     * @return ctx._source.{field}=params.{field};...
     */
    public static String generateUpdateScript(Collection<String> fields) {
        var script = new StringBuilder();
        for (var f : fields) {
            script.append("ctx._source.").append(f).append("=params.").append(f).append(";");
        }
        return script.toString();
    }

    /**
     * 生成查询条件
     *
     * @param <P>      P 主键类型
     * @param <D>      D extends com.tt.common.model.v0.BaseQueryDto
     * @param queryDto queryDto
     * @return BoolQueryBuilder
     */
    public static <P, D extends QueryDto<P>> BoolQueryBuilder generateQueryBuilder(D queryDto) {
        var query = QueryBuilders.boolQuery();
        // 默认不带已删除数据
        if (!Boolean.TRUE.equals(queryDto.getIncludeDeleted())) {
            query.must(QueryBuilders.termQuery("rowDeleted", false));
        }
        if (CollectionUtils.isNotEmpty(queryDto.getIds())) {
            query.must(QueryBuilders.termsQuery("id", queryDto.getIds()));
        }
        if (CollectionUtils.isNotEmpty(queryDto.getExcludeIds())) {
            query.mustNot(QueryBuilders.termsQuery("id", queryDto.getExcludeIds()));
        }
        if (StringUtils.isNotEmpty(queryDto.getRowCreateUser())) {
            query.must(QueryBuilders.termQuery("rowCreateUser", queryDto.getRowCreateUser().trim()));
        }
        if (StringUtils.isNotEmpty(queryDto.getRowCreateUsername())) {
            query.must(QueryBuilders.termQuery("rowCreateUsername", queryDto.getRowCreateUsername().trim()));
        }
        // 创建时间
        if (queryDto.getRowCreateDateFrom() != null && queryDto.getRowCreateDateTo() != null) {
            query.must(QueryBuilders.rangeQuery("rowCreateDate").from(queryDto.getRowCreateDateFrom()).to(queryDto.getRowCreateDateTo()));
        } else if (queryDto.getRowCreateDateFrom() != null) {
            query.must(QueryBuilders.rangeQuery("rowCreateDate").from(queryDto.getRowCreateDateFrom()));
        } else if (queryDto.getRowCreateDateTo() != null) {
            query.must(QueryBuilders.rangeQuery("rowCreateDate").to(queryDto.getRowCreateDateTo()));
        }
        // 修改时间
        if (queryDto.getRowUpdateDateFrom() != null && queryDto.getRowUpdateDateTo() != null) {
            query.must(QueryBuilders.rangeQuery("rowUpdateDate").from(queryDto.getRowUpdateDateFrom()).to(queryDto.getRowUpdateDateTo()));
        } else if (queryDto.getRowUpdateDateFrom() != null) {
            query.must(QueryBuilders.rangeQuery("rowUpdateDate").from(queryDto.getRowUpdateDateFrom()));
        } else if (queryDto.getRowUpdateDateTo() != null) {
            query.must(QueryBuilders.rangeQuery("rowUpdateDate").to(queryDto.getRowUpdateDateTo()));
        }
        return query;
    }

    /**
     * 生成查询条件
     *
     * @param <P>      P 主键类型
     * @param <T>      T 实体类型
     * @param <D>      D extends com.tt.common.model.QueryDto
     * @param tClass   实体类型
     * @param query    query
     * @param queryDto 查询条件
     * @param isPage   是否分页
     * @return NativeSearchQueryBuilder
     */
    public static <P, T, D extends QueryDto<P>> NativeSearchQueryBuilder generateNativeQueryBuilder(Class<T> tClass, BoolQueryBuilder query, D queryDto, boolean isPage) {
        var isAsc = "asc".equalsIgnoreCase(queryDto.getSortType());
        var queryBuilder = new NativeSearchQueryBuilder();
        queryBuilder.withIndicesOptions(IndicesOptions.LENIENT_EXPAND_OPEN)
                .withTrackTotalHits(true)
                //.withFilter(filter)
                .withSort(SortBuilders.fieldSort(getColumn(tClass, queryDto.getSortName())).order(isAsc ? SortOrder.ASC : SortOrder.DESC));
        if (isPage) {
            queryBuilder.withPageable(PageRequest.of(queryDto.getCurrent() - 1, queryDto.getSize()));
        } else {
            if (queryDto.getScrollId() != null) {
                if (isAsc) {
                    query.must(QueryBuilders.rangeQuery("id").gt(queryDto.getScrollId()));
                } else {
                    query.must(QueryBuilders.rangeQuery("id").lt(queryDto.getScrollId()));
                }
            }
            if (queryDto.getSize() != null) {
                queryBuilder.withMaxResults(queryDto.getSize());
            }
        }
        queryBuilder.withQuery(query);
        return queryBuilder;
    }

    /**
     * 生成查询条件
     *
     * @param <P>      P 主键类型
     * @param <T>      T 实体类型
     * @param <D>      D extends com.tt.common.model.QueryDto
     * @param tClass   实体类型
     * @param query    query
     * @param queryDto 查询条件
     * @param isPage   是否分页
     * @return SearchSourceBuilder
     */
    public static <P, T, D extends QueryDto<P>> SearchSourceBuilder generateSourceBuilder(Class<T> tClass, BoolQueryBuilder query, D queryDto, boolean isPage) {
        var isAsc = "asc".equalsIgnoreCase(queryDto.getSortType());
        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.trackTotalHits(true);
        sourceBuilder.query(query);
        if (StringUtils.isNotEmpty(queryDto.getSortName())) {
            sourceBuilder.sort(new FieldSortBuilder(getColumn(tClass, queryDto.getSortName())).order(isAsc ? SortOrder.ASC : SortOrder.DESC));
        }
        if (isPage) {
            sourceBuilder.from((queryDto.getCurrent() - 1) * queryDto.getSize());
        } else {
            if (queryDto.getScrollId() != null) {
                if (isAsc) {
                    query.must(QueryBuilders.rangeQuery("id").gt(queryDto.getScrollId()));
                } else {
                    query.must(QueryBuilders.rangeQuery("id").lt(queryDto.getScrollId()));
                }
            }
        }
        sourceBuilder.size(queryDto.getSize());
        return sourceBuilder;
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
        return getColumn(field);
    }

    /**
     * getColumn
     *
     * @param field field
     * @return String
     */
    public static String getColumn(Field field) {
        var value = "";
        if ("id".equalsIgnoreCase(field.getName())) {
            value = "id";
        } else {
            var f = field.getAnnotation(org.springframework.data.elasticsearch.annotations.Field.class);
            if (f == null) {
                throw new RuntimeException(String.format("属性：%s，未声明：@Field", field.getName()));
            }
            value = f.value();
        }
        if (StringUtils.isEmpty(value)) {
            value = field.getName();
        }
        return value;
    }
}
