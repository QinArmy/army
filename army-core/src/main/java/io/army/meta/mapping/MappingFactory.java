package io.army.meta.mapping;

import io.army.criteria.MetaException;

public interface MappingFactory {


    MappingMeta getMapping(Class<?> javaType) throws MetaException;

    MappingMeta getMapping(Class<?> javaType, Class<?> mappingClass) throws MetaException;

    MappingMeta getMapping(Class<?> javaType, String mappingType) throws MetaException;

    static MappingFactory build() {
        return DefaultMappingFactory.getInstance();
    }

    static MappingMeta getDefaultMapping(Class<?> javaType) throws MetaException {
        return DefaultMappingFactory.getDefaultMapping(javaType);
    }

}
