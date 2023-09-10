package com.tt.common.enums;

import lombok.var;

/**
 * ApplyResult
 *
 * @author Shuang Yu
 */
public enum ApplyResult {
    /**
     * 成功
     */
    NONE(0),
    /**
     * 成功
     */
    SUCCEED(1),
    /**
     * 超时
     */
    TIMEOUT(2),
    /**
     * 异常
     */
    EXCEPTION(3);

    private final int value;

    ApplyResult(int value) {
        this.value = value;
    }

    /**
     * valueOf
     *
     * @param value the value of the ApplyResult
     * @return the corresponding ApplyResult enum
     * @throws IllegalArgumentException if value is not a valid ApplyResult
     */
    public static ApplyResult valueOf(int value) {
        for (var v : values()) {
            if (v.value == value) {
                return v;
            }
        }
        throw new IllegalArgumentException("Illegal ApplyResult value: " + value);
    }

    /**
     * Returns value of this ApplyResult
     *
     * @return value of this ApplyResult
     */
    public int value() {
        return this.value;
    }
}
