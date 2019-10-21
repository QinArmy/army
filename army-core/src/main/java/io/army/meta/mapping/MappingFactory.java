package io.army.meta.mapping;

public interface MappingFactory {

    MappingType getMapping(Class<?> javaType) throws MappingException;

    MappingType getMapping(Class<?> javaType, String mappingType) throws MappingException;

    static MappingFactory getDefaultInstance() {
        return DefaultMappingFactory.getInstance();
    }

}
