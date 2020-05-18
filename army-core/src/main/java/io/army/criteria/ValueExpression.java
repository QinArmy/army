package io.army.criteria;

public interface ValueExpression<E> extends Expression<E> {

    E value();
}
