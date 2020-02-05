package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * created  on 2018/11/24.
 */
public abstract class AbstractExpression<E> implements Expression<E>, Selection {

    protected String alias;

    @Override
    public Selection as(String alias) {
        this.alias = alias;
        return this;
    }

    @Override
    public String alias() {
        return alias;
    }

    @Override
    public Expression<?> expression() {
        return this;
    }

    @Override
    public final Predicate eq(Expression<E> expression) {
        return new DualPredicate(this, DualOperator.EQ, expression);
    }

    @Override
    public final Predicate eq(E constant) {
        return new DualPredicate(this, DualOperator.EQ, SQLS.constant(constant, this.mappingType()));
    }

    @Override
    public final Predicate lt(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.LT, expression);
    }

    @Override
    public final Predicate lt(Comparable<E> constant) {
        return new DualPredicate(this, DualOperator.LT, SQLS.constant(constant, this.mappingType()));
    }

    @Override
    public final Predicate le(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.LE, expression);
    }

    @Override
    public final Predicate le(Comparable<E> constant) {
        return new DualPredicate(this, DualOperator.LE, SQLS.constant(constant, this.mappingType()));
    }

    @Override
    public final Predicate gt(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.GT, expression);
    }

    @Override
    public final Predicate gt(Comparable<E> constant) {
        return new DualPredicate(this, DualOperator.GT, SQLS.constant(constant, this.mappingType()));
    }

    @Override
    public final Predicate ge(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.GE, expression);
    }

    @Override
    public final Predicate ge(Comparable<E> constant) {
        return new DualPredicate(this, DualOperator.GE, SQLS.constant(constant, this.mappingType()));
    }

    @Override
    public final Predicate notEq(Expression<E> expression) {
        return new DualPredicate(this, DualOperator.NOT_EQ, expression);
    }

    @Override
    public final Predicate notEq(Comparable<E> constant) {
        return new DualPredicate(this, DualOperator.NOT_EQ, SQLS.constant(constant, this.mappingType()));
    }

    @Override
    public final Predicate not() {
        return NotPredicate.build(this);
    }

    @Override
    public final Predicate between(Expression<E> first, Expression<E> second) {
        return new BetweenPredicate(this, first, second);
    }

    @Override
    public final Predicate between(E first, E second) {
        return new BetweenPredicate(this, SQLS.constant(first, this), SQLS.constant(second, this));
    }

    @Override
    public final Predicate between(Expression<E> first, E second) {
        return new BetweenPredicate(this, first, SQLS.constant(second, this));
    }

    @Override
    public final Predicate between(E first, Expression<E> second) {
        return new BetweenPredicate(this, SQLS.constant(first, this), second);
    }

    @Override
    public final Predicate isNull() {
        return new UnaryPredicate(UnaryOperator.IS_NULL, this);
    }

    @Override
    public final Predicate isNotNull() {
        return new UnaryPredicate(UnaryOperator.IS_NOT_NULL, this);
    }

    @Override
    public final Predicate in(Collection<E> values) {
        return new InPredicate(true, this, values);
    }

    @Override
    public final Predicate in(Expression<Collection<E>> values) {
        return new InPredicate(true, this, values);
    }

    @Override
    public final Predicate notIn(Collection<E> values) {
        return new InPredicate(false, this, values);
    }

    @Override
    public final Predicate notIn(Expression<Collection<E>> values) {
        return new InPredicate(false, this, values);
    }

    @Override
    public final Predicate like(String pattern) {
        return new DualPredicate(this, DualOperator.LIKE, SQLS.constant(pattern, this.mappingType()));
    }

    @Override
    public final Predicate notLike(String pattern) {
        return new DualPredicate(this, DualOperator.NOT_LIKE, SQLS.constant(pattern));
    }

    @Override
    public final  <N extends Number> Expression<N> mod(Expression<N> operator) {
        return new DualExpresion<>(this, DualOperator.MOD, operator);
    }

    @Override
    public final  <N extends Number> Expression<N> mod(N operator) {
        return new DualExpresion<>(this, DualOperator.MOD, SQLS.constant(operator));
    }

    @Override
    public final  <N extends Number> Expression<N> multiply(Expression<N> multiplicand) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, multiplicand);
    }

    @Override
    public final  <N extends Number> Expression<N> multiply(N multiplicand) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, SQLS.constant(multiplicand));
    }

    @Override
    public final  <N extends Number> Expression<N> add(Expression<N> augend) {
        return new DualExpresion<>(this, DualOperator.ADD, augend);
    }

    @Override
    public final  <N extends Number> Expression<N> add(N augend) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.constant(augend));
    }

    @Override
    public final  <N extends Number> Expression<N> subtract(Expression<N> subtrahend) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, subtrahend);
    }

    @Override
    public final  <N extends Number> Expression<N> subtract(N subtrahend) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.constant(subtrahend));
    }

    @Override
    public final  <N extends Number> Expression<N> divide(Expression<N> divisor) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, divisor);
    }

    @Override
    public final  <N extends Number> Expression<N> divide(N divisor) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, SQLS.constant(divisor));
    }

    @Override
    public final  <N extends Number> Expression<N> negate() {
        return new UnaryExpression<>(this,UnaryOperator.NEGATED);
    }

    @Override
    public final  <N extends Number> Expression<N> and(Expression<N> operator) {
        return new DualExpresion<>(this, DualOperator.AND, operator);
    }

    @Override
    public final  <N extends Number> Expression<N> and(Long operator) {
        return new DualExpresion<>(this, DualOperator.AND, SQLS.constant(operator));
    }

    @Override
    public final  <N extends Number> Expression<N> or(Expression<N> operator) {
        return new DualExpresion<>(this, DualOperator.OR, operator);
    }

    @Override
    public final  <N extends Number> Expression<N> or(Long operator) {
        return new DualExpresion<>(this, DualOperator.OR, SQLS.constant(operator));
    }

    @Override
    public final  <N extends Number> Expression<N> xor(Expression<N> operator) {
        return new DualExpresion<>(this, DualOperator.XOR, operator);
    }

    @Override
    public final  <N extends Number> Expression<N> xor(Long operator) {
        return new DualExpresion<>(this, DualOperator.XOR, SQLS.constant(operator));
    }

    @Override
    public final  <N extends Number> Expression<N> inversion(Expression<N> operator) {
        return new DualExpresion<>(this, DualOperator.INVERT, operator);
    }

    @Override
    public final  <N extends Number> Expression<N> inversion(Long operator) {
        return new DualExpresion<>(this, DualOperator.INVERT, SQLS.constant(operator));
    }

    @Override
    public final  <N extends Number> Expression<N> rightShift(Integer bitNumber) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, SQLS.constant(bitNumber));
    }

    @Override
    public final  <N extends Number> Expression<N> rightShift(Expression<N> bitNumber) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, bitNumber);
    }

    @Override
    public final   <N extends Number> Expression<N> leftShift(Integer bitNumber) {
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, SQLS.constant(bitNumber));
    }

    @Override
    public final  <N extends Number> Expression<N> leftShift(Expression<N> bitNumber) {
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, bitNumber);
    }

    @Override
    public final <O> Expression<E> plusOther(Expression<O> other) {
        return new DualExpresion<>(this, DualOperator.ADD, other);
    }

    @Override
    public final <O> Expression<E> minusOther(Expression<O> other) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, other);
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType) {
        MappingType targetType = MappingFactory.getDefaultMapping(convertType);
        return new ConvertExpressionImpl<>(this, targetType);
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        return new ConvertExpressionImpl<>(this, longMapping);
    }

    @Override
    public final Predicate all(SubQuery<E> subQuery) {
        return null;
    }

    @Override
    public final Predicate any(SubQuery<E> subQuery) {
        return null;
    }

    @Override
    public final Predicate some(SubQuery<E> subQuery) {
        return null;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        List<ParamWrapper> paramWrapperList = new ArrayList<>();
        appendSQL(builder, paramWrapperList);
        return builder.toString();
    }
}
