package io.army.criteria;


public interface ConvertExpression<E> extends Expression<E> {

    Expression<?> originalExp();

}
