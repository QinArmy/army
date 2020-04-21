package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingType;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.Function;

abstract class AbstractNoNOperationExpression<E> implements Expression<E> {

    final String ERROR_MSG = "Non Expression not support this method.";

    AbstractNoNOperationExpression() {
    }


    @Override
    public final IPredicate eq(Expression<E> expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final IPredicate eq(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate eq(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate eq(E constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }


    @Override
    public final <C, S extends Expression<E>> IPredicate eq(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate eqSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate eqAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate eqAll(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lt(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lt(Comparable<E> constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lt(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lt(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate lt(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate ltAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate ltSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate ltAll(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate le(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate le(Comparable<E> constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate le(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate le(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate le(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate leAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate leSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate leAll(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate gt(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate gt(Comparable<E> constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate gt(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate gt(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate gt(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate gtAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate gtSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate gtAll(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate ge(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate ge(Comparable<E> constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate ge(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate ge(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate ge(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate geAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate geSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate geAll(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notEq(Expression<E> expression) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notEq(Comparable<E> constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notEq(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notEq(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate notEq(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqAll(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate not() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate between(Expression<E> first, Expression<E> second) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate between(E first, E second) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate between(Expression<E> first, E second) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate between(E first, Expression<E> second) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C> IPredicate between(Function<C, BetweenExp<E>> function) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate isNull() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate isNotNull() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate in(Collection<E> values) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate in(Expression<Collection<E>> values) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C> IPredicate in(Function<C, ColumnSubQuery<E>> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notIn(Collection<E> values) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notIn(Expression<Collection<E>> values) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C> IPredicate notIn(Function<C, ColumnSubQuery<E>> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <N extends Number> Expression<E> mod(Expression<N> operator) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <N extends Number> Expression<E> mod(N operator) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> mod(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }


    @Override
    public <N extends Number> Expression<E> mod(String tableAlias, FieldMeta<?, N> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> mod(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <N extends Number> Expression<E> multiply(Expression<N> multiplicand) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <N extends Number> Expression<E> multiply(N multiplicand) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> multiply(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> multiply(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> multiply(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <N extends Number> Expression<E> add(Expression<N> augend) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <N extends Number> Expression<E> add(N augend) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> add(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> add(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> add(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <N extends Number> Expression<E> subtract(Expression<N> subtrahend) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <N extends Number> Expression<E> subtract(N subtrahend) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> subtract(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> subtract(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> subtract(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <N extends Number> Expression<E> divide(Expression<N> divisor) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <N extends Number> Expression<E> divide(N divisor) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> divide(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> divide(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> divide(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> negate() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> and(Expression<O> operator) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> and(Long operator) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> and(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> and(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> and(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> or(Expression<O> operator) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> or(Long operator) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> or(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> or(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> or(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> xor(Expression<O> operator) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> xor(Long operator) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> xor(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> xor(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> xor(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> inversion(Expression<O> operator) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> inversion(Long operator) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> inversion(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> inversion(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> inversion(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> rightShift(Integer bitNumber) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> rightShift(Expression<O> bitNumber) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> rightShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> rightShift(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> rightShift(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> leftShift(Integer bitNumber) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> leftShift(Expression<O> bitNumber) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> leftShift(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<BigInteger> leftShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> leftShift(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<E> plusOther(Expression<O> other) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> plusOther(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<E> plusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> plusOther(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<E> minusOther(Expression<O> other) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> minusOther(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> minusOther(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<E> minusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> brackets() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public SortPart asc() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public SortPart desc() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate like(String pattern) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<String>> IPredicate like(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notLike(String pattern) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate like(Expression<String> pattern) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notLike(Expression<String> pattern) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<String>> IPredicate notLike(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public Selection as(String alias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final void appendSQL(SQLContext context) {
        context.sqlBuilder()
                .append(" ");
        this.afterSpace(context);
    }

    @Override
    public void appendSortPart(SQLContext context) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    protected abstract void afterSpace(SQLContext context);

}
