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
import java.util.Objects;
import java.util.function.Function;

/**
 * this class is base class of most implementation of {@link Expression}
 */
abstract class OperationExpression<E> implements _Expression<E> {

    OperationExpression() {
    }


    @SuppressWarnings("unchecked")
    @Override
    public final Selection as(final String alias) {
        final Selection selection;
        if (this instanceof GenericField) {
            selection = new FieldSelectionImpl<>((GenericField<?, E>) this, alias);
        } else {
            selection = new ExpressionSelection(this, alias);
        }
        return selection;
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
    public final IPredicate equal(Expression<?> operand) {
        return DualPredicate.create(this, DualOperator.EQ, operand);
    }

    @Override
    public final IPredicate equal(Object parameter) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.paramWithExp(this, parameter));
    }

    @Nullable
    @Override
    public final IPredicate ifEqual(final @Nullable Object parameter) {
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
    public final <C> IPredicate equalAny(Function<C, ColumnSubQuery> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, ColumnSubQuery> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final IPredicate lessThan(Expression<?> expression) {
        return DualPredicate.create(this, DualOperator.LT, expression);
    }

    @Override
    public final IPredicate lessThan(Object parameter) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.paramWithExp(this, parameter));
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
    public final <C, O> IPredicate lessThan(Function<C, Expression<O>> function) {
        return DualPredicate.create(this, DualOperator.LT, function);
    }


    @Override
    public final <C, O> IPredicate lessThanAny(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ANY, function);
    }

    @Override
    public final <C, O> IPredicate lessThanSome(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.SOME, function);
    }

    @Override
    public final <C, O> IPredicate lessThanAll(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate lessEqual(Expression<?> operand) {
        return DualPredicate.create(this, DualOperator.LE, operand);
    }

    @Override
    public final IPredicate lessEqual(Object parameter) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.paramWithExp(this, parameter));
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
    public final <C, O> IPredicate lessEqual(Function<C, Expression<O>> function) {
        return DualPredicate.create(this, DualOperator.LE, function);
    }

    @Override
    public final <C, O> IPredicate lessEqualAny(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ANY, function);
    }

    @Override
    public final <C, O> IPredicate lessEqualSome(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.SOME, function);
    }

    @Override
    public final <C, O> IPredicate lessEqualAll(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate greatThan(Expression<?> operand) {
        return DualPredicate.create(this, DualOperator.GT, operand);
    }

    @Override
    public final IPredicate greatThan(Object parameter) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.paramWithExp(this, parameter));
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
    public final <C, O> IPredicate greatThan(Function<C, Expression<O>> function) {
        return DualPredicate.create(this, DualOperator.GT, function);
    }

    @Override
    public final <C, O> IPredicate greatThanAny(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ANY, function);
    }

    @Override
    public final <C, O> IPredicate greatThanSome(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.SOME, function);
    }

    @Override
    public final <C, O> IPredicate greatThanAll(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate greatEqual(Expression<?> operand) {
        return DualPredicate.create(this, DualOperator.GE, operand);
    }

    @Override
    public final IPredicate greatEqual(Object parameter) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.paramWithExp(this, parameter));
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
    public final IPredicate greatEqual(String tableAlias, FieldMeta<?, ?> field) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.field(tableAlias, field));
    }


    @Override
    public final <C, O> IPredicate greatEqual(Function<C, Expression<O>> function) {
        return DualPredicate.create(this, DualOperator.GE, function);
    }

    @Override
    public final <C> IPredicate greatEqualAny(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ANY, function);
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.SOME, function);
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate notEqual(Expression<?> expression) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, expression);
    }

    @Override
    public final IPredicate notEqual(Object parameter) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final IPredicate ifNotEqual(@Nullable Object parameter) {
        return parameter == null ? null : this.notEqual(parameter);
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
    public final <C, O> IPredicate notEqual(Function<C, Expression<O>> function) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, function);
    }

    @Override
    public final <C, O> IPredicate notEqualAny(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ANY, function);
    }

    @Override
    public final <C, O> IPredicate notEqualSome(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.SOME, function);
    }

    @Override
    public final <C, O> IPredicate notEqualAll(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ALL, function);
    }


    @Override
    public final IPredicate between(Expression<?> first, Expression<?> parameter) {
        return BetweenPredicate.between(this, first, parameter);
    }

    @Override
    public final IPredicate between(Object firstParameter, Object secondParameter) {
        Objects.requireNonNull(firstParameter);
        Objects.requireNonNull(secondParameter);
        return BetweenPredicate.between(this, SQLs.paramWithExp(this, firstParameter), SQLs.paramWithExp(this, secondParameter));
    }

    @Override
    public final IPredicate ifBetween(@Nullable Object firstParameter, @Nullable Object secondParameter) {
        final IPredicate predicate;
        if (firstParameter != null && secondParameter != null) {
            predicate = this.between(firstParameter, secondParameter);
        } else {
            predicate = null;
        }
        return predicate;
    }

    @Override
    public final IPredicate between(Expression<?> first, Object parameter) {
        return BetweenPredicate.between(this, first, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final IPredicate ifBetween(Expression<?> first, @Nullable Object parameter) {
        return parameter == null ? null : this.between(first, parameter);
    }

    @Override
    public final IPredicate between(Object parameter, Expression<?> second) {
        return BetweenPredicate.between(this, SQLs.paramWithExp(this, parameter), second);
    }

    @Override
    public final IPredicate ifBetween(@Nullable Object firstParameter, Expression<?> second) {
        final IPredicate predicate;
        if (firstParameter != null) {
            predicate = this.between(firstParameter, second);
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
        return BetweenPredicate.between(this, wrapper.first(), wrapper.second());
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
        return (parameters == null || parameters.size() == 0) ? null : this.in(parameters);
    }

    @Override
    public final <O> IPredicate in(Expression<Collection<O>> parameters) {
        return DualPredicate.create(this, DualOperator.IN, parameters);
    }

    @Override
    public final <C, O> IPredicate in(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.IN, function);
    }

    @Override
    public final <O> IPredicate notIn(Collection<O> parameters) {
        return DualPredicate.create(this, DualOperator.NOT_IN, SQLs.collectionParam(this, parameters));
    }

    @Override
    public final <O> IPredicate ifNotIn(@Nullable Collection<O> parameters) {
        return (parameters == null || parameters.size() == 0) ? null : this.notIn(parameters);
    }

    @Override
    public final <O> IPredicate notIn(Expression<Collection<O>> values) {
        return DualPredicate.create(this, DualOperator.NOT_IN, values);
    }

    @Override
    public final <C, O> IPredicate notIn(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_IN, function);
    }

    @Override
    public final IPredicate like(String patternParameter) {
        return DualPredicate.create(this, DualOperator.LIKE, SQLs.paramWithExp(this, patternParameter));
    }

    @Override
    public final IPredicate like(Expression<String> pattern) {
        return DualPredicate.create(this, DualOperator.LIKE, pattern);
    }

    @Override
    public final <C> IPredicate like(Function<C, Expression<String>> function) {
        return DualPredicate.create(this, DualOperator.LIKE, function);
    }

    @Override
    public final IPredicate notLike(String patternParameter) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, SQLs.paramWithExp(this, patternParameter));
    }

    @Override
    public final IPredicate notLike(Expression<String> pattern) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, pattern);
    }

    @Override
    public final <C> IPredicate notLike(Function<C, Expression<String>> function) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, function);
    }

    @Override
    public final Expression<E> mod(Expression<?> operator) {
        return DualExpression.create(this, DualOperator.MOD, operator);
    }

    @Override
    public final Expression<E> mod(Object operator) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.paramWithExp(this, operator));
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
    public final <C, O> Expression<E> mod(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.MOD, function);
    }

    @Override
    public final Expression<E> multiply(Expression<?> multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, multiplicand);
    }

    @Override
    public final Expression<E> multiply(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.paramWithExp(this, multiplicand));
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
    public final <C, O> Expression<E> multiply(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.MULTIPLY, function);
    }

    @Override
    public final Expression<E> plus(Expression<?> augend) {
        return DualExpression.create(this, DualOperator.PLUS, augend);
    }

    @Override
    public final Expression<E> plus(Object parameter) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.paramWithExp(this, parameter));
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
    public final <C, O> Expression<E> plus(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.PLUS, function);
    }

    @Override
    public final Expression<E> minus(Expression<?> subtrahend) {
        return DualExpression.create(this, DualOperator.MINUS, subtrahend);
    }

    @Override
    public final Expression<E> minus(Object subtrahend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.paramWithExp(this, subtrahend));
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
    public final <C, O> Expression<E> minus(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.MINUS, function);
    }

    @Override
    public final Expression<E> divide(Expression<?> divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, divisor);
    }

    @Override
    public final Expression<E> divide(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.paramWithExp(this, divisor));
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
    public final <C, O> Expression<E> divide(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.DIVIDE, function);
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
        return DualExpression.create(this, DualOperator.AND, SQLs.paramWithExp(this, parameter));
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
    public final <C, O> Expression<E> and(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.AND, function);
    }

    @Override
    public final Expression<E> or(Expression<?> operand) {
        return DualExpression.create(this, DualOperator.OR, operand);
    }

    @Override
    public final Expression<E> or(Object operand) {
        return DualExpression.create(this, DualOperator.OR, SQLs.paramWithExp(this, operand));
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
    public final <C, O> Expression<E> or(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.OR, function);
    }

    @Override
    public final Expression<E> xor(Expression<?> operand) {
        return DualExpression.create(this, DualOperator.XOR, operand);
    }

    @Override
    public final Expression<E> xor(Object parameter) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.paramWithExp(this, parameter));
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
    public final <C, O> Expression<E> xor(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.XOR, function);
    }

    @Override
    public final Expression<E> inversion() {
        return UnaryExpression.create(this, UnaryOperator.INVERT);
    }

    @Override
    public final Expression<E> rightShift(Number bitNumberParameter) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.paramWithExp(this, bitNumberParameter));
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
    public final <C, N extends Number> Expression<E> rightShift(Function<C, Expression<N>> function) {
        return DualExpression.functionCreate(this, DualOperator.RIGHT_SHIFT, function);
    }

    @Override
    public final Expression<E> leftShift(Number bitNumberParameter) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.paramWithExp(this, bitNumberParameter));
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
    public final <C, N extends Number> Expression<E> leftShift(Function<C, Expression<N>> function) {
        return DualExpression.functionCreate(this, DualOperator.LEFT_SHIFT, function);
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType) {
        return CastExpression.cast(this, _MappingFactory.getMapping(convertType));
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        return CastExpression.cast(this, longMapping);
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, FieldMeta<?, O> longMapping) {
        return null;
    }

    public final Expression<E> brackets() {
        return BracketsExpression.bracket(this);
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
