package io.army.mapping;

import io.army.meta.MetaException;

public abstract class _MappingFactory {

    private _MappingFactory() {
        throw new UnsupportedOperationException();
    }


    public static MappingType getMapping(Class<?> javaType) throws MetaException {
        return DefaultMappingFactory.getDefaultMapping(javaType);
    }

    public static MappingType getMapping(Class<?> mappingClass, Class<?> fieldJavaType)
            throws MetaException, IllegalArgumentException {
        return DefaultMappingFactory.createMappingMeta(mappingClass, fieldJavaType);
    }

}
