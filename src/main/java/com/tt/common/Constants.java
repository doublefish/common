package com.tt.common;

/**
 * Constants
 *
 * @author Shuang Yu
 */
public final class Constants {

    /**
     * 最大结果数量
     */
    public static final int MAX_RESULTS = 10000;

    /**
     * 默认结果数量
     */
    public static final int DEFAULT_RESULTS = 10;

    /**
     * 日期格式
     */
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * 时间格式
     */
    public static final String TIME_FORMAT = "HH:mm:ss";

    /**
     * 时间格式
     */
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式
     */
    public static final String DATE_TIME_FORMAT_MILLS = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 时间戳格式（秒）
     */
    public static final String TIMESTAMP_FORMAT = "yyyyMMddHHmmss";

    /**
     * 时间戳格式（毫秒）
     */
    public static final String LONG_TIMESTAMP_FORMAT = "yyyyMMddHHmmssSSS";

    /**
     * 链路ID
     */
    public static final String TRACE_ID = "traceId";
    /**
     * 日志中的错误代码
     */
    public static final String LOG_ERROR_CODE = "errcode";
    /**
     * 请求ID
     */
    public static final String REQUEST_ID = "requestId";
    /**
     * 请求时间戳
     */
    public static final String REQUEST_TIMESTAMP = "timestamp";
    /**
     * 消息ID
     */
    public static final String MESSAGE_ID = "messageId";

    /**
     * 语言代码
     */
    public static class LanguageCode {
        /**
         * 中文（简体）
         */
        public static final String ZH_CN = "zh-CN";
        /**
         * 英文（美国）
         */
        public static final String EN_US = "en-US";
    }

    /**
     * 字段
     */
    public static class Field {
        /**
         * 版本号
         */
        public static final String ROW_VERSION = "rowVersion";
        /**
         * 创建时间
         */
        public static final String ROW_CREATE_DATE = "rowCreateDate";
        /**
         * 创建用户
         */
        public static final String ROW_CREATE_USER = "rowCreateUser";
        /**
         * 创建用户名
         */
        public static final String ROW_CREATE_USERNAME = "rowCreateUsername";
        /**
         * 修改时间
         */
        public static final String ROW_UPDATE_DATE = "rowUpdateDate";
        /**
         * 修改用户
         */
        public static final String ROW_UPDATE_USER = "rowUpdateUser";
        /**
         * 修改用户名
         */
        public static final String ROW_UPDATE_USERNAME = "rowUpdateUsername";
        /**
         * 删除标识
         */
        public static final String ROW_DELETED = "rowDeleted";
        /**
         * 删除时间
         */
        public static final String ROW_DELETED_DATE = "rowDeletedDate";
        /**
         * 删除用户
         */
        public static final String ROW_DELETED_USER = "rowDeletedUser";
    }

    /**
     * 列
     */
    public static class Column {
        /**
         * 版本号
         */
        public static final String ROW_VERSION = "row_version";
        /**
         * 创建时间
         */
        public static final String ROW_CREATE_DATE = "row_create_date";
        /**
         * 创建用户
         */
        public static final String ROW_CREATE_USER = "row_create_user";
        /**
         * 创建用户名
         */
        public static final String ROW_CREATE_USERNAME = "row_create_username";
        /**
         * 修改时间
         */
        public static final String ROW_UPDATE_DATE = "row_update_date";
        /**
         * 修改用户
         */
        public static final String ROW_UPDATE_USER = "row_update_user";
        /**
         * 修改用户名
         */
        public static final String ROW_UPDATE_USERNAME = "row_update_username";
        /**
         * 删除标识
         */
        public static final String ROW_DELETED = "row_deleted";
        /**
         * 删除时间
         */
        public static final String ROW_DELETED_DATE = "row_deleted_date";
        /**
         * 删除用户
         */
        public static final String ROW_DELETED_USER = "row_deleted_user";
    }

}
