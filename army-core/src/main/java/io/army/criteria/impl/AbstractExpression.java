package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.Function;

/**
 * this class is base class of most implementation of {@link Expression}
 */
abstract class AbstractExpression<E> implements _Expression<E> {

    AbstractExpression() {
    }


    @Override
    public Selection as(String alias) {
        return new ExpressionSelection(this, alias);
    }

    @Override
    public final boolean nullableExp() {
        final boolean nullable;
        if (this instanceof Selection) {
            nullable = ((Selection) this).nullable();
        } else if (this instanceof ScalarSubQuery) {
            nullable = ((ScalarSubQuery<?>) this).selection().nullable();
        } else {
            nullable = false;
        }
        return nullable;
    }

    @Override
    public final IPredicate equal(Expression<E> expression) {
        return DualPredicate.build(this, DualPredicateOperator.EQ, (_Expression<?>) expression);
    }

    @Override
    public final IPredicate equal(E constant) {
        return DualPredicate.build(this, DualPredicateOperator.EQ, (_Expression<?>) SQLs.paramWithExp(constant, this));
    }

    @Nullable
    @Override
    public final IPredicate equalIfNonNull(@Nullable E constant) {
        return constant == null ? null : this.equal(constant);
    }

    @Override
    public final IPredicate equal(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.EQ, (_Expression<?>) SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate equal(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.EQ, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate equal(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.EQ, (_Expression<?>) expOrSubQuery.apply(criteria));
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
    public final IPredicate lessThan(Expression<E> expression) {
        return DualPredicate.build(this, DualPredicateOperator.LT, (_Expression<?>) expression);
    }

    @Override
    public final IPredicate lessThan(E constant) {
        return DualPredicate.build(this, DualPredicateOperator.LT, (_Expression<?>) SQLs.paramWithExp(constant, this));
    }

    @Override
    public final IPredicate lessThan(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.LT, (_Expression<?>) SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate lessThan(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.LT, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate lessThan(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.LT, (_Expression<?>) expOrSubQuery.apply(criteria));
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
    public final IPredicate lessEqual(Expression<E> expression) {
        return DualPredicate.build(this, DualPredicateOperator.LE, (_Expression<?>) expression);
    }

    @Override
    public final IPredicate lessEqual(E constant) {
        return DualPredicate.build(this, DualPredicateOperator.LE, (_Expression<?>) SQLs.paramWithExp(constant, this));
    }

    @Override
    public final IPredicate lessEqual(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.LE, (_Expression<?>) SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate lessEqual(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.LE, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate lessEqual(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.LE, (_Expression<?>) expOrSubQuery.apply(criteria));
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
    public final IPredicate greatThan(Expression<E> expression) {
        return DualPredicate.build(this, DualPredicateOperator.GT, (_Expression<?>) expression);
    }

    @Override
    public final IPredicate greatThan(E constant) {
        return DualPredicate.build(this, DualPredicateOperator.GT, (_Expression<?>) SQLs.paramWithExp(constant, this));
    }

    @Override
    public final IPredicate greatThan(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.GT, (_Expression<?>) SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate greatThan(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.GT, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate greatThan(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.GT, (_Expression<?>) expOrSubQuery.apply(criteria));
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
    public final IPredicate greatEqual(Expression<E> expression) {
        return DualPredicate.build(this, DualPredicateOperator.GE, (_Expression<?>) expression);
    }

    @Override
    public final IPredicate greatEqual(E constant) {
        return DualPredicate.build(this, DualPredicateOperator.GE, (_Expression<?>) SQLs.paramWithExp(constant, this));
    }

    @Override
    public final IPredicate greatEqual(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.GE, (_Expression<?>) SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate greatEqual(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.GE, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }


    @Override
    public final <C, S extends Expression<E>> IPredicate greatEqual(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.GE, (_Expression<?>) expOrSubQuery.apply(criteria));
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
        return DualPredicate.build(this, DualPredicateOperator.NOT_EQ, (_Expression<?>) expression);
    }

    @Override
    public final IPredicate notEqual(E constant) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_EQ, (_Expression<?>) SQLs.paramWithExp(constant, this));
    }

    @Override
    public final IPredicate notEqual(String subQueryAlias, String fieldAlias) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_EQ, (_Expression<?>) SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate notEqual(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_EQ, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, S extends Expression<E>> IPredicate notEqual(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.NOT_EQ, (_Expression<?>) expOrSubQuery.apply(criteria));
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
        return BetweenPredicate.build(this, (_Expression<?>) first, (_Expression<?>) second);
    }

    @Override
    public final IPredicate between(E first, E second) {
        return BetweenPredicate.build(this, (_Expression<?>) SQLs.paramWithExp(first, this), (_Expression<?>) SQLs.paramWithExp(second, this));
    }

    @Override
    public final IPredicate between(Expression<E> first, E second) {
        return BetweenPredicate.build(this, (_Expression<?>) first, (_Expression<?>) SQLs.paramWithExp(second, this));
    }

    @Override
    public final IPredicate between(E first, Expression<E> second) {
        return BetweenPredicate.build(this, (_Expression<?>) SQLs.paramWithExp(first, this), (_Expression<?>) second);
    }

    @Override
    public final <C> IPredicate between(Function<C, BetweenWrapper<E>> function) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        BetweenWrapper<E> betweenExp = function.apply(criteria);
        return BetweenPredicate.build(this, (_Expression<?>) betweenExp.first(), (_Expression<?>) betweenExp.second());
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
        return DualPredicate.build(this, DualPredicateOperator.IN, (_Expression<?>) SQLs.collectionWithExp(this, values));
    }

    @Override
    public final IPredicate in(Expression<Collection<E>> values) {
        return DualPredicate.build(this, DualPredicateOperator.IN, (_Expression<?>) values);
    }

    @Override
    public final <C> IPredicate in(Function<C, ColumnSubQuery<E>> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.IN, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate notIn(Collection<E> values) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_IN, (_Expression<?>) SQLs.collectionWithExp(this, values));
    }

    @Override
    public final IPredicate notIn(Expression<Collection<E>> values) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_IN, (_Expression<?>) values);
    }

    @Override
    public final <C> IPredicate notIn(Function<C, ColumnSubQuery<E>> subQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return ColumnSubQueryPredicate.build(this, DualPredicateOperator.NOT_IN, subQuery.apply(criteria));
    }

    @Override
    public final IPredicate like(String pattern) {
        return DualPredicate.build(this, DualPredicateOperator.LIKE, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), pattern));
    }

