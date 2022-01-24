package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;
import io.army.stmt.ParamValue;

import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

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
abstract class NoNOperationExpression<E> implements ArmyExpression<E>, _SortItem {


    NoNOperationExpression() {
    }


    @Override
    public final Selection as(String alias) {
        if (!(this instanceof ParamValue)) {
            throw unsupportedOperation();
        }
        return new ExpressionSelection(this, alias);
    }

    @Override
    public final boolean isNullableValue() {
        final boolean nullable;
        if (this instanceof ValueExpression) {
            nullable = ((ValueExpression<?>) this).value() == null;
        } else {
            nullable = this instanceof SQLs.NullWord;
        }
        return nullable;
    }

    @Override
    public final IPredicate equal(Expression<?> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equal(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifEqual(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifEqualParam(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate equal(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate equal(Supplier<Expression<O>> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalAny(Function<C, ColumnSubQuery> subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalAny(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, ColumnSubQuery> subQuery) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate equalSome(Supplier<ColumnSubQuery> subQuery) {
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
    public final IPredicate lessThanParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessThan(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessThanParam(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate lessThan(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate lessThan(Supplier<Expression<O>> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessThanAny(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanAny(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessThanSome(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanSome(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessThanAll(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessThanAll(Supplier<ColumnSubQuery> supplier) {
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
    public final IPredicate lessEqualParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessEqual(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLessEqualParam(@Nullable Object parameter) {
        throw unsupportedOperation();
    }


    @Override
    public final <C, O> IPredicate lessEqual(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate lessEqual(Supplier<Expression<O>> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualAny(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAny(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualSome(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualSome(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate lessEqualAll(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate lessEqualAll(Supplier<ColumnSubQuery> supplier) {
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
    public final IPredicate greatThanParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanNamed(String paramName) {
        throw unsupportedOperation();
    }


    @Override
    public final IPredicate ifGreatThan(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatThanParam(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate greatThan(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate greatEqual(Supplier<Expression<O>> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatThanAny(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAny(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatThanSome(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualSome(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatThanAll(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualAll(Supplier<ColumnSubQuery> supplier) {
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
    public final IPredicate greatEqualParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatEqualNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate IfGreatEqual(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifGreatEqualParam(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate greatEqual(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate greatThan(Supplier<Expression<O>> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAny(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanAny(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanSome(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate greatThanAll(Supplier<ColumnSubQuery> supplier) {
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
    public final IPredicate notEqualParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotEqual(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotEqualParam(@Nullable Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> IPredicate notEqual(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate notEqual(Supplier<Expression<O>> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualAny(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAny(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualSome(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualSome(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notEqualAll(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notEqualAll(Supplier<ColumnSubQuery> supplier) {
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
    public final <O> IPredicate inParam(Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate inNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate ifIn(@Nullable Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate ifInParam(@Nullable Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate in(Expression<Collection<O>> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate in(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate in(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate notIn(Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notIn(Supplier<ColumnSubQuery> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate notInParam(Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notInNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate ifNotIn(@Nullable Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate ifNotInParam(@Nullable Collection<O> parameters) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> IPredicate notIn(Expression<Collection<O>> values) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate notIn(Function<C, ColumnSubQuery> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate like(String patternParameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifLike(@Nullable String patternParameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate likeNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C> IPredicate like(Function<C, Expression<String>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate like(Supplier<Expression<String>> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLike(String patternParameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate ifNotLike(@Nullable String patternParameter) {
        throw unsupportedOperation();
    }

    @Override
    public final IPredicate notLikeNamed(String paramName) {
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
    public final IPredicate notLike(Supplier<Expression<String>> supplier) {
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
    public final Expression<E> modParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> modNamed(String paramName) {
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
    public final <O> Expression<E> mod(Supplier<Expression<O>> supplier) {
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
    public final Expression<E> multiplyParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> multiplyNamed(String paramName) {
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
    public final <O> Expression<E> multiply(Supplier<Expression<O>> supplier) {
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
    public final Expression<E> plusParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> plusNamed(String paramName) {
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
    public final <O> Expression<E> plus(Supplier<Expression<O>> supplier) {
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
    public final Expression<E> minusParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> minusNamed(String paramName) {
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
    public final <O> Expression<E> minus(Supplier<Expression<O>> supplier) {
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
    public final Expression<E> divideParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> divideNamed(String paramName) {
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
    public final <O> Expression<E> divide(Supplier<Expression<O>> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> negate() {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> bitwiseAnd(Expression<?> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> bitwiseAnd(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> bitwiseAndParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> bitwiseAndNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> Expression<E> bitwiseAnd(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> Expression<E> bitwiseAnd(Supplier<Expression<O>> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> bitwiseOr(Expression<?> operand) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> bitwiseOr(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> bitwiseOrParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> bitwiseOrNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> Expression<E> bitwiseOr(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> Expression<E> bitwiseOr(Supplier<Expression<O>> supplier) {
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
    public final Expression<E> xorParam(Object parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> xorNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, O> Expression<E> xor(Function<C, Expression<O>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <O> Expression<E> xor(Supplier<Expression<O>> supplier) {
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
    public final Expression<E> rightShiftParam(Number parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> rightShiftNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <N extends Number> Expression<E> rightShift(Expression<N> bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, N extends Number> Expression<E> rightShift(Function<C, Expression<N>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <N extends Number> Expression<E> rightShift(Supplier<Expression<N>> supplier) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> leftShift(Number bitNumberParameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> leftShiftParam(Number parameter) {
        throw unsupportedOperation();
    }

    @Override
    public final Expression<E> leftShiftNamed(String paramName) {
        throw unsupportedOperation();
    }

    @Override
    public final <N extends Number> Expression<E> leftShift(Expression<N> bitNumber) {
        throw unsupportedOperation();
    }

    @Override
    public final <C, N extends Number> Expression<E> leftShift(Function<C, Expression<N>> function) {
        throw unsupportedOperation();
    }

    @Override
    public final <N extends Number> Expression<E> leftShift(Supplier<Expression<N>> supplier) {
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
    public final Expression<E> bracket() {
        throw unsupportedOperation();
    }

    @Override
    public final SortItem asc() {
        throw unsupportedOperation();
    }

    @Override
    public final SortItem desc() {
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


    static UnsupportedOperationException unsupportedOperation() {
        return new UnsupportedOperationException("Non Expression not support this method.");
    }

}
