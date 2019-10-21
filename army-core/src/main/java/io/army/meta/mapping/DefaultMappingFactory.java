package io.army.meta.mapping;

import io.army.ErrorCode;
import io.army.struct.CodeEnum;
import io.army.util.ClassUtils;

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


    private boolean isCodeEnum(Class<?> javaType) {
        return javaType.isEnum() && CodeEnum.class.isAssignableFrom(javaType);
    }


    @Override
    public MappingType getMapping(Class<?> javaType) throws MappingException {
        MappingType mappingType = CUSTOM_MAPPING.get(javaType);
        if (mappingType == null) {
            if (isCodeEnum(javaType)) {
                mappingType = getOrCreateCodeEnumMapping(javaType);
            } else {
                mappingType = DEFAULT_MAPPING.get(javaType);
            }
        }
        if (mappingType == null) {
            throw new MappingException(ErrorCode.META_ERROR,
                    "not found MappingType for java type[%s]", javaType.getName());
        }
        return mappingType;
    }

    @Override
    public MappingType getMapping(Class<?> javaType, String mappingType) throws MappingException {
        try {
            Class<?> mappingTypeClass = ClassUtils.forName(mappingType, getClass().getClassLoader());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*################################## blow private method ##################################*/

    private MappingType getOrCreateCodeEnumMapping(Class<?> javaType) {
        MappingType mappingType = CODE_ENUM_MAPPING.get(javaType);
        if (mappingType == null) {
            CODE_ENUM_MAPPING.putIfAbsent(javaType, new CodeEnumMapping(javaType));
            mappingType = CODE_ENUM_MAPPING.get(javaType);
        }
        return mappingType;

    }
}
