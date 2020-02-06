package io.army.criteria;

import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * created  on 2018/10/8.
 */
public interface Expression<E> extends SelectAble {

    Predicate eq(Expression<E> expression);

    Predicate eq(E constant);

    Predicate lt(Expression<? extends Comparable<E>> expression);

    Predicate lt(Comparable<E> constant);

    Predicate le(Expression<? extends Comparable<E>> expression);

    Predicate le(Comparable<E> constant);

    Predicate gt(Expression<? extends Comparable<E>> expression);

    Predicate gt(Comparable<E> constant);

    Predicate ge(Expression<? extends Comparable<E>> expression);

    Predicate ge(Comparable<E> constant);

    Predicate notEq(Expression<E> expression);

    Predicate notEq(Comparable<E> constant);

    Predicate not();

    Predicate between(Expression<E> first, Expression<E> second);

    Predicate between(E first, E second);

    Predicate between(Expression<E> first, E second);

    Predicate between(E first, Expression<E> second);

    Predicate isNull();

    Predicate isNotNull();

    Predicate in(Collection<E> values);

    Predicate in(Expression<Collection<E>> values);

    Predicate notIn(Collection<E> values);

    Predicate notIn(Expression<Collection<E>> values);

    <N extends Number> Expression<N> mod(Expression<N> operator);

    <N extends Number> Expression<N> mod(N operator);

    <N extends Number> Expression<N> multiply(Expression<N> multiplicand);

    <N extends Number> Expression<N> multiply(N multiplicand);

    <N extends Number> Expression<N> add(Expression<N> augend);

    <N extends Number> Expression<N> add(N augend);

    <N extends Number> Expression<N> subtract(Expression<N> subtrahend);

    <N extends Number> Expression<N> subtract(N subtrahend);

    <N extends Number> Expression<N> divide(Expression<N> divisor);

    <N extends Number> Expression<N> divide(N divisor);

    <N extends Number> Expression<N> negate();

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


    Predicate like(String pattern);

    Predicate notLike(String pattern);

    Predicate all(SubQuery<E> subQuery);

    Predicate any(SubQuery<E> subQuery);

    Predicate some(SubQuery<E> subQuery);

    void appendSQL(SQL sql, StringBuilder builder, List<ParamWrapper> paramWrapperList);

    MappingType mappingType();

    @Override
    String toString();


}
