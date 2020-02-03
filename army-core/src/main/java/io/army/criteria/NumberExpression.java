package io.army.criteria;

public interface NumberExpression<E extends Number> extends Expression<E> {

    NumberExpression<E> mod(NumberExpression<E> operator);

    NumberExpression<E> mod(E e);

    NumberExpression<E> multiply(NumberExpression<E> multiplicand);

    NumberExpression<E> multiply(E e);

    NumberExpression<E> add(NumberExpression<E> augend);

    NumberExpression<E> add(E e);

    NumberExpression<E> subtract(NumberExpression<E> subtrahend);

    NumberExpression<E> subtract(E e);

    NumberExpression<E> divide(NumberExpression<E> divisor);

    NumberExpression<E> divide(E e);

    NumberExpression<E> negate();

}
