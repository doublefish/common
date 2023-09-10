package com.tt.common.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tt.common.json.CustomInstantSerializer;
import lombok.var;

import java.io.IOException;
import java.time.Instant;


/**
 * JSONUtils
 *
 * @author Shuang Yu
 */
public class JSONUtils {

    public static ObjectMapper mapper;
    public static ObjectWriter prettyWriter;

    private JSONUtils() {
    }

    static {
        mapper = new ObjectMapper();
        var javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(Instant.class, new CustomInstantSerializer());
        mapper.registerModule(javaTimeModule);
        prettyWriter = mapper.writerWithDefaultPrettyPrinter();
    }

    /**
     * 序列化
     *
     * @param value value
     * @return String
     */
    public static String toJSONString(Object value) {
        return toJSONString(value, false);
    }

    /**
     * 序列化
     *
     * @param value  value
     * @param pretty pretty
     * @return String
     */
    public static String toJSONString(Object value, boolean pretty) {
        try {
            if (pretty) {
                return prettyWriter.writeValueAsString(value);
            } else {
                return mapper.writeValueAsString(value);
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("序列化JSON失败：" + e.getMessage(), e);
        }
    }

    /**
     * 反序列化
     *
     * @param <T>       T
     * @param content   content
     * @param valueType valueType
     * @return T
     */
    public static <T> T parse(String content, Class<T> valueType) {
        try {
            return mapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("反序列化JSON失败：" + e.getMessage(), e);
        }
    }

    /**
     * 反序列化
     *
     * @param <T>          T
     * @param content      content
     * @param valueTypeRef valueTypeRef
     * @return T
     */
    public static <T> T parse(String content, TypeReference<T> valueTypeRef) {
        try {
            return mapper.readValue(content, valueTypeRef);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("反序列化JSON失败：" + e.getMessage(), e);
        }
    }

    /**
     * 反序列化
     *
     * @param <T>       T
     * @param content   content
     * @param valueType valueType
     * @return T
     */
    public static <T> T parse(String content, JavaType valueType) {
        try {
            return mapper.readValue(content, valueType);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("反序列化JSON失败：" + e.getMessage(), e);
        }
    }

    /**
     * 反序列化
     *
     * @param content content
     * @return JsonNode
     */
    public static JsonNode parseToNode(String content) {
        try {
            return mapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("反序列化JSON失败：" + e.getMessage(), e);
        }
    }

    /**
     * 反序列化
     *
     * @param content content
     * @return JsonNode
     */
    public static JsonNode parseToNode(byte[] content) {
        try {
            return mapper.readTree(content);
        } catch (IOException e) {
            throw new RuntimeException("反序列化JSON失败：" + e.getMessage(), e);
        }
    }

    /**
     * 反序列化
     *
     * @param content content
     * @param offset  offset
     * @param len     len
     * @return JsonNode
     */
    public static JsonNode parseToNode(byte[] content, int offset, int len) {
        try {
            return mapper.readTree(content, offset, len);
        } catch (IOException e) {
            throw new RuntimeException("反序列化JSON失败：" + e.getMessage(), e);
        }
    }
}
