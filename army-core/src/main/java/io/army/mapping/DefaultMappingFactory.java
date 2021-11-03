package io.army.mapping;

import io.army.lang.Nullable;
import io.army.mapping.optional.OffsetDateTimeType;
import io.army.mapping.optional.OffsetTimeType;
import io.army.mapping.optional.ZonedDateTimeType;
import io.army.meta.MetaException;
import io.army.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

abstract class DefaultMappingFactory {

    private DefaultMappingFactory() {
    }

    private static final Map<Class<?>, MappingType> DEFAULT_MAPPING = createDefaultMapping();

    private static final ConcurrentMap<Class<?>, MappingType> OVERRIDE_DEFAULT_MAP = new ConcurrentHashMap<>();


    private static Map<Class<?>, MappingType> createDefaultMapping() {
        Map<Class<?>, MappingType> map = new HashMap<>();

        map.put(Long.class, LongType.build(Long.class));
        map.put(Integer.class, IntegerType.build(Integer.class));
        map.put(BigDecimal.class, BigDecimalType.build(BigDecimal.class));
        map.put(String.class, StringType.build(String.class));

        map.put(Boolean.class, TrueFalseType.build(Boolean.class));
        map.put(LocalDateTime.class, LocalDateTimeType.build(LocalDateTime.class));
        map.put(LocalDate.class, LocalDateType.build(LocalDate.class));
        map.put(Double.class, DoubleType.build(Double.class));

        map.put(OffsetTime.class, OffsetTimeType.build(OffsetTime.class));
        map.put(OffsetDateTime.class, OffsetDateTimeType.build(OffsetDateTime.class));
        map.put(ZonedDateTime.class, ZonedDateTimeType.build(ZonedDateTime.class));

        return Collections.unmodifiableMap(map);
    }

    static MappingType getDefaultMapping(Class<?> javaType) throws MetaException {
        MappingType mappingType = obtainDefaultMapping(javaType);
        if (mappingType == null) {
            throw new MetaException("not found MappingType for %s", javaType.getName());
        }
        return mappingType;
    }

    @Nullable
    static MappingType obtainDefaultMapping(Class<?> javaType) {
        MappingType mappingType;
        if (javaType.isEnum()) {
            mappingType = CodeEnumType.build(javaType);
        } else {
            mappingType = OVERRIDE_DEFAULT_MAP.get(javaType);
            if (mappingType == null) {
                mappingType = DEFAULT_MAPPING.get(javaType);
            }
        }
        return mappingType;
    }

    static void overrideDefaultMapping(Class<?> javaType, MappingType mappingType) throws MetaException {
        assertMappingMeta(javaType, mappingType);
        if (OVERRIDE_DEFAULT_MAP.putIfAbsent(javaType, mappingType) != null) {
            throw new IllegalStateException(String.format("java type[%s] override mapping duplication."
                    , javaType.getName()));
        }

    }


    static MappingType createMappingMeta(Class<?> mappingClass, Class<?> typeClass)
            throws MetaException, IllegalArgumentException {

        if (!MappingType.class.isAssignableFrom(mappingClass)) {
            throw new IllegalArgumentException(String.format("mappingClass isn't %s type."
                    , MappingType.class.getName()));
        }

        Method method = ReflectionUtils.findMethod(mappingClass, "build", Class.class);
        if (method != null
                && Modifier.isPublic(method.getModifiers())
                && Modifier.isStatic(method.getModifiers())
                && mappingClass == method.getReturnType()) {
            try {
                MappingType mappingType = (MappingType) method.invoke(null, typeClass);
                if (mappingType == null) {
                    throw new MetaException("MappingMeta[%s] build method return null.", mappingClass.getName());
                }
                assertMappingMeta(typeClass, mappingType);
                return mappingType;
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MetaException(e, "invoke MappingMeta[%s] build method occur error.", mappingClass.getName());
            }
        } else {
            throw new MetaException("MappingMeta[%s] build(Class<?> typeClass) method definite error."
                    , mappingClass.getName());
        }
    }




    /*################################## blow private method ##################################*/

    private static void assertMappingMeta(Class<?> javaType, MappingType mappingType) {
        if (mappingType.javaType().isAssignableFrom(javaType)) {
            throw new MetaException("MappingMeta[%s] isn't singleton,javaType() must be base class/interface of %s ."
                    , mappingType.getClass().getName(), javaType.getName());
        }
    }

}
