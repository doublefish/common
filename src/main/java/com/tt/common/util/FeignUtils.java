package com.tt.common.util;

import com.tt.common.feign.DynamicClient;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.ApplicationContext;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * FeignUtils
 *
 * @author Shuang Yu
 */
public class FeignUtils {

    private static final Map<String, DynamicClient> clients;

    private FeignUtils() {
    }

    static {
        clients = new HashMap<>(0);
    }

    /**
     * getClient
     *
     * @param context context
     * @param name    服务名
     * @return DynamicClient
     */
    public static DynamicClient getClient(ApplicationContext context, String name) {
        var client = clients.get(name);
        if (client == null) {
            var builder = new FeignClientBuilder(context);
            client = builder.forType(DynamicClient.class, name).build();
            clients.put(name, client);
        }
        return client;
    }

    /**
     * 获取匹配的header中的第一个值
     *
     * @param headers headers
     * @param header  header
     * @return String
     */
    public static String getFirstHeader(Map<String, Collection<String>> headers, String header) {
        return getFirstHeader(headers, header, null);
    }

    /**
     * 获取匹配的header中的第一个值
     *
     * @param headers      headers
     * @param header       header
     * @param defaultValue defaultValue
     * @return String
     */
    public static String getFirstHeader(Map<String, Collection<String>> headers, String header, String defaultValue) {
        if (MapUtils.isEmpty(headers)) {
            return defaultValue;
        }
        var values = headers.getOrDefault(header, null);
        if (CollectionUtils.isEmpty(values)) {
            return defaultValue;
        }
        return values.stream().findFirst().orElse(defaultValue);
    }

}
