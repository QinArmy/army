package io.army.mapping;

public interface MapMappingType extends MappingType {

    Class<?> keyType();

    Class<?> valueType();


}
