package com.tt.common.annotation;

import java.lang.annotation.*;

/**
 * TableColumn
 *
 * @author Shuang Yu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
public @interface TableColumn {

    /**
     * 数据库字段值
     */
    String value() default "";

    /**
     * 是否Id
     */
    boolean id() default false;

    /**
     * 是否逻辑删除
     */
    boolean logic() default false;

    /**
     * 版本号，乐观锁
     */
    boolean version() default false;

    /**
     * 是否为数据库表字段
     * <p>
     * 默认 true 存在，false 不存在
     */
    boolean exist() default true;
}
