package io.army.criteria;

import io.army.dialect.ParamWrapper;
import io.army.meta.mapping.MappingType;

import java.math.BigInteger;
import java.util.Collection;

/**
 * extends {@link ParamWrapper} to avoid new instance of {@link ParamWrapper}
 * created  on 2018/12/4.
 */
public interface ParamExpression<E> extends Expression<E>, ParamWrapper {

    String MSG = "operation isn'table supported by ParamExpression";

    E value();

    /**
     *
     */
    @Override
    void appendSQL(SQLContext context);

    @Override
    MappingType mappingType();

    @Override
    String toString();


    @Override
    default IPredicate eq(Expression<E> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate eq(E constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate lt(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate lt(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate le(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate le(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate gt(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate gt(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate ge(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate ge(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate notEq(Expression<E> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate notEq(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate not() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate between(Expression<E> first, Expression<E> second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate between(E first, E second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate between(Expression<E> first, E second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate between(E first, Expression<E> second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate isNull() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate isNotNull() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate in(Collection<E> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate in(Expression<Collection<E>> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate notIn(Collection<E> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate notIn(Expression<Collection<E>> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<E> mod(Expression<N> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<E> mod(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<E> multiply(Expression<N> multiplicand) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<E> multiply(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<E> add(Expression<N> augend) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<E> add(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<E> subtract(Expression<N> subtrahend) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<E> subtract(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<E> divide(Expression<N> divisor) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <N extends Number> Expression<E> divide(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default  Expression<E> negate() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> and(Expression<O> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<BigInteger> and(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> or(Expression<O> operator) {
        return null;
    }

    @Override
    default Expression<BigInteger> or(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> xor(Expression<O> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<BigInteger> xor(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> inversion(Expression<O> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<BigInteger> inversion(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<BigInteger> rightShift(Integer bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> rightShift(Expression<O> bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<BigInteger> leftShift(Integer bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<BigInteger> leftShift(Expression<O> bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<E> plusOther(Expression<O> other) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<E> minusOther(Expression<O> other) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<O> asType(Class<O> convertType) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Expression<E> brackets() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default Selection as(String alias) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate like(String pattern) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    default IPredicate notLike(String pattern) {
        throw new UnsupportedOperationException(MSG);
    }



}
