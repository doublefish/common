package com.tt.common.util;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * FunctionUtils
 *
 * @author Shuang Yu
 */
@Slf4j
public class FunctionUtils {

    private FunctionUtils() {
    }

    /**
     * 获取结果（可重试）
     *
     * @param <V>        V
     * @param callable   callable
     * @param retryTimes 重试次数
     * @param waitMills  等待时间（毫秒）
     */
    public static <V> V getWithRetry(Callable<V> callable, int retryTimes, long waitMills) {
        var i = 0;
        while (true) {
            try {
                var res = callable.call();
                if (res == null) {
                    if (i < retryTimes) {
                        i++;
                        sleep(waitMills, i);
                        continue;
                    }
                }
                return res;
            } catch (Exception e) {
                throw new RuntimeException("执行方法发生异常：" + e.getMessage(), e);
            }
        }
    }

    /**
     * 获取结果（可重试）
     *
     * @param <V>        V
     * @param callable   callable
     * @param retryTimes 重试次数
     * @param waitMills  等待时间（毫秒）
     */
    public static <V> List<V> getListWithRetry(Callable<List<V>> callable, int retryTimes, long waitMills) {
        var i = 0;
        while (true) {
            try {
                var res = callable.call();
                if (CollectionUtils.isEmpty(res)) {
                    if (i < retryTimes) {
                        i++;
                        sleep(waitMills, i);
                        continue;
                    }
                }
                return res;
            } catch (Exception e) {
                throw new RuntimeException("执行方法发生异常：" + e.getMessage(), e);
            }
        }
    }

    private static void sleep(long mills, int i) {
        log.info("没有结果：等待{}ms后，开始第{}次重试", mills, i);
        try {
            TimeUnit.MILLISECONDS.sleep(mills);
        } catch (InterruptedException e) {
            log.error("线程休眠发生异常：" + e, e);
        }
    }
}
