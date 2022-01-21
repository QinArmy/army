package io.army.mapping;

import io.army.meta.MetaException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public abstract class _MappingFactory {

    private _MappingFactory() {
        throw new UnsupportedOperationException();
    }


    private static final Map<Class<?>, Function<Class<?>, MappingType>> standardMap;

    static {
        final Map<Class<?>, Function<Class<?>, MappingType>> standardMappingMap;
        standardMappingMap = createStandardMappingMap();


        standardMap = Collections.unmodifiableMap(standardMappingMap);
    }


    public static MappingType getMapping(Class<?> javaType) throws MetaException {
        return DefaultMappingFactory.getDefaultMapping(javaType);
    }

    public static MappingType getMapping(Class<?> mappingClass, Class<?> fieldJavaType)
            throws MetaException, IllegalArgumentException {
        return DefaultMappingFactory.createMappingMeta(mappingClass, fieldJavaType);
    }


    private static Map<Class<?>, Function<Class<?>, MappingType>> createStandardMappingMap() {
        return new HashMap<>();
    }

}
