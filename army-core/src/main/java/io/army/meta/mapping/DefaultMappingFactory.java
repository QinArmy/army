package io.army.meta.mapping;

import io.army.ErrorCode;

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

    private static final Map<Class<?>, MappingType<?>> DEFAULT_MAPPING = createDefaultMapping();

    private static final ConcurrentMap<Class<?>, MappingType<?>> CUSTOM_MAPPING = new ConcurrentHashMap<>();


    private static Map<Class<?>, MappingType<?>> createDefaultMapping() {
        Map<Class<?>, MappingType<?>> map = new HashMap<>();

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
    @Override
    public <T> MappingType<T> getMapping(Class<T> javaType) throws MappingException {
        MappingType<?> mappingType;
        mappingType = CUSTOM_MAPPING.get(javaType);
        if (mappingType == null) {
            mappingType = DEFAULT_MAPPING.get(javaType);
        }
        if (mappingType == null) {
            throw new MappingException(ErrorCode.MAPPING_NOT_FOUND, "not found mapping for %s", javaType.getName());
        }
        return (MappingType<T>) mappingType;
    }

    @Override
    public <T> MappingType<T> getMapping(Class<T> javaType, String mappingType) {
        return null;
    }
}
