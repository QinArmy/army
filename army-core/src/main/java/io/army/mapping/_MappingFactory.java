package io.army.mapping;

import io.army.mapping.optional.OffsetDateTimeType;
import io.army.mapping.optional.OffsetTimeType;
import io.army.meta.MetaException;
import io.army.struct.CodeEnum;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class _MappingFactory {

    private _MappingFactory() {
        throw new UnsupportedOperationException();
    }


    private static final Map<Class<?>, Function<Class<?>, MappingType>> defaultMappingMap = createDefaultMappingMap();


    public static MappingType getMapping(Class<?> javaType) throws MetaException {
        final MappingType mappingType;
        if (CodeEnum.class.isAssignableFrom(javaType)) {
            if (!javaType.isEnum()) {
                String m = String.format("%s isn't enum.", javaType.getName());
                throw new MetaException(m);
            }
            mappingType = CodeEnumType.create(javaType);
        } else if (javaType.isEnum()) {
            mappingType = NameEnumType.create(javaType);
        } else {
            final Function<Class<?>, MappingType> function;
            function = defaultMappingMap.get(javaType);
            if (function == null) {
                String m = String.format("Not found default mapping for %s .", javaType.getName());
                throw new MetaException(m);
            }
            mappingType = function.apply(javaType);
        }
        return mappingType;
    }

    public static MappingType getMapping(Class<?> mappingClass, Class<?> javaType) throws MetaException {
        final MappingType mappingType;
        if (CodeEnum.class.isAssignableFrom(javaType)) {
            if (!javaType.isEnum()) {
                String m = String.format("%s isn't enum.", javaType.getName());
                throw new MetaException(m);
            }
            mappingType = CodeEnumType.create(javaType);
        } else if (javaType.isEnum()) {
            mappingType = NameEnumType.create(javaType);
        } else {
            mappingType = createMappingType(mappingClass, javaType);
        }
        return mappingType;
    }

    private static MappingType createMappingType(Class<?> mappingClass, Class<?> javaType) throws MetaException {
        if (!MappingType.class.isAssignableFrom(mappingClass)) {
            String m = String.format("%s isn't %s instance.", mappingClass.getName(), MappingType.class.getName());
            throw new MetaException(m);
        }
        try {
            final Method method;
            method = javaType.getMethod("create", Class.class);
            if (!(Modifier.isPublic(method.getModifiers())
                    && Modifier.isStatic(method.getModifiers())
                    && mappingClass == method.getReturnType())) {
                String m = String.format("%s create(Class<?> typeClass) method definite error."
                        , mappingClass.getName());
                throw new MetaException(m);
            }
            final MappingType mappingType;
            mappingType = (MappingType) method.invoke(null, javaType);
            if (mappingType == null) {
                String m = String.format("%s create(Class<?> javaType) method return null.", mappingClass.getName());
                throw new MetaException(m);
            }
            final Class<?> actualType = mappingType.javaType();
            if (!actualType.isAssignableFrom(javaType)) {
                String m = String.format("%s javaType() return value[%s] and java type[%s] not match."
                        , mappingClass.getName(), actualType.getName(), javaType.getName());
                throw new MetaException(m);
            }
            return mappingType;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new MetaException(e.getMessage(), e);
        }

    }


    private static Map<Class<?>, Function<Class<?>, MappingType>> createDefaultMappingMap() {
        final Map<Class<?>, Function<Class<?>, MappingType>> map = new HashMap<>();
        map.put(Boolean.class, BooleanType::create);
        map.put(byte[].class, ByteArrayType::create);
        map.put(String.class, StringType::create);

        map.put(Byte.class, ByteType::create);
        map.put(Short.class, ShortType::create);
        map.put(Integer.class, IntegerType::create);
        map.put(Long.class, LongType::create);
        map.put(Double.class, DoubleType::create);
        map.put(BigDecimal.class, BigDecimalType::create);
        map.put(BigInteger.class, BigIntegerType::create);


        map.put(LocalDateTime.class, LocalDateTimeType::create);
        map.put(LocalDate.class, LocalDateType::create);
        map.put(LocalTime.class, LocalTimeType::create);
        map.put(MonthDay.class, MonthDayType::create);
        map.put(YearMonth.class, YearMonthType::create);
        map.put(Year.class, YearType::create);
        map.put(OffsetDateTime.class, OffsetDateTimeType::create);
        map.put(OffsetTime.class, OffsetTimeType::create);
        return Collections.unmodifiableMap(map);
    }

}
