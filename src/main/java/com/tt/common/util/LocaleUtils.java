package com.tt.common.util;

import lombok.var;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Locale;

/**
 * LocaleUtil
 *
 * @author Shuang Yu
 */
public class LocaleUtils {

    private LocaleUtils() {
    }

    /**
     * 设置语言环境 - 根据请求头
     *
     * @return 设置结果
     */
    public static boolean setLocale() {
        var language = getAcceptLanguage();
        if (language != null && language.length() > 0) {
            language = language.split(",")[0];
            var array = language.split("-");

            var builder = new Locale.Builder();
            builder.setLanguage(array[0]);
            builder.setRegion(array[1]);
            builder.setLanguageTag(language);
            Locale locale = builder.build();

            Locale.setDefault(locale);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 设置语言环境 - 根据请求头
     *
     * @return 设置结果
     */
    public static String getLang() {
        var language = getAcceptLanguage();
        if (language != null && language.length() > 0) {
            language = language.split(",")[0];
        }
        return language;
    }

    /**
     * 获取请求头 - 接受语言
     */
    public static String getAcceptLanguage() {
        return get("Accept-Language");
    }

    /**
     * 获取请求头
     */
    public static String get(String key) {
        var requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        var request = requestAttributes.getRequest();
        return request.getHeader(key);
    }
}