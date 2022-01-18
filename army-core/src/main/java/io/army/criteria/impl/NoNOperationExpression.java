package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.dialect._SqlContext;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.ParamValue;

import java.util.Collection;
import java.util.function.Function;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link SQLs.DefaultWord}</li>
 *         <li>{@link SQLs.NullWord}</li>
 *         <li>{@link CollectionParamExpression}</li>
 *         <li>{@link ParamExpression.OptimizingNullExpression}</li>
 *     </ul>
 * </p>
 *
 * @param <E> java type of expression
 */
abstract class NoNOperationExpression<E> implements ArmyExpression<E> {

    final String ERROR_MSG = "Non Expression not support this method.";

    NoNOperationExpression() {
    }

    @Override
    public final boolean isVersion() {
        // always false
        return false;
    }

    @Override
    public final Selection as(String alias) {
        if (!(this instanceof ParamValue)) {
            throw unsupportedOperation();
        }
        return new ExpressionSelection(this, alias);
    }

    @Override
    public final boolean nullableExp() {
        final boolean nullable;
        if (this instanceof ValueExpression) {
            nullable = ((ValueExpression<?>) this).value() == null;
        } else {
            nullable = this instanceof SQLUtils.NullWord;
        }
        return nullable;
    }

    @Override
    public final IPredicate equal(Expression<?> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equal(String subQueryAlias, String fieldAlias) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate equal(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equal(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifEqual(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate equal(Function<C, Expression<O>> expOrSubQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalAny(Function<C, ColumnSubQuery> subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, ColumnSubQuery> subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThan(Expression<?> expression) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThan(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessThan(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThan(String subQueryAlias, String fieldAlias) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThan(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate lessThan(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate lessThanAny(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate lessThanSome(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate lessThanAll(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqual(Expression<?> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqual(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessEqual(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqual(String subQueryAlias, String fieldAlias) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqual(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate lessEqual(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate lessEqualAny(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate lessEqualSome(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate lessEqualAll(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThan(Expression<?> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThan(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatThan(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThan(String subQueryAlias, String fieldAlias) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThan(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate greatThan(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate greatThanAny(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate greatThanSome(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate greatThanAll(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqual(Expression<?> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqual(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate IfGreatEqual(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqual(String subQueryAlias, String fieldAlias) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqual(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate greatEqual(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAny(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(Expression<?> expression) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotEqual(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(String subQueryAlias, String fieldAlias) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqual(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate notEqual(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate notEqualAny(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate notEqualSome(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate notEqualAll(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(Expression<?> first, Expression<?> parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(Object firstParameter, Object secondParameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifBetween(@Nullable Object firstParameter, @Nullable Object secondParameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(Expression<?> first, Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifBetween(Expression<?> first, @Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate between(Object parameter, Expression<?> second) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifBetween(@Nullable Object firstParameter, Expression<?> second) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate between(Function<C, BetweenWrapper> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate isNull() {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate isNotNull() {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate in(Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate ifIn(@Nullable Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate in(Expression<Collection<O>> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate in(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate notIn(Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate ifNotIn(@Nullable Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate notIn(Expression<Collection<O>> values) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate notIn(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate like(String patternParameter) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate like(Function<C, Expression<String>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(String patternParameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate like(Expression<String> pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(Expression<String> pattern) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notLike(Function<C, Expression<String>> function) {
        throw unsupportedOperation();
    }


    @Override
    public final Expression<E> mod(Expression<?> operator) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> mod(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> mod(String subQueryAlias, String derivedFieldName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> mod(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> Expression<E> mod(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> multiply(Expression<?> multiplicand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> multiply(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> multiply(String subQueryAlias, String derivedFieldName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> multiply(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> Expression<E> multiply(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> plus(Expression<?> augend) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> plus(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> plus(String subQueryAlias, String derivedFieldName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> plus(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> Expression<E> plus(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> minus(Expression<?> subtrahend) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> minus(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> minus(String subQueryAlias, String derivedFieldName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> minus(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> Expression<E> minus(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> divide(Expression<?> divisor) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> divide(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> divide(String subQueryAlias, String derivedFieldName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> divide(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> Expression<E> divide(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> negate() {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> and(Expression<?> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> and(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> and(String subQueryAlias, String derivedFieldName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> and(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> Expression<E> and(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> or(Expression<?> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> or(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> or(String subQueryAlias, String derivedFieldName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> or(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> Expression<E> or(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> xor(Expression<?> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> xor(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> xor(String subQueryAlias, String derivedFieldName) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> xor(String tableAlias, FieldMeta<?, ?> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> Expression<E> xor(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> inversion() {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> rightShift(Number bitNumberParameter) {
        throw unsupportedOperation();
    }

    @Override
    public final <N extends Number> Expression<E> rightShift(Expression<N> bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final <N extends Number> Expression<E> rightShift(String tableAlias, FieldMeta<?, N> field) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> rightShift(String subQueryAlias, String derivedFieldName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, N extends Number> Expression<E> rightShift(Function<C, Expression<N>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> leftShift(Number bitNumberParameter) {
        throw unsupportedOperation();
    }

    @Override
    public final <N extends Number> Expression<E> leftShift(Expression<N> bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> leftShift(String subQueryAlias, String derivedFieldName) {
        throw unsupportedOperation();
    }

    @Override
    public final <N extends Number> Expression<E> leftShift(String tableAlias, FieldMeta<?, N> field) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, N extends Number> Expression<E> leftShift(Function<C, Expression<N>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, FieldMeta<?, O> longMapping) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> brackets() {
        throw unsupportedOperation();
    }

    @Override
    public final SortPart asc() {
        throw unsupportedOperation();
    }

    @Override
    public final SortPart desc() {
        throw unsupportedOperation();
    }

    @Override
    public final boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        return false;
    }

    @Override
    public final boolean containsFieldOf(TableMeta<?> tableMeta) {
        return false;
    }


    @Override
    public final boolean containsSubQuery() {
        return false;
    }

    @Override
    public final int containsFieldCount(TableMeta<?> tableMeta) {
        return 0;
    }


    @Override
    public final void appendSortPart(_SqlContext context) {
        throw new UnsupportedOperationException(ERROR_MSG);
    }

    @Deprecated
    protected void afterSpace(_SqlContext context) {
        throw new UnsupportedOperationException();
    }


    static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("Non Expression not support this method.");
    }

}
