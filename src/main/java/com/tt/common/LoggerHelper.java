package com.tt.common;

import lombok.var;
import org.slf4j.Logger;
import org.slf4j.MDC;

/**
 * ExceptionHelper
 *
 * @author Shuang Yu
 */
public class LoggerHelper {

    /**
     * 打印错误码的日志
     *
     * @param logger logger
     * @param t      t
     */
    public static void error(Logger logger, Throwable t) {
        error(logger, "发生异常：", t);
    }

    /**
     * 打印错误码的日志
     *
     * @param logger logger
     * @param prefix prefix
     * @param t      t
     */
    public static void error(Logger logger, String prefix, Throwable t) {
        var code = "500";
        var msg = t.getMessage();
        if (t instanceof ServiceException) {
            var se = (ServiceException) t;
            msg = String.format("[%s]", se.getCode()) + msg;
            code = String.valueOf(se.getCode());
        }
        MDC.put(Constants.LOG_ERROR_CODE, code);
        logger.error(prefix + msg, t);
        MDC.remove(Constants.LOG_ERROR_CODE);
    }
}
