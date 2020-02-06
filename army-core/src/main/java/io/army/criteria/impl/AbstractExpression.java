package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.dialect.ParamWrapper;
import io.army.dialect.SQL;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

/**
 * created  on 2018/11/24.
 */
 abstract class AbstractExpression<E> implements Expression<E>, Selection {

    protected String alias;

     AbstractExpression() {
    }


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
    public final Expression<?> expression() {
        return this;
    }

    @Override
    public final Predicate eq(Expression<E> expression) {
        return new DualPredicateImpl(this, DualOperator.EQ, expression);
    }

    @Override
    public final Predicate eq(E constant) {
        return new DualPredicateImpl(this, DualOperator.EQ, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final Predicate lt(Expression<? extends Comparable<E>> expression) {
        return new DualPredicateImpl(this, DualOperator.LT, expression);
    }

    @Override
    public final Predicate lt(Comparable<E> constant) {
        return new DualPredicateImpl(this, DualOperator.LT, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final Predicate le(Expression<? extends Comparable<E>> expression) {
        return new DualPredicateImpl(this, DualOperator.LE, expression);
    }

    @Override
    public final Predicate le(Comparable<E> constant) {
        return new DualPredicateImpl(this, DualOperator.LE, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final Predicate gt(Expression<? extends Comparable<E>> expression) {
        return new DualPredicateImpl(this, DualOperator.GT, expression);
    }

    @Override
    public final Predicate gt(Comparable<E> constant) {
        return new DualPredicateImpl(this, DualOperator.GT, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final Predicate ge(Expression<? extends Comparable<E>> expression) {
        return new DualPredicateImpl(this, DualOperator.GE, expression);
    }

    @Override
    public final Predicate ge(Comparable<E> constant) {
        return new DualPredicateImpl(this, DualOperator.GE, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final Predicate notEq(Expression<E> expression) {
        return new DualPredicateImpl(this, DualOperator.NOT_EQ, expression);
    }

    @Override
    public final Predicate notEq(Comparable<E> constant) {
        return new DualPredicateImpl(this, DualOperator.NOT_EQ, SQLS.param(constant, this.mappingType()));
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
        return new BetweenPredicate(this, SQLS.param(first, this), SQLS.param(second, this));
    }

    @Override
    public final Predicate between(Expression<E> first, E second) {
        return new BetweenPredicate(this, first, SQLS.param(second, this.mappingType()));
    }

    @Override
    public final Predicate between(E first, Expression<E> second) {
        return new BetweenPredicate(this, SQLS.param(first, this), second);
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
        return new DualPredicateImpl(this, DualOperator.LIKE, SQLS.constant(pattern, this.mappingType()));
    }

    @Override
    public final Predicate notLike(String pattern) {
        return new DualPredicateImpl(this, DualOperator.NOT_LIKE, SQLS.param(pattern,this.mappingType()));
    }

    @Override
    public final <N extends Number> Expression<E> mod(Expression<N> operator) {
        return new DualExpresion<>(this, DualOperator.MOD, operator);
    }

    @Override
    public final <N extends Number> Expression<E> mod(N operator) {
        return new DualExpresion<>(this, DualOperator.MOD, SQLS.param(operator));
    }

    @Override
    public final <N extends Number> Expression<E> multiply(Expression<N> multiplicand) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, multiplicand);
    }

    @Override
    public final <N extends Number> Expression<E> multiply(N multiplicand) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, SQLS.param(multiplicand));
    }

    @Override
    public final <N extends Number> Expression<E> add(Expression<N> augend) {
        return new DualExpresion<>(this, DualOperator.ADD, augend);
    }

    @Override
    public final <N extends Number> Expression<E> add(N augend) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.param(augend));
    }

    @Override
    public final <N extends Number> Expression<E> subtract(Expression<N> subtrahend) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, subtrahend);
    }

    @Override
    public final <N extends Number> Expression<E> subtract(N subtrahend) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.param(subtrahend));
    }

    @Override
    public final <N extends Number> Expression<E> divide(Expression<N> divisor) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, divisor);
    }

    @Override
    public final <N extends Number> Expression<E> divide(N divisor) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, SQLS.param(divisor));
    }

    @Override
    public final  Expression<E> negate() {
        return new UnaryExpression<>(this, UnaryOperator.NEGATED);
    }

    @Override
    public final <O> Expression<BigInteger>  and(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.AND, operator);
    }

    @Override
    public final  Expression<BigInteger>  and(Long operator) {
        return new DualExpresion<>(this, DualOperator.AND, SQLS.param(operator));
    }

    @Override
    public final <O> Expression<BigInteger>  or(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.OR, operator);
    }

    @Override
    public final  Expression<BigInteger>  or(Long operator) {
        return new DualExpresion<>(this, DualOperator.OR, SQLS.param(operator));
    }

    @Override
    public final <O> Expression<BigInteger>  xor(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.XOR, operator);
    }

    @Override
    public final  Expression<BigInteger>  xor(Long operator) {
        return new DualExpresion<>(this, DualOperator.XOR, SQLS.param(operator));
    }

    @Override
    public final <O> Expression<BigInteger>  inversion(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.INVERT, operator);
    }

    @Override
    public final  Expression<BigInteger>  inversion(Long operator) {
        return new DualExpresion<>(this, DualOperator.INVERT, SQLS.param(operator));
    }

    @Override
    public final  Expression<BigInteger>  rightShift(Integer bitNumber) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, SQLS.param(bitNumber));
    }

    @Override
    public final <O> Expression<BigInteger>  rightShift(Expression<O> bitNumber) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, bitNumber);
    }

    @Override
    public final  Expression<BigInteger>  leftShift(Integer bitNumber) {
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, SQLS.param(bitNumber));
    }

    @Override
    public final <O> Expression<BigInteger>  leftShift(Expression<O> bitNumber) {
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
        return new ConvertExpressionImpl<>(this, MappingFactory.getDefaultMapping(convertType));
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        return new ConvertExpressionImpl<>(this, longMapping);
    }

    @Override
    public final Expression<E> brackets() {
        return new BracketsExpression<>(this);
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
    public final void appendSQL(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList) {
        appendSQLBeforeWhitespace(sql,builder, paramWrapperList);
        builder.append(" ");
    }


    /*################################## blow protected template method ##################################*/

    protected abstract void appendSQLBeforeWhitespace(SQL sql,StringBuilder builder, List<ParamWrapper> paramWrapperList);
}
