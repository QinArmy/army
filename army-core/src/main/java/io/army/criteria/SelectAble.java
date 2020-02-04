package io.army.criteria;

import io.army.meta.mapping.MappingType;

public interface SelectAble extends FieldSelectAble {

    Selection as(String alias, MappingType mappingType);



}
