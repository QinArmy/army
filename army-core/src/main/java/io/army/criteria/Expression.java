package io.army.criteria;

import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingMeta;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.Function;

/**
 * created  on 2018/10/8.
 */
@SuppressWarnings("unused")
public interface Expression<E> extends SelectionAble, SelfDescribed, MappingMetaAble, SortPart {

    IPredicate eq(Expression<E> expression);

    IPredicate eq(String subQueryAlias, String fieldAlias);

    IPredicate eq(String tableAlias, FieldMeta<?, E> fieldMeta);

    IPredicate eq(E constant);

    <C, S extends Expression<E>> IPredicate eq(Function<C, S> expOrSubQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate eqAny(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate eqSome(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate eqAll(Function<C, S> subQuery);

    IPredicate lt(Expression<? extends Comparable<E>> expression);

    IPredicate lt(Comparable<E> constant);

    IPredicate lt(String subQueryAlias, String fieldAlias);

    IPredicate lt(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, S extends Expression<E>> IPredicate lt(Function<C, S> expOrSubQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate ltAny(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate ltSome(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate ltAll(Function<C, S> subQuery);

    IPredicate le(Expression<? extends Comparable<E>> expression);

    IPredicate le(Comparable<E> constant);

    IPredicate le(String subQueryAlias, String fieldAlias);

    IPredicate le(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, S extends Expression<E>> IPredicate le(Function<C, S> expOrSubQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate leAny(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate leSome(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate leAll(Function<C, S> subQuery);

    IPredicate gt(Expression<? extends Comparable<E>> expression);

    IPredicate gt(Comparable<E> constant);

    IPredicate gt(String subQueryAlias, String fieldAlias);

    IPredicate gt(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, S extends Expression<E>> IPredicate gt(Function<C, S> expOrSubQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate gtAny(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate gtSome(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate gtAll(Function<C, S> subQuery);

    IPredicate ge(Expression<? extends Comparable<E>> expression);

    IPredicate ge(Comparable<E> constant);

    IPredicate ge(String subQueryAlias, String fieldAlias);

    IPredicate ge(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, S extends Expression<E>> IPredicate ge(Function<C, S> expOrSubQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate geAny(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate geSome(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate geAll(Function<C, S> subQuery);

    IPredicate notEq(Expression<E> expression);

    IPredicate notEq(Comparable<E> constant);

    IPredicate notEq(String subQueryAlias, String fieldAlias);

    IPredicate notEq(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, S extends Expression<E>> IPredicate notEq(Function<C, S> expOrSubQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate notEqAny(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate notEqSome(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate notEqAll(Function<C, S> subQuery);

    IPredicate not();

    IPredicate between(Expression<E> first, Expression<E> second);

    IPredicate between(E first, E second);

    IPredicate between(Expression<E> first, E second);

    IPredicate between(E first, Expression<E> second);

    <C> IPredicate between(Function<C, BetweenExp<E>> function);

    IPredicate isNull();

    IPredicate isNotNull();

    IPredicate in(Collection<E> values);

    IPredicate in(Expression<Collection<E>> values);

    <C> IPredicate in(Function<C, ColumnSubQuery<E>> subQuery);

    IPredicate notIn(Collection<E> values);

    IPredicate notIn(Expression<Collection<E>> values);

    <C> IPredicate notIn(Function<C, ColumnSubQuery<E>> subQuery);

    <N extends Number> Expression<E> mod(Expression<N> operator);

    <N extends Number> Expression<E> mod(N operator);

    Expression<E> mod(String subQueryAlias, String derivedFieldName);

    <N extends Number> Expression<E> mod(String tableAlias, FieldMeta<?, N> fieldMeta);

    <C, N extends Number, S extends Expression<N>> Expression<E> mod(Function<C, S> expOrSubQuery);

    <N extends Number> Expression<E> multiply(Expression<N> multiplicand);

    <N extends Number> Expression<E> multiply(N multiplicand);

    Expression<E> multiply(String subQueryAlias, String derivedFieldName);

    Expression<E> multiply(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, N extends Number, S extends Expression<N>> Expression<E> multiply(Function<C, S> expOrSubQuery);

    <N extends Number> Expression<E> add(Expression<N> augend);

    <N extends Number> Expression<E> add(N augend);

    Expression<E> add(String subQueryAlias, String derivedFieldName);

    Expression<E> add(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, N extends Number, S extends Expression<N>> Expression<E> add(Function<C, S> expOrSubQuery);

    <N extends Number> Expression<E> subtract(Expression<N> subtrahend);

    <N extends Number> Expression<E> subtract(N subtrahend);

    Expression<E> subtract(String subQueryAlias, String derivedFieldName);

    Expression<E> subtract(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, N extends Number, S extends Expression<N>> Expression<E> subtract(Function<C, S> expOrSubQuery);

    <N extends Number> Expression<E> divide(Expression<N> divisor);

    <N extends Number> Expression<E> divide(N divisor);

    Expression<E> divide(String subQueryAlias, String derivedFieldName);

    Expression<E> divide(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, N extends Number, S extends Expression<N>> Expression<E> divide(Function<C, S> expOrSubQuery);

    Expression<E> negate();

    <O> Expression<BigInteger> and(Expression<O> operator);

    Expression<BigInteger> and(Long operator);

    Expression<BigInteger> and(String subQueryAlias, String derivedFieldName);

    <O> Expression<BigInteger> and(String tableAlias, FieldMeta<?, O> fieldMeta);

    <C, O, S extends Expression<O>> Expression<E> and(Function<C, S> expOrSubQuery);

    <O> Expression<BigInteger> or(Expression<O> operator);

    Expression<BigInteger> or(Long operator);

    Expression<BigInteger> or(String subQueryAlias, String derivedFieldName);

    <O> Expression<BigInteger> or(String tableAlias, FieldMeta<?, O> fieldMeta);

    <C, O, S extends Expression<O>> Expression<E> or(Function<C, S> expOrSubQuery);

    <O> Expression<BigInteger> xor(Expression<O> operator);

    Expression<BigInteger> xor(Long operator);

    Expression<BigInteger> xor(String subQueryAlias, String derivedFieldName);

    <O> Expression<BigInteger> xor(String tableAlias, FieldMeta<?, O> fieldMeta);

    <C, O, S extends Expression<O>> Expression<E> xor(Function<C, S> expOrSubQuery);

    <O> Expression<BigInteger> inversion(Expression<O> operator);

    Expression<BigInteger> inversion(Long operator);

    Expression<BigInteger> inversion(String subQueryAlias, String derivedFieldName);

    <O> Expression<BigInteger> inversion(String tableAlias, FieldMeta<?, O> fieldMeta);

    <C, O, S extends Expression<O>> Expression<E> inversion(Function<C, S> expOrSubQuery);

    Expression<BigInteger> rightShift(Integer bitNumber);

    <O> Expression<BigInteger> rightShift(Expression<O> bitNumber);

    <O> Expression<BigInteger> rightShift(String tableAlias, FieldMeta<?, O> fieldMeta);

    Expression<BigInteger> rightShift(String subQueryAlias, String derivedFieldName);

    <C, O, S extends Expression<O>> Expression<E> rightShift(Function<C, S> expOrSubQuery);

    Expression<BigInteger> leftShift(Integer bitNumber);

    <O> Expression<BigInteger> leftShift(Expression<O> bitNumber);

    Expression<BigInteger> leftShift(String subQueryAlias, String derivedFieldName);

    <O> Expression<BigInteger> leftShift(String tableAlias, FieldMeta<?, O> fieldMeta);

    <C, O, S extends Expression<O>> Expression<E> leftShift(Function<C, S> expOrSubQuery);

    <O> Expression<E> plusOther(Expression<O> other);

    Expression<E> plusOther(String subQueryAlias, String derivedFieldName);

    <O> Expression<E> plusOther(String tableAlias, FieldMeta<?, O> fieldMeta);

    <C, O, S extends Expression<O>> Expression<E> plusOther(Function<C, S> expOrSubQuery);

    <O> Expression<E> minusOther(Expression<O> other);

    Expression<E> minusOther(String subQueryAlias, String derivedFieldName);

    <O> Expression<E> minusOther(String tableAlias, FieldMeta<?, O> fieldMeta);

    <C, O, S extends Expression<O>> Expression<E> minusOther(Function<C, S> expOrSubQuery);

    <O> Expression<O> asType(Class<O> convertType);

    <O> Expression<O> asType(Class<O> convertType, MappingMeta longMapping);

    Expression<E> brackets();

    SortPart asc();

    SortPart desc();

    IPredicate like(String pattern);

    <C, S extends Expression<String>> IPredicate like(Function<C, S> expOrSubQuery);

    IPredicate notLike(String pattern);

    IPredicate like(Expression<String> pattern);

    IPredicate notLike(Expression<String> pattern);

    <C, S extends Expression<String>> IPredicate notLike(Function<C, S> expOrSubQuery);

}
