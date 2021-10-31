package io.army.criteria;

import io.army.meta.mapping.MappingType;

import java.util.List;

public interface FuncExpression<E> extends Expression<E> {

    String name();

    List<MappingType> argumentTypeList();
}
