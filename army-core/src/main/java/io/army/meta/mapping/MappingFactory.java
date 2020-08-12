package io.army.meta.mapping;

import io.army.criteria.MetaException;

public abstract class MappingFactory {

    protected MappingFactory() {
        throw new UnsupportedOperationException();
    }

    public static boolean hasMappingMeta(Class<?> javaType) {
        return DefaultMappingFactory.obtainDefaultMapping(javaType) != null;
    }

    public static void registerDefaultMapping(Class<?> javaType, MappingMeta mappingMeta)
            throws MetaException, IllegalStateException {
        DefaultMappingFactory.overrideDefaultMapping(javaType, mappingMeta);
    }

    public static MappingMeta getDefaultMapping(Class<?> javaType) throws MetaException {
        return DefaultMappingFactory.getDefaultMapping(javaType);
    }

    public static MappingMeta build(Class<?> mappingClass, Class<?> typeClass)
            throws MetaException, IllegalArgumentException {
        return DefaultMappingFactory.createMappingMeta(mappingClass, typeClass);
    }

}
