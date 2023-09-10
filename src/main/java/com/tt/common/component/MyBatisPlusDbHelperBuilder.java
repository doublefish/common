package com.tt.common.component;

import org.springframework.lang.Nullable;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

/**
 * MyBatisPlusDbHelperBuilder
 *
 * @param <T> T
 * @author Shuang Yu
 */
public class MyBatisPlusDbHelperBuilder<T> {

    private Class<T> clazz;
    private String tableName;
    private Map<String, String> mappings;
    private boolean includeId;
    private Collection<String> includeFields;
    private Collection<String> excludeFields;
    private DateTimeFormatter dateTimeFormatter;

    public MyBatisPlusDbHelperBuilder() {
    }

    public MyBatisPlusDbHelperBuilder<T> clazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    public MyBatisPlusDbHelperBuilder<T> tableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public MyBatisPlusDbHelperBuilder<T> mappings(@Nullable Map<String, String> mappings) {
        this.mappings = mappings;
        return this;
    }

    public MyBatisPlusDbHelperBuilder<T> includeId(boolean includeId) {
        this.includeId = includeId;
        return this;
    }

    public MyBatisPlusDbHelperBuilder<T> includeFields(@Nullable Collection<String> includeFields) {
        this.includeFields = includeFields;
        return this;
    }

    public MyBatisPlusDbHelperBuilder<T> excludeFields(@Nullable Collection<String> excludeFields) {
        this.excludeFields = excludeFields;
        return this;
    }

    public MyBatisPlusDbHelperBuilder<T> dateTimeFormatter(@Nullable DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
        return this;
    }

    public MyBatisPlusDbHelper<T> build() {
        return new MyBatisPlusDbHelper<>(this.clazz, this.tableName, this.mappings, this.includeId, this.includeFields, this.excludeFields, this.dateTimeFormatter);
    }

    public String toString() {
        return "MyBatisPlusDbHelperBuilder(clazz=" + this.clazz + ", tableName=" + this.tableName + ", mappings=" + this.mappings + ", includeId=" + this.includeId + ", includeFields=" + this.includeFields + ", excludeFields=" + this.excludeFields + ", dateTimeFormatter=" + this.dateTimeFormatter + ")";
    }
}
