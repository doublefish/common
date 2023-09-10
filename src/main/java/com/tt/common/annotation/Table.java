package com.tt.common.annotation;

import java.lang.annotation.*;

/**
 * TableColumn
 *
 * @author Shuang Yu
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.ANNOTATION_TYPE})
public @interface Table {

    /**
     * 实体对应的表名
     */
    String value() default "";

    /**
     * SQL语句中主表的别名
     */
    String alias() default "";

    /**
     * SQL语句中关联表的别名
     */
    String alias1() default "";

    /**
     * schema
     */
    String schema() default "";

    /**
     * 需要排除的属性名
     */
    String[] excludeProperty() default {};
}
