package com.tt.common.util;

import com.tt.common.enums.ApplyResult;
import com.tt.common.model.Tuple2;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * RedisUtils
 *
 * @author Shuang Yu
 */
public class RedisUtils {

    private RedisUtils() {
    }

    /**
     * 带锁执行
     * <T> – the type of the input to the function <R> – the type of the result of the function
     *
     * @param <K>        the Redis key type against which the template works (usually a String)
     * @param <V>        the Redis value type against which the template works
     * @param <T>        the type of the input to the function
     * @param <R>        the type of the result of the function
     * @param operations operations
     * @param key        锁的key
     * @param value      锁的值
     * @param timeout    超时时间
     * @param function   需要执行的方法
     * @param input      需要执行的方法的参数
     * @param retryTimes 重试次数
     * @return Tuple2<锁的状态, R>
     */
    public static <K, V, T, R> Tuple2<ApplyResult, R> executeWithLock(RedisOperations<K, V> operations, K key, V value, Duration timeout, Function<T, R> function, T input, int retryTimes) {
        try {
            var i = 0;
            var result = ApplyResult.NONE;
            while (true) {
                var res = operations.opsForValue().setIfAbsent(key, value, timeout);
                if (Boolean.FALSE.equals(res)) {
                    if (i < retryTimes) {
                        TimeUnit.MILLISECONDS.sleep(100);
                        i++;
                        continue;
                    }
                    result = ApplyResult.TIMEOUT;
                } else {
                    result = ApplyResult.SUCCEED;
                }
                break;
            }
            R res = null;
            if (result == ApplyResult.SUCCEED) {
                res = function.apply(input);
            }
            return new Tuple2<>(result, res);
        } catch (InterruptedException e) {
            return new Tuple2<>(ApplyResult.EXCEPTION, null);
        } finally {
            operations.delete(key);
        }
    }


    /**
     * hExists
     *
     * @param <K>        K
     * @param <V>        V
     * @param <HK>       HK
     * @param operations operations
     * @param key        key
     * @param hashKeys   hashKeys
     * @param size       size
     */

    public static <K, V, HK> Map<HK, Boolean> hExists(RedisOperations<K, V> operations, K key, Collection<HK> hashKeys, int size) {
        var results = new HashMap<HK, Boolean>();
        var skip = 0;
        var keySerializer = getKeySerializer(operations);
        var hashKeySerializer = getHashKeySerializer(operations);
        var keyBytes = keySerializer.serialize(key);
        assert keyBytes != null;
        var iterator = hashKeys.iterator();
        while (true) {
            var hks = hashKeys.stream().skip(skip).limit(size).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(hks)) {
                break;
            }

            var list = operations.executePipelined((RedisCallback<List<Boolean>>) connection -> {
                for (var hk : hks) {
                    var hkBytes = hashKeySerializer.serialize(hk);
                    assert hkBytes != null;
                    connection.hExists(keyBytes, hkBytes);
                }
                return null;
            });

            for (var item : list) {
                var res = (Boolean) item;
                var hk = iterator.next();
                results.put(hk, res);
            }
            skip += size;
        }
        return results;
    }

    /**
     * hSet
     *
     * @param <K>        K
     * @param <V>        V
     * @param <HK>       HK
     * @param <HV>       HV
     * @param operations operations
     * @param key        key
     * @param hash       hash
     * @param size       size
     */
    public static <K, V, HK, HV> Map<HK, Boolean> hSet(RedisOperations<K, V> operations, K key, List<Tuple2<HK, HV>> hash, int size) {
        var results = new HashMap<HK, Boolean>();
        var skip = 0;
        var keySerializer = getKeySerializer(operations);
        var hashKeySerializer = getHashKeySerializer(operations);
        var hashValueSerializer = getHashValueSerializer(operations);
        var keyBytes = keySerializer.serialize(key);
        assert keyBytes != null;
        var iterator = hash.iterator();
        while (true) {
            var temp = hash.stream().skip(skip).limit(size).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(temp)) {
                break;
            }

            var list = operations.executePipelined((RedisCallback<List<Boolean>>) connection -> {
                for (var t : temp) {
                    var hkBytes = hashKeySerializer.serialize(t.getT1());
                    assert hkBytes != null;
                    var hvBytes = hashValueSerializer.serialize(t.getT2());
                    assert hvBytes != null;
                    connection.hSet(keyBytes, hkBytes, hvBytes);
                }
                return null;
            });

            for (var item : list) {
                var res = (Boolean) item;
                var hk = iterator.next();
                results.put(hk.getT1(), res);
            }
            skip += size;
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    public static <K, V> RedisSerializer<K> getKeySerializer(RedisOperations<K, V> operations) {
        return (RedisSerializer<K>) operations.getKeySerializer();
    }

    @SuppressWarnings("unchecked")
    public static <K, V> RedisSerializer<V> getValueSerializer(RedisOperations<K, V> operations) {
        return (RedisSerializer<V>) operations.getValueSerializer();
    }

    @SuppressWarnings("unchecked")
    public static <K, V, HK> RedisSerializer<HK> getHashKeySerializer(RedisOperations<K, V> operations) {
        return (RedisSerializer<HK>) operations.getHashKeySerializer();
    }

    @SuppressWarnings("unchecked")
    public static <K, V, HV> RedisSerializer<HV> getHashValueSerializer(RedisOperations<K, V> operations) {
        return (RedisSerializer<HV>) operations.getHashValueSerializer();
    }
}