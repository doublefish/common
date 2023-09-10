package com.tt.common.component;

import com.tt.common.PageData;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.data.elasticsearch.core.convert.ElasticsearchConverter;
import org.springframework.data.elasticsearch.core.document.DocumentAdapters;
import org.springframework.data.elasticsearch.core.index.MappingBuilder;
import org.springframework.data.elasticsearch.core.query.BulkOptions;
import org.springframework.data.elasticsearch.core.routing.DefaultRoutingResolver;
import org.springframework.data.elasticsearch.core.routing.RoutingResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * ElasticsearchHelper
 *
 * @author Shuang.Yu
 */
@Slf4j
public class ElasticsearchBaseHelper {

    protected final ApplicationContext context;
    protected final RestHighLevelClient client;
    protected final ElasticsearchConverter converter;
    @Nullable
    protected RefreshPolicy refreshPolicy;
    @Nullable
    protected RoutingResolver routingResolver;
    protected RequestOptions requestOptions;

    /**
     * 构造函数
     *
     * @param context context
     */
    public ElasticsearchBaseHelper(ApplicationContext context) {
        this(context, null);
    }

    /**
     * 构造函数
     *
     * @param context context
     * @param client  client
     */
    public ElasticsearchBaseHelper(ApplicationContext context, RestHighLevelClient client) {
        this.context = context;
        this.client = client != null ? client : context.getBean(RestHighLevelClient.class);
        this.converter = context.getBean(ElasticsearchConverter.class);
        this.routingResolver = new DefaultRoutingResolver(this.converter.getMappingContext());
        this.requestOptions = RequestOptions.DEFAULT;
    }

    /**
     * 设置刷新策略
     *
     * @param refreshPolicy refreshPolicy
     */
    public void setRefreshPolicy(@Nullable RefreshPolicy refreshPolicy) {
        this.refreshPolicy = refreshPolicy;
    }

    /**
     * 设置请求配置
     *
     * @param requestOptions requestOptions
     */
    public void setRequestOptions(RequestOptions requestOptions) {
        Assert.notNull(requestOptions, "requestOptions must not be null");
        this.requestOptions = requestOptions;
    }

    /**
     * 设置默认配置
     *
     * @param <R>     R
     * @param request request
     */
    public <R extends WriteRequest<R>> void setDefaultConfig(WriteRequest<R> request) {
        if (refreshPolicy != null) {
            var policy = "";
            if (refreshPolicy == RefreshPolicy.IMMEDIATE) {
                policy = "true";
            } else if (refreshPolicy == RefreshPolicy.WAIT_UNTIL) {
                policy = "wait_for";
            } else {
                policy = "false";
            }
            request.setRefreshPolicy(policy);
        }
    }

