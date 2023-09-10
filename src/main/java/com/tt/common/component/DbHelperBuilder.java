package com.tt.common.component;

import org.springframework.lang.Nullable;

import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Map;

/**
 * DbHelperBuilder
 *
 * @param <T> T
 * @author Shuang Yu
 */
public class DbHelperBuilder<T> {
    private Class<T> clazz;
    private String tableName;
    private Map<String, String> mappings;
    private Boolean includeId;
    private Collection<String> includeFields;
    private Collection<String> excludeFields;
    private DateTimeFormatter dateTimeFormatter;

    public DbHelperBuilder() {
    }

    public DbHelperBuilder<T> clazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    public DbHelperBuilder<T> tableName(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public DbHelperBuilder<T> mappings(@Nullable Map<String, String> mappings) {
        this.mappings = mappings;
        return this;
    }

    public DbHelperBuilder<T> includeId(Boolean includeId) {
        this.includeId = includeId;
        return this;
    }

    public DbHelperBuilder<T> includeFields(@Nullable Collection<String> includeFields) {
        this.includeFields = includeFields;
        return this;
    }

    public DbHelperBuilder<T> excludeFields(@Nullable Collection<String> excludeFields) {
        this.excludeFields = excludeFields;
        return this;
    }

    public DbHelperBuilder<T> dateTimeFormatter(@Nullable DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
        return this;
    }

    public DbHelper<T> build() {
        return new DbHelper<>(this.clazz, this.tableName, this.mappings, this.includeId, this.includeFields, this.excludeFields, this.dateTimeFormatter);
    }

    public String toString() {
        return "DbHelperBuilder(clazz=" + this.clazz + ", tableName=" + this.tableName + ", mappings=" + this.mappings + ", includeId=" + this.includeId + ", includeFields=" + this.includeFields + ", excludeFields=" + this.excludeFields + ", dateTimeFormatter=" + this.dateTimeFormatter + ")";
    }
}
