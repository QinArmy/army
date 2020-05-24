package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.meta.mapping.MappingFactory;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;
import io.army.util.StringUtils;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.Function;

/**
 * this class is a implementation of {@link Expression}
 */
abstract class AbstractExpression<E> implements Expression<E>, Selection, ExpressionCounselor {

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
    public final IPredicate equal(Expression<E> expression) {
        return DualPredicate.build(this, DualPredicateOperator.EQ, expression);
    }

    @Override
    public final IPredicate equal(E constant) {
        return DualPredicate.build(this, DualPredicateOperator.EQ, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate equal(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.EQ, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate equal(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.EQ, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate equal(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.EQ, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate equalAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.EQ, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate equalSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.EQ, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate equalAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.EQ, SubQueryOperator.ALL, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate lessThan(Expression<? extends Comparable<E>> expression) {
        return DualPredicate.build(this, DualPredicateOperator.LT, expression);
    }

    @Override
    public final IPredicate lessThan(Comparable<E> constant) {
        return DualPredicate.build(this, DualPredicateOperator.LT, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate lessThan(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.LT, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate lessThan(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.LT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate lessThan(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.LT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessThanAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.LT, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessThanSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.LT, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessThanAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.LT, SubQueryOperator.ALL, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate lessEqual(Expression<? extends Comparable<E>> expression) {
        return DualPredicate.build(this, DualPredicateOperator.LE, expression);
    }

    @Override
    public final IPredicate lessEqual(Comparable<E> constant) {
        return DualPredicate.build(this, DualPredicateOperator.LE, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate lessEqual(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.LE, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate lessEqual(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.LE, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate lessEqual(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.LE, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessEqualAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.LE, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessEqualSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.LE, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate lessEqualAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.LE, SubQueryOperator.ALL, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate greatThan(Expression<? extends Comparable<E>> expression) {
        return DualPredicate.build(this, DualPredicateOperator.GT, expression);
    }

    @Override
    public final IPredicate greatThan(Comparable<E> constant) {
        return DualPredicate.build(this, DualPredicateOperator.GT, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate greatThan(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.GT, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate greatThan(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.GT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate greatThan(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.GT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatThanAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.GT, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatThanSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.GT, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatThanAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.GT, SubQueryOperator.ALL, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate greatEqual(Expression<? extends Comparable<E>> expression) {
        return DualPredicate.build(this, DualPredicateOperator.GE, expression);
    }

    @Override
    public final IPredicate greatEqual(Comparable<E> constant) {
        return DualPredicate.build(this, DualPredicateOperator.GE, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate greatEqual(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.GE, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate greatEqual(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.GE, SQLS.field(tableAlias, fieldMeta));
    }


    @Override
    public final <C, S extends Expression<E>> IPredicate greatEqual(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.GE, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatEqualAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.GE, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatEqualSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.GE, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate greatEqualAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.GE, SubQueryOperator.ALL, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate notEqual(Expression<E> expression) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_EQ, expression);
    }

    @Override
    public final IPredicate notEqual(Comparable<E> constant) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_EQ, SQLS.param(constant, this.mappingMeta()));
    }

    @Override
    public final IPredicate notEqual(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_EQ, SQLS.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate notEqual(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_EQ, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate notEqual(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.NOT_EQ, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqualAny(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.NOT_EQ, SubQueryOperator.ANY, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqualSome(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.NOT_EQ, SubQueryOperator.SOME, subQuery.apply(criteria));
    }

    @Override
    public final <C, S extends ColumnSubQuery<E>> IPredicate notEqualAll(Function<C, S> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.NOT_EQ, SubQueryOperator.ALL, subQuery.apply(criteria));
    }


    @Override
    public final IPredicate between(Expression<E> first, Expression<E> second) {
        return BetweenPredicate.build(this, first, second);
    }

    @Override
    public final IPredicate between(E first, E second) {
        return BetweenPredicate.build(this, SQLS.paramWithExp(first, this), SQLS.paramWithExp(second, this));
    }

    @Override
    public final IPredicate between(Expression<E> first, E second) {
        return BetweenPredicate.build(this, first, SQLS.param(second, this.mappingMeta()));
    }

    @Override
    public final IPredicate between(E first, Expression<E> second) {
        return BetweenPredicate.build(this, SQLS.paramWithExp(first, this), second);
    }

    @Override
    public final <C> IPredicate between(Function<C, BetweenWrapper<E>> function) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        BetweenWrapper<E> betweenExp = function.apply(criteria);
        return BetweenPredicate.build(this, betweenExp.first(), betweenExp.second());
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
        return DualPredicate.build(this, DualPredicateOperator.IN, CollectionExpressionImpl.build(this.mappingMeta(), values));
    }

    @Override
    public final IPredicate in(Expression<Collection<E>> values) {
        return DualPredicate.build(this, DualPredicateOperator.IN, values);
    }

    @Override
    public final <C> IPredicate in(Function<C, ColumnSubQuery<E>> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.IN, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate notIn(Collection<E> values) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_IN, CollectionExpressionImpl.build(mappingMeta(), values));
    }

    @Override
    public final IPredicate notIn(Expression<Collection<E>> values) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_IN, values);
    }

    @Override
    public final <C> IPredicate notIn(Function<C, ColumnSubQuery<E>> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.NOT_IN, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate like(String pattern) {
        return DualPredicate.build(this, DualPredicateOperator.LIKE, SQLS.constant(pattern, this.mappingMeta()));
    }

    @Override
    public final IPredicate like(Expression<String> pattern) {
        return DualPredicate.build(this, DualPredicateOperator.LIKE, pattern);
    }

    @Override
    public final <C, S extends Expression<String>> IPredicate like(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.LIKE, expOrSubQuery.apply(criteria));
    }

    @Override
    public final IPredicate notLike(String pattern) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_LIKE, SQLS.param(pattern, this.mappingMeta()));
    }

    @Override
    public final IPredicate notLike(Expression<String> pattern) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_LIKE, pattern);
    }

    @Override
    public final <C, S extends Expression<String>> IPredicate notLike(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.NOT_LIKE, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <N extends Number> Expression<E> mod(Expression<N> operator) {
        return DualExpresion.build(this, DualOperator.MOD, operator);
    }

    @Override
    public final <N extends Number> Expression<E> mod(N operator) {
        return DualExpresion.build(this, DualOperator.MOD, SQLS.param(operator));
    }

    @Override
    public final Expression<E> mod(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.MOD, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <N extends Number> Expression<E> mod(String tableAlias, FieldMeta<?, N> fieldMeta) {
        return DualExpresion.build(this, DualOperator.MOD, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> mod(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.MOD, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <N extends Number> Expression<E> multiply(Expression<N> multiplicand) {
        return DualExpresion.build(this, DualOperator.MULTIPLY, multiplicand);
    }

    @Override
    public final <N extends Number> Expression<E> multiply(N multiplicand) {
        return DualExpresion.build(this, DualOperator.MULTIPLY, SQLS.param(multiplicand));
    }

    @Override
    public final Expression<E> multiply(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.MULTIPLY, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> multiply(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualExpresion.build(this, DualOperator.MULTIPLY, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> multiply(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.MULTIPLY, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <N extends Number> Expression<E> add(Expression<N> augend) {
        return DualExpresion.build(this, DualOperator.ADD, augend);
    }

    @Override
    public final <N extends Number> Expression<E> add(N augend) {
        return DualExpresion.build(this, DualOperator.ADD, SQLS.param(augend));
    }

    @Override
    public final Expression<E> add(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.ADD, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> add(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualExpresion.build(this, DualOperator.ADD, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> add(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.ADD, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <N extends Number> Expression<E> subtract(Expression<N> subtrahend) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, subtrahend);
    }

    @Override
    public final <N extends Number> Expression<E> subtract(N subtrahend) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, SQLS.param(subtrahend));
    }

    @Override
    public final Expression<E> subtract(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> subtract(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> subtract(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.SUBTRACT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <N extends Number> Expression<E> divide(Expression<N> divisor) {
        return DualExpresion.build(this, DualOperator.DIVIDE, divisor);
    }

    @Override
    public final <N extends Number> Expression<E> divide(N divisor) {
        return DualExpresion.build(this, DualOperator.DIVIDE, SQLS.param(divisor));
    }

    @Override
    public final Expression<E> divide(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.DIVIDE, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> divide(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualExpresion.build(this, DualOperator.DIVIDE, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> divide(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.DIVIDE, expOrSubQuery.apply(criteria));
    }

    @Override
    public final Expression<E> negate() {
        return UnaryExpression.build(this, UnaryOperator.NEGATED);
    }

    @Override
    public final <O> Expression<BigInteger> and(Expression<O> operand) {
        return DualExpresion.build(this, DualOperator.AND, operand);
    }

    @Override
    public final Expression<BigInteger> and(Long operand) {
        return DualExpresion.build(this, DualOperator.AND, SQLS.param(operand));
    }

    @Override
    public final Expression<BigInteger> and(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.AND, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> and(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.AND, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> and(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.AND, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<BigInteger> or(Expression<O> operand) {
        return DualExpresion.build(this, DualOperator.OR, operand);
    }

    @Override
    public final Expression<BigInteger> or(Long operand) {
        return DualExpresion.build(this, DualOperator.OR, SQLS.param(operand));
    }

    @Override
    public final Expression<BigInteger> or(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.OR, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> or(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.OR, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> or(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.OR, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<BigInteger> xor(Expression<O> operand) {
        return DualExpresion.build(this, DualOperator.XOR, operand);
    }

    @Override
    public final Expression<BigInteger> xor(Long operand) {
        return DualExpresion.build(this, DualOperator.XOR, SQLS.param(operand));
    }

    @Override
    public final Expression<BigInteger> xor(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.XOR, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> xor(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.XOR, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> xor(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.XOR, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<BigInteger> inversion(Expression<O> operand) {
        return DualExpresion.build(this, DualOperator.INVERT, operand);
    }

    @Override
    public final Expression<BigInteger> inversion(Long operand) {
        return DualExpresion.build(this, DualOperator.INVERT, SQLS.param(operand));
    }

    @Override
    public final Expression<BigInteger> inversion(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.INVERT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> inversion(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.INVERT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> inversion(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.INVERT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final Expression<BigInteger> rightShift(Integer bitNumber) {
        return DualExpresion.build(this, DualOperator.RIGHT_SHIFT, SQLS.param(bitNumber));
    }

    @Override
    public final <O> Expression<BigInteger> rightShift(Expression<O> bitNumber) {
        return DualExpresion.build(this, DualOperator.RIGHT_SHIFT, bitNumber);
    }

    @Override
    public final Expression<BigInteger> rightShift(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.RIGHT_SHIFT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> rightShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.RIGHT_SHIFT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> rightShift(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.RIGHT_SHIFT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final Expression<BigInteger> leftShift(Integer bitNumber) {
        return DualExpresion.build(this, DualOperator.LEFT_SHIFT, SQLS.param(bitNumber));
    }

    @Override
    public final <O> Expression<BigInteger> leftShift(Expression<O> bitNumber) {
        return DualExpresion.build(this, DualOperator.LEFT_SHIFT, bitNumber);
    }

    @Override
    public final Expression<BigInteger> leftShift(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.LEFT_SHIFT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> leftShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.LEFT_SHIFT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> leftShift(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.LEFT_SHIFT, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<E> plusOther(Expression<O> other) {
        return DualExpresion.build(this, DualOperator.ADD, other);
    }

    @Override
    public final Expression<E> plusOther(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.ADD, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<E> plusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.ADD, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> plusOther(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.ADD, expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<E> minusOther(Expression<O> other) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, other);
    }

    @Override
    public final Expression<E> minusOther(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, SQLS.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<E> minusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, SQLS.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> minusOther(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.SUBTRACT, expOrSubQuery.apply(criteria));
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
    public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        return false;
    }

    @Override
    public boolean containsFieldOf(TableMeta<?> tableMeta) {
        return false;
    }

    @Override
    public int containsFieldCount(TableMeta<?> tableMeta) {
        return 0;
    }

    @Override
    public boolean containsSubQuery() {
        return false;
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
