package com.tt.common.util;

import com.tt.common.Constants;
import lombok.var;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

/**
 * DateTimeUtils
 *
 * @author Shuang Yu
 */
public class DateTimeUtils {

    public static HashMap<String, SimpleDateFormat> DATE_FORMATS;
    public static HashMap<String, DateTimeFormatter> DATE_TIME_FORMATTERS;

    public static DateFormat dateFormat;
    public static SimpleDateFormat simpleDateFormat;
    public static DateTimeFormatter dateTimeFormatter;
    public static DateTimeFormatter timestampFormatter;
    public static DateTimeFormatter longTimestampFormatter;

    private DateTimeUtils() {
    }

    static {
        DATE_TIME_FORMATTERS = new HashMap<>();
        DATE_FORMATS = new HashMap<>();
        dateFormat = getDateFormat(Constants.DATE_FORMAT);
        simpleDateFormat = getDateFormat(Constants.DATE_TIME_FORMAT);
        dateTimeFormatter = getDateTimeFormatter(Constants.DATE_TIME_FORMAT);
        timestampFormatter = getDateTimeFormatter(Constants.TIMESTAMP_FORMAT);
        longTimestampFormatter = getDateTimeFormatter(Constants.LONG_TIMESTAMP_FORMAT);
    }

    /**
     * getDateFormat
     *
     * @param format format
     * @return SimpleDateFormat
     */
    public static SimpleDateFormat getDateFormat(String format) {
        var value = DATE_FORMATS.getOrDefault(format, null);
        if (value == null) {
            value = new SimpleDateFormat(format);
            DATE_FORMATS.put(format, value);
        }
        return value;
    }

    /**
     * getDateTimeFormatter
     *
     * @param format format
     * @return DateTimeFormatter
     */
    public static DateTimeFormatter getDateTimeFormatter(String format) {
        var value = DATE_TIME_FORMATTERS.getOrDefault(format, null);
        if (value == null) {
            value = DateTimeFormatter.ofPattern(format).withZone(ZoneId.systemDefault());
            DATE_TIME_FORMATTERS.put(format, value);
        }
        return value;
    }

    /**
     * 获取时间戳 - 秒
     *
     * @return timestamp
     */
    public static int getTimestamp() {
        var millis = System.currentTimeMillis();
        var timestamp = millis / 1000;
        return Math.toIntExact(timestamp);
    }

    /**
     * toDate
     *
     * @param text text
     * @return Date
     */
    public static Date toDate(String text) {
        return toDate(text, dateFormat);
    }

    /**
     * toDate
     *
     * @param text   text
     * @param format format
     * @return Date
     */
    public static Date toDate(String text, String format) {
        return toDate(text, getDateFormat(format));
    }

    /**
     * toDate
     *
     * @param text   text
     * @param format format
     * @return Date
     */
    public static Date toDate(String text, DateFormat format) {
        try {
            return format.parse(text);
        } catch (ParseException e) {
            throw new RuntimeException("转换日期失败：" + e.getMessage(), e);
        }
    }

    /**
     * toLocal
     *
     * @param text text
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(String text) {
        return toLocalDateTime(text, dateTimeFormatter);
    }

    /**
     * toLocal
     *
     * @param text   text
     * @param format format
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(String text, String format) {
        return toLocalDateTime(text, getDateTimeFormatter(format));
    }

    /**
     * toLocal
     *
     * @param text      text
     * @param formatter formatter
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(String text, DateTimeFormatter formatter) {
        return LocalDateTime.parse(text, formatter);
    }


    /**
     * toInstant
     *
     * @param text text
     * @return Instant
     */
    public static Instant toInstant(String text) {
        return toInstant(text, dateTimeFormatter);
    }

    /**
     * toInstant
     *
     * @param text   text
     * @param format 支持格式：yyyy-MM-dd HH:mm:ss/yyyy-MM-dd HH:mm:ss.SSS/yyyyMMddHHmmss
     * @return Instant
     */
    public static Instant toInstant(String text, String format) {
        return toInstant(text, getDateTimeFormatter(format));
    }

    /**
     * toInstant
     *
     * @param text      text
     * @param formatter formatter
     * @return Instant
     */
    public static Instant toInstant(String text, DateTimeFormatter formatter) {
        return Instant.from(formatter.parse(text));
    }

    /**
     * toString
     *
     * @param value value
     * @return String
     */
    public static String toString(Instant value) {
        return toString(value, dateTimeFormatter);
    }

    /**
     * toString
     *
     * @param value  value
     * @param format format
     * @return String
     */
    public static String toString(Instant value, String format) {
        return toString(value, getDateTimeFormatter(format));
    }

    /**
     * toString
     *
     * @param value     value
     * @param formatter formatter
     * @return String
     */
    public static String toString(Instant value, DateTimeFormatter formatter) {
        return LocalDateTime.ofInstant(value, ZoneId.systemDefault()).format(formatter);
    }

    /**
     * toString
     *
     * @param value value
     * @return String
     */
    public static String toString(LocalDateTime value) {
        return toString(value, dateTimeFormatter);
    }

    /**
     * toString
     *
     * @param value  value
     * @param format format
     * @return String
     */
    public static String toString(LocalDateTime value, String format) {
        return toString(value, getDateTimeFormatter(format));
    }

    /**
     * toString
     *
     * @param value     value
     * @param formatter formatter
     * @return String
     */
    public static String toString(LocalDateTime value, DateTimeFormatter formatter) {
        return formatter.format(value);
    }

    /**
     * toTimestampString
     *
     * @param value value
     * @return String
     */
    public static String toTimestampString(LocalDateTime value) {
        return timestampFormatter.format(value);
    }

    /**
     * toLongTimestampString
     *
     * @param value value
     * @return String
     */
    public static String toLongTimestampString(LocalDateTime value) {
        return longTimestampFormatter.format(value);
    }

    /**
     * toString
     *
     * @param value value
     * @return String
     */
    public static String toString(Date value) {
        return simpleDateFormat.format(value);
    }

}