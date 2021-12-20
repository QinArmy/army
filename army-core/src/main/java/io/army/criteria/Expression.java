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
    <C, O> IPredicate equal(Function<C, Expression<O>> expOrSubQuery);

    /**
     * relational operate with {@code = ANY}
     */
    <C, O> IPredicate equalAny(Function<C, ColumnSubQuery<O>> subQuery);

    /**
     * relational operate with {@code = SOME}
     */
    <C, O> IPredicate equalSome(Function<C, ColumnSubQuery<O>> subQuery);

    IPredicate lessThan(Expression<?> expression);

    IPredicate lessThan(Object parameter);

    @Nullable
    IPredicate ifLessThan(@Nullable Object parameter);

    IPredicate lessThan(String subQueryAlias, String fieldAlias);

    IPredicate lessThan(String tableAlias, FieldMeta<?, ?> field);

    <C, O> IPredicate lessThan(Function<C, Expression<O>> expOrSubQuery);

    <C, O> IPredicate lessThanAny(Function<C, ColumnSubQuery<O>> subQuery);

    <C, O> IPredicate lessThanSome(Function<C, ColumnSubQuery<O>> subQuery);

    <C, O> IPredicate lessThanAll(Function<C, ColumnSubQuery<O>> subQuery);

    IPredicate lessEqual(Expression<?> expression);

    IPredicate lessEqual(Object parameter);

    @Nullable
    IPredicate ifLessEqual(@Nullable Object parameter);

    IPredicate lessEqual(String subQueryAlias, String fieldAlias);

    IPredicate lessEqual(String tableAlias, FieldMeta<?, ?> field);

    <C, O> IPredicate lessEqual(Function<C, Expression<O>> expOrSubQuery);

    <C, O> IPredicate lessEqualAny(Function<C, ColumnSubQuery<O>> subQuery);

    <C, O> IPredicate lessEqualSome(Function<C, ColumnSubQuery<O>> subQuery);

    <C, O> IPredicate lessEqualAll(Function<C, ColumnSubQuery<O>> subQuery);

    IPredicate greatThan(Expression<?> expression);

    IPredicate greatThan(Object parameter);

    @Nullable
    IPredicate ifGreatThan(@Nullable Object parameter);

    IPredicate greatThan(String subQueryAlias, String fieldAlias);

    IPredicate greatThan(String tableAlias, FieldMeta<?, ?> field);

    <C, O> IPredicate greatThan(Function<C, Expression<O>> expOrSubQuery);

    <C, O> IPredicate greatThanAny(Function<C, ColumnSubQuery<O>> subQuery);

    <C, O> IPredicate greatThanSome(Function<C, ColumnSubQuery<O>> subQuery);

    <C, O> IPredicate greatThanAll(Function<C, ColumnSubQuery<O>> subQuery);

    IPredicate greatEqual(Expression<?> expression);

    IPredicate greatEqual(Object parameter);

    @Nullable
    IPredicate IfGreatEqual(@Nullable Object parameter);

    IPredicate greatEqual(String subQueryAlias, String fieldAlias);

    IPredicate greatEqual(String tableAlias, FieldMeta<?, ?> fieldMeta);

    <C, O> IPredicate greatEqual(Function<C, Expression<O>> expOrSubQuery);

    <C, O> IPredicate greatEqualAny(Function<C, ColumnSubQuery<O>> subQuery);

    <C, O> IPredicate greatEqualSome(Function<C, ColumnSubQuery<O>> subQuery);

    <C, O> IPredicate greatEqualAll(Function<C, ColumnSubQuery<O>> subQuery);

    IPredicate notEqual(Expression<?> expression);

    IPredicate notEqual(Object constant);

    @Nullable
    IPredicate ifNotEqual(@Nullable Object constant);

    IPredicate notEqual(String subQueryAlias, String fieldAlias);

    IPredicate notEqual(String tableAlias, FieldMeta<?, ?> fieldMeta);

    <C, O> IPredicate notEqual(Function<C, Expression<O>> expOrSubQuery);

    <C, O> IPredicate notEqualAny(Function<C, ColumnSubQuery<O>> subQuery);

    <C, O> IPredicate notEqualSome(Function<C, ColumnSubQuery<O>> subQuery);

    <C, O> IPredicate notEqualAll(Function<C, ColumnSubQuery<O>> subQuery);

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

    <O> IPredicate in(Collection<O> parameters);

    @Nullable
    <O> IPredicate ifIn(@Nullable Collection<O> parameters);

    <O> IPredicate in(Expression<Collection<O>> values);

    <C, O> IPredicate in(Function<C, ColumnSubQuery<O>> subQuery);

    <O> IPredicate notIn(Collection<O> values);

    @Nullable
    IPredicate ifNotIn(@Nullable Collection<?> values);

    <O> IPredicate notIn(Expression<Collection<O>> values);

    <C, O> IPredicate notIn(Function<C, ColumnSubQuery<O>> subQuery);

    Expression<E> mod(Expression<?> operator);

    Expression<E> mod(Object parameter);

    Expression<E> mod(String subQueryAlias, String derivedFieldName);

    Expression<E> mod(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> mod(Function<C, Expression<O>> expOrSubQuery);

    Expression<E> multiply(Expression<?> multiplicand);

    Expression<E> multiply(Object parameter);

    Expression<E> multiply(String subQueryAlias, String derivedFieldName);

    Expression<E> multiply(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> multiply(Function<C, Expression<O>> expOrSubQuery);

    Expression<E> plus(Expression<?> augend);

    Expression<E> plus(Object augend);

    Expression<E> plus(String subQueryAlias, String derivedFieldName);

    Expression<E> plus(String tableAlias, FieldMeta<?, ?> fieldMeta);

    <C, O> Expression<E> plus(Function<C, Expression<O>> expOrSubQuery);

    Expression<E> minus(Expression<?> subtrahend);

    Expression<E> minus(Object parameter);

    Expression<E> minus(String subQueryAlias, String derivedFieldName);

    Expression<E> minus(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> minus(Function<C, Expression<O>> expOrSubQuery);

    Expression<E> divide(Expression<?> parameter);

    Expression<E> divide(Object parameter);


    Expression<E> divide(String subQueryAlias, String derivedFieldName);

    Expression<E> divide(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> divide(Function<C, Expression<O>> expOrSubQuery);

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
    <C, O> Expression<E> and(Function<C, Expression<O>> expOrSubQuery);

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
    Expression<E> or(String subQueryAlias, String derivedFieldName);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> or(String tableAlias, FieldMeta<?, ?> field);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    <C, O> Expression<E> or(Function<C, Expression<O>> expOrSubQuery);

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
    Expression<E> xor(String subQueryAlias, String derivedFieldName);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> xor(String tableAlias, FieldMeta<?, ?> field);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    <C, O> Expression<E> xor(Function<C, Expression<O>> expOrSubQuery);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> inversion();

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> rightShift(Number bitNumber);

    /**
     * Shifts a  number to the right.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <N extends Number> Expression<E> rightShift(Expression<N> bitNumber);

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    <N extends Number> Expression<E> rightShift(String tableAlias, FieldMeta<?, N> field);

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
    <C, N extends Number> Expression<E> rightShift(Function<C, Expression<N>> expOrSubQuery);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> leftShift(Number bitNumber);

    /**
     * Shifts a  number to the left.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <N extends Number> Expression<E> leftShift(Expression<N> bitNumber);

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
    <N extends Number> Expression<E> leftShift(String tableAlias, FieldMeta<?, N> field);

    /**
     * Shifts a  number to the left.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C, N extends Number> Expression<E> leftShift(Function<C, Expression<N>> expOrSubQuery);

    <O> Expression<O> asType(Class<O> convertType);

    <O> Expression<O> asType(Class<O> convertType, MappingType longMapping);

    <O> Expression<O> asType(Class<O> convertType, FieldMeta<?, O> longMapping);

    Expression<E> brackets();

    SortPart asc();

    SortPart desc();

    IPredicate like(String pattern);

    <C> IPredicate like(Function<C, Expression<String>> expOrSubQuery);

    IPredicate notLike(String pattern);

    IPredicate like(Expression<String> pattern);

    IPredicate notLike(Expression<String> pattern);

    <C> IPredicate notLike(Function<C, Expression<String>> expOrSubQuery);


}
