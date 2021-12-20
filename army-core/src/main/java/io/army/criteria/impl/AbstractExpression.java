package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.impl.inner._Expression;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

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
        } else if (this instanceof ValueExpression) {
            nullable = ((ValueExpression<?>) this).value() == null;
        } else if (this instanceof ScalarSubQuery) {
            nullable = ((ScalarSubQuery<?>) this).selection().nullable();
        } else {
            nullable = false;
        }
        return nullable;
    }

    @Override
    public final IPredicate equal(Expression<?> expression) {
        return DualPredicate.create(this, DualOperator.EQ, expression);
    }

    @Override
    public final IPredicate equal(Object parameter) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.param(this, parameter));
    }

    @Nullable
    @Override
    public final IPredicate ifEqual(@Nullable Object parameter) {
        return parameter == null ? null : this.equal(parameter);
    }

    @Override
    public final IPredicate equal(String subQueryAlias, String fieldAlias) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate equal(String tableAlias, FieldMeta<?, ?> field) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, O> IPredicate equal(Function<C, Expression<O>> expOrSubQuery) {
        return DualPredicate.create(this, DualOperator.EQ, expOrSubQuery);
    }

    @Override
    public final <C, O> IPredicate equalAny(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final <C, O> IPredicate equalSome(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final IPredicate lessThan(Expression<?> expression) {
        return DualPredicate.create(this, DualOperator.LT, expression);
    }

    @Override
    public final IPredicate lessThan(Object parameter) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.param(this, parameter));
    }

    @Override
    public final IPredicate ifLessThan(@Nullable Object parameter) {
        return parameter == null ? null : this.lessThan(parameter);
    }

    @Override
    public final IPredicate lessThan(String subQueryAlias, String fieldAlias) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate lessThan(String tableAlias, FieldMeta<?, ?> field) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, O> IPredicate lessThan(Function<C, Expression<O>> expOrSubQuery) {
        return DualPredicate.create(this, DualOperator.LT, expOrSubQuery);
    }

    @Override
    public final <C, O> IPredicate lessThanAny(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final <C, O> IPredicate lessThanSome(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final <C, O> IPredicate lessThanAll(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ALL, subQuery);
    }

    @Override
    public final IPredicate lessEqual(Expression<?> expression) {
        return DualPredicate.create(this, DualOperator.LE, expression);
    }

    @Override
    public final IPredicate lessEqual(Object parameter) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.param(this, parameter));
    }

    @Override
    public final IPredicate ifLessEqual(@Nullable Object parameter) {
        return parameter == null ? null : this.lessEqual(parameter);
    }

    @Override
    public final IPredicate lessEqual(String subQueryAlias, String fieldAlias) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate lessEqual(String tableAlias, FieldMeta<?, ?> field) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, O> IPredicate lessEqual(Function<C, Expression<O>> expOrSubQuery) {
        return DualPredicate.create(this, DualOperator.LE, expOrSubQuery);
    }

    @Override
    public final <C, O> IPredicate lessEqualAny(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final <C, O> IPredicate lessEqualSome(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final <C, O> IPredicate lessEqualAll(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ALL, subQuery);
    }

    @Override
    public final IPredicate greatThan(Expression<?> expression) {
        return DualPredicate.create(this, DualOperator.GT, expression);
    }

    @Override
    public final IPredicate greatThan(Object parameter) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.param(this, parameter));
    }

    @Override
    public final IPredicate ifGreatThan(@Nullable Object parameter) {
        return parameter == null ? null : this.greatThan(parameter);
    }

    @Override
    public final IPredicate greatThan(String subQueryAlias, String fieldAlias) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate greatThan(String tableAlias, FieldMeta<?, ?> field) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, O> IPredicate greatThan(Function<C, Expression<O>> expOrSubQuery) {
        return DualPredicate.create(this, DualOperator.GT, expOrSubQuery);
    }

    @Override
    public final <C, O> IPredicate greatThanAny(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final <C, O> IPredicate greatThanSome(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final <C, O> IPredicate greatThanAll(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ALL, subQuery);
    }

    @Override
    public final IPredicate greatEqual(Expression<?> expression) {
        return DualPredicate.create(this, DualOperator.GE, expression);
    }

    @Override
    public final IPredicate greatEqual(Object parameter) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.param(this, parameter));
    }

    @Override
    public final IPredicate IfGreatEqual(@Nullable Object parameter) {
        return parameter == null ? null : this.greatEqual(parameter);
    }

    @Override
    public final IPredicate greatEqual(String subQueryAlias, String fieldAlias) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate greatEqual(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.field(tableAlias, fieldMeta));
    }


    @Override
    public final <C, O> IPredicate greatEqual(Function<C, Expression<O>> expOrSubQuery) {
        return DualPredicate.create(this, DualOperator.GE, expOrSubQuery);
    }

    @Override
    public final <C, O> IPredicate greatEqualAny(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final <C, O> IPredicate greatEqualSome(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final <C, O> IPredicate greatEqualAll(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ALL, subQuery);
    }

    @Override
    public final IPredicate notEqual(Expression<?> expression) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, expression);
    }

    @Override
    public final IPredicate notEqual(Object constant) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.param(this, constant));
    }

    @Override
    public final IPredicate ifNotEqual(@Nullable Object constant) {
        return constant == null ? null : this.notEqual(constant);
    }

    @Override
    public final IPredicate notEqual(String subQueryAlias, String fieldAlias) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.ref(subQueryAlias, fieldAlias));
    }

    @Override
    public final IPredicate notEqual(String tableAlias, FieldMeta<?, ?> field) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, O> IPredicate notEqual(Function<C, Expression<O>> expOrSubQuery) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, expOrSubQuery);
    }

    @Override
    public final <C, O> IPredicate notEqualAny(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final <C, O> IPredicate notEqualSome(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final <C, O> IPredicate notEqualAll(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ALL, subQuery);
    }


    @Override
    public final IPredicate between(Expression<?> first, Expression<?> second) {
        return BetweenPredicate.build(this, first, second);
    }

    @Override
    public final IPredicate between(Object first, Object second) {
        return BetweenPredicate.build(this, SQLs.param(this, first), SQLs.param(this, second));
    }

    @Override
    public final IPredicate ifBetween(@Nullable Object first, @Nullable Object second) {
        final IPredicate predicate;
        if (first != null && second != null) {
            predicate = this.between(first, second);
        } else {
            predicate = null;
        }
        return predicate;
    }

    @Override
    public final IPredicate between(Expression<?> first, Object second) {
        return BetweenPredicate.build(this, first, SQLs.param(this, second));
    }

    @Override
    public final IPredicate ifBetween(Expression<?> first, @Nullable Object second) {
        final IPredicate predicate;
        if (second != null) {
            predicate = this.between(first, second);
        } else {
            predicate = null;
        }
        return predicate;
    }

    @Override
    public final IPredicate between(Object first, Expression<?> second) {
        return BetweenPredicate.build(this, SQLs.param(this, first), second);
    }

    @Override
    public final IPredicate ifBetween(@Nullable Object first, Expression<?> second) {
        final IPredicate predicate;
        if (first != null) {
            predicate = this.between(first, second);
        } else {
            predicate = null;
        }
        return predicate;
    }

    @Override
    public <C> IPredicate IfBetween(Function<C, BetweenWrapper> function) {
        final BetweenWrapper wrapper;
        wrapper = function.apply(CriteriaContextStack.getCriteria());
        final IPredicate predicate;
        if (wrapper != null) {
            predicate = BetweenPredicate.build(this, wrapper.first(), wrapper.second());
        } else {
            predicate = null;
        }
        return predicate;
    }

    @Override
    public final <C> IPredicate between(Function<C, BetweenWrapper> function) {
        final BetweenWrapper wrapper;
        wrapper = function.apply(CriteriaContextStack.getCriteria());
        assert wrapper != null;
        return BetweenPredicate.build(this, wrapper.first(), wrapper.second());
    }

    @Override
    public final IPredicate isNull() {
        return UnaryPredicate.create(UnaryOperator.IS_NULL, this);
    }

    @Override
    public final IPredicate isNotNull() {
        return UnaryPredicate.create(UnaryOperator.IS_NOT_NULL, this);
    }

    @Override
    public final <O> IPredicate in(Collection<O> values) {
        return DualPredicate.create(this, DualOperator.IN, SQLs.collectionParam(this, values));
    }

    @Override
    public final <O> IPredicate ifIn(@Nullable Collection<O> parameters) {
        final IPredicate predicate;
        if (parameters != null && parameters.size() > 0) {
            predicate = this.in(parameters);
        } else {
            predicate = null;
        }
        return predicate;
    }

    @Override
    public final <O> IPredicate in(Expression<Collection<O>> values) {
        return DualPredicate.create(this, DualOperator.IN, values);
    }

    @Override
    public final <C, O> IPredicate in(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.IN, subQuery);
    }

    @Override
    public final <O> IPredicate notIn(Collection<O> values) {
        return DualPredicate.create(this, DualOperator.NOT_IN, SQLs.collectionParam(this, values));
    }

    @Override
    public final IPredicate ifNotIn(@Nullable Collection<?> values) {
        return (values == null || values.size() == 0) ? null : this.notIn(values);
    }

    @Override
    public final <O> IPredicate notIn(Expression<Collection<O>> values) {
        return DualPredicate.create(this, DualOperator.NOT_IN, values);
    }

    @Override
    public final <C, O> IPredicate notIn(Function<C, ColumnSubQuery<O>> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_IN, subQuery);
    }

    @Override
    public final IPredicate like(String pattern) {
        return DualPredicate.create(this, DualOperator.LIKE, SQLs.param(this, pattern));
    }

    @Override
    public final IPredicate like(Expression<String> pattern) {
        return DualPredicate.create(this, DualOperator.LIKE, pattern);
    }

    @Override
    public final <C> IPredicate like(Function<C, Expression<String>> expOrSubQuery) {
        return DualPredicate.create(this, DualOperator.LIKE, expOrSubQuery);
    }

    @Override
    public final IPredicate notLike(String pattern) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, SQLs.param(this, pattern));
    }

    @Override
    public final IPredicate notLike(Expression<String> pattern) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, pattern);
    }

    @Override
    public final <C> IPredicate notLike(Function<C, Expression<String>> expOrSubQuery) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, expOrSubQuery);
    }

    @Override
    public final Expression<E> mod(Expression<?> operator) {
        return DualExpression.create(this, DualOperator.MOD, operator);
    }

    @Override
    public final Expression<E> mod(Object operator) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.param(this, operator));
    }

    @Override
    public final Expression<E> mod(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> mod(String tableAlias, FieldMeta<?, ?> field) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, O> Expression<E> mod(Function<C, Expression<O>> expOrSubQuery) {
        return DualExpression.create(this, DualOperator.MOD, expOrSubQuery);
    }

    @Override
    public final Expression<E> multiply(Expression<?> multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, multiplicand);
    }

    @Override
    public final Expression<E> multiply(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.param(this, multiplicand));
    }

    @Override
    public final Expression<E> multiply(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> multiply(String tableAlias, FieldMeta<?, ?> field) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, O> Expression<E> multiply(Function<C, Expression<O>> expOrSubQuery) {
        return DualExpression.create(this, DualOperator.MULTIPLY, expOrSubQuery);
    }

    @Override
    public final Expression<E> plus(Expression<?> augend) {
        return DualExpression.create(this, DualOperator.PLUS, augend);
    }

    @Override
    public final Expression<E> plus(Object augend) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.param(this, augend));
    }

    @Override
    public final Expression<E> plus(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> plus(String tableAlias, FieldMeta<?, ?> field) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, O> Expression<E> plus(Function<C, Expression<O>> expOrSubQuery) {
        return DualExpression.create(this, DualOperator.PLUS, expOrSubQuery);
    }

    @Override
    public final Expression<E> minus(Expression<?> subtrahend) {
        return DualExpression.create(this, DualOperator.MINUS, subtrahend);
    }

    @Override
    public final Expression<E> minus(Object subtrahend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.param(this, subtrahend));
    }

    @Override
    public final Expression<E> minus(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> minus(String tableAlias, FieldMeta<?, ?> field) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, O> Expression<E> minus(Function<C, Expression<O>> expOrSubQuery) {
        return DualExpression.create(this, DualOperator.MINUS, expOrSubQuery);
    }

    @Override
    public final Expression<E> divide(Expression<?> divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, divisor);
    }

    @Override
    public final Expression<E> divide(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.param(this, divisor));
    }

    @Override
    public final Expression<E> divide(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> divide(String tableAlias, FieldMeta<?, ?> field) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, O> Expression<E> divide(Function<C, Expression<O>> expOrSubQuery) {
        return DualExpression.create(this, DualOperator.DIVIDE, expOrSubQuery);
    }

    @Override
    public final Expression<E> negate() {
        return UnaryExpression.create(this, UnaryOperator.NEGATED);
    }

    @Override
    public final Expression<E> and(Expression<?> operand) {
        return DualExpression.create(this, DualOperator.AND, operand);
    }

    @Override
    public final Expression<E> and(Object parameter) {
        return DualExpression.create(this, DualOperator.AND, SQLs.param(this, parameter));
    }

    @Override
    public final Expression<E> and(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.AND, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> and(String tableAlias, FieldMeta<?, ?> field) {
        return DualExpression.create(this, DualOperator.AND, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, O> Expression<E> and(Function<C, Expression<O>> expOrSubQuery) {
        return DualExpression.create(this, DualOperator.AND, expOrSubQuery);
    }

    @Override
    public final Expression<E> or(Expression<?> operand) {
        return DualExpression.create(this, DualOperator.OR, operand);
    }

    @Override
    public final Expression<E> or(Object operand) {
        return DualExpression.create(this, DualOperator.OR, SQLs.param(this, operand));
    }

    @Override
    public final Expression<E> or(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.OR, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> or(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        return DualExpression.create(this, DualOperator.OR, SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O> Expression<E> or(Function<C, Expression<O>> expOrSubQuery) {
        return DualExpression.create(this, DualOperator.OR, expOrSubQuery);
    }

    @Override
    public final Expression<E> xor(Expression<?> operand) {
        return DualExpression.create(this, DualOperator.XOR, operand);
    }

    @Override
    public final Expression<E> xor(Object parameter) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.param(this, parameter));
    }

    @Override
    public final Expression<E> xor(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> xor(String tableAlias, FieldMeta<?, ?> fieldMeta) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.field(tableAlias, fieldMeta));
    }

    @Override
    public final <C, O> Expression<E> xor(Function<C, Expression<O>> expOrSubQuery) {
        return DualExpression.create(this, DualOperator.XOR, expOrSubQuery);
    }

    @Override
    public final Expression<E> inversion() {
        return UnaryExpression.create(this, UnaryOperator.INVERT);
    }

    @Override
    public final Expression<E> rightShift(Number bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.param(this, bitNumber));
    }

    @Override
    public final <N extends Number> Expression<E> rightShift(Expression<N> bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, bitNumber);
    }

    @Override
    public final Expression<E> rightShift(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <N extends Number> Expression<E> rightShift(String tableAlias, FieldMeta<?, N> field) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, N extends Number> Expression<E> rightShift(Function<C, Expression<N>> expOrSubQuery) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, expOrSubQuery);
    }

    @Override
    public final Expression<E> leftShift(Number bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.param(this, bitNumber));
    }

    @Override
    public final <N extends Number> Expression<E> leftShift(Expression<N> bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, bitNumber);
    }

    @Override
    public final Expression<E> leftShift(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final <N extends Number> Expression<E> leftShift(String tableAlias, FieldMeta<?, N> field) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.field(tableAlias, field));
    }

    @Override
    public final <C, N extends Number> Expression<E> leftShift(Function<C, Expression<N>> expOrSubQuery) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, expOrSubQuery);
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType) {
        return ConvertExpressionImpl.build(this, _MappingFactory.getMapping(convertType));
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        return ConvertExpressionImpl.build(this, longMapping);
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, FieldMeta<?, O> longMapping) {
        return null;
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