    @Override
    public final IPredicate like(Expression<String> pattern) {
        return DualPredicate.build(this, DualPredicateOperator.LIKE, (_Expression<?>) pattern);
    }

    @Override
    public final <C, S extends Expression<String>> IPredicate like(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.LIKE, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final IPredicate notLike(String pattern) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_LIKE
                , (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), pattern));
    }

    @Override
    public final IPredicate notLike(Expression<String> pattern) {
        return DualPredicate.build(this, DualPredicateOperator.NOT_LIKE, (_Expression<?>) pattern);
    }

    @Override
    public final <C, S extends Expression<String>> IPredicate notLike(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualPredicate.build(this, DualPredicateOperator.NOT_LIKE, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final <N extends Number> Expression<E> mod(Expression<N> operator) {
        return DualExpresion.build(this, DualOperator.MOD, (_Expression<?>) operator);
    }

    @Override
    public final <N extends Number> Expression<E> mod(N operator) {
        return DualExpresion.build(this, DualOperator.MOD, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), operator));
    }

    @Override
    public final Expression<E> mod(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.MOD, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <N extends Number> Expression<E> mod(String tableAlias, FieldMeta<?, N> fieldMeta) {
        return DualExpresion.build(this, DualOperator.MOD, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> mod(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.MOD, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final <N extends Number> Expression<E> multiply(Expression<N> multiplicand) {
        return DualExpresion.build(this, DualOperator.MULTIPLY, (_Expression<?>) multiplicand);
    }

    @Override
    public final <N extends Number> Expression<E> multiply(N multiplicand) {
        return DualExpresion.build(this, DualOperator.MULTIPLY, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), multiplicand));
    }

    @Override
    public final Expression<E> multiply(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.MULTIPLY, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> multiply(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualExpresion.build(this, DualOperator.MULTIPLY, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> multiply(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.MULTIPLY, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final <N extends Number> Expression<E> plus(Expression<N> augend) {
        return DualExpresion.build(this, DualOperator.ADD, (_Expression<?>) augend);
    }

    @Override
    public final <N extends Number> Expression<E> plus(N augend) {
        return DualExpresion.build(this, DualOperator.ADD, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), augend));
    }

    @Override
    public final Expression<E> plus(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.ADD, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> plus(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualExpresion.build(this, DualOperator.ADD, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> plus(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.ADD, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final <N extends Number> Expression<E> minus(Expression<N> subtrahend) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, (_Expression<?>) subtrahend);
    }

    @Override
    public final <N extends Number> Expression<E> minus(N subtrahend) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), subtrahend));
    }

    @Override
    public final Expression<E> minus(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> minus(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> minus(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.SUBTRACT, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final <N extends Number> Expression<E> divide(Expression<N> divisor) {
        return DualExpresion.build(this, DualOperator.DIVIDE, (_Expression<?>) divisor);
    }

    @Override
    public final <N extends Number> Expression<E> divide(N divisor) {
        return DualExpresion.build(this, DualOperator.DIVIDE, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), divisor));
    }

    @Override
    public final Expression<E> divide(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.DIVIDE, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> divide(String tableAlias, FieldMeta<?, E> fieldMeta) {
        return DualExpresion.build(this, DualOperator.DIVIDE, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, N extends Number, S extends Expression<N>> Expression<E> divide(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.DIVIDE, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final Expression<E> negate() {
        return UnaryExpression.build(this, UnaryOperator.NEGATED);
    }

    @Override
    public final <O> Expression<BigInteger> and(Expression<O> operand) {
        return DualExpresion.build(this, DualOperator.AND, (_Expression<?>) operand);
    }

    @Override
    public final Expression<BigInteger> and(Long operand) {
        return DualExpresion.build(this, DualOperator.AND, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), operand));
    }

    @Override
    public final Expression<BigInteger> and(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.AND, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> and(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.AND, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> and(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.AND, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<BigInteger> or(Expression<O> operand) {
        return DualExpresion.build(this, DualOperator.OR, (_Expression<?>) operand);
    }

    @Override
    public final Expression<BigInteger> or(Long operand) {
        return DualExpresion.build(this, DualOperator.OR, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), operand));
    }

    @Override
    public final Expression<BigInteger> or(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.OR, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> or(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.OR, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> or(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.OR, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<BigInteger> xor(Expression<O> operand) {
        return DualExpresion.build(this, DualOperator.XOR, (_Expression<?>) operand);
    }

    @Override
    public final Expression<BigInteger> xor(Long operand) {
        return DualExpresion.build(this, DualOperator.XOR, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), operand));
    }

    @Override
    public final Expression<BigInteger> xor(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.XOR, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> xor(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.XOR, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> xor(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.XOR, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<BigInteger> inversion(Expression<O> operand) {
        return DualExpresion.build(this, DualOperator.INVERT, (_Expression<?>) operand);
    }

    @Override
    public final Expression<BigInteger> inversion(Long operand) {
        return DualExpresion.build(this, DualOperator.INVERT, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), operand));
    }

    @Override
    public final Expression<BigInteger> inversion(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.INVERT, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> inversion(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.INVERT, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> inversion(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.INVERT, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final Expression<BigInteger> rightShift(Integer bitNumber) {
        return DualExpresion.build(this, DualOperator.RIGHT_SHIFT, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), bitNumber));
    }

    @Override
    public final <O> Expression<BigInteger> rightShift(Expression<O> bitNumber) {
        return DualExpresion.build(this, DualOperator.RIGHT_SHIFT, (_Expression<?>) bitNumber);
    }

    @Override
    public final Expression<BigInteger> rightShift(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.RIGHT_SHIFT, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> rightShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.RIGHT_SHIFT, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> rightShift(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.RIGHT_SHIFT, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final Expression<BigInteger> leftShift(Integer bitNumber) {
        return DualExpresion.build(this, DualOperator.LEFT_SHIFT, (_Expression<?>) SQLs.param(SQLs.obtainParamMeta(this), bitNumber));
    }

    @Override
    public final <O> Expression<BigInteger> leftShift(Expression<O> bitNumber) {
        return DualExpresion.build(this, DualOperator.LEFT_SHIFT, (_Expression<?>) bitNumber);
    }

    @Override
    public final Expression<BigInteger> leftShift(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.LEFT_SHIFT, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<BigInteger> leftShift(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.LEFT_SHIFT, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> leftShift(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.LEFT_SHIFT, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<E> plusOther(Expression<O> other) {
        return DualExpresion.build(this, DualOperator.ADD, (_Expression<?>) other);
    }

    @Override
    public final Expression<E> plusOther(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.ADD, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<E> plusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.ADD, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> plusOther(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.ADD, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<E> minusOther(Expression<O> other) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, (_Expression<?>) other);
    }

    @Override
    public final Expression<E> minusOther(String subQueryAlias, String derivedFieldName) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, (_Expression<?>) SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <O> Expression<E> minusOther(String tableAlias, FieldMeta<?, O> fieldMeta) {
        return DualExpresion.build(this, DualOperator.SUBTRACT, (_Expression<?>) SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O, S extends Expression<O>> Expression<E> minusOther(Function<C, S> expOrSubQuery) {
        @SuppressWarnings("unchecked")
        C criteria = (C) CriteriaContextHolder.getContext();
        return DualExpresion.build(this, DualOperator.SUBTRACT, (_Expression<?>) expOrSubQuery.apply(criteria));
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType) {
        return ConvertExpressionImpl.build(this, _MappingFactory.getMapping(convertType));
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        return ConvertExpressionImpl.build(this, longMapping);
    }

    public final Expression<E> brackets() {
        return BracketsExpression.build(this);
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


    /*################################## blow protected template method ##################################*/

    @Override
    public final void appendSortPart(_SqlContext context) {
        this.appendSql(context);
    }


}
