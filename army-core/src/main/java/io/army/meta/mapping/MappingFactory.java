package io.army.meta.mapping;

public interface MappingFactory {


    MappingMeta getMapping(Class<?> javaType) throws MappingException;

    MappingMeta getMapping(Class<?> javaType, Class<?> mappingClass) throws MappingException;

    MappingMeta getMapping(Class<?> javaType, String mappingType) throws MappingException;

    static MappingFactory build() {
        return DefaultMappingFactory.getInstance();
    }

    static MappingMeta getDefaultMapping(Class<?> javaType) throws MappingException {
        return DefaultMappingFactory.getDefaultMapping(javaType);
    }

}
