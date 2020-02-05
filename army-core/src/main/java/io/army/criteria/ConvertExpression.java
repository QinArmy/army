package io.army.criteria;

import io.army.meta.mapping.MappingType;

public interface ConvertExpression<E> extends Expression<E> {

    Expression<?> originalExp();

}
