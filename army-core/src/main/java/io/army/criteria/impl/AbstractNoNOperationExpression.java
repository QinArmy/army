package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.Function;

abstract class AbstractNoNOperationExpression<E> implements _Expression<E> {

    final String ERROR_MSG = "Non Expression not support this method.";

    AbstractNoNOperationExpression() {
    }


    @Override
    public final IPredicate equal(Expression<E> expression) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final IPredicate equal(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate equal(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate equal(E constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Nullable
    @Override
    public final IPredicate equalIfNonNull(@Nullable E constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate equal(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate equalSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate equalAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate equalAll(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lessThan(Expression<E> expression) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lessThan(E constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lessThan(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lessThan(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate lessThan(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessThanAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessThanSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessThanAll(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lessEqual(Expression<E> expression) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lessEqual(E constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lessEqual(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate lessEqual(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate lessEqual(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessEqualAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessEqualSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessEqualAll(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate greatThan(Expression<E> expression) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate greatThan(E constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate greatThan(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate greatThan(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate greatThan(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatThanAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatThanSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatThanAll(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate greatEqual(Expression<E> expression) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate greatEqual(E constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate greatEqual(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate greatEqual(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate greatEqual(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatEqualAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatEqualSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatEqualAll(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notEqual(Expression<E> expression) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notEqual(E constant) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notEqual(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final IPredicate notEqual(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate notEqual(Function<C, S> expOrSubQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqualAny(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqualSome(Function<C, S> subQuery) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqualAll(Function<C, S> subQuery) {
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
    public final <C> IPredicate between(Function<C, BetweenWrapper<E>> function) {
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
    public final <N extends Number> Expression<E> mod(String tableAlias, FieldMeta<?, N> fieldMeta) {
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
    public final <O> Expression<BigInteger> and(Expression<O> operand) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> and(Long operand) {
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
    public final <O> Expression<BigInteger> or(Expression<O> operand) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> or(Long operand) {
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
    public final <O> Expression<BigInteger> xor(Expression<O> operand) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> xor(Long operand) {
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
    public final <O> Expression<BigInteger> inversion(Expression<O> operand) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final Expression<BigInteger> inversion(Long operand) {
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
    public final SortPart asc() {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final SortPart desc() {
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
    public final boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        return false;
    }

    @Override
    public final boolean containsFieldOf(TableMeta<?> tableMeta) {
        return false;
    }

    @Override
    public final Selection as(String alias) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Override
    public final boolean containsSubQuery() {
        return false;
    }

    @Override
    public final int containsFieldCount(TableMeta<?> tableMeta) {
        return 0;
    }

    @Override
    public final void appendSql(_SqlContext context) {
        context.sqlBuilder()
                .append(" ");
        this.afterSpace(context);
    }

    @Override
    public void appendSortPart(_SqlContext context) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    protected abstract void afterSpace(_SqlContext context);

}
