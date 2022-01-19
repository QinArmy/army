package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.Function;

/**
 * Interface representing the sql expression, eg: column,function.
 *
 * @param <E> expression result java type
 * @see FieldMeta
 * @since 1.0
 */
@SuppressWarnings("unused")
public interface Expression<E> extends SelectionAble, TypeInfer, SortPart, SetTargetPart {

    /**
     * relational operate with {@code =}
     *
     * @param operand right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     */
    IPredicate equal(Expression<?> operand);

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
     * <p>
     * Operand will be wrapped with optimizing param
     * </p>
     *
     * @param parameter right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     */
    IPredicate equal(Object parameter);

    /**
     * relational operate with {@code =}
     * <p>
     * If operand non-null than operand will be wrapped with optimizing param.
     * </p>
     *
     * @param parameter right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     * @return If operand null return null,or return predicate instance.
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
    <C> IPredicate equalAny(Function<C, ColumnSubQuery> subQuery);

    /**
     * relational operate with {@code = SOME}
     */
    <C> IPredicate equalSome(Function<C, ColumnSubQuery> subQuery);


    IPredicate lessThan(Expression<?> expression);

    IPredicate lessThan(Object parameter);

    @Nullable
    IPredicate ifLessThan(@Nullable Object parameter);

    IPredicate lessThan(String subQueryAlias, String fieldAlias);

    IPredicate lessThan(String tableAlias, FieldMeta<?, ?> field);

    <C, O> IPredicate lessThan(Function<C, Expression<O>> function);

    <C, O> IPredicate lessThanAny(Function<C, ColumnSubQuery> function);

    <C, O> IPredicate lessThanSome(Function<C, ColumnSubQuery> function);

    <C, O> IPredicate lessThanAll(Function<C, ColumnSubQuery> function);

    IPredicate lessEqual(Expression<?> operand);

    IPredicate lessEqual(Object parameter);

    @Nullable
    IPredicate ifLessEqual(@Nullable Object parameter);

    IPredicate lessEqual(String subQueryAlias, String fieldAlias);

    IPredicate lessEqual(String tableAlias, FieldMeta<?, ?> field);

    <C, O> IPredicate lessEqual(Function<C, Expression<O>> function);

    <C, O> IPredicate lessEqualAny(Function<C, ColumnSubQuery> function);

    <C, O> IPredicate lessEqualSome(Function<C, ColumnSubQuery> function);

    <C, O> IPredicate lessEqualAll(Function<C, ColumnSubQuery> function);

    IPredicate greatThan(Expression<?> operand);

    IPredicate greatThan(Object parameter);

    @Nullable
    IPredicate ifGreatThan(@Nullable Object parameter);

    IPredicate greatThan(String subQueryAlias, String fieldAlias);

    IPredicate greatThan(String tableAlias, FieldMeta<?, ?> field);

    <C, O> IPredicate greatThan(Function<C, Expression<O>> function);

    <C, O> IPredicate greatThanAny(Function<C, ColumnSubQuery> function);

    <C, O> IPredicate greatThanSome(Function<C, ColumnSubQuery> function);

    <C, O> IPredicate greatThanAll(Function<C, ColumnSubQuery> function);

    IPredicate greatEqual(Expression<?> operand);

    IPredicate greatEqual(Object parameter);

    @Nullable
    IPredicate IfGreatEqual(@Nullable Object parameter);

    IPredicate greatEqual(String subQueryAlias, String fieldAlias);

    IPredicate greatEqual(String tableAlias, FieldMeta<?, ?> field);

    <C, O> IPredicate greatEqual(Function<C, Expression<O>> function);

    <C> IPredicate greatEqualAny(Function<C, ColumnSubQuery> function);

    <C> IPredicate greatEqualSome(Function<C, ColumnSubQuery> function);

    <C> IPredicate greatEqualAll(Function<C, ColumnSubQuery> function);

    IPredicate notEqual(Expression<?> expression);

    IPredicate notEqual(Object parameter);

    @Nullable
    IPredicate ifNotEqual(@Nullable Object parameter);

    IPredicate notEqual(String subQueryAlias, String fieldAlias);

    IPredicate notEqual(String tableAlias, FieldMeta<?, ?> field);

    <C, O> IPredicate notEqual(Function<C, Expression<O>> function);

    <C, O> IPredicate notEqualAny(Function<C, ColumnSubQuery> function);

    <C, O> IPredicate notEqualSome(Function<C, ColumnSubQuery> function);

    <C, O> IPredicate notEqualAll(Function<C, ColumnSubQuery> function);

    IPredicate between(Expression<?> first, Expression<?> parameter);

    IPredicate between(Object firstParameter, Object secondParameter);

    @Nullable
    IPredicate ifBetween(@Nullable Object firstParameter, @Nullable Object secondParameter);

    IPredicate between(Expression<?> first, Object parameter);

    @Nullable
    IPredicate ifBetween(Expression<?> first, @Nullable Object parameter);

    IPredicate between(Object parameter, Expression<?> second);

    @Nullable
    IPredicate ifBetween(@Nullable Object firstParameter, Expression<?> second);

