package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;

import java.math.BigInteger;
import java.util.Collection;

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
    public final IPredicate eq(Expression<E> expression) {
        return new DualIPredicateImpl(this, DualOperator.EQ, expression);
    }

    @Override
    public final IPredicate eq(E constant) {
        return new DualIPredicateImpl(this, DualOperator.EQ, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate lt(Expression<? extends Comparable<E>> expression) {
        return new DualIPredicateImpl(this, DualOperator.LT, expression);
    }

    @Override
    public final IPredicate lt(Comparable<E> constant) {
        return new DualIPredicateImpl(this, DualOperator.LT, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate le(Expression<? extends Comparable<E>> expression) {
        return new DualIPredicateImpl(this, DualOperator.LE, expression);
    }

    @Override
    public final IPredicate le(Comparable<E> constant) {
        return new DualIPredicateImpl(this, DualOperator.LE, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate gt(Expression<? extends Comparable<E>> expression) {
        return new DualIPredicateImpl(this, DualOperator.GT, expression);
    }

    @Override
    public final IPredicate gt(Comparable<E> constant) {
        return new DualIPredicateImpl(this, DualOperator.GT, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate ge(Expression<? extends Comparable<E>> expression) {
        return new DualIPredicateImpl(this, DualOperator.GE, expression);
    }

    @Override
    public final IPredicate ge(Comparable<E> constant) {
        return new DualIPredicateImpl(this, DualOperator.GE, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate notEq(Expression<E> expression) {
        return new DualIPredicateImpl(this, DualOperator.NOT_EQ, expression);
    }

    @Override
    public final IPredicate notEq(Comparable<E> constant) {
        return new DualIPredicateImpl(this, DualOperator.NOT_EQ, SQLS.param(constant, this.mappingType()));
    }

    @Override
    public final IPredicate not() {
        return NotIPredicate.build(this);
    }

    @Override
    public final IPredicate between(Expression<E> first, Expression<E> second) {
        return new BetweenIPredicate(this, first, second);
    }

    @Override
    public final IPredicate between(E first, E second) {
        return new BetweenIPredicate(this, SQLS.param(first, this), SQLS.param(second, this));
    }

    @Override
    public final IPredicate between(Expression<E> first, E second) {
        return new BetweenIPredicate(this, first, SQLS.param(second, this.mappingType()));
    }

    @Override
    public final IPredicate between(E first, Expression<E> second) {
        return new BetweenIPredicate(this, SQLS.param(first, this), second);
    }

    @Override
    public final IPredicate isNull() {
        return new UnaryIPredicate(UnaryOperator.IS_NULL, this);
    }

    @Override
    public final IPredicate isNotNull() {
        return new UnaryIPredicate(UnaryOperator.IS_NOT_NULL, this);
    }

    @Override
    public final IPredicate in(Collection<E> values) {
        return new InIPredicate(true, this, values);
    }

    @Override
    public final IPredicate in(Expression<Collection<E>> values) {
        return new InIPredicate(true, this, values);
    }

    @Override
    public final IPredicate notIn(Collection<E> values) {
        return new InIPredicate(false, this, values);
    }

    @Override
    public final IPredicate notIn(Expression<Collection<E>> values) {
        return new InIPredicate(false, this, values);
    }

    @Override
    public final IPredicate like(String pattern) {
        return new DualIPredicateImpl(this, DualOperator.LIKE, SQLS.constant(pattern, this.mappingType()));
    }

    @Override
    public final IPredicate notLike(String pattern) {
        return new DualIPredicateImpl(this, DualOperator.NOT_LIKE, SQLS.param(pattern,this.mappingType()));
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
    public final IPredicate all(SubQuery<E> subQuery) {
        return null;
    }

    @Override
    public final IPredicate any(SubQuery<E> subQuery) {
        return null;
    }

    @Override
    public final IPredicate some(SubQuery<E> subQuery) {
        return null;
    }

    @Override
    public final void appendSQL(SQLContext context) {
        context.stringBuilder().append(" ");
        afterSpace(context);
    }

    @Override
    public String toString() {
        return super.toString();
    }

    /*################################## blow protected template method ##################################*/

    protected abstract void afterSpace(SQLContext context);
}
