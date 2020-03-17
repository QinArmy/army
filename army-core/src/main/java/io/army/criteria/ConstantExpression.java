package io.army.criteria;

public interface ConstantExpression<E> extends Expression<E> {

    E constant();
}
