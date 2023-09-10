package com.tt.common.feign;

import com.fasterxml.jackson.core.type.TypeReference;
import com.tt.common.util.FeignUtils;
import com.tt.common.util.JSONUtils;
import lombok.var;
import org.springframework.context.ApplicationContext;

import java.util.Map;

/**
 * BaseDynamicClient
 *
 * @author Shuang Yu
 */
public class BaseDynamicClient {

    public final String name;
    protected final DynamicClient client;

    /**
     * 构造函数
     *
     * @param context context
     * @param name    服务名
     */
    public BaseDynamicClient(ApplicationContext context, String name) {
        this.name = name;
        this.client = FeignUtils.getClient(context, name);
    }

    /**
     * 发送GET请求
     *
     * @param type  返回值类型
     * @param path  请求路径
     * @param query 请求参数
     */
    public <T> T get(Class<T> type, String path, Map<String, Object> query) {
        var res = this.client.get(path, query);
        return JSONUtils.parse(res, type);
    }

    /**
     * 发送GET请求
     *
     * @param type  返回值类型
     * @param path  请求路径
     * @param query 请求参数
     */
    public <T> T get(TypeReference<T> type, String path, Map<String, Object> query) {
        var res = this.client.get(path, query);
        return JSONUtils.parse(res, type);
    }

    /**
     * 发送POST请求
     *
     * @param type 返回值类型
     * @param path 请求路径
     * @param body 请求内容
     */
    public <T> T post(Class<T> type, String path, Object body) {
        var res = this.client.post(path, body);
        return JSONUtils.parse(res, type);
    }

    /**
     * 发送POST请求
     *
     * @param type 返回值类型
     * @param path 请求路径
     * @param body 请求内容
     */
    public <T> T post(TypeReference<T> type, String path, Object body) {
        var res = this.client.post(path, body);
        return JSONUtils.parse(res, type);
    }

    /**
     * 发送PUT请求
     *
     * @param type 返回值类型
     * @param path 请求路径
     * @param body 请求内容
     */
    public <T> T put(Class<T> type, String path, Object body) {
        var res = this.client.put(path, body);
        return JSONUtils.parse(res, type);
    }

    /**
     * 发送PUT请求
     *
     * @param type 返回值类型
     * @param path 请求路径
     * @param body 请求内容
     */
    public <T> T put(TypeReference<T> type, String path, Object body) {
        var res = this.client.put(path, body);
        return JSONUtils.parse(res, type);
    }

    /**
     * 发送DELETE请求
     *
     * @param type  返回值类型
     * @param path  请求路径
     * @param query 请求参数
     */
    public <T> T delete(Class<T> type, String path, Map<String, Object> query) {
        var res = this.client.delete(path, query);
        return JSONUtils.parse(res, type);
    }

    /**
     * 发送DELETE请求
     *
     * @param type  返回值类型
     * @param path  请求路径
     * @param query 请求参数
     */
    public <T> T delete(TypeReference<T> type, String path, Map<String, Object> query) {
        var res = this.client.delete(path, query);
        return JSONUtils.parse(res, type);
    }

    /**
     * 发送DELETE请求
     *
     * @param type 返回值类型
     * @param path 请求路径
     * @param body 请求内容
     */
    public <T> T delete(Class<T> type, String path, Object body) {
        var res = this.client.delete(path, body);
        return JSONUtils.parse(res, type);
    }

    /**
     * 发送DELETE请求
     *
     * @param type 返回值类型
     * @param path 请求路径
     * @param body 请求内容
     */
    public <T> T delete(TypeReference<T> type, String path, Object body) {
        var res = this.client.delete(path, body);
        return JSONUtils.parse(res, type);
    }

}
