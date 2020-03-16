package io.army.criteria;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingType;

import java.math.BigInteger;
import java.util.Collection;

/**
 * created  on 2018/10/8.
 */
public interface Expression<E> extends SelectionAble, SelfDescribed, MappingTypeAble {

    IPredicate eq(Expression<E> expression);

    IPredicate eq(String subQueryAlias, String fieldAlias);

    IPredicate eq(String tableAlias, FieldMeta<?,E> fieldMeta);

    IPredicate eq(E constant);

    IPredicate eq(KeyOperator keyOperator, ColumnSubQuery<E> subQuery);

    IPredicate lt(Expression<? extends Comparable<E>> expression);

    IPredicate lt(Comparable<E> constant);

    IPredicate lt(String subQueryAlias, String fieldAlias);

    IPredicate lt(String tableAlias, FieldMeta<?,E> fieldMeta);

    IPredicate lt(KeyOperator keyOperator, ColumnSubQuery<E> subQuery);

    IPredicate le(Expression<? extends Comparable<E>> expression);

    IPredicate le(Comparable<E> constant);

    IPredicate le(String subQueryAlias, String fieldAlias);

    IPredicate le(String tableAlias, FieldMeta<?,E> fieldMeta);

    IPredicate le(KeyOperator keyOperator, ColumnSubQuery<E> subQuery);

    IPredicate gt(Expression<? extends Comparable<E>> expression);

    IPredicate gt(Comparable<E> constant);

    IPredicate gt(String subQueryAlias, String fieldAlias);

    IPredicate gt(String tableAlias, FieldMeta<?,E> fieldMeta);

    IPredicate gt(KeyOperator keyOperator, ColumnSubQuery<E> subQuery);

    IPredicate ge(Expression<? extends Comparable<E>> expression);

    IPredicate ge(Comparable<E> constant);

    IPredicate ge(String subQueryAlias, String fieldAlias);

    IPredicate ge(String tableAlias, FieldMeta<?,E> fieldMeta);

    IPredicate ge(KeyOperator keyOperator, ColumnSubQuery<E> subQuery);

    IPredicate notEq(Expression<E> expression);

    IPredicate notEq(Comparable<E> constant);

    IPredicate notEq(String subQueryAlias, String fieldAlias);

    IPredicate notEq(String tableAlias, FieldMeta<?,E> fieldMeta);

    IPredicate notEq(KeyOperator keyOperator, ColumnSubQuery<E> subQuery);

    IPredicate not();

    IPredicate between(Expression<E> first, Expression<E> second);

    IPredicate between(E first, E second);

    IPredicate between(Expression<E> first, E second);

    IPredicate between(E first, Expression<E> second);

    IPredicate between(String subQueryAlias, String firstDerivedFieldName, String secondDerivedFieldName);

    IPredicate between(String subQueryAlias, String firstDerivedFieldName, Expression<E> second);

    IPredicate between(String subQueryAlias, String fieldAlias, E second);

    IPredicate between(Expression<E> first, String subQueryAlias, String fieldAlias);

    IPredicate between(E first, String subQueryAlias, String fieldAlias);

    IPredicate isNull();

    IPredicate isNotNull();

    IPredicate in(Collection<E> values);

    IPredicate in(Expression<Collection<E>> values);

    IPredicate in(ColumnSubQuery<E> subQuery);

    IPredicate notIn(Collection<E> values);

    IPredicate notIn(Expression<Collection<E>> values);

    IPredicate notIn(ColumnSubQuery<E> subQuery);

    <N extends Number> Expression<E> mod(Expression<N> operator);

    <N extends Number> Expression<E> mod(N operator);

    Expression<E> mod(String subQueryAlias, String fieldAlias);

    Expression<E> mod(String tableAlias, FieldMeta<?,E> fieldMeta);

    <N extends Number> Expression<E> multiply(Expression<N> multiplicand);

    <N extends Number> Expression<E> multiply(N multiplicand);

