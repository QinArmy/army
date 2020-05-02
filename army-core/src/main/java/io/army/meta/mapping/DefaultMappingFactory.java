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

    private static final Map<Class<?>, MappingMeta> DEFAULT_MAPPING = createDefaultMapping();

    private static final ConcurrentMap<Class<?>, MappingMeta> CUSTOM_MAPPING = new ConcurrentHashMap<>();


    private static Map<Class<?>, MappingMeta> createDefaultMapping() {
        Map<Class<?>, MappingMeta> map = new HashMap<>();

        map.put(Long.class, LongType.build(Long.class));
        map.put(Integer.class, IntegerType.build(Integer.class));
        map.put(BigDecimal.class, BigDecimalType.build(BigDecimal.class));
        map.put(String.class, StringType.build(String.class));

        map.put(Boolean.class, YesNoType.build(Boolean.class));
        map.put(LocalDateTime.class, LocalDateTimeType.build(LocalDateTime.class));
        map.put(LocalDate.class, LocalDateType.build(LocalDate.class));
        map.put(Double.class, DoubleType.build(Double.class));

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


    static MappingMeta getDefaultMapping(Class<?> javaType) throws MappingException {
        MappingMeta mappingType;
        if (javaType.isEnum()) {
            mappingType = CodeEnumType.build(javaType);
        } else {
            mappingType = DEFAULT_MAPPING.get(javaType);
        }
        if (mappingType == null) {
            throw new MappingException(ErrorCode.META_ERROR, "not found MappingType for %s", javaType.getName());
        }
        return mappingType;
    }

    @Override
    public MappingMeta getMapping(Class<?> javaType, Class<?> mappingClass) throws MappingException {
        return null;
    }

    @Override
    public MappingMeta getMapping(Class<?> javaType) throws MappingException {
        if (javaType.isEnum()) {
            return CodeEnumType.build(javaType);
        }
        MappingMeta mappingType = CUSTOM_MAPPING.get(javaType);
        if (mappingType == null) {
            mappingType = DEFAULT_MAPPING.get(javaType);
        }
        if (mappingType == null) {
            throw new MappingException(ErrorCode.META_ERROR,
                    "not found MappingType for java type[%s]", javaType.getName());
        }
        return mappingType;
    }

    @Override
    public MappingMeta getMapping(Class<?> javaType, String mappingType) throws MappingException {
        try {
            Class<?> mappingTypeClass = ClassUtils.forName(mappingType, getClass().getClassLoader());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*################################## blow private method ##################################*/


}
