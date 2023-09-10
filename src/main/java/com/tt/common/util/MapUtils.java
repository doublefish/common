package com.tt.common.util;

import lombok.var;

import java.util.HashMap;
import java.util.Map;

/**
 * MapExtension
 *
 * @author Shuang Yu
 */
public class MapUtils {

    private MapUtils() {
    }

    /**
     * 转为 Integer,V Map
     *
     * @param map map
     * @return Integer,V Map
     */
    public static <V> Map<Integer, V> convertKeyToInteger(Map<String, V> map) {
        if (map == null) {
            return null;
        }

        var newMap = new HashMap<Integer, V>(0);
        map.forEach((k, v) -> newMap.put(Integer.valueOf(k), v));

        return newMap;
    }

}