    /**
     * 判读索引是否存在
     *
     * @param indices indices
     * @return boolean
     */
    public boolean indexExists(String... indices) {
        var getIndexRequest = new GetIndexRequest(indices);
        try {
            return client.indices().exists(getIndexRequest, requestOptions);
        } catch (IOException e) {
            throw new RuntimeException("判断索引是否存在发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 生成映射
     *
     * @param clazz clazz
     * @return mapping
     */
    public String buildMapping(Class<?> clazz) {
        return new MappingBuilder(converter).buildPropertyMapping(clazz);
    }

    /**
     * 生成数据源
     *
     * @param source source
     * @return String
     */
    public String buildJsonSource(Object source) {
        return converter.mapObject(source).toJson();
    }

    /**
     * 创建索引
     *
     * @param index    index
     * @param settings settings
     * @param clazz    clazz
     * @return boolean
     */
    public boolean create(String index, Settings settings, Class<?> clazz) {
        var mapping = buildMapping(clazz);
        return create(index, settings, mapping, XContentType.JSON);
    }

    /**
     * 创建索引
     *
     * @param index    index
     * @param settings settings
     * @param mapping  mapping
     * @return boolean
     */
    public boolean create(String index, Settings settings, String mapping) {
        return create(index, settings, mapping, XContentType.JSON);
    }

    /**
     * 创建索引
     *
     * @param index        index
     * @param settings     settings
     * @param mapping      mapping
     * @param xContentType xContentType
     * @return boolean
     */
    public boolean create(String index, Settings settings, String mapping, XContentType xContentType) {
        var request = new CreateIndexRequest(index);
        request.settings(settings).mapping(mapping, xContentType);
        return create(request);
    }

    /**
     * 创建索引
     *
     * @param index    index
     * @param settings settings
     * @param mapping  mapping
     * @return boolean
     */
    public boolean create(String index, Settings settings, Map<String, ?> mapping) {
        var request = new CreateIndexRequest(index);
        request.settings(settings).mapping(mapping);
        return create(request);
    }

    /**
     * 创建索引
     *
     * @param request request
     * @return boolean
     */
    public boolean create(CreateIndexRequest request) {
        try {
            var response = client.indices().create(request, requestOptions);
            return response.isAcknowledged();
        } catch (IOException e) {
            throw new RuntimeException("创建索引发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 新增/新增或删除后新增/创建
     *
     * @param index  index
     * @param id     id
     * @param source source
     * @return IndexResponse
     */
    public IndexResponse index(String index, String id, String source) {
        return index(index, id, source, XContentType.JSON);
    }

    /**
     * 新增/新增或删除后新增/创建
     *
     * @param index        index
     * @param id           id
     * @param source       source
     * @param xContentType xContentType
     * @return IndexResponse
     */
    public IndexResponse index(String index, String id, String source, XContentType xContentType) {
        var request = new IndexRequest(index).id(id);
        setDefaultConfig(request);
        request.source(source, xContentType);
        return index(request);
    }

    /**
     * 新增/新增或删除后新增/创建
     *
     * @param index        index
     * @param id           id
     * @param source       source
     * @param xContentType xContentType
     * @return IndexResponse
     */
    public IndexResponse index(String index, String id, Map<String, ?> source, XContentType xContentType) {
        var request = new IndexRequest(index).id(id);
        setDefaultConfig(request);
        request.source(source, xContentType);
        return index(request);
    }

    /**
     * 新增/新增或删除后新增/创建
     *
     * @param indexRequest request
     * @return IndexResponse
     */
    public IndexResponse index(IndexRequest indexRequest) {
        return index(indexRequest, requestOptions);
    }

    /**
     * 新增/新增或删除后新增/创建
     *
     * @param indexRequest request
     * @param options      options
     * @return IndexResponse
     */
    public IndexResponse index(IndexRequest indexRequest, RequestOptions options) {
        Assert.notNull(indexRequest, "indexRequest must not be null");
        try {
            return client.index(indexRequest, options);
        } catch (IOException e) {
            throw new RuntimeException("保存文档发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 修改
     *
     * @param updateRequest request
     * @return BulkByScrollResponse
     */
    public UpdateResponse update(UpdateRequest updateRequest) {
        return update(updateRequest, requestOptions);
    }

    /**
     * 修改
     *
     * @param updateRequest request
     * @param options       options
     * @return BulkByScrollResponse
     */
    public UpdateResponse update(UpdateRequest updateRequest, RequestOptions options) {
        Assert.notNull(updateRequest, "updateRequest must not be null");
        try {
            return client.update(updateRequest, options);
        } catch (IOException e) {
            throw new RuntimeException("修改文档发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 修改
     *
     * @param updateByQueryRequest request
     * @return BulkByScrollResponse
     */
    public BulkByScrollResponse updateByQuery(UpdateByQueryRequest updateByQueryRequest) {
        return updateByQuery(updateByQueryRequest, requestOptions);
    }

    /**
     * 修改
     *
     * @param updateByQueryRequest request
     * @param options              options
     * @return BulkByScrollResponse
     */
    public BulkByScrollResponse updateByQuery(UpdateByQueryRequest updateByQueryRequest, RequestOptions options) {
        Assert.notNull(updateByQueryRequest, "updateByQueryRequest must not be null");
        try {
            return client.updateByQuery(updateByQueryRequest, options);
        } catch (IOException e) {
            throw new RuntimeException("修改文档发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 删除
     *
     * @param index index
     * @param id    id
     * @return DeleteResponse
     */
    public DeleteResponse delete(String index, String id) {
        Assert.notNull(routingResolver, "routingResolver must not be null");
        return delete(index, id, routingResolver.getRouting());
    }

    /**
     * 删除
     *
     * @param index   index
     * @param id      id
     * @param routing routing
     * @return DeleteResponse
     */
    public DeleteResponse delete(String index, String id, @Nullable String routing) {
        var request = new DeleteRequest(index, id);
        setDefaultConfig(request);
        if (routing != null) {
            request.routing(routing);
        }
        return delete(request);
    }

    /**
     * 删除
     *
     * @param deleteRequest request
     * @return DeleteResponse
     */
    public DeleteResponse delete(DeleteRequest deleteRequest) {
        return delete(deleteRequest, requestOptions);
    }

    /**
     * 删除
     *
     * @param deleteRequest request
     * @param options       options
     * @return DeleteResponse
     */
    public DeleteResponse delete(DeleteRequest deleteRequest, RequestOptions options) {
        Assert.notNull(deleteRequest, "deleteRequest must not be null");
        try {
            return client.delete(deleteRequest, options);
        } catch (IOException e) {
            throw new RuntimeException("删除文档发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 删除
     *
     * @param deleteByQueryRequest request
     * @return BulkByScrollResponse
     */
    public BulkByScrollResponse deleteByQuery(DeleteByQueryRequest deleteByQueryRequest) {
        return deleteByQuery(deleteByQueryRequest, requestOptions);
    }

    /**
     * 删除
     *
     * @param deleteByQueryRequest request
     * @param options              options
     * @return BulkByScrollResponse
     */
    public BulkByScrollResponse deleteByQuery(DeleteByQueryRequest deleteByQueryRequest, RequestOptions options) {
        Assert.notNull(deleteByQueryRequest, "deleteByQueryRequest must not be null");
        try {
            return client.deleteByQuery(deleteByQueryRequest, options);
        } catch (IOException e) {
            throw new RuntimeException("删除文档发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 批量
     *
     * @param bulkRequest bulkRequest
     * @return BulkResponse
     */
    public BulkResponse bulk(BulkRequest bulkRequest) {
        return bulk(bulkRequest, BulkOptions.defaultOptions(), requestOptions);
    }

    /**
     * 批量
     *
     * @param bulkRequest bulkRequest
     * @param bulkOptions bulkOptions
     * @param options     options
     * @return BulkResponse
     */
    public BulkResponse bulk(BulkRequest bulkRequest, BulkOptions bulkOptions, RequestOptions options) {
        Assert.notNull(bulkRequest, "bulkRequest must not be null");
        if (bulkOptions.getTimeout() != null) {
            bulkRequest.timeout(bulkOptions.getTimeout());
        }
        if (bulkOptions.getRefreshPolicy() != null) {
            bulkRequest.setRefreshPolicy(bulkOptions.getRefreshPolicy());
        }
        if (bulkOptions.getWaitForActiveShards() != null) {
            bulkRequest.waitForActiveShards(bulkOptions.getWaitForActiveShards());
        }
        if (bulkOptions.getPipeline() != null) {
            bulkRequest.pipeline(bulkOptions.getPipeline());
        }
        if (bulkOptions.getRoutingId() != null) {
            bulkRequest.routing(bulkOptions.getRoutingId());
        }
        try {
            return client.bulk(bulkRequest, options);
        } catch (IOException e) {
            throw new RuntimeException("批量操作文档发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 查询
     *
     * @param <T>   T
     * @param id    id
     * @param clazz clazz
     * @param index index
     * @return T
     */
    public <T> T searchById(Object id, Class<T> clazz, String index) {
        var sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.trackTotalHits(true);
        sourceBuilder.query(QueryBuilders.termQuery("_id", id));
        var list = search(sourceBuilder, clazz, index);
        return list.size() > 0 ? list.get(0) : null;
    }

    /**
     * 查询
     *
     * @param <T>           T
     * @param sourceBuilder sourceBuilder
     * @param clazz         clazz
     * @param indices       indices
     * @return PageData<T>
     */
    public <T> PageData<T> page(SearchSourceBuilder sourceBuilder, Class<T> clazz, String... indices) {
        var request = new SearchRequest(indices).source(sourceBuilder);
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        return page(request, clazz);
    }

    /**
     * 分页查询
     *
     * @param <T>           T
     * @param searchRequest request
     * @param clazz         clazz
     * @return PageData<T>
     */
    public <T> PageData<T> page(SearchRequest searchRequest, Class<T> clazz) {
        var response = search(searchRequest);
        var hits = response.getHits();
        var list = new ArrayList<T>();
        var total = hits.getTotalHits().value;
        if (hits.getHits().length > 0) {
            for (var hit : hits) {
                var document = DocumentAdapters.from(hit);
                var obj = converter.read(clazz, document);
                list.add(obj);
            }
        }
        return PageData.of(list, total);
    }

    /**
     * 查询
     *
     * @param <T>           T
     * @param sourceBuilder sourceBuilder
     * @param clazz         clazz
     * @param indices       indices
     * @return List<T>
     */
    public <T> List<T> search(SearchSourceBuilder sourceBuilder, Class<T> clazz, String... indices) {
        var request = new SearchRequest(indices).source(sourceBuilder);
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        return search(request, clazz);
    }

    /**
     * 查询
     *
     * @param <T>           T
     * @param searchRequest request
     * @param clazz         clazz
     * @return List<T>
     */
    public <T> List<T> search(SearchRequest searchRequest, Class<T> clazz) {
        var response = search(searchRequest);
        var hits = response.getHits();
        var list = new ArrayList<T>();
        for (var hit : hits.getHits()) {
            var document = DocumentAdapters.from(hit);
            var obj = converter.read(clazz, document);
            list.add(obj);
        }
        return list;
    }

    /**
     * 查询
     *
     * @param sourceBuilder sourceBuilder
     * @param indices       indices
     * @return SearchResponse
     */
    public SearchResponse search(SearchSourceBuilder sourceBuilder, String... indices) {
        var request = new SearchRequest(indices).source(sourceBuilder);
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        return search(request);
    }

    /**
     * 查询
     *
     * @param searchRequest request
     * @return SearchResponse
     */
    public SearchResponse search(SearchRequest searchRequest) {
        return search(searchRequest, requestOptions);
    }

    /**
     * 查询
     *
     * @param searchRequest request
     * @param options       options
     * @return SearchResponse
     */
    public SearchResponse search(SearchRequest searchRequest, RequestOptions options) {
        Assert.notNull(searchRequest, "searchRequest must not be null");
        try {
            return client.search(searchRequest, options);
        } catch (IOException e) {
            throw new RuntimeException("查询文档发生异常：" + e.getMessage(), e);
        }
    }


    /**
     * 滚动查询
     *
     * @param <T>           T
     * @param sourceBuilder sourceBuilder
     * @param clazz         clazz
     * @param indices       indices
     * @return List<T>
     */
    public <T> List<T> scroll(SearchSourceBuilder sourceBuilder, Class<T> clazz, String... indices) {
        var request = new SearchRequest(indices).source(sourceBuilder);
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        return scroll(request, clazz);
    }

    /**
     * 滚动查询
     *
     * @param <T>           T
     * @param searchRequest request
     * @param clazz         clazz
     * @return List<T>
     */
    public <T> List<T> scroll(SearchRequest searchRequest, Class<T> clazz) {
        var hits = scroll(searchRequest);
        var list = new ArrayList<T>();
        for (var hit : hits) {
            var document = DocumentAdapters.from(hit);
            var obj = converter.read(clazz, document);
            list.add(obj);
        }
        return list;
    }

    /**
     * 滚动查询
     *
     * @param searchRequest request
     * @return List<SearchHit>
     */
    public List<SearchHit> scroll(SearchRequest searchRequest) {
        return scroll(searchRequest, 10000, 30000L, requestOptions);
    }

    /**
     * 滚动查询
     *
     * @param searchRequest request
     * @param options       options
     * @return List<SearchHit>
     */
    public List<SearchHit> scroll(SearchRequest searchRequest, RequestOptions options) {
        return scroll(searchRequest, 10000, 30000L, options);
    }

    /**
     * 滚动查询
     *
     * @param searchRequest   request
     * @param size            每次查询数据量
     * @param keepAliveMillis keepAliveMillis
     * @param options         options
     * @return List<SearchHit>
     */
    public List<SearchHit> scroll(SearchRequest searchRequest, int size, long keepAliveMillis, RequestOptions options) {
        var keepAlive = TimeValue.timeValueMillis(keepAliveMillis);
        var sourceBuilder = searchRequest.source();
        var total = sourceBuilder.size();
        sourceBuilder.size(size);
        searchRequest.scroll(keepAlive);
        var scrollIds = new ArrayList<String>();
        var list = new ArrayList<SearchHit>();
        try {
            var response = client.search(searchRequest, options);
            var scrollId = response.getScrollId();
            scrollIds.add(scrollId);
            var hits = response.getHits().getHits();
            while (hits.length > 0) {
                list.addAll(Arrays.asList(hits));
                if (hits.length < size || list.size() >= total) {
                    break;
                }
                var scrollRequest = new SearchScrollRequest(scrollId);
                scrollRequest.scroll(keepAlive);
                var res = client.scroll(scrollRequest, options);
                scrollId = res.getScrollId();
                scrollIds.add(scrollId);
                hits = res.getHits().getHits();
            }
        } catch (Exception e) {
            throw new RuntimeException("查询文档发生异常：" + e.getMessage(), e);
        } finally {
            try {
                clearScroll(scrollIds, options);
            } catch (Exception e) {
                log.warn("清理滚动Id发生异常：" + e.getMessage(), e);
            }
        }
        return list;
    }

    /**
     * 清理滚动Id
     *
     * @param scrollIds scrollIds
     * @return ClearScrollResponse
     */
    public ClearScrollResponse clearScroll(List<String> scrollIds) {
        return clearScroll(scrollIds, requestOptions);
    }

    /**
     * 清理滚动Id
     *
     * @param scrollIds scrollIds
     * @param options   options
     * @return ClearScrollResponse
     */
    public ClearScrollResponse clearScroll(List<String> scrollIds, RequestOptions options) {
        var request = new ClearScrollRequest();
        request.setScrollIds(scrollIds);
        return clearScroll(request, options);
    }

    /**
     * 清理滚动信息
     *
     * @param clearScrollRequest request
     * @return ClearScrollResponse
     */
    public ClearScrollResponse clearScroll(ClearScrollRequest clearScrollRequest) {
        return clearScroll(clearScrollRequest, requestOptions);
    }

    /**
     * 清理滚动信息
     *
     * @param clearScrollRequest request
     * @param options            options
     * @return ClearScrollResponse
     */
    public ClearScrollResponse clearScroll(ClearScrollRequest clearScrollRequest, RequestOptions options) {
        try {
            return client.clearScroll(clearScrollRequest, options);
        } catch (IOException e) {
            throw new RuntimeException("清理滚动信息发生异常：" + e.getMessage(), e);
        }
    }


    /**
     * 统计
     *
     * @param queryBuilder queryBuilder
     * @param indices      indices
     * @return CountResponse
     */
    public CountResponse count(QueryBuilder queryBuilder, String... indices) {
        var request = new CountRequest(indices).query(queryBuilder);
        request.indicesOptions(IndicesOptions.lenientExpandOpen());
        return count(request);
    }

    /**
     * 统计
     *
     * @param countRequest request
     * @return CountResponse
     */
    public CountResponse count(CountRequest countRequest) {
        return count(countRequest, requestOptions);
    }

    /**
     * 统计
     *
     * @param countRequest request
     * @param options      options
     * @return CountResponse
     */
    public CountResponse count(CountRequest countRequest, RequestOptions options) {
        Assert.notNull(countRequest, "countRequest must not be null");
        try {
            return client.count(countRequest, options);
        } catch (IOException e) {
            throw new RuntimeException("查询文档发生异常：" + e.getMessage(), e);
        }
    }

}
