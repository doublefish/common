package com.tt.common.annotation;

import java.lang.annotation.*;

/**
 * TableAlias
 *
 * @author Shuang Yu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface TableAlias {

    /**
     * 实体对应的表名
     */
    String value() default "";
    /**
     * 实体对应的表名
     */
    String language() default "";
}
