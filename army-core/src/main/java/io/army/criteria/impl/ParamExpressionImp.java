package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.ParamWrapper;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

import java.math.BigInteger;
import java.util.Collection;

final class ParamExpressionImp<E> implements ParamExpression<E> {

    static <E> ParamExpressionImp<E> build(@Nullable MappingType mappingType, @Nullable E value) {
        ParamExpressionImp<E> paramExpression;
        if (mappingType != null && value != null) {
            paramExpression = new ParamExpressionImp<>(mappingType, value);
        } else if (mappingType != null) {
            paramExpression = new ParamExpressionImp<>(mappingType);
        } else if (value != null) {
            paramExpression = new ParamExpressionImp<>(value);
        } else {
            throw new IllegalArgumentException("mappingType then value all is null");
        }
        return paramExpression;
    }

    private final MappingType mappingType;

    private final E value;

    private ParamExpressionImp(MappingType mappingType, E value) {
        this.mappingType = mappingType;
        this.value = value;
    }

    private ParamExpressionImp(MappingType mappingType) {
        Assert.notNull(mappingType, "");
        this.mappingType = mappingType;
        this.value = null;
    }

    private ParamExpressionImp(E value) {
        Assert.notNull(value, "");
        this.value = value;
        this.mappingType = MappingFactory.getDefaultMapping(this.value.getClass());
    }


    @Override
    public E value() {
        return value;
    }

    @Override
    public MappingType mappingType() {
        return mappingType;
    }

    @Override
    public void appendSQL(SQLContext context) {
        context.stringBuilder().append(" ?");
        context.appendParam(ParamWrapper.build(mappingType, value));
    }

    @Override
    public String toString() {
        return "?";
    }


    @Override
    public IPredicate eq(Expression<E> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate eq(E constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate eq(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate eq(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate eq(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate lt(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate lt(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate lt(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate lt(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate lt(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate le(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate le(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate le(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate le(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate le(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate gt(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate gt(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate gt(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate gt(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate gt(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate ge(Expression<? extends Comparable<E>> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate ge(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate ge(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate ge(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate ge(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate notEq(Expression<E> expression) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate notEq(Comparable<E> constant) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate notEq(String subQueryAlias, String fieldAlias) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate notEq(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate notEq(KeyOperator keyOperator, ColumnSubQuery<E> subQuery) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate not() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate between(Expression<E> first, Expression<E> second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate between(E first, E second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate between(Expression<E> first, E second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate between(E first, Expression<E> second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate between(String subQueryAlias1, String derivedFieldName1
            , String subQueryAlias2, String derivedFieldName2) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate between(String subQueryAlias, String derivedFieldName, Expression<E> second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate between(String subQueryAlias, String derivedFieldName, E second) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate between(Expression<E> first, String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate between(E first, String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate isNull() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate isNotNull() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate in(Collection<E> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate in(Expression<Collection<E>> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate in(ColumnSubQuery<E> subQuery) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate notIn(Collection<E> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate notIn(Expression<Collection<E>> values) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate notIn(ColumnSubQuery<E> subQuery) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <N extends Number> Expression<E> mod(Expression<N> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <N extends Number> Expression<E> mod(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> mod(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> mod(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <N extends Number> Expression<E> multiply(Expression<N> multiplicand) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <N extends Number> Expression<E> multiply(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> multiply(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> multiply(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <N extends Number> Expression<E> add(Expression<N> augend) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <N extends Number> Expression<E> add(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> add(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> add(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <N extends Number> Expression<E> subtract(Expression<N> subtrahend) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <N extends Number> Expression<E> subtract(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> subtract(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> subtract(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <N extends Number> Expression<E> divide(Expression<N> divisor) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <N extends Number> Expression<E> divide(N e) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> divide(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> divide(String tableAlias, FieldMeta<?, E> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> negate() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> and(Expression<O> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<BigInteger> and(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<BigInteger> and(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> and(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> or(Expression<O> operator) {
        return null;
    }

    @Override
    public Expression<BigInteger> or(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<BigInteger> or(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> or(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> xor(Expression<O> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<BigInteger> xor(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<BigInteger> xor(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> xor(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> inversion(Expression<O> operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<BigInteger> inversion(Long operator) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<BigInteger> inversion(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> inversion(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<BigInteger> rightShift(Integer bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> rightShift(Expression<O> bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> rightShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<BigInteger> rightShift(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<BigInteger> leftShift(Integer bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> leftShift(Expression<O> bitNumber) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<BigInteger> leftShift(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<BigInteger> leftShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<E> plusOther(Expression<O> other) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> plusOther(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<E> plusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<E> minusOther(Expression<O> other) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> minusOther(String subQueryAlias, String derivedFieldName) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<E> minusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<O> asType(Class<O> convertType) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> brackets() {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Expression<E> sort(@Nullable Boolean asc) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Selection as(String alias) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate like(String pattern) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate like(Expression<String> pattern) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public IPredicate notLike(String pattern) {
        throw new UnsupportedOperationException(MSG);
    }


    @Override
    public IPredicate notLike(Expression<String> pattern) {
        throw new UnsupportedOperationException(MSG);
    }

    @Override
    public Boolean sortExp() {
        throw new UnsupportedOperationException(MSG);
    }

}
