package io.army.mapping;

import java.util.List;

public interface MultiGenericsMappingType extends MappingType.GenericsMappingType {

    List<Class<?>> genericsTypeList();

}
