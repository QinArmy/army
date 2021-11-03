package io.army.mapping;

import io.army.meta.MetaException;

public abstract class MappingFactory {

    protected MappingFactory() {
        throw new UnsupportedOperationException();
    }

    public static boolean hasMappingMeta(Class<?> javaType) {
        return DefaultMappingFactory.obtainDefaultMapping(javaType) != null;
    }

    public static void registerDefaultMapping(Class<?> javaType, MappingType mappingType)
            throws MetaException, IllegalStateException {
        DefaultMappingFactory.overrideDefaultMapping(javaType, mappingType);
    }

    public static MappingType getDefaultMapping(Class<?> javaType) throws MetaException {
        return DefaultMappingFactory.getDefaultMapping(javaType);
    }

    public static MappingType build(Class<?> mappingClass, Class<?> typeClass)
            throws MetaException, IllegalArgumentException {
        return DefaultMappingFactory.createMappingMeta(mappingClass, typeClass);
    }

}
