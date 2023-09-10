package com.tt.common.amqp;

import lombok.var;

/**
 * 处理结果类型
 *
 * @author Shuang Yu
 */
public enum ResultType {
    /**
     * 准备：0
     */
    READY(0),
    /**
     * 成功：1
     */
    SUCCESS(1),
    /**
     * 失败（把消息从队列中移除）：2
     */
    FAIL(2),
    /**
     * 重试（把消息追加到队尾）：3
     */
    RETRY(3),
    /**
     * 异常：9
     */
    EXCEPTION(9);

    private final int value;

    ResultType(int value) {
        this.value = value;
    }

    /**
     * valueOf
     *
     * @param value the value of the ResultType
     * @return the corresponding ResultType enum
     * @throws IllegalArgumentException if value is not a valid ResultType
     */
    public static ResultType valueOf(int value) {
        for (var v : values()) {
            if (v.value == value) {
                return v;
            }
        }
        throw new IllegalArgumentException("Illegal ResultType value: " + value);
    }

    /**
     * Returns value of this ResultType
     *
     * @return value of this ResultType
     */
    public int value() {
        return this.value;
    }
}
