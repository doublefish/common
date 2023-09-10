package com.tt.common.component;

import com.tt.common.util.IdUtils;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * IdFactory
 *
 * @author Shuang.Yu
 */
public class IdFactoryBase {

    protected final StringRedisTemplate redisTemplate;

    /**
     * 构造函数
     */
    public IdFactoryBase(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * getId
     *
     * @param <T>      T
     * @param key      key
     * @param hashKey  hashKey
     * @param delta    delta
     * @param getMaxId getMaxId
     * @param t        t
     * @return startId
     */
    public <T> long getId(String key, String hashKey, long delta, Function<T, Long> getMaxId, T t) {
        return IdUtils.getId(redisTemplate, key, hashKey, delta, getMaxId, t);
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
    public long getId(StringRedisTemplate redisTemplate, String key, String hashKey, long delta, Supplier<Long> getMaxId) {
        return IdUtils.getId(redisTemplate, key, hashKey, delta, getMaxId);
    }
}