    Expression<E> multiply(String subQueryAlias, String fieldAlias);

    Expression<E> multiply(String tableAlias, FieldMeta<?,E> fieldMeta);

    <N extends Number> Expression<E> add(Expression<N> augend);

    <N extends Number> Expression<E> add(N augend);

    Expression<E> add(String subQueryAlias, String fieldAlias);

    Expression<E> add(String tableAlias, FieldMeta<?,E> fieldMeta);

    <N extends Number> Expression<E> subtract(Expression<N> subtrahend);

    <N extends Number> Expression<E> subtract(N subtrahend);

    Expression<E> subtract(String subQueryAlias, String fieldAlias);

    Expression<E> subtract(String tableAlias, FieldMeta<?,E> fieldMeta);

    <N extends Number> Expression<E> divide(Expression<N> divisor);

    <N extends Number> Expression<E> divide(N divisor);

    Expression<E> divide(String subQueryAlias, String fieldAlias);

    Expression<E> divide(String tableAlias, FieldMeta<?,E> fieldMeta);

    Expression<E> negate();

    <O> Expression<BigInteger> and(Expression<O> operator);

    Expression<BigInteger> and(Long operator);

    Expression<BigInteger> and(String subQueryAlias, String fieldAlias);

    <O> Expression<BigInteger> and(String tableAlias, FieldMeta<?,O> fieldMeta);

    <O> Expression<BigInteger> or(Expression<O> operator);

    Expression<BigInteger> or(Long operator);

    Expression<BigInteger> or(String subQueryAlias, String fieldAlias);

    <O> Expression<BigInteger> or(String tableAlias, FieldMeta<?,O> fieldMeta);

    <O> Expression<BigInteger> xor(Expression<O> operator);

    Expression<BigInteger> xor(Long operator);

    Expression<BigInteger> xor(String subQueryAlias, String fieldAlias);

    <O> Expression<BigInteger> xor(String tableAlias, FieldMeta<?,O> fieldMeta);

    <O> Expression<BigInteger> inversion(Expression<O> operator);

    Expression<BigInteger> inversion(Long operator);

    Expression<BigInteger> inversion(String subQueryAlias, String fieldAlias);

    <O> Expression<BigInteger> inversion(String tableAlias, FieldMeta<?,O> fieldMeta);

    Expression<BigInteger> rightShift(Integer bitNumber);

    <O> Expression<BigInteger> rightShift(Expression<O> bitNumber);

    <O> Expression<BigInteger> rightShift(String tableAlias, FieldMeta<?,O> fieldMeta);

    Expression<BigInteger> rightShift(String subQueryAlias, String fieldAlias);

    Expression<BigInteger> leftShift(Integer bitNumber);

    <O> Expression<BigInteger> leftShift(Expression<O> bitNumber);

    Expression<BigInteger> leftShift(String subQueryAlias, String fieldAlias);

    <O> Expression<BigInteger> leftShift(String tableAlias, FieldMeta<?,O> fieldMeta);

    <O> Expression<E> plusOther(Expression<O> other);

    Expression<E> plusOther(String subQueryAlias, String fieldAlias);

    <O> Expression<E> plusOther(String tableAlias, FieldMeta<?,O> fieldMeta);

    <O> Expression<E> minusOther(Expression<O> other);

    Expression<E> minusOther(String subQueryAlias, String fieldAlias);

    <O> Expression<E> minusOther(String tableAlias, FieldMeta<?,O> fieldMeta);

    <O> Expression<O> asType(Class<O> convertType);

    <O> Expression<O> asType(Class<O> convertType, MappingType longMapping);

    Expression<E> brackets();

    @Nullable
    default Boolean sortExp() {
        return null;
    }

    Expression<E> sort(@Nullable Boolean asc);

    IPredicate like(String pattern);

    IPredicate notLike(String pattern);

    IPredicate like(Expression<String> pattern);

    IPredicate notLike(Expression<String> pattern);

}
