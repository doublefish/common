package com.tt.common;

/**
 * RetryableException
 *
 * @author Shuang Yu
 */
public class RetryableException extends RuntimeException {

    /**
     * 构造函数
     */
    public RetryableException() {
        super();
    }

    /**
     * 构造函数
     *
     * @param message message
     */
    public RetryableException(String message) {
        super(message);
    }

    /**
     * 构造函数
     *
     * @param message message
     * @param cause   cause
     */
    public RetryableException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * 构造函数
     *
     * @param cause cause
     */
    public RetryableException(Throwable cause) {
        super(cause);
    }

    /**
     * 构造函数
     *
     * @param message            message
     * @param cause              cause
     * @param enableSuppression  enableSuppression
     * @param writableStackTrace writableStackTrace
     */
    protected RetryableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
