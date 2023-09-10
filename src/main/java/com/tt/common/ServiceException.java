package com.tt.common;

import com.tt.common.util.ErrorCodeUtils;

/**
 * ServiceException
 *
 * @author Shuang Yu
 */
public class ServiceException extends RuntimeException {
    private int code;
    private Object data;
    private Object extraData;

    /**
     * getCode
     */
    public int getCode() {
        return this.code;
    }

    /**
     * getData
     */
    public Object getData() {
        return this.data;
    }

    /**
     * getExtraData
     */
    public Object getExtraData() {
        return this.extraData;
    }

    /**
     * 构造函数
     */
    public ServiceException() {
        super();
    }

    /**
     * 构造函数
     *
     * @param code code
     */
    public ServiceException(int code) {
        this(code, null, null, null);
    }

    /**
     * 构造函数
     *
     * @param code    code
     * @param message message
     */
    public ServiceException(int code, String message) {
        this(code, message, null);
    }

    /**
     * 构造函数
     *
     * @param code  code
     * @param cause cause
     */
    public ServiceException(int code, Throwable cause) {
        this(code, null, cause);
    }

    /**
     * 构造函数
     *
     * @param code    code
     * @param message message
     * @param data    data
     */
    public ServiceException(int code, String message, Object data) {
        this(code, message, data, null);
    }

    /**
     * 构造函数
     *
     * @param code    code
     * @param message message
     * @param data    data
     */
    public ServiceException(int code, String message, Object data, Object extraData) {
        this(code, message, data, extraData, null);
    }

    /**
     * 构造函数
     *
     * @param code    code
     * @param message message
     * @param cause   cause
     */
    public ServiceException(int code, String message, Throwable cause) {
        this(code, message, null, null, cause);
    }

    /**
     * 构造函数
     *
     * @param code      code
     * @param message   message
     * @param data      data
     * @param extraData extraData
     * @param cause     cause
     */
    public ServiceException(int code, String message, Object data, Object extraData, Throwable cause) {
        this(code, message, data, extraData, cause, false, false);
    }

    /**
     * 构造函数
     *
     * @param code               code
     * @param message            message
     * @param data               data
     * @param extraData          extraData
     * @param cause              cause
     * @param enableSuppression  enableSuppression
     * @param writableStackTrace writableStackTrace
     */
    protected ServiceException(int code, String message, Object data, Object extraData, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message != null ? message : ErrorCodeUtils.getMessage(code), cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.data = data;
        this.extraData = extraData;
    }
}
