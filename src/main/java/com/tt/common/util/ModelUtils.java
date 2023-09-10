package com.tt.common.util;

import lombok.var;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * ModelUtils
 *
 * @author Shuang Yu
 */
public class ModelUtils {

    private ModelUtils() {
    }

    /**
     * 设置属性的值为null的属性的值为其类型对应的默认值
     *
     * @param object 对象
     */
    public static <T> void setDefaultValue(T object) {
        var declaredFields = ReflectUtils.getDeclaredFields(object.getClass());
        for (var field : declaredFields) {
            var accessible = field.isAccessible();
            field.setAccessible(true);
            try {
                var value = field.get(object);
                if (value == null) {
                    var type = field.getType();
                    if (Short.class.isAssignableFrom(type)) {
                        field.set(object, 0);
                    } else if (Integer.class.isAssignableFrom(type)) {
                        field.set(object, 0);
                    } else if (Long.class.isAssignableFrom(type)) {
                        field.set(object, 0L);
                    } else if (Float.class.isAssignableFrom(type)) {
                        field.set(object, 0F);
                    } else if (Double.class.isAssignableFrom(type)) {
                        field.set(object, 0D);
                    } else if (BigDecimal.class.isAssignableFrom(type)) {
                        field.set(object, new BigDecimal(0));
                    } else if (Boolean.class.isAssignableFrom(type)) {
                        field.set(object, Boolean.FALSE);
                    } else if (Character.class.isAssignableFrom(type)) {
                        field.set(object, Character.MIN_VALUE);
                    } else if (String.class.isAssignableFrom(type)) {
                        field.set(object, "");
                    } else if (Instant.class.isAssignableFrom(type)) {
                        field.set(object, Instant.EPOCH);
                    }
                }
                field.setAccessible(accessible);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("设置默认值发生异常：" + e.getMessage(), e);
            }
        }
    }
}
