package com.tt.common.util;

import lombok.var;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * IdUtils
 *
 * @author Shuang Yu
 */
public class IdUtils {

    private IdUtils() {
    }

    /**
     * getId
     *
     * @param <T>           T
     * @param redisTemplate redisTemplate
     * @param key           key
     * @param hashKey       hashKey
     * @param delta         delta
     * @param getMaxId      getMaxId
     * @param t             t
     * @return startId
     */
    public static <T> long getId(StringRedisTemplate redisTemplate, String key, String hashKey, long delta, Function<T, Long> getMaxId, T t) {
        if (!redisTemplate.opsForHash().hasKey(key, hashKey)) {
            var maxId = getMaxId.apply(t);
            redisTemplate.opsForHash().put(key, hashKey, maxId == null ? "0" : maxId.toString());
        }
        var value = redisTemplate.opsForHash().increment(key, hashKey, delta);
        return value - delta + 1L;
    }

    /**
     * getId
     *
     * @param redisTemplate redisTemplate
     * @param key           key
     * @param hashKey       hashKey
     * @param delta         delta
     * @param getMaxId      getMaxId
     * @return startId
     */
    public static long getId(StringRedisTemplate redisTemplate, String key, String hashKey, long delta, Supplier<Long> getMaxId) {
        if (!redisTemplate.opsForHash().hasKey(key, hashKey)) {
            var maxId = getMaxId.get();
            redisTemplate.opsForHash().put(key, hashKey, maxId == null ? "0" : maxId.toString());
        }
        var value = redisTemplate.opsForHash().increment(key, hashKey, delta);
        return value - delta + 1L;
    }
}
