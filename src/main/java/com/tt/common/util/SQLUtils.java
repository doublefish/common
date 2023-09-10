package com.tt.common.util;

import lombok.var;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * SQLUtils
 *
 * @author Shuang Yu
 */
public class SQLUtils {

    private SQLUtils() {
    }

    public static final char[] SCRIPT_SYMBOLS;
    public static final char[] SPECIAL_SYMBOLS;

    static {
        SCRIPT_SYMBOLS = new char[]{'\''};
        SPECIAL_SYMBOLS = new char[]{'_', '%', '?', '*', '\''};
    }

    /**
     * 转义指定的特殊符号
     *
     * @param value   值
     * @param symbols 特殊符号
     * @return String
     */
    public static String escapeSymbols(String value, char[] symbols) {
        if (StringUtils.isEmpty(value)) {
            return value;
        }
        var chars = value.toCharArray();
        var builder = new StringBuilder();
        for (var c : chars) {
            for (var ss : symbols) {
                if (c == ss) {
                    builder.append("\\");
                    break;
                }
            }
            builder.append(c);
        }
        return builder.toString();
    }

    /**
     * 转义指定的特殊符号
     *
     * @param values  值
     * @param symbols 符号
     * @return String
     */
    public static List<String> escapeSymbols(Collection<?> values, char[] symbols) {
        var list = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(values)) {
            for (var value : values) {
                list.add(escapeSymbols(value.toString(), symbols));
            }
        }
        return list;
    }

    /**
     * 转义特殊符号：'
     *
     * @param string string
     * @return String
     */
    public static String escapeScriptSymbols(String string) {
        return escapeSymbols(string, SCRIPT_SYMBOLS);
    }

    /**
     * 转义特殊符号：'
     *
     * @param values values
     * @return String
     */
    public static List<String> escapeScriptSymbols(Collection<?> values) {
        return escapeSymbols(values, SCRIPT_SYMBOLS);
    }

    /**
     * 转义特殊符号：%、_、?、*、'
     *
     * @param string string
     * @return String
     */
    public static String escapeSpecialSymbols(String string) {
        return escapeSymbols(string, SPECIAL_SYMBOLS);
    }

    /**
     * 转义特殊符号：%、_、?、*、'
     *
     * @param values values
     * @return String
     */
    public static List<String> escapeSpecialSymbols(Collection<?> values) {
        return escapeSymbols(values, SPECIAL_SYMBOLS);
    }

    public static String eq(String column, Object value) {
        return eq(true, column, value);
    }

    public static String eq(boolean condition, String column, Object value) {
        return condition ? String.format("%s = %s", column, value(value)) : "";
    }

    public static String ne(String column, Object value) {
        return ne(true, column, value);
    }

    public static String ne(boolean condition, String column, Object value) {
        return condition ? String.format("%s <> %s", column, value(value)) : "";
    }

    public static String gt(String column, Object value) {
        return gt(true, column, value);
    }

    public static String gt(boolean condition, String column, Object value) {
        return condition ? String.format("%s > %s", column, value(value)) : "";
    }

    public static String ge(String column, Object value) {
        return ge(true, column, value);
    }

    public static String ge(boolean condition, String column, Object value) {
        return condition ? String.format("%s >= %s", column, value(value)) : "";
    }

    public static String lt(String column, Object value) {
        return lt(true, column, value);
    }

    public static String lt(boolean condition, String column, Object value) {
        return condition ? String.format("%s < %s", column, value(value)) : "";
    }

    public static String le(String column, Object value) {
        return le(true, column, value);
    }

    public static String le(boolean condition, String column, Object value) {
        return condition ? String.format("%s <= %s", column, value(value)) : "";
    }

    public static String in(String column, Collection<?> values) {
        return in(true, column, values);
    }

    public static String in(boolean condition, String column, Collection<?> values) {
        return condition ? String.format("%s IN (%s)", column, values(values)) : "";
    }

    public static String notIn(String column, Collection<?> values) {
        return notIn(true, column, values);
    }

    public static String notIn(boolean condition, String column, Collection<?> values) {
        return condition ? String.format("%s NOT IN (%s)", column, values(values)) : "";
    }

    public static String like(String column, String value) {
        return like(true, column, value);
    }

    public static String like(boolean condition, String column, String value) {
        return condition ? String.format("%s LIKE '%%%s%%'", column, escapeSpecialSymbols(value)) : "";
    }

    public static String notLike(String column, String value) {
        return notLike(true, column, value);
    }

    public static String notLike(boolean condition, String column, String value) {
        return condition ? String.format("%s NOT LIKE '%%%s%%'", column, escapeSpecialSymbols(value)) : "";
    }

    public static String likeLeft(String column, String value) {
        return likeLeft(true, column, value);
    }

    public static String likeLeft(boolean condition, String column, String value) {
        return condition ? String.format("%s LIKE '%s%%'", column, escapeSpecialSymbols(value)) : "";
    }

    public static String likeRight(String column, String value) {
        return likeRight(true, column, value);
    }

    public static String likeRight(boolean condition, String column, String value) {
        return condition ? String.format("%s LIKE '%%%s'", column, escapeSpecialSymbols(value)) : "";
    }

    public static String value(Object value) {
        var clazz = value.getClass();
        if (Number.class.isAssignableFrom(clazz)) {
            return value.toString();
        } else if (String.class.isAssignableFrom(clazz)) {
            return "'" + escapeScriptSymbols(value.toString()) + "'";
        } else {
            return "'" + value + "'";
        }
    }

    public static String values(Collection<?> values) {
        var value = values.iterator().next();
        var clazz = value.getClass();
        if (Number.class.isAssignableFrom(clazz)) {
            return StringUtils.join(values, ",");
        } else if (String.class.isAssignableFrom(clazz)) {
            return String.format("'%s'", StringUtils.join(escapeScriptSymbols(values), "','"));
        } else {
            return String.format("'%s'", StringUtils.join(values, "','"));
        }
    }

}
