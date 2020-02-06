package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.ParamExpression;
import io.army.criteria.Predicate;
import io.army.criteria.SubQuery;
import io.army.dialect.ParamWrapper;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
    public Predicate eq(Expression<E> expression) {
        throw createUnsupported();
    }

    @Override
    public Predicate eq(E constant) {
        throw createUnsupported();
    }

    @Override
    public Predicate lt(Expression<? extends Comparable<E>> expression) {
        throw createUnsupported();
    }

    @Override
    public Predicate lt(Comparable<E> constant) {
        throw createUnsupported();
    }

    @Override
    public Predicate le(Expression<? extends Comparable<E>> expression) {
        throw createUnsupported();
    }

    @Override
    public Predicate le(Comparable<E> constant) {
        throw createUnsupported();
    }

    @Override
    public Predicate gt(Expression<? extends Comparable<E>> expression) {
        throw createUnsupported();
    }

    @Override
    public Predicate gt(Comparable<E> constant) {
        throw createUnsupported();
    }

    @Override
    public Predicate ge(Expression<? extends Comparable<E>> expression) {
        throw createUnsupported();
    }

    @Override
    public Predicate ge(Comparable<E> constant) {
        throw createUnsupported();
    }

    @Override
    public Predicate notEq(Expression<E> expression) {
        throw createUnsupported();
    }

    @Override
    public Predicate notEq(Comparable<E> constant) {
        throw createUnsupported();
    }

    @Override
    public Predicate not() {
        throw createUnsupported();
    }

    @Override
    public Predicate between(Expression<E> first, Expression<E> second) {
        throw createUnsupported();
    }

    @Override
    public Predicate between(E first, E second) {
        throw createUnsupported();
    }

    @Override
    public Predicate between(Expression<E> first, E second) {
        throw createUnsupported();
    }

    @Override
    public Predicate between(E first, Expression<E> second) {
        throw createUnsupported();
    }

    @Override
    public Predicate isNull() {
        throw createUnsupported();
    }

    @Override
    public Predicate isNotNull() {
        throw createUnsupported();
    }

    @Override
    public Predicate in(Collection<E> values) {
        throw createUnsupported();
    }

    @Override
    public Predicate in(Expression<Collection<E>> values) {
        throw createUnsupported();
    }

    @Override
    public Predicate notIn(Collection<E> values) {
        throw createUnsupported();
    }

    @Override
    public Predicate notIn(Expression<Collection<E>> values) {
        throw createUnsupported();
    }

    @Override
    public Predicate like(String pattern) {
        throw createUnsupported();
    }

    @Override
    public Predicate notLike(String pattern) {
        throw createUnsupported();
    }

    @Override
    public Predicate all(SubQuery<E> subQuery) {
        throw createUnsupported();
    }

    @Override
    public Predicate any(SubQuery<E> subQuery) {
        throw createUnsupported();
    }

    @Override
    public Predicate some(SubQuery<E> subQuery) {
        throw createUnsupported();
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
    public void appendSQL(StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        builder.append("?");
        paramWrapperList.add(ParamWrapper.build(mappingType, value));
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        List<ParamWrapper> paramWrapperList = new ArrayList<>();
        appendSQL(builder, paramWrapperList);
        return builder.toString();
    }


     static UnsupportedOperationException createUnsupported() {
        return new UnsupportedOperationException("operation supported by ParamExpressionImp ");
    }
}
