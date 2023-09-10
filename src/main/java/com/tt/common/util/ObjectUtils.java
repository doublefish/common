package com.tt.common.util;

import lombok.var;

import java.util.*;
import java.util.function.Function;

/**
 * ObjectUtils
 *
 * @author Shuang Yu
 */
public class ObjectUtils {

    private ObjectUtils() {
    }

    /**
     * group
     *
     * @param <T>  T
     * @param <K>  K
     * @param list list
     * @return Map
     */
    public static <T, K, U> Map<K, List<T>> group(Collection<T> list, Function<? super T, ? extends K> keyMapper) {
        var map = new HashMap<K, List<T>>(0);
        for (var p : list) {
            var key = keyMapper.apply(p);
            var temps = map.computeIfAbsent(key, k -> new ArrayList<>());
            temps.add(p);
        }
        return map;
    }
}
