package io.army.criteria;

import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.Function;

/**
 * Interface representing the sql expression, eg: column,function.
 * <p>
 * the implementation of this interface must implement {@link ExpressionCounselor}
 * </p>
 *
 * @param <E> expression result java type
 * @see FieldMeta
 * @see ExpressionCounselor
 * @since 1.0
 */
@SuppressWarnings("unused")
public interface Expression<E> extends SelectionAble, MappingTypeAble {

    /**
     * relational operate with {@code =}
     */
    IPredicate equal(Expression<?> expression);

    /**
     * relational operate with {@code =}
     */
    IPredicate equal(String subQueryAlias, String fieldAlias);

    /**
     * relational operate with {@code =}
     */
    IPredicate equal(String tableAlias, FieldMeta<?, ?> field);

    /**
     * relational operate with {@code =}
     */
    IPredicate equal(Object parameter);

    /**
     * relational operate with {@code =}
     *
     * @see Query.WhereAndSpec#ifAnd(IPredicate)
     * @see Update.WhereAndSpec#ifAnd(IPredicate)
     * @see Delete.WhereAndSpec#ifAnd(IPredicate)
     */
    @Nullable
    IPredicate ifEqual(@Nullable Object parameter);

    /**
     * relational operate with {@code =}
     */
    <C> IPredicate equal(Function<C, Expression<?>> expOrSubQuery);

    /**
     * relational operate with {@code = ANY}
     */
    <C> IPredicate equalAny(Function<C, ColumnSubQuery<?>> subQuery);

    /**
     * relational operate with {@code = SOME}
     */
    <C> IPredicate equalSome(Function<C, ColumnSubQuery<?>> subQuery);

    IPredicate lessThan(Expression<?> expression);

    IPredicate lessThan(Object parameter);

    @Nullable
    IPredicate ifLessThan(@Nullable Object parameter);

    IPredicate lessThan(String subQueryAlias, String fieldAlias);

    IPredicate lessThan(String tableAlias, FieldMeta<?, ?> field);

    <C> IPredicate lessThan(Function<C, Expression<?>> expOrSubQuery);

    <C> IPredicate lessThanAny(Function<C, ColumnSubQuery<?>> subQuery);

    <C> IPredicate lessThanSome(Function<C, ColumnSubQuery<?>> subQuery);

    <C> IPredicate lessThanAll(Function<C, ColumnSubQuery<?>> subQuery);

    IPredicate lessEqual(Expression<?> expression);

    IPredicate lessEqual(Object parameter);

    @Nullable
    IPredicate ifLessEqual(@Nullable Object parameter);

    IPredicate lessEqual(String subQueryAlias, String fieldAlias);

    IPredicate lessEqual(String tableAlias, FieldMeta<?, ?> field);

    <C> IPredicate lessEqual(Function<C, Expression<?>> expOrSubQuery);

    <C> IPredicate lessEqualAny(Function<C, ColumnSubQuery<?>> subQuery);

    <C> IPredicate lessEqualSome(Function<C, ColumnSubQuery<?>> subQuery);

    <C> IPredicate lessEqualAll(Function<C, ColumnSubQuery<?>> subQuery);

    IPredicate greatThan(Expression<?> expression);

    IPredicate greatThan(Object parameter);

    @Nullable
    IPredicate ifGreatThan(@Nullable Object parameter);

    IPredicate greatThan(String subQueryAlias, String fieldAlias);

    IPredicate greatThan(String tableAlias, FieldMeta<?, ?> field);

    <C> IPredicate greatThan(Function<C, Expression<?>> expOrSubQuery);

    <C> IPredicate greatThanAny(Function<C, ColumnSubQuery<?>> subQuery);

    <C> IPredicate greatThanSome(Function<C, ColumnSubQuery<?>> subQuery);

    <C> IPredicate greatThanAll(Function<C, ColumnSubQuery<?>> subQuery);

    IPredicate greatEqual(Expression<?> expression);

    IPredicate greatEqual(Object parameter);

    @Nullable
    IPredicate IfGreatEqual(@Nullable Object parameter);

    IPredicate greatEqual(String subQueryAlias, String fieldAlias);

    IPredicate greatEqual(String tableAlias, FieldMeta<?, ?> fieldMeta);

    <C> IPredicate greatEqual(Function<C, Expression<?>> expOrSubQuery);

    <C> IPredicate greatEqualAny(Function<C, ColumnSubQuery<?>> subQuery);

    <C> IPredicate greatEqualSome(Function<C, ColumnSubQuery<?>> subQuery);

    <C> IPredicate greatEqualAll(Function<C, ColumnSubQuery<?>> subQuery);

    IPredicate notEqual(Expression<?> expression);

    IPredicate notEqual(Object constant);

    @Nullable
    IPredicate ifNotEqual(@Nullable Object constant);

    IPredicate notEqual(String subQueryAlias, String fieldAlias);

    IPredicate notEqual(String tableAlias, FieldMeta<?, ?> fieldMeta);

    <C> IPredicate notEqual(Function<C, Expression<?>> expOrSubQuery);

    <C> IPredicate notEqualAny(Function<C, ColumnSubQuery<?>> subQuery);

    <C> IPredicate notEqualSome(Function<C, ColumnSubQuery<?>> subQuery);

    <C> IPredicate notEqualAll(Function<C, ColumnSubQuery<?>> subQuery);

    IPredicate between(Expression<?> first, Expression<?> second);

    IPredicate between(Object first, Object second);

    @Nullable
    IPredicate ifBetween(@Nullable Object first, @Nullable Object second);

    IPredicate between(Expression<?> first, Object second);

