package io.army.meta.mapping;

public interface MappingFactory {

    <T> MappingType<T> getMapping(Class<T> javaType) throws MappingException;

    <T> MappingType<T> getMapping(Class<T> javaType, String mappingType) throws MappingException;

    static MappingFactory getDefaultInstance() {
        return DefaultMappingFactory.getInstance();
    }

}
