package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect.ParamWrapper;
import io.army.lang.Nullable;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

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
    public IPredicate eq(Expression<E> expression) {
        throw createUnsupported();
    }

    @Override
    public IPredicate eq(E constant) {
        throw createUnsupported();
    }

    @Override
    public IPredicate lt(Expression<? extends Comparable<E>> expression) {
        throw createUnsupported();
    }

    @Override
    public IPredicate lt(Comparable<E> constant) {
        throw createUnsupported();
    }

    @Override
    public IPredicate le(Expression<? extends Comparable<E>> expression) {
        throw createUnsupported();
    }

    @Override
    public IPredicate le(Comparable<E> constant) {
        throw createUnsupported();
    }

    @Override
    public IPredicate gt(Expression<? extends Comparable<E>> expression) {
        throw createUnsupported();
    }

    @Override
    public IPredicate gt(Comparable<E> constant) {
        throw createUnsupported();
    }

    @Override
    public IPredicate ge(Expression<? extends Comparable<E>> expression) {
        throw createUnsupported();
    }

    @Override
    public IPredicate ge(Comparable<E> constant) {
        throw createUnsupported();
    }

    @Override
    public IPredicate notEq(Expression<E> expression) {
        throw createUnsupported();
    }

    @Override
    public IPredicate notEq(Comparable<E> constant) {
        throw createUnsupported();
    }

    @Override
    public IPredicate not() {
        throw createUnsupported();
    }

    @Override
    public IPredicate between(Expression<E> first, Expression<E> second) {
        throw createUnsupported();
    }

    @Override
    public IPredicate between(E first, E second) {
        throw createUnsupported();
    }

    @Override
    public IPredicate between(Expression<E> first, E second) {
        throw createUnsupported();
    }

    @Override
    public IPredicate between(E first, Expression<E> second) {
        throw createUnsupported();
    }

    @Override
    public IPredicate isNull() {
        throw createUnsupported();
    }

    @Override
    public IPredicate isNotNull() {
        throw createUnsupported();
    }

    @Override
    public IPredicate in(Collection<E> values) {
        throw createUnsupported();
    }

    @Override
    public IPredicate in(Expression<Collection<E>> values) {
        throw createUnsupported();
    }

    @Override
    public IPredicate notIn(Collection<E> values) {
        throw createUnsupported();
    }

    @Override
    public IPredicate notIn(Expression<Collection<E>> values) {
        throw createUnsupported();
    }

    @Override
    public IPredicate like(String pattern) {
        throw createUnsupported();
    }

    @Override
    public IPredicate notLike(String pattern) {
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
    public void appendSQL(SQLContext context) {
        context.stringBuilder().append(" ?");
        context.appendParam(ParamWrapper.build(mappingType, value));
    }

    @Override
    public String toString() {
        return "?";
    }


     static UnsupportedOperationException createUnsupported() {
        return new UnsupportedOperationException("operation supported by ParamExpressionImp ");
    }
}