    @Nullable
    IPredicate ifBetween(Expression<?> first, @Nullable Object second);

    IPredicate between(Object first, Expression<?> second);

    @Nullable
    IPredicate ifBetween(@Nullable Object first, Expression<?> second);

    <C> IPredicate between(Function<C, BetweenWrapper> function);

    @Nullable
    <C> IPredicate IfBetween(Function<C, BetweenWrapper> function);

    IPredicate isNull();

    IPredicate isNotNull();

    IPredicate in(Collection<?> parameters);

    @Nullable
    IPredicate ifIn(@Nullable Collection<?> parameters);

    <O> IPredicate in(Expression<Collection<O>> values);

    <C> IPredicate in(Function<C, ColumnSubQuery<?>> subQuery);

    IPredicate notIn(Collection<?> values);

    @Nullable
    IPredicate ifNotIn(@Nullable Collection<?> values);

    <O> IPredicate notIn(Expression<Collection<O>> values);

    <C, O> IPredicate notIn(Function<C, ColumnSubQuery<O>> subQuery);

    Expression<E> mod(Expression<?> operator);

    Expression<E> mod(Object parameter);

    Expression<E> mod(String subQueryAlias, String derivedFieldName);

    Expression<E> mod(String tableAlias, FieldMeta<?, ?> field);

    <C> Expression<E> mod(Function<C, Expression<?>> expOrSubQuery);

    Expression<E> multiply(Expression<?> multiplicand);

    Expression<E> multiply(Object parameter);

    Expression<E> multiply(String subQueryAlias, String derivedFieldName);

    Expression<E> multiply(String tableAlias, FieldMeta<?, ?> field);

    <C> Expression<E> multiply(Function<C, Expression<?>> expOrSubQuery);

    Expression<E> plus(Expression<?> augend);

    Expression<E> plus(Object augend);

    Expression<E> plus(String subQueryAlias, String derivedFieldName);

    Expression<E> plus(String tableAlias, FieldMeta<?, ?> fieldMeta);

    <C> Expression<E> plus(Function<C, Expression<?>> expOrSubQuery);

    Expression<E> minus(Expression<?> subtrahend);

    Expression<E> minus(Object parameter);

    Expression<E> minus(String subQueryAlias, String derivedFieldName);

    Expression<E> minus(String tableAlias, FieldMeta<?, ?> field);

    <C> Expression<E> minus(Function<C, Expression<?>> expOrSubQuery);

    Expression<E> divide(Expression<?> parameter);

    Expression<E> divide(Object parameter);


    Expression<E> divide(String subQueryAlias, String derivedFieldName);

    Expression<E> divide(String tableAlias, FieldMeta<?, ?> field);

    <C> Expression<E> divide(Function<C, Expression<?>> expOrSubQuery);

    Expression<E> negate();

    /**
     * Bitwise AND
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    Expression<E> and(Expression<?> operand);

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> and(Object parameter);

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> and(String subQueryAlias, String derivedFieldName);

    /**
     * Bitwise AND
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    Expression<E> and(String tableAlias, FieldMeta<?, ?> field);

    /**
     * Bitwise AND
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C> Expression<E> and(Function<C, Expression<?>> expOrSubQuery);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> or(Expression<?> operand);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> or(Object parameter);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> or(String subQueryAlias, String derivedFieldName);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    <O> Expression<BigInteger> or(String tableAlias, FieldMeta<?, ?> field);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    <C> Expression<E> or(Function<C, Expression<?>> expOrSubQuery);

    /**
     * Bitwise XOR
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    Expression<E> xor(Expression<?> operand);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> xor(Object parameter);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> xor(String subQueryAlias, String derivedFieldName);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> xor(String tableAlias, FieldMeta<?, ?> field);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    <C> Expression<E> xor(Function<C, Expression<?>> expOrSubQuery);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> inversion(Expression<?> operand);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> inversion(Object parameter);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> inversion(String subQueryAlias, String derivedFieldName);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> inversion(String tableAlias, FieldMeta<?, ?> field);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    <C> Expression<E> inversion(Function<C, Expression<?>> expOrSubQuery);

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> rightShift(Object parameter);

    /**
     * Shifts a  number to the right.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    Expression<E> rightShift(Expression<?> bitNumber);

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> rightShift(String tableAlias, FieldMeta<?, ?> field);

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> rightShift(String subQueryAlias, String derivedFieldName);

    /**
     * Shifts a  number to the right.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C> Expression<E> rightShift(Function<C, Expression<?>> expOrSubQuery);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> leftShift(Object bitNumber);

    /**
     * Shifts a  number to the left.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    Expression<E> leftShift(Expression<?> bitNumber);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> leftShift(String subQueryAlias, String derivedFieldName);

    /**
     * Shifts a  number to the left.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    Expression<E> leftShift(String tableAlias, FieldMeta<?, ?> fieldMeta);

    /**
     * Shifts a  number to the left.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C> Expression<E> leftShift(Function<C, Expression<?>> expOrSubQuery);

    <O> Expression<O> asType(Class<O> convertType);

    <O> Expression<O> asType(Class<O> convertType, MappingType longMapping);

    Expression<E> brackets();

    SortPart asc();

    SortPart desc();

    IPredicate like(String pattern);

    <C, S extends Expression<String>> IPredicate like(Function<C, S> expOrSubQuery);

    IPredicate notLike(String pattern);

    IPredicate like(Expression<String> pattern);

    IPredicate notLike(Expression<String> pattern);

    <C, S extends Expression<String>> IPredicate notLike(Function<C, S> expOrSubQuery);


}
