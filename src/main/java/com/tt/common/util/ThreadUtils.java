package com.tt.common.util;

import com.alibaba.ttl.TtlCallable;
import com.tt.common.RequestDataHelper;
import lombok.var;
import org.apache.skywalking.apm.toolkit.trace.CallableWrapper;
import org.apache.skywalking.apm.toolkit.trace.RunnableWrapper;
import org.slf4j.MDC;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * ThreadUtils
 * 多线程工具类：主线程的线程变量RequestDataHelper.getData()和链路Id自动带入子线程
 *
 * @author Shuang Yu
 */
public class ThreadUtils {

    private static final long keepAliveTime = 3000;
    private static final int workQueue = 64;

    private ThreadUtils() {
    }

    /**
     * createExecutor
     *
     * @return ExecutorService
     */
    public static ExecutorService createExecutor() {
        var availableProcessors = Runtime.getRuntime().availableProcessors();
        return createExecutor(availableProcessors, availableProcessors, keepAliveTime, TimeUnit.MILLISECONDS);
    }

    /**
     * createExecutor
     *
     * @param corePoolSize    corePoolSize
     * @param maximumPoolSize maximumPoolSize
     * @param keepAliveTime   keepAliveTime
     * @param unit            unit
     * @return ExecutorService
     */
    public static ExecutorService createExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit) {
        return createExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    /**
     * createExecutor
     *
     * @param corePoolSize    corePoolSize
     * @param maximumPoolSize maximumPoolSize
     * @param keepAliveTime   keepAliveTime
     * @param unit            unit
     * @param workQueue       workQueue
     * @return ExecutorService
     */
    public static ExecutorService createExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, int workQueue) {
        var queue = new ArrayBlockingQueue<Runnable>(workQueue);
        var policy = new ThreadPoolExecutor.AbortPolicy();
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit, queue, policy);
    }

    /**
     * 等待任务执行结束后关闭线程池
     *
     * @param executor executor
     */
    public static boolean shutdown(ExecutorService executor) {
        // 所有任务执行完成且等待队列中也无任务关闭线程池
        executor.shutdown();
        try {
            // 阻塞主线程, 直至线程池关闭
            return executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException("等待任务执行结束发生异常" + e.getMessage(), e);
        }
    }

    /**
     * submit
     *
     * @param <T>     T
     * @param tasks   tasks
     * @param timeout 单个任务的超时时间
     * @return List<T>
     */
    public static <T> List<T> submit(Collection<Callable<T>> tasks, long timeout) {
        return submit(tasks, timeout, true);
    }

    /**
     * submit
     *
     * @param <T>      T
     * @param tasks    tasks
     * @param timeout  单个任务的超时时间
     * @param shutdown shutdown
     * @return List<T>
     */
    public static <T> List<T> submit(Collection<Callable<T>> tasks, long timeout, boolean shutdown) {
        var executor = createExecutor();
        var ecs = new ExecutorCompletionService<T>(executor);
        return submit(executor, ecs, tasks, workQueue, timeout, shutdown);
    }

    /**
     * submit
     *
     * @param <T>               T
     * @param executor          executor
     * @param completionService completionService
     * @param tasks             tasks
     * @param workQueue         workQueue
     * @param timeout           单个任务的超时时间
     * @param shutdown          shutdown
     * @return List<T>
     */
    public static <T> List<T> submit(ExecutorService executor, ExecutorCompletionService<T> completionService, Collection<Callable<T>> tasks, int workQueue, long timeout, boolean shutdown) {
        var total = tasks.size();
        var index = 0;
        var res = new ArrayList<T>();
        for (var task : tasks) {
            index++;
            completionService.submit(task);
            var remainder = index % workQueue;
            if (remainder == 0 || index == total) {
                var size = remainder == 0 ? workQueue : remainder;
                var r = getResults(completionService, size, timeout);
                res.addAll(r);
            }
        }
        if (shutdown) {
            executor.shutdown();
        }
        return res;
    }

    /**
     * createTask
     *
     * @param <T>   T
     * @param tasks tasks
     * @return CallableWrapper<T>
     */
    public static <T> List<CallableWrapper<T>> createTask(Collection<Callable<T>> tasks) {
        var requestData = RequestDataHelper.getData();
        var contextMap = MDC.getCopyOfContextMap();
        var res = new ArrayList<CallableWrapper<T>>();
        for (var t : tasks) {
            res.add(CallableWrapper.of(() -> {
                RequestDataHelper.setData(requestData);
                MDC.setContextMap(contextMap);
                return t.call();
            }));
        }
        return res;
    }

    /**
     * createTask
     *
     * @param <T>  T
     * @param task task
     * @return CallableWrapper<T>
     */
    public static <T> Callable<T> createTask(Callable<T> task) {
        var requestData = RequestDataHelper.getData();
        var mdcContext = MDC.getCopyOfContextMap();

        return TtlCallable.get(CallableWrapper.of(() -> {
            RequestDataHelper.remove();
            MDC.clear();
            if (requestData != null) {
                RequestDataHelper.setData(new HashMap<>(requestData));
            }

            if (mdcContext != null) {
                MDC.setContextMap(mdcContext);
            }

            try {
                return task.call();
            } finally {
                MDC.clear();
                RequestDataHelper.remove();
            }
        }));
    }

    /**
     * getResults
     *
     * @param <T>               T
     * @param completionService completionService
     * @param size              单个任务的大小
     * @param timeout           单个任务的超时时间
     * @return List<T>
     */
    public static <T> List<T> getResults(ExecutorCompletionService<T> completionService, int size, long timeout) {
        var res = new ArrayList<T>();
        try {
            for (var i = 0; i < size; i++) {
                var r = completionService.take().get(timeout, TimeUnit.MILLISECONDS);
                res.add(r);
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("获取任务执行结果发生异常：" + e.getMessage(), e);
        } catch (TimeoutException e) {
            throw new RuntimeException("获取任务执行结果超时：" + e.getMessage(), e);
        }
        return res;
    }
}
