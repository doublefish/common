package com.tt.common.util;


import lombok.var;
import org.springframework.beans.BeanUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.*;

/**
 * ReflectUtils
 *
 * @author Shuang Yu
 */
public class ReflectUtils {

    public static String LIST_CLASS_NAME;

    public static Map<String, List<Field>> FIELDS;

    private ReflectUtils() {
    }

    static {
        LIST_CLASS_NAME = List.class.getName();
        FIELDS = new HashMap<>(0);
    }

    /**
     * castList
     *
     * @param <T>   目标类型
     * @param obj   数据源
     * @param clazz 目标类型
     */
    public static <T> List<T> castList(Object obj, Class<T> clazz) {
        var result = new ArrayList<T>();
        if (obj instanceof Collection<?>) {
            for (var o : (Collection<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * 封装 org.springframework.beans.copyProperties
     * 缺点：1.不能复制复杂对象，2.性能比convert慢20倍左右
     *
     * @param <E>              源类型
     * @param <T>              目标类型
     * @param source           数据源
     * @param targetClass      目标类型
     * @param ignoreProperties 忽略属性
     * @return T
     */
    public static <E, T> T copyProperties(E source, Class<T> targetClass, String... ignoreProperties) {
        if (source == null) {
            return null;
        }
        T target;
        try {
            target = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("创建实例发生异常：" + e.getMessage(), e);
        }
        BeanUtils.copyProperties(source, target, ignoreProperties);
        return target;
    }

    /**
     * 封装 org.springframework.beans.copyProperties
     * 缺点：1.不能复制复杂对象，2.性能比convert慢20倍左右
     *
     * @param <E>              源类型
     * @param <T>              目标类型
     * @param sources          数据源
     * @param targetClass      目标类型
     * @param ignoreProperties 忽略属性
     * @return List<T>
     */
    public static <E, T> List<T> copyProperties(Collection<E> sources, Class<T> targetClass, String... ignoreProperties) {
        if (sources == null) {
            return null;
        }
        var targets = new ArrayList<T>();
        for (var source : sources) {
            T target;
            try {
                target = targetClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("创建实例发生异常：" + e.getMessage(), e);
            }
            BeanUtils.copyProperties(source, target, ignoreProperties);
            targets.add(target);
        }
        return targets;
    }

    /**
     * convert
     *
     * @param <E>         源类型
     * @param <T>         目标类型
     * @param sources     数据源
     * @param targetClass 目标类型
     * @return List<T>
     */
    public static <E, T> List<T> convert(Collection<E> sources, Class<T> targetClass) {
        if (sources == null) {
            return null;
        }
        var targets = new ArrayList<T>();
        for (var source : sources) {
            targets.add(convert(source, targetClass));
        }
        return targets;
    }

    /**
     * convert
     *
     * @param <E>         源类型
     * @param <T>         目标类型
     * @param source      数据源
     * @param targetClass 目标类型
     * @return T
     */
    public static <E, T> T convert(E source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        T t;
        try {
            t = targetClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("创建实例发生异常：" + e.getMessage(), e);
        }
        var sourceClass = source.getClass();
        var sourceFields = getDeclaredFields(sourceClass);
        var targetFields = getDeclaredFields(targetClass);
        for (var tField : targetFields) {
            var tFieldName = tField.getName();
            for (var sField : sourceFields) {
                var sFieldName = sField.getName();
                if (!sFieldName.equals(tFieldName)) {
                    continue;
                }
                sField.setAccessible(true);
                tField.setAccessible(true);
                try {
                    var value = sField.get(source);
                    if (value == null) {
                        continue;
                    }
                    var tTypeName = tField.getType().getName();
                    if (tTypeName.equals(LIST_CLASS_NAME)) {
                        var tGenericType = tField.getGenericType();
                        var sGenericType = sField.getGenericType();
                        if (tGenericType.getTypeName().equals(sGenericType.getTypeName())) {
                            tField.set(t, value);
                        } else if (tGenericType instanceof ParameterizedType && sGenericType instanceof ParameterizedType) {
                            var sActualType = ((ParameterizedType) sGenericType).getActualTypeArguments()[0];
                            var sClass = Class.forName(sActualType.getTypeName());
                            var sList = castList(value, sClass);
                            var tActualType = ((ParameterizedType) tGenericType).getActualTypeArguments()[0];
                            if (tActualType instanceof TypeVariable) {
                                var parameterizedType = (ParameterizedType) targetClass.getGenericSuperclass();
                                tActualType = (parameterizedType).getActualTypeArguments()[0];
                            }
                            var tClass = Class.forName(tActualType.getTypeName());
                            var tList = convert(sList, tClass);
                            tField.set(t, tList);
                        } else {
                            tField.set(t, value);
                        }
                        break;
                    }

                    var sTypeName = sField.getType().getName();
                    if (sTypeName.equals(tTypeName)) {
                        tField.set(t, value);
                        break;
                    }

                    if (tTypeName.startsWith("java.lang.")) {
                        switch (tTypeName) {
                            case "java.lang.String":
                                tField.set(t, value.toString());
                                break;
                            case "java.lang.Integer":
                                tField.set(t, ((Number) value).intValue());
                                break;
                            case "java.lang.Long":
                                tField.set(t, ((Number) value).longValue());
                                break;
                            default:
                                tField.set(t, value);
                                break;
                        }
                        break;
                    }

                    var tClazz = Class.forName(tTypeName);
                    var newValue = convert(value, tClazz);
                    tField.set(t, newValue);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("转换字段%s发生异常：%s", tField.getName(), e.getMessage()), e);
                }
            }
        }

        return t;
    }

    /**
     * 获取声明的字段名称（包含继承的）
     *
     * @param <T>    T
     * @param tClass tClass
     */
    public static <T> Collection<String> getDeclaredFieldNames(Class<T> tClass) {
        var list = new ArrayList<String>();
        var fields = getDeclaredFields(tClass);
        for (var field : fields) {
            list.add(field.getName());
        }
        return list;
    }

    /**
     * getGenericClass
     *
     * @param <T>        T
     * @param clazz      clazz
     * @param superClass superClass
     * @return Class<?>
     */
    public static <T> Class<T> getGenericClass(Class<?> clazz, Class<?> superClass) throws ClassNotFoundException {
        do {
            var genericSuperclass = clazz.getGenericSuperclass();
            while (!(genericSuperclass instanceof ParameterizedType)) {
                genericSuperclass = ((Class<?>) genericSuperclass).getGenericSuperclass();
            }
            var parameterizedType = (ParameterizedType) genericSuperclass;
            var typeArguments = parameterizedType.getActualTypeArguments();
            for (var type : typeArguments) {
                if (type instanceof TypeVariable) {
                    continue;
                }
                var class1 = Class.forName(type.getTypeName());
                if (superClass.isAssignableFrom(class1)) {
                    @SuppressWarnings("unchecked")
                    var tClass = (Class<T>) class1;
                    return tClass;
                }
            }
            clazz = clazz.getSuperclass();
        } while (!clazz.getName().equals(Object.class.getName()));
        throw new ClassNotFoundException("无法识别的类型");
    }

    /**
     * 获取声明的字段（包含继承的）
     *
     * @param clazz clazz
     * @return Map<String, Field>
     */
    public static Map<String, Field> getDeclaredFieldMap(Class<?> clazz) {
        var fields = getDeclaredFields(clazz);
        var map = new LinkedHashMap<String, Field>(0);
        for (var f : fields) {
            map.put(f.getName(), f);
        }
        return map;
    }

    /**
     * 获取声明的字段（包含继承的）
     *
     * @param clazz clazz
     * @return Collection<Field>
     */
    public static Collection<Field> getDeclaredFields(Class<?> clazz) {
        var list = FIELDS.get(clazz.getName());
        if (list == null) {
            list = new ArrayList<>();
            var names = new HashSet<String>();
            var fields = clazz.getDeclaredFields();
            for (var f : fields) {
                names.add(f.getName());
                list.add(f);
            }
            var superclass = clazz.getSuperclass();
            while (superclass != null && superclass != Object.class) {
                fields = superclass.getDeclaredFields();
                for (var f : fields) {
                    if (names.add(f.getName())) {
                        list.add(f);
                    }
                }
                superclass = superclass.getSuperclass();
            }
            FIELDS.put(clazz.getName(), list);
        }
        return list;
    }

    /**
     * 根据名称获取声明的字段（包含继承的）
     *
     * @param clazz clazz
     * @param name  name
     * @return Field
     */
    public static Field getDeclaredField(Class<?> clazz, String name) {
        var fields = getDeclaredFields(clazz);
        for (var f : fields) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    /**
     * 根据注解获取声明的字段（包含继承的）
     *
     * @param clazz           clazz
     * @param annotationClass annotationClass
     * @return Field
     */
    public static Field getDeclaredFieldByAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        var fields = getDeclaredFields(clazz);
        for (var field : fields) {
            var annotation = field.getAnnotation(annotationClass);
            if (annotation != null) {
                return field;
            }
        }
        return null;
    }

    /**
     * getCurrentAnnotation
     *
     * @param <T>            T  extends Annotation
     * @param tClass         tClass
     * @param parameterTypes parameterTypes
     */
    public static <T extends Annotation> T getCurrentAnnotation(Class<T> tClass, Class<?>... parameterTypes) {
        var method = getCurrentMethod(parameterTypes);
        var annotation = method.getAnnotation(tClass);
        if (annotation == null) {
            annotation = method.getDeclaringClass().getAnnotation(tClass);
        }
        return annotation;
    }

    /**
     * getCurrentClass
     */
    public static Class<?> getCurrentClass() {
        var element = getCurrentStackTraceElement();
        var className = element.getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("获取当前类型发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * getCurrentMethod
     *
     * @param parameterTypes parameterTypes
     */
    public static Method getCurrentMethod(Class<?>... parameterTypes) {
        var element = getCurrentStackTraceElement();
        var className = element.getClassName();
        var methodName = element.getMethodName();
        try {
            var clazz = Class.forName(className);
            return clazz.getMethod(methodName, parameterTypes);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException("获取当前方法发生异常：" + e.getMessage(), e);
        }
    }

    /**
     * getCurrentStackTraceElement
     */
    public static StackTraceElement getCurrentStackTraceElement() {
        var stackTraceElements = Thread.currentThread().getStackTrace();
        StackTraceElement element = null;
        for (var e : stackTraceElements) {
            if (e.getClassName().contains("$$") || "sun.reflect.NativeMethodAccessorImpl".equals(e.getClassName())) {
                break;
            }
            element = e;
        }
        return element;
    }

    /**
     * 获取值
     *
     * @param entity entity
     * @param field  field
     * @return Object
     */
    public static Object getValue(Object entity, String field) {
        var reader = getReadMethod(entity.getClass(), field);
        try {
            return reader.invoke(entity);
        } catch (Exception e) {
            throw new RuntimeException(String.format("获取属性【%s】的值发生异常：%s", field, e.getMessage()), e);
        }
    }

    /**
     * 设置值
     *
     * @param entity entity
     * @param field  field
     * @param value  value
     */
    public static void setValue(Object entity, String field, Object value) {
        var writer = getWriteMethod(entity.getClass(), field);
        try {
            writer.invoke(entity, value);
        } catch (Exception e) {
            throw new RuntimeException(String.format("设置属性【%s】的值发生异常：%s", field, e.getMessage()), e);
        }
    }

    /**
     * 获取读取方法
     *
     * @param clazz clazz
     * @param field field
     * @return Object
     */
    public static Method getReadMethod(Class<?> clazz, String field) {
        var descriptor = BeanUtils.getPropertyDescriptor(clazz, field);
        if (descriptor == null || descriptor.getReadMethod() == null) {
            throw new NullPointerException(String.format("获取属性【%s】的读取方法发生异常：没有PropertyDescriptor或ReadMethod", field));
        }
        return descriptor.getReadMethod();
    }

    /**
     * 获取写入方法
     *
     * @param clazz clazz
     * @param field field
     */
    public static Method getWriteMethod(Class<?> clazz, String field) {
        var descriptor = BeanUtils.getPropertyDescriptor(clazz, field);
        if (descriptor == null || descriptor.getWriteMethod() == null) {
            throw new NullPointerException(String.format("获取属性【%s】的写入方法发生异常：没有PropertyDescriptor或WriteMethod", field));
        }
        return descriptor.getWriteMethod();
    }

}
