package com.tt.common.component;

import lombok.var;
import com.tt.common.util.ElasticsearchUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.UpdateByQueryRequest;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * ElasticsearchHelper
 *
 * @author Shuang.Yu
 */
public class ElasticsearchHelper extends ElasticsearchBaseHelper {

    /**
     * 构造函数
     *
     * @param context context
     */
    public ElasticsearchHelper(ApplicationContext context) {
        this(context, null);
    }

    /**
     * 构造函数
     *
     * @param context context
     * @param client  client
     */
    public ElasticsearchHelper(ApplicationContext context, RestHighLevelClient client) {
        super(context, client);
    }

    /**
     * 批量：新增/新增或删除后新增/创建
     *
     * @param index  index
     * @param id     id
     * @param object object
     * @return IndexResponse
     */
    public IndexResponse index(String index, String id, Object object) {
        var source = converter.mapObject(object).toJson();
        return index(index, id, source);
    }

    /**
     * 批量：新增/新增或删除后新增/创建
     * 说明：根据@Id主键获取主键值，存在额外开销，推荐自己转换为id,entity的Map
     *
     * @param index   index
     * @param objects objects
     * @return IndexResponse
     */
    public BulkResponse index(String index, Collection<?> objects) {
        var clazz = objects.iterator().next().getClass();
        var map = ElasticsearchUtils.toIdMap(objects, clazz);
        return index(index, map);
    }

    /**
     * 批量：新增/新增或删除后新增/创建
     *
     * @param index   index
     * @param objects objects
     * @return IndexResponse
     */
    public BulkResponse index(String index, Map<String, ?> objects) {
        var request = new BulkRequest();
        setDefaultConfig(request);
        for (var entry : objects.entrySet()) {
            var source = converter.mapObject(entry.getValue()).toJson();
            request.add(new IndexRequest(index).id(entry.getKey()).source(source, XContentType.JSON));
        }
        return bulk(request);
    }

    /**
     * 修改
     *
     * @param index    index
     * @param id       id
     * @param idOrCode idOrCode
     * @param params   params
     * @return BulkByScrollResponse
     */
    public BulkByScrollResponse updateById(String index, Object id, String idOrCode, Map<String, Object> params) {
        return updateById(index, id, idOrCode, params, false);
    }

    /**
     * 修改
     *
     * @param index    index
     * @param id       id
     * @param idOrCode idOrCode
     * @param params   params
     * @param refresh  refresh
     * @return BulkByScrollResponse
     */
    public BulkByScrollResponse updateById(String index, Object id, String idOrCode, Map<String, Object> params, boolean refresh) {
        var map = Optional.ofNullable(params).orElse(new HashMap<>());
        var script = new Script(ScriptType.INLINE, Script.DEFAULT_SCRIPT_LANG, idOrCode, map);
        return updateById(index, id, script, refresh);
    }

    /**
     * 修改
     *
     * @param index  index
     * @param id     id
     * @param script script
     * @return BulkByScrollResponse
     */
    public BulkByScrollResponse updateById(String index, Object id, Script script) {
        return updateById(index, id, script, false);
    }

    /**
     * 修改
     *
     * @param index   index
     * @param id      id
     * @param script  script
     * @param refresh refresh
     * @return BulkByScrollResponse
     */
    public BulkByScrollResponse updateById(String index, Object id, Script script, boolean refresh) {
        var query = QueryBuilders.boolQuery();
        query.must(QueryBuilders.termQuery("_id", id));
        var request = new UpdateByQueryRequest(index);
        request.setRefresh(refresh);
        request.setQuery(query);
        request.setScript(script);
        return updateByQuery(request);
    }
}
