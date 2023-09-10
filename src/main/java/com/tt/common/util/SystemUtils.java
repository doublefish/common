package com.tt.common.util;


/**
 * SystemUtils
 *
 * @author Shuang Yu
 */
public class SystemUtils {

    private SystemUtils() {
    }

    /**
     * isLinux
     *
     * @return boolean
     */
    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().contains("linux");
    }
}
