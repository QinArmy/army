package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingType;

import java.math.BigInteger;
import java.util.Collection;

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
    public final IPredicate eq(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
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
    public final IPredicate lt(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
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
    public final IPredicate le(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
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
    public final IPredicate gt(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
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
    public final IPredicate ge(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
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
    public final IPredicate notEq(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
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
    public final IPredicate between(String subQueryAlias, String derivedFieldName, Expression<E> second) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate between(String subQueryAlias, String derivedFieldName, E second) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate between(String subQueryAlias1, String derivedFieldName1, String subQueryAlias2, String derivedFieldName2) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate between(Expression<E> first, String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate between(E first, String subQueryAlias, String derivedFieldName) {
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
    public final IPredicate in(ColumnSubQuery<E> subQuery) {
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
    public final IPredicate notIn(ColumnSubQuery<E> subQuery) {
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
    public final Expression<E> mod(String tableAlias, FieldMeta<?, E> fieldMeta) {
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
    public final <O> Expression<E> minusOther(Expression<O> other) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<E> minusOther(String subQueryAlias, String derivedFieldName) {
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
    public final Expression<E> sort(@Nullable Boolean asc) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate like(String pattern) {
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
    public Selection as(String alias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final void appendSQL(SQLContext context) {
        context.stringBuilder()
                .append(" ");
        this.afterSpace(context);
    }

    protected abstract void afterSpace(SQLContext context);

}
