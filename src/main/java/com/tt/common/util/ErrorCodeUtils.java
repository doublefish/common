package com.tt.common.util;

import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * ErrorCodeUtils
 *
 * @author Shuang Yu
 */
@Slf4j
public class ErrorCodeUtils {

    private static final Map<Integer, String> RESOURCES = new HashMap<>();

    private ErrorCodeUtils() {
    }

    private static void init() {
        try {
            var contents = FileUtils.getResourcesFileLines("error-code.properties");
            if (CollectionUtils.isNotEmpty(contents)) {
                for (var line : contents) {
                    if (StringUtils.isEmpty(line) || line.startsWith("#")) {
                        continue;
                    }
                    var array = line.split("=");
                    if (array.length < 2 || StringUtils.isEmpty(array[0])) {
                        continue;
                    }
                    try {
                        RESOURCES.put(Integer.valueOf(array[0].trim()), array[1].trim());
                    } catch (Exception e) {
                        log.warn("转换错误代码发生异常：[{" + line + "}]" + e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("初始化发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * 获取所有
     *
     * @return map
     */
    public static Map<Integer, String> getResources() {
        if (MapUtils.isEmpty(RESOURCES)) {
            init();
        }
        return RESOURCES;
    }

    /**
     * 获取消息
     *
     * @param code code
     * @return String
     */
    public static String getMessage(int code) {
        return getResources().getOrDefault(code, "");
    }

}
