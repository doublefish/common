package com.tt.common.component;

import lombok.extern.slf4j.Slf4j;

/**
 * EmptyServiceImpl
 *
 * @author Shuang Yu
 */
@Slf4j
public class EmptyServiceImpl {

    /**
     * 构造函数
     */
    public EmptyServiceImpl() {
    }

    /**
     * 执行之前
     */
    protected void beforeExecute() {
        // 子类根据需要重写此方法
    }

    /**
     * 执行之后
     */
    protected void afterExecuted() {
        // 子类根据需要重写此方法
    }

    /**
     * 抛出异常之前
     *
     * @param throwable throwable
     */
    protected void beforeThrow(Throwable throwable) {
        log.error("发生异常：" + throwable.getMessage(), throwable);
    }

}
