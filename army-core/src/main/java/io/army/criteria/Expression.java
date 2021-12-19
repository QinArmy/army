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
    IPredicate equal(Expression<E> expression);

    /**
     * relational operate with {@code =}
     */
    IPredicate equal(String subQueryAlias, String fieldAlias);

    /**
     * relational operate with {@code =}
     */
    IPredicate equal(String tableAlias, FieldMeta<?, E> fieldMeta);

    /**
     * relational operate with {@code =}
     */
    IPredicate equal(E constant);

    /**
     * relational operate with {@code =}
     *
     * @see Query.WhereAndSpec#ifAnd(IPredicate)
     * @see Update.WhereAndSpec#ifAnd(IPredicate)
     * @see Delete.WhereAndSpec#ifAnd(IPredicate)
     */
    @Nullable
    IPredicate equalIfNonNull(@Nullable E constant);

    /**
     * relational operate with {@code =}
     */
    <C, S extends Expression<E>> IPredicate equal(Function<C, S> expOrSubQuery);

    /**
     * relational operate with {@code = ANY}
     */
    <C, S extends ColumnSubQuery<E>> IPredicate equalAny(Function<C, S> subQuery);

    /**
     * relational operate with {@code = SOME}
     */
    <C, S extends ColumnSubQuery<E>> IPredicate equalSome(Function<C, S> subQuery);

    /**
     * relational operate with {@code = ALL}
     */
    <C, S extends ColumnSubQuery<E>> IPredicate equalAll(Function<C, S> subQuery);

    IPredicate lessThan(Expression<E> expression);

    IPredicate lessThan(E constant);

    IPredicate lessThan(String subQueryAlias, String fieldAlias);

    IPredicate lessThan(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, S extends Expression<E>> IPredicate lessThan(Function<C, S> expOrSubQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate lessThanAny(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate lessThanSome(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate lessThanAll(Function<C, S> subQuery);

    IPredicate lessEqual(Expression<E> expression);

    IPredicate lessEqual(E constant);

    IPredicate lessEqual(String subQueryAlias, String fieldAlias);

    IPredicate lessEqual(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, S extends Expression<E>> IPredicate lessEqual(Function<C, S> expOrSubQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate lessEqualAny(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate lessEqualSome(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate lessEqualAll(Function<C, S> subQuery);

    IPredicate greatThan(Expression<E> expression);

    IPredicate greatThan(E constant);

    IPredicate greatThan(String subQueryAlias, String fieldAlias);

    IPredicate greatThan(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, S extends Expression<E>> IPredicate greatThan(Function<C, S> expOrSubQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate greatThanAny(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate greatThanSome(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate greatThanAll(Function<C, S> subQuery);

    IPredicate greatEqual(Expression<E> expression);

    IPredicate greatEqual(E constant);

    IPredicate greatEqual(String subQueryAlias, String fieldAlias);

    IPredicate greatEqual(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, S extends Expression<E>> IPredicate greatEqual(Function<C, S> expOrSubQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate greatEqualAny(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate greatEqualSome(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate greatEqualAll(Function<C, S> subQuery);

    IPredicate notEqual(Expression<E> expression);

    IPredicate notEqual(E constant);

    IPredicate notEqual(String subQueryAlias, String fieldAlias);

    IPredicate notEqual(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, S extends Expression<E>> IPredicate notEqual(Function<C, S> expOrSubQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate notEqualAny(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate notEqualSome(Function<C, S> subQuery);

    <C, S extends ColumnSubQuery<E>> IPredicate notEqualAll(Function<C, S> subQuery);

    IPredicate between(Expression<E> first, Expression<E> second);

    IPredicate between(E first, E second);

    IPredicate between(Expression<E> first, E second);

    IPredicate between(E first, Expression<E> second);

    <C> IPredicate between(Function<C, BetweenWrapper<E>> function);

    IPredicate isNull();

    IPredicate isNotNull();

    IPredicate in(Collection<E> values);

    IPredicate in(Expression<Collection<E>> values);

    <C> IPredicate in(Function<C, ColumnSubQuery<E>> subQuery);

    IPredicate notIn(Collection<E> values);

    IPredicate notIn(Expression<Collection<E>> values);

    <C> IPredicate notIn(Function<C, ColumnSubQuery<E>> subQuery);

    <N extends Number> Expression<E> mod(Expression<N> operator);

    <N extends Number> Expression<E> mod(N operator);

    Expression<E> mod(String subQueryAlias, String derivedFieldName);

    <N extends Number> Expression<E> mod(String tableAlias, FieldMeta<?, N> fieldMeta);

    <C, N extends Number, S extends Expression<N>> Expression<E> mod(Function<C, S> expOrSubQuery);

    <N extends Number> Expression<E> multiply(Expression<N> multiplicand);

    <N extends Number> Expression<E> multiply(N multiplicand);

    Expression<E> multiply(String subQueryAlias, String derivedFieldName);

    Expression<E> multiply(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, N extends Number, S extends Expression<N>> Expression<E> multiply(Function<C, S> expOrSubQuery);

    <N extends Number> Expression<E> plus(Expression<N> augend);

    <N extends Number> Expression<E> plus(N augend);

    Expression<E> plus(String subQueryAlias, String derivedFieldName);

    Expression<E> plus(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, N extends Number, S extends Expression<N>> Expression<E> plus(Function<C, S> expOrSubQuery);

    <N extends Number> Expression<E> minus(Expression<N> subtrahend);

    <N extends Number> Expression<E> minus(N subtrahend);

    Expression<E> minus(String subQueryAlias, String derivedFieldName);

    Expression<E> minus(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, N extends Number, S extends Expression<N>> Expression<E> minus(Function<C, S> expOrSubQuery);

    <N extends Number> Expression<E> divide(Expression<N> divisor);

    <N extends Number> Expression<E> divide(N divisor);

    Expression<E> divide(String subQueryAlias, String derivedFieldName);

    Expression<E> divide(String tableAlias, FieldMeta<?, E> fieldMeta);

    <C, N extends Number, S extends Expression<N>> Expression<E> divide(Function<C, S> expOrSubQuery);

    Expression<E> negate();

    /**
     * Bitwise AND
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <O> Expression<BigInteger> and(Expression<O> operand);

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> and(Long operand);

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> and(String subQueryAlias, String derivedFieldName);

    /**
     * Bitwise AND
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <O> Expression<BigInteger> and(String tableAlias, FieldMeta<?, O> fieldMeta);

    /**
     * Bitwise AND
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C, O, S extends Expression<O>> Expression<E> and(Function<C, S> expOrSubQuery);

    /**
     * Bitwise OR
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <O> Expression<BigInteger> or(Expression<O> operand);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> or(Long operand);

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
    <O> Expression<BigInteger> or(String tableAlias, FieldMeta<?, O> fieldMeta);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    <C, O, S extends Expression<O>> Expression<E> or(Function<C, S> expOrSubQuery);

    /**
     * Bitwise XOR
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <O> Expression<BigInteger> xor(Expression<O> operand);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> xor(Long operand);

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
    <O> Expression<BigInteger> xor(String tableAlias, FieldMeta<?, O> fieldMeta);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    <C, O, S extends Expression<O>> Expression<E> xor(Function<C, S> expOrSubQuery);

    /**
     * Bitwise Inversion
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <O> Expression<BigInteger> inversion(Expression<O> operand);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> inversion(Long operand);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> inversion(String subQueryAlias, String derivedFieldName);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    <O> Expression<BigInteger> inversion(String tableAlias, FieldMeta<?, O> fieldMeta);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    <C, O, S extends Expression<O>> Expression<E> inversion(Function<C, S> expOrSubQuery);

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> rightShift(Integer bitNumber);

    /**
     * Shifts a  number to the right.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <O> Expression<BigInteger> rightShift(Expression<O> bitNumber);

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    <O> Expression<BigInteger> rightShift(String tableAlias, FieldMeta<?, O> fieldMeta);

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> rightShift(String subQueryAlias, String derivedFieldName);

    /**
     * Shifts a  number to the right.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C, O, S extends Expression<O>> Expression<E> rightShift(Function<C, S> expOrSubQuery);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> leftShift(Integer bitNumber);

    /**
     * Shifts a  number to the left.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <O> Expression<BigInteger> leftShift(Expression<O> bitNumber);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression<BigInteger> leftShift(String subQueryAlias, String derivedFieldName);

    /**
     * Shifts a  number to the left.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <O> Expression<BigInteger> leftShift(String tableAlias, FieldMeta<?, O> fieldMeta);

    /**
     * Shifts a  number to the left.
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C, O, S extends Expression<O>> Expression<E> leftShift(Function<C, S> expOrSubQuery);

    <O> Expression<E> plusOther(Expression<O> other);

    Expression<E> plusOther(String subQueryAlias, String derivedFieldName);

    <O> Expression<E> plusOther(String tableAlias, FieldMeta<?, O> fieldMeta);

    <C, O, S extends Expression<O>> Expression<E> plusOther(Function<C, S> expOrSubQuery);

    <O> Expression<E> minusOther(Expression<O> other);

    Expression<E> minusOther(String subQueryAlias, String derivedFieldName);

    <O> Expression<E> minusOther(String tableAlias, FieldMeta<?, O> fieldMeta);

    <C, O, S extends Expression<O>> Expression<E> minusOther(Function<C, S> expOrSubQuery);

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
