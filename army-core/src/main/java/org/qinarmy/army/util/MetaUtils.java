package org.qinarmy.army.util;

import org.qinarmy.army.ErrorCode;
import org.qinarmy.army.annotation.*;
import org.qinarmy.army.criteria.MetaException;
import org.qinarmy.army.domain.IDomain;
import org.qinarmy.army.meta.MappingMode;
import org.qinarmy.army.struct.CodeEnum;
import org.springframework.lang.NonNull;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.JDBCType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * created  on 2019-02-21.
 */
public abstract class MetaUtils {


    public static final Map<Class<?>, JDBCType> DEFAULT_MAPPING = createDefaultMapping();

    public static final Map<Class<?>, Integer> DEFAULT_LENGTH = createDefaultLength();

    public static final Set<Class<?>> VARIABLE_LENGTH = ArrayUtils.asUnmodifiableSet(
            String.class
    );


    private static Map<Class<?>, JDBCType> createDefaultMapping() {
        Map<Class<?>, JDBCType> map = new HashMap<>();

        map.put(String.class, JDBCType.VARCHAR);
        map.put(Long.class, JDBCType.BIGINT);
        map.put(Integer.class, JDBCType.INTEGER);
        map.put(Boolean.class, JDBCType.CHAR);

        map.put(BigDecimal.class, JDBCType.DECIMAL);
        map.put(CodeEnum.class, JDBCType.INTEGER);
        map.put(LocalDateTime.class, JDBCType.TIMESTAMP);
        map.put(LocalDate.class, JDBCType.DATE);

        map.put(LocalTime.class, JDBCType.TIME);

        return Collections.unmodifiableMap(map);
    }

    private static Map<Class<?>, Integer> createDefaultLength() {
        Map<Class<?>, Integer> map = new HashMap<>();

        map.put(String.class, 255);
        map.put(Long.class, 20);
        map.put(Integer.class, 11);
        map.put(Boolean.class, 1);

        map.put(CodeEnum.class, 11);

        return Collections.unmodifiableMap(map);
    }


    @NonNull
    public static Table tableMeta(@NonNull Class<? extends IDomain> entityClass) {
        Table table = AnnotationUtils.getAnnotation(entityClass, Table.class);
        if (table == null) {
            throw createNonAnnotationException(entityClass, Table.class);
        }
        return table;
    }

    @NonNull
    public static Column columnMeta(@NonNull Class<? extends IDomain> entityClass, @NonNull String propertyName) {
        Field field = ReflectionUtils.findField(entityClass, propertyName);
        if (field == null) {
            throw new MetaException(ErrorCode.META_ERROR, "not found field[%s] in class[%s]"
                    , propertyName, entityClass.getName());
        }
        Column column = AnnotationUtils.getAnnotation(field, Column.class);
        if (column == null) {
            throw createNonAnnotationException(entityClass, Column.class);
        }
        return column;
    }


    @NonNull
    public static Class<?> fieldClass(@NonNull Class<? extends IDomain> entityClass, @NonNull String propertyName) {
        Field field = ReflectionUtils.findField(entityClass, propertyName);
        if (field == null) {
            throw new MetaException(ErrorCode.META_ERROR, "not found field[%s] in class[%s]"
                    , propertyName, entityClass.getName());
        }
        return field.getType();
    }


    @NonNull
    public static JDBCType jdbcType(@NonNull Class<? extends IDomain> entityClass, @NonNull String propertyName) {
        Class<?> javaType = fieldClass(entityClass, propertyName);
        JDBCType jdbcType;
        if (javaType.isEnum() && CodeEnum.class.isAssignableFrom(javaType)) {
            javaType = CodeEnum.class;
        } else {
            if (!DEFAULT_MAPPING.containsKey(javaType)) {
                throw new MetaException(ErrorCode.META_ERROR, "Domain[%s]'s property[%s] type not support.",
                        entityClass.getName(), propertyName);
            }
        }
        jdbcType = DEFAULT_MAPPING.get(javaType);
        return jdbcType;
    }

    public static int length(@NonNull Column column, @NonNull Class<?> propertyClass) {
        int len;

        if (VARIABLE_LENGTH.contains(propertyClass)) {
            len = column.length();
        } else {
            len = DEFAULT_LENGTH.getOrDefault(propertyClass, -1);
        }
        return len;
    }


    @NonNull
    public static String tableName(@NonNull Class<? extends IDomain> entityClass) throws MetaException {
        return tableMeta(entityClass).name();
    }

    public static boolean immutable(@NonNull Class<? extends IDomain> entityClass) {
        return AnnotationUtils.getAnnotation(entityClass, Immutable.class) != null;

    }

    public static int fieldCount(@NonNull Class<? extends IDomain> entityClass) {
        final int[] count = new int[]{0};
        ReflectionUtils.doWithFields(entityClass, fc -> count[0]++, ff -> {
            boolean match = false;
            if (ff.getAnnotation(Column.class) != null) {
                if (ff.getDeclaringClass() == entityClass) {
                    match = true;
                } else {
                    match = AnnotationUtils.getAnnotation(ff.getDeclaringClass(), Table.class) == null
                            && AnnotationUtils.getAnnotation(ff.getDeclaringClass(), MappedSuperclass.class) != null;
                }
            }
            return match;
        });
        return count[0];
    }

    public static MappingMode mappingMode(@NonNull Class<? extends IDomain> entityClass) {
        Class<?> clazz = entityClass;
        int count = 0;
        for (; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            if (AnnotationUtils.getAnnotation(clazz, Table.class) != null) {
                count++;
            }
        }
        MappingMode mappingMode;
        switch (count) {
            case 0:
                throw createNonAnnotationException(entityClass, Table.class);
            case 1:
                if (AnnotationUtils.getAnnotation(entityClass, Inheritance.class) == null) {
                    mappingMode = MappingMode.SIMPLE;
                } else {
                    mappingMode = MappingMode.PARENT;
                }
                break;
            default:
                mappingMode = MappingMode.CHILD;
                break;
        }
        return mappingMode;

    }


    private static MetaException createNonAnnotationException(Class<? extends IDomain> entityClass
            , Class<?> annotationClass) {
        return new MetaException(ErrorCode.META_ERROR, "class[%s] isn't annotated by %s "
                , entityClass.getName()
                , annotationClass.getName());
    }

}
