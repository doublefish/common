package com.tt.common;


import lombok.var;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求参数传递辅助类
 *
 * @author Shuang Yu
 */
public class RequestDataHelper {

    public RequestDataHelper() {
    }

    /**
     * 请求参数存取
     */
    private static final ThreadLocal<Map<String, Object>> REQUEST_DATA = new ThreadLocal<>();

    /**
     * getData
     *
     * @return Map<String, Object>
     */
    public static Map<String, Object> getData() {
        return REQUEST_DATA.get();
    }

    /**
     * setData
     *
     * @param data data
     */
    public static void setData(Map<String, Object> data) {
        REQUEST_DATA.set(data);
    }

    /**
     * 设置请求参数
     *
     * @param key   请求参数名
     * @param value 请求参数值
     */
    public static void setValue(String key, Object value) {
        var map = REQUEST_DATA.get();
        if (map == null) {
            map = new HashMap<>(0);
            REQUEST_DATA.set(map);
        }
        map.put(key, value);
    }

    /**
     * 获取请求参数
     *
     * @param key 请求参数名
     * @return 请求参数值
     */
    public static <T> T getValue(String key) {
        var map = REQUEST_DATA.get();
        if (map != null && map.containsKey(key)) {
            var obj = map.get(key);
            if (obj != null) {
                @SuppressWarnings("unchecked")
                var res = (T) obj;
                return res;
            }
        }
        return null;
    }

    /**
     * 移除请求参数值
     *
     * @param key 请求参数名
     */
    public static void remove(String key) {
        var map = REQUEST_DATA.get();
        if (map != null) {
            map.remove(key);
            //REQUEST_DATA.set(map);
        }
    }


    /**
     * 移除请求参数
     */
    public static void remove() {
        REQUEST_DATA.remove();
    }

    /**
     * setTableSuffix
     *
     * @param tableSuffix tableSuffix
     */
    public static void setTableSuffix(String tableSuffix) {
        setValue("tableSuffix", tableSuffix);
    }

    /**
     * setTableSuffix
     *
     * @return tableSuffix
     */
    public static String getTableSuffix() {
        return getValue("tableSuffix");
    }

    /**
     * removeTableSuffix
     */
    public static void removeTableSuffix() {
        remove("tableSuffix");
    }

    /**
     * setReferer
     *
     * @param referer referer
     */
    public static void setReferer(String referer) {
        setValue("referer", referer);
    }

    /**
     * getReferer
     *
     * @return referer
     */
    public static String getReferer() {
        return getValue("referer");
    }

    /**
     * removeReferer
     */
    public static void removeReferer() {
        remove("referer");
    }

    /**
     * setUser
     *
     * @param user user
     */
    public static void setUser(User user) {
        setValue("user", user);
    }

    /**
     * getUser
     *
     * @return user
     */
    public static User getUser() {
        return getValue("user");
    }

    /**
     * removeUser
     */
    public static void removeUser() {
        remove("user");
    }


    /**
     * setLanguage
     *
     * @param language language
     */
    public static void setLanguage(String language) {
        setValue("language", language);
    }

    /**
     * getLanguage
     *
     * @return language
     */
    public static String getLanguage() {
        return getValue("language");
    }

    /**
     * removeLanguage
     */
    public static void removeLanguage() {
        remove("language");
    }


    /**
     * setEnterpriseNo
     *
     * @param enterpriseNo enterpriseNo
     */
    public static void setEnterpriseNo(String enterpriseNo) {
        setValue("enterpriseNo", enterpriseNo);
    }

    /**
     * getEnterpriseNo
     *
     * @return enterpriseNo
     */
    public static String getEnterpriseNo() {
        return getValue("enterpriseNo");
    }

    /**
     * removeEnterpriseNo
     */
    public static void removeEnterpriseNo() {
        remove("enterpriseNo");
    }

}
