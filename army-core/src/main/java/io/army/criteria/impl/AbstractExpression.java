package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.Function;

/**
 * created  on 2018/11/24.
 */
abstract class AbstractExpression<E> implements Expression<E>, Selection {

    protected String alias;

    AbstractExpression() {
    }


    @Override
    public Selection as(String alias) {
        Assert.hasText(alias, "alias required");
        this.alias = alias;
        return this;
    }

    @Override
    public String alias() {
        if (this.alias == null) {
            throw new IllegalStateException("alias is null,expression state error.");
        }
        return alias;
    }


    @Override
    public final IPredicate eq(Expression<E> expression) {
        return DualPredicate.build(this, DualOperator.EQ, expression);
    }

    @Override
    public final IPredicate eq(E constant) {
        return DualPredicate.build(this, DualOperator.EQ, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate eq(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualOperator.EQ, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate eq(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualOperator.EQ, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate eq(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualOperator.EQ, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate eqAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.EQ, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate eqSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.EQ, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate eqAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.EQ, SubQueryOperator.ALL, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate lt(Expression<? extends Comparable<E>> expression) {
        return DualPredicate.build(this, DualOperator.LT, expression);
    }

    @Override
    public final IPredicate lt(Comparable<E> constant) {
        return DualPredicate.build(this, DualOperator.LT, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate lt(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualOperator.LT, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate lt(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualOperator.LT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate lt(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualOperator.LT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate ltAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.LT, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate ltSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.LT, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate ltAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.LT, SubQueryOperator.ALL, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate le(Expression<? extends Comparable<E>> expression) {
        return DualPredicate.build(this, DualOperator.LE, expression);
    }

    @Override
    public final IPredicate le(Comparable<E> constant) {
        return DualPredicate.build(this, DualOperator.LE, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate le(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualOperator.LE, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate le(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualOperator.LE, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate le(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualOperator.LE, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate leAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.LE, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate leSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.LE, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate leAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.LE, SubQueryOperator.ALL, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate gt(Expression<? extends Comparable<E>> expression) {
        return DualPredicate.build(this, DualOperator.GT, expression);
    }

    @Override
    public final IPredicate gt(Comparable<E> constant) {
        return DualPredicate.build(this, DualOperator.GT, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate gt(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualOperator.GT, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate gt(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualOperator.GT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate gt(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualOperator.GT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate gtAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.GT, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate gtSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.GT, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate gtAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.GT, SubQueryOperator.ALL, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate ge(Expression<? extends Comparable<E>> expression) {
        return DualPredicate.build(this, DualOperator.GE, expression);
    }

    @Override
    public final IPredicate ge(Comparable<E> constant) {
        return DualPredicate.build(this, DualOperator.GE, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate ge(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualOperator.GE, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate ge(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualOperator.GE, SQLS.field(tableAlias, fieldMeta));
    }


    @Override
    public final <C, S extends Expression<E>> IPredicate ge(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualOperator.GE, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate geAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.GE, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate geSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.GE, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate geAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.GE, SubQueryOperator.ALL, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate notEq(Expression<E> expression) {
        return DualPredicate.build(this, DualOperator.NOT_EQ, expression);
    }

    @Override
    public final IPredicate notEq(Comparable<E> constant) {
        return DualPredicate.build(this, DualOperator.NOT_EQ, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate notEq(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualOperator.NOT_EQ, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate notEq(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualOperator.NOT_EQ, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate notEq(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualOperator.NOT_EQ, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.NOT_EQ, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.NOT_EQ, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualOperator.NOT_EQ, SubQueryOperator.ALL, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate not() {
        return NotPredicate.build(this);
    }

    @Override
    public final IPredicate between(Expression<E> first, Expression<E> second) {
        return new BetweenPredicate(this, first, second);
    }

    @Override
    public final IPredicate between(E first, E second) {
        return new BetweenPredicate(this, SQLS.param(first, this), SQLS.param(second, this));
    }

    @Override
    public final IPredicate between(Expression<E> first, E second) {
        return new BetweenPredicate(this, first, SQLS.param(second, this.mappingMeta()));
    }

    @Override
    public final IPredicate between(E first, Expression<E> second) {
        return new BetweenPredicate(this, SQLS.param(first, this), second);
    }

    @Override
    public final <C> IPredicate between(Function<C, BetweenExp<E>> function) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        BetweenExp<E> betweenExp = function.apply(criteria);
        return new BetweenPredicate(this, betweenExp.first(), betweenExp.second());
    }

    @Override
    public final IPredicate isNull() {
        return UnaryPredicate.build(UnaryOperator.IS_NULL, this);
    }

    @Override
    public final IPredicate isNotNull() {
        return UnaryPredicate.build(UnaryOperator.IS_NOT_NULL, this);
    }

    @Override
    public final IPredicate in(Collection<E> values) {
        return DualPredicate.build(this, DualOperator.IN, CollectionExpression.build(mappingMeta(), values));
    }

    @Override
    public final IPredicate in(Expression<Collection<E>> values) {
        return DualPredicate.build(this, DualOperator.IN, values);
    }

    @Override
    public final <C> IPredicate in(Function<C, ColumnSubQuery<E>> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, SubQueryOperator.IN, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate notIn(Collection<E> values) {
        return DualPredicate.build(this, DualOperator.NOT_IN, CollectionExpression.build(mappingMeta(), values));
    }

    @Override
    public final IPredicate notIn(Expression<Collection<E>> values) {
        return DualPredicate.build(this, DualOperator.NOT_IN, values);
    }

    @Override
    public final <C> IPredicate notIn(Function<C, ColumnSubQuery<E>> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, SubQueryOperator.NOT_IN, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate like(String pattern) {
        return DualPredicate.build(this, DualOperator.LIKE, SQLS.constant(pattern, this.mappingMeta()));
    }

    @Override
    public final IPredicate like(Expression<String> pattern) {
        return DualPredicate.build(this, DualOperator.LIKE, pattern);
    }

    @Override
    public final <C, S extends Expression<String>> IPredicate like(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualOperator.LIKE, expOrSubQuery.apply(criteria));
    }

    @Override
    public final IPredicate notLike(String pattern) {
        return DualPredicate.build(this, DualOperator.NOT_LIKE, SQLS.param(pattern, this.mappingMeta()));
    }

    @Override
    public final IPredicate notLike(Expression<String> pattern) {
        return DualPredicate.build(this, DualOperator.NOT_LIKE, pattern);
    }

    @Override
    public final <C, S extends Expression<String>> IPredicate notLike(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualOperator.NOT_LIKE, expOrSubQuery.apply(criteria));
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
    public final Expression<E> mod(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.MOD, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <N extends Number> Expression<E> mod(String tableAlias, FieldMeta<?, N> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.MOD, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> mod(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.MOD, expOrSubQuery.apply(criteria));
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
    public final Expression<E> multiply(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> multiply(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.MULTIPLY, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> multiply(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.MULTIPLY, expOrSubQuery.apply(criteria));
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
    public final Expression<E> add(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> add(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> add(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.ADD, expOrSubQuery.apply(criteria));
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
    public final Expression<E> subtract(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> subtract(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> subtract(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.SUBTRACT, expOrSubQuery.apply(criteria));
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
    public final Expression<E> divide(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> divide(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.DIVIDE, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> divide(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.DIVIDE, expOrSubQuery.apply(criteria));
    }

    @Override
    public final Expression<E> negate() {
        return UnaryExpression.build(this, UnaryOperator.NEGATED);
    }

    @Override
    public final <O> Expression<BigInteger> and(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.AND, operator);
    }

    @Override
    public final Expression<BigInteger> and(Long operator) {
        return new DualExpresion<>(this, DualOperator.AND, SQLS.param(operator));
    }

    @Override
    public final Expression<BigInteger> and(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.AND, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> and(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.AND, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> and(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.AND, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<BigInteger> or(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.OR, operator);
    }

    @Override
    public final Expression<BigInteger> or(Long operator) {
        return new DualExpresion<>(this, DualOperator.OR, SQLS.param(operator));
    }

    @Override
    public final Expression<BigInteger> or(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.OR, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> or(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.OR, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> or(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.OR, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<BigInteger> xor(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.XOR, operator);
    }

    @Override
    public final Expression<BigInteger> xor(Long operator) {
        return new DualExpresion<>(this, DualOperator.XOR, SQLS.param(operator));
    }

    @Override
    public final Expression<BigInteger> xor(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.XOR, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> xor(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.XOR, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> xor(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.XOR, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<BigInteger> inversion(Expression<O> operator) {
        return new DualExpresion<>(this, DualOperator.INVERT, operator);
    }

    @Override
    public final Expression<BigInteger> inversion(Long operator) {
        return new DualExpresion<>(this, DualOperator.INVERT, SQLS.param(operator));
    }

    @Override
    public final Expression<BigInteger> inversion(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.INVERT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> inversion(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.INVERT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> inversion(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.INVERT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final Expression<BigInteger> rightShift(Integer bitNumber) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, SQLS.param(bitNumber));
    }

    @Override
    public final <O> Expression<BigInteger> rightShift(Expression<O> bitNumber) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, bitNumber);
    }

    @Override
    public final Expression<BigInteger> rightShift(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> rightShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> rightShift(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.RIGHT_SHIFT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final Expression<BigInteger> leftShift(Integer bitNumber) {
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, SQLS.param(bitNumber));
    }

    @Override
    public final <O> Expression<BigInteger> leftShift(Expression<O> bitNumber) {
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, bitNumber);
    }

    @Override
    public final Expression<BigInteger> leftShift(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> leftShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> leftShift(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.LEFT_SHIFT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<E> plusOther(Expression<O> other) {
        return new DualExpresion<>(this, DualOperator.ADD, other);
    }

    @Override
    public final Expression<E> plusOther(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<E> plusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.ADD, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> plusOther(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.ADD, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<E> minusOther(Expression<O> other) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, other);
    }

    @Override
    public final Expression<E> minusOther(String subQueryAlias, String derivedFieldName) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<E> minusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return new DualExpresion<>(this, DualOperator.SUBTRACT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> minusOther(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return new DualExpresion<>(this, DualOperator.SUBTRACT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType) {
        return new ConvertExpressionImpl<>(this, MappingFactory.getDefaultMapping(convertType));
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, MappingMeta longMapping) {
        return new ConvertExpressionImpl<>(this, longMapping);
    }

    @Override
    public final Expression<E> brackets() {
        return new BracketsExpression<>(this);
    }

    @Override
    public final SortPart asc() {
        return new SortPartImpl(this, true);
    }

    @Override
    public final SortPart desc() {
        return new SortPartImpl(this, false);
    }


    @Override
    public final void appendSQL(SQLContext context) {
        context.sqlBuilder()
                .append(" ");
        // invoke descendant method
        afterSpace(context);

        if (this.alias != null) {
            context.sqlBuilder()
                    .append(" AS ")
                    .append(context.dql().quoteIfNeed(this.alias))
            ;
        }
    }

    @Override
    public void appendSortPart(SQLContext context) {
        if (this.alias == null) {
            this.appendSQL(context);
        } else {
            context.appendText(this.alias);
        }
    }

    @Override
    public final String toString() {
        String text = beforeAs();
        if (StringUtils.hasText(this.alias)) {
            text = text + " AS " + this.alias;
        }
        return text;
    }

    /*################################## blow protected template method ##################################*/

    protected abstract void afterSpace(SQLContext context);

    protected abstract String beforeAs();

}
