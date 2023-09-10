package com.tt.common.feign;

import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * DynamicClient
 *
 * @author Shuang Yu
 */
public interface DynamicClient {

    /**
     * 发送GET请求
     *
     * @param path  path
     * @param query query
     * @return String
     */
    @RequestMapping(value = "{path}", method = RequestMethod.GET)
    String get(@PathVariable("path") String path, @SpringQueryMap Map<String, Object> query);

    /**
     * 发送POST请求
     *
     * @param path path
     * @param body body
     * @return String
     */
    @RequestMapping(value = "{path}", method = RequestMethod.POST)
    String post(@PathVariable("path") String path, @RequestBody Object body);

    /**
     * 发送PUT请求
     *
     * @param path path
     * @param body body
     * @return String
     */
    @RequestMapping(value = "{path}", method = RequestMethod.PUT)
    String put(@PathVariable("path") String path, @RequestBody Object body);

    /**
     * 发送DELETE请求
     *
     * @param path  path
     * @param query query
     * @return String
     */
    @RequestMapping(value = "{path}", method = RequestMethod.DELETE)
    String delete(@PathVariable("path") String path, @SpringQueryMap Map<String, Object> query);

    /**
     * 发送DELETE请求
     *
     * @param path path
     * @param body body
     * @return String
     */
    @RequestMapping(value = "{path}", method = RequestMethod.DELETE)
    String delete(@PathVariable("path") String path, @RequestBody Object body);

}
