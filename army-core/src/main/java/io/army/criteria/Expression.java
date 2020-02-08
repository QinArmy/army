package io.army.criteria;

import io.army.meta.mapping.MappingType;

import java.math.BigInteger;
import java.util.Collection;

/**
 * created  on 2018/10/8.
 */
public interface Expression<E> extends SelectionAble {

    IPredicate eq(Expression<E> expression);

    IPredicate eq(E constant);

    IPredicate lt(Expression<? extends Comparable<E>> expression);

    IPredicate lt(Comparable<E> constant);

    IPredicate le(Expression<? extends Comparable<E>> expression);

    IPredicate le(Comparable<E> constant);

    IPredicate gt(Expression<? extends Comparable<E>> expression);

    IPredicate gt(Comparable<E> constant);

    IPredicate ge(Expression<? extends Comparable<E>> expression);

    IPredicate ge(Comparable<E> constant);

    IPredicate notEq(Expression<E> expression);

    IPredicate notEq(Comparable<E> constant);

    IPredicate not();

    IPredicate between(Expression<E> first, Expression<E> second);

    IPredicate between(E first, E second);

    IPredicate between(Expression<E> first, E second);

    IPredicate between(E first, Expression<E> second);

    IPredicate isNull();

    IPredicate isNotNull();

    IPredicate in(Collection<E> values);

    IPredicate in(Expression<Collection<E>> values);

    IPredicate notIn(Collection<E> values);

    IPredicate notIn(Expression<Collection<E>> values);

    <N extends Number> Expression<E> mod(Expression<N> operator);

    <N extends Number> Expression<E> mod(N operator);

    <N extends Number> Expression<E> multiply(Expression<N> multiplicand);

    <N extends Number> Expression<E> multiply(N multiplicand);

    <N extends Number> Expression<E> add(Expression<N> augend);

    <N extends Number> Expression<E> add(N augend);

    <N extends Number> Expression<E> subtract(Expression<N> subtrahend);

    <N extends Number> Expression<E> subtract(N subtrahend);

    <N extends Number> Expression<E> divide(Expression<N> divisor);

    <N extends Number> Expression<E> divide(N divisor);

    Expression<E> negate();

    <O> Expression<BigInteger> and(Expression<O> operator);

    Expression<BigInteger> and(Long operator);

    <O> Expression<BigInteger> or(Expression<O> operator);

    Expression<BigInteger> or(Long operator);

    <O> Expression<BigInteger> xor(Expression<O> operator);

    Expression<BigInteger> xor(Long operator);

    <O> Expression<BigInteger> inversion(Expression<O> operator);

    Expression<BigInteger> inversion(Long operator);

    Expression<BigInteger> rightShift(Integer bitNumber);

    <O> Expression<BigInteger> rightShift(Expression<O> bitNumber);

    Expression<BigInteger> leftShift(Integer bitNumber);

    <O> Expression<BigInteger> leftShift(Expression<O> bitNumber);

    <O> Expression<E> plusOther(Expression<O> other);

    <O> Expression<E> minusOther(Expression<O> other);

    <O> Expression<O> asType(Class<O> convertType);

    <O> Expression<O> asType(Class<O> convertType, MappingType longMapping);

    Expression<E> brackets();


    IPredicate like(String pattern);

    IPredicate notLike(String pattern);


    void appendSQL(SQLContext context);

    MappingType mappingType();


}
