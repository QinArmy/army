package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.Predicate;
import io.army.criteria.SubQuery;
import io.army.dialect.ParamWrapper;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingType;
import org.springframework.expression.spel.ast.Ternary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * created  on 2018/11/24.
 */
public abstract class AbstractExpression<E> implements Expression<E> {

    @Override
    public final Predicate eq(Expression<E> expression) {
        return new DualPredicate(this, DualOperator.EQ, expression);
    }

    @Override
    public final Predicate eq(E constant) {
        return new DualConstantPredicate(this, DualOperator.EQ, constant);
    }

    @Override
    public final Predicate lt(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.LT, expression);
    }

    @Override
    public final Predicate lt(Comparable<E> constant) {
        return new DualConstantPredicate(this, DualOperator.LT, constant);
    }

    @Override
    public final Predicate le(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.LE, expression);
    }

    @Override
    public final Predicate le(Comparable<E> constant) {
        return new DualConstantPredicate(this, DualOperator.LE, constant);
    }

    @Override
    public final Predicate gt(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.GT, expression);
    }

    @Override
    public final Predicate gt(Comparable<E> constant) {
        return new DualConstantPredicate(this, DualOperator.GT, constant);
    }

    @Override
    public final Predicate ge(Expression<? extends Comparable<E>> expression) {
        return new DualPredicate(this, DualOperator.GE, expression);
    }

    @Override
    public final Predicate ge(Comparable<E> constant) {
        return new DualConstantPredicate(this, DualOperator.GE, constant);
    }

    @Override
    public final Predicate notEq(Expression<E> expression) {
        return new DualPredicate(this, DualOperator.NOT_EQ, expression);
    }

    @Override
    public final Predicate notEq(Comparable<E> constant) {
        return new DualConstantPredicate(this, DualOperator.NOT_EQ, constant);
    }

    @Override
    public final Predicate not() {
        return NotPredicate.build(this);
    }

    @Override
    public final Predicate between(Expression<E> first, Expression<E> second) {
        return new TernaryPredicate(TernaryOperator.BETWEEN, this, first, second);
    }

    @Override
    public final Predicate between(E first, E second) {
        return new BetweenConstantPredicate(this, first, second);
    }

    @Override
    public final Predicate between(Expression<E> first, E second) {
        return new BetweenConstantPredicate(this, first, second);
    }

    @Override
    public final Predicate between(E first, Expression<E> second) {
        return new BetweenConstantPredicate(this, first, second);
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
        return new DualConstantPredicate(this, DualOperator.LIKE, pattern);
    }

    @Override
    public final Predicate notLike(String pattern) {
        return new DualConstantPredicate(this, DualOperator.NOT_LIKE, pattern);
    }

    @Override
    public final Expression<E> mod(Expression<E> divisor) {
        return new DualExpresion<>(this, DualOperator.MOD, divisor);
    }

    @Override
    public final Expression<E> mod(E divisor) {
        return new DualExpresion<>(this, DualOperator.MOD, SQLS.constant(divisor, this.mappingType()));
    }

    @Override
    public final Expression<E> multiply(Expression<E> multiplicand) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, multiplicand);
    }

    @Override
    public final Expression<E> multiply(E multiplicand) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, SQLS.constant(multiplicand, this.mappingType()));
    }

    @Override
    public final Expression<E> add(Expression<E> augend) {
        return new DualExpresion<>(this, DualOperator.ADD, augend);
    }

    @Override
    public final Expression<E> add(E augend) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.constant(augend, this.mappingType()));
    }

    @Override
    public final Expression<E> subtract(Expression<E> subtrahend) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, subtrahend);
    }

    @Override
    public final Expression<E> subtract(E subtrahend) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.constant(subtrahend, this.mappingType()));
    }

    @Override
    public final Expression<E> divide(Expression<E> divisor) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, divisor);
    }

    @Override
    public final Expression<E> divide(E divisor) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, SQLS.constant(divisor, this.mappingType()));
    }

    @Override
    public final Expression<E> negate() {
        return new UnaryExpression<>(this, UnaryOperator.NEGATED);
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
    public final <O> Expression<O> as(Class<O> convertType) {
        MappingType targetType = MappingFactory.getDefaultMapping(convertType);
        return new ConvertExpression<>(this, targetType);
    }

    @Override
    public final <O> Expression<O> as(Class<O> convertType, MappingType longMapping) {
        return new ConvertExpression<>(this, longMapping);
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
    public final String toString() {
        StringBuilder builder = new StringBuilder();
        List<ParamWrapper> paramWrapperList = new ArrayList<>();
        appendSQL(builder, paramWrapperList);
        return builder.toString();
    }
}
