package com.tt.common.util;


import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * ValidateUtils
 *
 * @author Shuang Yu
 */
public class ValidateUtils {

    private ValidateUtils() {
    }

    /**
     * Regex
     */
    public static class Regex {
        /**
         * 数字和字母
         */
        public static final String ONLY_NUMBERS_LETTERS = "^[A-Za-z\\d]+$";

        /**
         * 任意字符（不含中文和空格）
         */
        public static final String ANY_CHARACTER = "[^\\u4e00-\\u9fa5\\s]+$";

        /**
         * 移动电话号码：1380013800、+86-1380013800
         */
        public static final String CELLPHONE = "^(\\+\\d{2}-)?(1[3-9]\\d\\d{8})$";

        /**
         * 固定电话号码
         */
        public static final String TELEPHONE = "^((\\+\\d{2,3}-)?0\\d{2,3}-\\d{7,8}(-\\d{1,4})?)$";

        /**
         * 电子邮箱地址（名称：数字、字母、中文、下划线，域名：数字、字母、下划线）
         */
        public static final String EMAIL = "^[A-Za-z\\d\\u4e00-\\u9fa5]+@[a-zA-Z\\d_-]+(\\.[a-zA-Z\\d_-]+)+$";
    }

    /**
     * 是否只有数字和字母
     *
     * @param input input
     */
    public static boolean isOnlyNumbersAndLetters(String input) {
        return Pattern.matches(Regex.ONLY_NUMBERS_LETTERS, input);
    }

    /**
     * 是否移动电话号码
     *
     * @param input input
     */
    public static boolean isCellphone(String input) {
        return Pattern.matches(Regex.CELLPHONE, input);
    }

    /**
     * 是否固定电话号码
     *
     * @param input input
     */
    public static boolean isTelephone(String input) {
        return Pattern.matches(Regex.TELEPHONE, input);
    }

    /**
     * 是否电子邮箱地址
     *
     * @param input input
     */
    public static boolean isEmail(String input) {
        return Pattern.matches(Regex.EMAIL, input);
    }

    /**
     * 是否有效的编码：任意字符（不含中文和空格）,4-30位
     *
     * @param input input
     */
    public static boolean isValidCode(String input) {
        return isValidCode(input, 4, 30);
    }

    /**
     * 是否有效的编码：任意字符（不含中文和空格）
     *
     * @param input     input
     * @param minLength minLength
     * @param maxLength maxLength
     */
    public static boolean isValidCode(String input, int minLength, int maxLength) {
        return StringUtils.isNotEmpty(input) && input.length() >= minLength && input.length() <= maxLength && Pattern.matches(Regex.ANY_CHARACTER, input);
    }

    /**
     * 是否有效的名称（2-30位）
     *
     * @param input input
     */
    public static boolean isValidName(String input) {
        return isValidName(input, 2, 30);
    }

    /**
     * 是否有效的名称
     *
     * @param input input
     */
    public static boolean isValidName(String input, int minLength, int maxLength) {
        return StringUtils.isNotEmpty(input) && input.length() >= minLength && input.length() <= maxLength;
    }
}
