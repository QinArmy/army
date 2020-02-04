package io.army.meta.mapping;

public interface MappingFactory {


    MappingType getMapping(Class<?> javaType) throws MappingException;

    MappingType getMapping(Class<?> javaType, Class<?> mappingClass) throws MappingException;

    MappingType getMapping(Class<?> javaType, String mappingType) throws MappingException;

    static MappingFactory build() {
        return DefaultMappingFactory.getInstance();
    }

    static MappingType getDefaultMapping(Class<?> javaType) throws MappingException {
        return DefaultMappingFactory.getDefaultMapping(javaType);
    }

}
