package io.army.meta.mapping;

import io.army.struct.CodeEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class DefaultMappingFactory implements MappingFactory {

    private static final DefaultMappingFactory INSTANCE = new DefaultMappingFactory();

    private static final Map<Class<?>, MappingType> DEFAULT_MAPPING = createDefaultMapping();

    private static final ConcurrentMap<Class<?>, MappingType> CODE_ENUM_MAPPING = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Class<?>, MappingType> CUSTOM_MAPPING = new ConcurrentHashMap<>();


    private static Map<Class<?>, MappingType> createDefaultMapping() {
        Map<Class<?>, MappingType> map = new HashMap<>();

        map.put(Long.class, LongMapping.INSTANCE);
        map.put(Integer.class, IntegerMapping.INSTANCE);
        map.put(BigDecimal.class, BigDecimalMapping.INSTANCE);
        map.put(String.class, StringMapping.INSTANCE);

        map.put(Boolean.class, BooleanMapping.INSTANCE);
        map.put(LocalDateTime.class, LocalDateTimeMapping.INSTANCE);
        map.put(LocalDate.class, LocalDateMapping.INSTANCE);

        return Collections.unmodifiableMap(map);
    }

    private DefaultMappingFactory() {
    }


    public static DefaultMappingFactory getInstance() {
        return INSTANCE;
    }


    @SuppressWarnings("unchecked")


    private boolean isCodeEnum(Class<?> javaType) {
        return javaType.isEnum() && CodeEnum.class.isAssignableFrom(javaType);
    }


    @Override
    public MappingType getMapping(Class<?> javaType) throws MappingException {
        return null;
    }

    @Override
    public MappingType getMapping(Class<?> javaType, String mappingType) throws MappingException {
        return null;
    }
}