    <C> IPredicate between(Function<C, BetweenWrapper> function);

    IPredicate isNull();

    IPredicate isNotNull();

    /**
     * <p>
     * Parameters will be wrapped with {@link SQLs#collectionParam(Expression, Collection)}.
     * </p>
     *
     * @param <O> java type of element of right operand of {@code in},the element is weak weakly instance, because sql is weakly typed.
     */
    <O> IPredicate in(Collection<O> parameters);

    /**
     * <p>
     * If parameters non-null parameters will be wrapped with {@link SQLs#collectionParam(Expression, Collection)}.
     * </p>
     *
     * @param <O> java type of element of parameters,the element is weak weakly instance, because sql is weakly typed.
     */
    @Nullable
    <O> IPredicate ifIn(@Nullable Collection<O> parameters);

    <O> IPredicate in(Expression<Collection<O>> parameters);

    <C, O> IPredicate in(Function<C, ColumnSubQuery> function);

    /**
     * <p>
     * Parameters will be wrapped with {@link SQLs#collectionParam(Expression, Collection)}.
     * </p>
     *
     * @param <O> java type of element of right operand of {@code in},the element is weak weakly instance, because sql is weakly typed.
     */
    <O> IPredicate notIn(Collection<O> parameters);

    /**
     * <p>
     * If parameters non-null,then parameters will be wrapped with {@link SQLs#collectionParam(Expression, Collection)}.
     * </p>
     *
     * @param <O> java type of element of parameters,the element is weak weakly instance, because sql is weakly typed.
     */
    @Nullable
    <O> IPredicate ifNotIn(@Nullable Collection<O> parameters);

    <O> IPredicate notIn(Expression<Collection<O>> values);

    <C, O> IPredicate notIn(Function<C, ColumnSubQuery> function);

    Expression<E> mod(Expression<?> operator);

    Expression<E> mod(Object parameter);

    Expression<E> mod(String subQueryAlias, String derivedFieldName);

    Expression<E> mod(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> mod(Function<C, Expression<O>> function);

    Expression<E> multiply(Expression<?> multiplicand);

    Expression<E> multiply(Object parameter);

    Expression<E> multiply(String subQueryAlias, String derivedFieldName);

    Expression<E> multiply(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> multiply(Function<C, Expression<O>> function);

    Expression<E> plus(Expression<?> augend);

    Expression<E> plus(Object parameter);

    Expression<E> plus(String subQueryAlias, String derivedFieldName);

    Expression<E> plus(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> plus(Function<C, Expression<O>> function);

    Expression<E> minus(Expression<?> subtrahend);

    Expression<E> minus(Object parameter);

    Expression<E> minus(String subQueryAlias, String derivedFieldName);

    Expression<E> minus(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> minus(Function<C, Expression<O>> function);

    Expression<E> divide(Expression<?> divisor);

    Expression<E> divide(Object parameter);


    Expression<E> divide(String subQueryAlias, String derivedFieldName);

    Expression<E> divide(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> divide(Function<C, Expression<O>> function);

    Expression<E> negate();

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> bitwiseAnd(Expression<?> operand);

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> bitwiseAnd(Object parameter);

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> bitwiseAnd(String subQueryAlias, String derivedFieldName);

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> bitwiseAnd(String tableAlias, FieldMeta<?, ?> field);

    /**
     * Bitwise AND
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C, O> Expression<E> bitwiseAnd(Function<C, Expression<O>> function);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> bitwiseOr(Expression<?> operand);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> bitwiseOr(Object parameter);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> bitwiseOr(String subQueryAlias, String derivedFieldName);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> bitwiseOr(String tableAlias, FieldMeta<?, ?> field);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    <C, O> Expression<E> bitwiseOr(Function<C, Expression<O>> function);

    /**
     * Bitwise XOR
     *
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
    <C, O> Expression<E> xor(Function<C, Expression<O>> function);

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
    Expression<E> rightShift(Number bitNumberParameter);

    /**
     * Shifts a  number to the right.
     *
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
     * @param <N> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C, N extends Number> Expression<E> rightShift(Function<C, Expression<N>> function);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> leftShift(Number bitNumberParameter);

    /**
     * Shifts a  number to the left.
     *
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
     * @return {@link BigInteger} expression
     */
    <N extends Number> Expression<E> leftShift(String tableAlias, FieldMeta<?, N> field);

    /**
     * Shifts a  number to the left.
     *
     * @param <N> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C, N extends Number> Expression<E> leftShift(Function<C, Expression<N>> function);

    <O> Expression<O> asType(Class<O> convertType);

    <O> Expression<O> asType(Class<O> convertType, MappingType longMapping);

    <O> Expression<O> asType(Class<O> convertType, FieldMeta<?, O> longMapping);

    Expression<E> bracket();

    SortPart asc();

    SortPart desc();

    IPredicate like(String patternParameter);

    <C> IPredicate like(Function<C, Expression<String>> function);

    IPredicate notLike(String patternParameter);

    IPredicate like(Expression<String> pattern);

    IPredicate notLike(Expression<String> pattern);

    <C> IPredicate notLike(Function<C, Expression<String>> function);


}
