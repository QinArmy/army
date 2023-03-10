package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Item;
@Deprecated
public interface _ItemExpression<I extends Item> extends Expression {

    @Override
    _ItemExpression<I> bracket();

}
