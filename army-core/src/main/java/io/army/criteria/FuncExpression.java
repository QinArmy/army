package io.army.criteria;

import io.army.meta.mapping.MappingMeta;

import java.util.List;

public interface FuncExpression<E> extends Expression<E> {

    String name();

    List<MappingMeta> argumentTypeList();
}
