package com.tt.common;

/**
 * FriendlyException
 *
 * @author Shuang Yu
 */
public class FriendlyException extends RuntimeException {

    /**
     * 异常原因
     */
    private String reason;
    /**
     * 解决办法
     */
    private String solution;

    /**
     * getReason
     */
    public String getReason() {
        return this.reason;
    }

    /**
     * getSolution
     */
    public String getSolution() {
        return this.solution;
    }

    /**
     * 构造函数
     */
    public FriendlyException() {
        super();
    }

    /**
     * 构造函数
     *
     * @param message  message
     * @param reason   reason
     * @param solution solution
     */
    public FriendlyException(String message, String reason, String solution) {
        this(message, reason, solution, null);
    }

    /**
     * 构造函数
     *
     * @param message  message
     * @param reason   reason
     * @param solution solution
     * @param cause    cause
     */
    public FriendlyException(String message, String reason, String solution, Throwable cause) {
        super(message, cause);
        this.reason = reason;
        this.solution = solution;
    }
}
