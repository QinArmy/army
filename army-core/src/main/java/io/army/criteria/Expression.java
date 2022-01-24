package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;

import java.math.BigInteger;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Interface representing the sql expression, eg: column,function.
 *
 * @param <E> expression result java type
 * @see FieldMeta
 * @since 1.0
 */
@SuppressWarnings("unused")
public interface Expression<E> extends SelectionSpec, TypeInfer, SortItem, SetValueItem {

    /**
     * relational operate with {@code =}
     *
     * @param operand right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     */
    IPredicate equal(Expression<?> operand);

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
     * <p>
     * Equivalence : this.equal({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate equalParam(Object parameter);


    /**
     * <p>
     * Equivalence : this.equal({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate equalNamed(String paramName);

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
     * <p>
     * Equivalence : this.equal({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    @Nullable
    IPredicate ifEqualParam(@Nullable Object parameter);


    /**
     * relational operate with {@code =}
     */
    <C, O> IPredicate equal(Function<C, Expression<O>> function);

    <O> IPredicate equal(Supplier<Expression<O>> supplier);

    /**
     * relational operate with {@code = ANY}
     */
    <C> IPredicate equalAny(Function<C, ColumnSubQuery> supplier);

    /**
     * relational operate with {@code = ANY}
     */
    IPredicate equalAny(Supplier<ColumnSubQuery> supplier);

    /**
     * relational operate with {@code = SOME}
     */
    <C> IPredicate equalSome(Function<C, ColumnSubQuery> function);

    /**
     * relational operate with {@code = SOME}
     */
    IPredicate equalSome(Supplier<ColumnSubQuery> subQuery);


    IPredicate lessThan(Expression<?> expression);

    IPredicate lessThan(Object parameter);

    /**
     * <p>
     * Equivalence : this.lessThan({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate lessThanParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.lessThan({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate lessThanNamed(String paramName);

    @Nullable
    IPredicate ifLessThan(@Nullable Object parameter);

    @Nullable
    IPredicate ifLessThanParam(@Nullable Object parameter);

    <C, O> IPredicate lessThan(Function<C, Expression<O>> function);

    <O> IPredicate lessThan(Supplier<Expression<O>> supplier);

    <C> IPredicate lessThanAny(Function<C, ColumnSubQuery> function);

    IPredicate lessThanAny(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate lessThanSome(Function<C, ColumnSubQuery> function);

    IPredicate lessThanSome(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate lessThanAll(Function<C, ColumnSubQuery> function);

    IPredicate lessThanAll(Supplier<ColumnSubQuery> supplier);

    IPredicate lessEqual(Expression<?> operand);

    IPredicate lessEqual(Object parameter);

    /**
     * <p>
     * Equivalence : this.lessEqual({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate lessEqualParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.lessEqual({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate lessEqualNamed(String paramName);

    @Nullable
    IPredicate ifLessEqual(@Nullable Object parameter);

    @Nullable
    IPredicate ifLessEqualParam(@Nullable Object parameter);

    <C, O> IPredicate lessEqual(Function<C, Expression<O>> function);

    <O> IPredicate lessEqual(Supplier<Expression<O>> supplier);

    <C> IPredicate lessEqualAny(Function<C, ColumnSubQuery> function);

    IPredicate lessEqualAny(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate lessEqualSome(Function<C, ColumnSubQuery> function);

    IPredicate lessEqualSome(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate lessEqualAll(Function<C, ColumnSubQuery> function);

    IPredicate lessEqualAll(Supplier<ColumnSubQuery> supplier);

    IPredicate greatThan(Expression<?> operand);

    IPredicate greatThan(Object parameter);

    /**
     * <p>
     * Equivalence : this.greatThan({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate greatThanParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.greatThan({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate greatThanNamed(String paramName);

    @Nullable
    IPredicate ifGreatThan(@Nullable Object parameter);

    @Nullable
    IPredicate ifGreatThanParam(@Nullable Object parameter);

    <C, O> IPredicate greatThan(Function<C, Expression<O>> function);

    <O> IPredicate greatThan(Supplier<Expression<O>> supplier);

    <C> IPredicate greatThanAny(Function<C, ColumnSubQuery> function);

    IPredicate greatThanAny(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate greatThanSome(Function<C, ColumnSubQuery> function);

    IPredicate greatThanSome(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate greatThanAll(Function<C, ColumnSubQuery> function);

    IPredicate greatThanAll(Supplier<ColumnSubQuery> supplier);

    IPredicate greatEqual(Expression<?> operand);

    IPredicate greatEqual(Object parameter);

    /**
     * <p>
     * Equivalence : this.greatEqual({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate greatEqualParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.greatEqual({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate greatEqualNamed(String paramName);

    @Nullable
    IPredicate IfGreatEqual(@Nullable Object parameter);

    @Nullable
    IPredicate ifGreatEqualParam(@Nullable Object parameter);

    <C, O> IPredicate greatEqual(Function<C, Expression<O>> function);

    <O> IPredicate greatEqual(Supplier<Expression<O>> supplier);

    <C> IPredicate greatEqualAny(Function<C, ColumnSubQuery> function);

    IPredicate greatEqualAny(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate greatEqualSome(Function<C, ColumnSubQuery> function);

    IPredicate greatEqualSome(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate greatEqualAll(Function<C, ColumnSubQuery> function);

    IPredicate greatEqualAll(Supplier<ColumnSubQuery> supplier);

    IPredicate notEqual(Expression<?> expression);

    IPredicate notEqual(Object parameter);

    /**
     * <p>
     * Equivalence : this.notEqual({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate notEqualParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.notEqual({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate notEqualNamed(String paramName);

    @Nullable
    IPredicate ifNotEqual(@Nullable Object parameter);

    @Nullable
    IPredicate ifNotEqualParam(@Nullable Object parameter);

    <C, O> IPredicate notEqual(Function<C, Expression<O>> function);

    <O> IPredicate notEqual(Supplier<Expression<O>> supplier);

    <C> IPredicate notEqualAny(Function<C, ColumnSubQuery> function);

    IPredicate notEqualAny(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate notEqualSome(Function<C, ColumnSubQuery> function);

    IPredicate notEqualSome(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate notEqualAll(Function<C, ColumnSubQuery> function);

    IPredicate notEqualAll(Supplier<ColumnSubQuery> supplier);

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
     * Parameters will be wrapped with {@link SQLs#optimizingParams(ParamMeta, Collection)}.
     * </p>
     *
     * @param <O> java type of element of right operand of {@code in},the element is weak weakly instance, because sql is weakly typed.
     */
    <O> IPredicate in(Collection<O> parameters);

    /**
     * <p>
     * Equivalence : this.in({@link SQLs#params(ParamMeta, Collection)})
     * </p>
     */
    <O> IPredicate inParam(Collection<O> parameters);

    /**
     * <p>
     * Equivalence : this.in({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate inNamed(String paramName);

    /**
     * <p>
     * If parameters non-null parameters will be wrapped with {@link SQLs#optimizingParams(ParamMeta, Collection)}.
     * </p>
     *
     * @param <O> java type of element of parameters,the element is weak weakly instance, because sql is weakly typed.
     */
    @Nullable
    <O> IPredicate ifIn(@Nullable Collection<O> parameters);

    @Nullable
    <O> IPredicate ifInParam(@Nullable Collection<O> parameters);

    <O> IPredicate in(Expression<Collection<O>> parameters);

    <C> IPredicate in(Function<C, ColumnSubQuery> function);

    IPredicate in(Supplier<ColumnSubQuery> supplier);

    /**
     * <p>
     * Parameters will be wrapped with {@link SQLs#optimizingParams(ParamMeta, Collection)}.
     * </p>
     *
     * @param <O> java type of element of right operand of {@code in},the element is weak weakly instance, because sql is weakly typed.
     */
    <O> IPredicate notIn(Collection<O> parameters);

    /**
     * <p>
     * Equivalence : this.notIn({@link SQLs#params(ParamMeta, Collection)})
     * </p>
     */
    <O> IPredicate notInParam(Collection<O> parameters);

    /**
     * <p>
     * Equivalence : this.notIn({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate notInNamed(String paramName);

    /**
     * <p>
     * If parameters non-null,then parameters will be wrapped with {@link SQLs#optimizingParams(ParamMeta, Collection)}.
     * </p>
     *
     * @param <O> java type of element of parameters,the element is weak weakly instance, because sql is weakly typed.
     */
    @Nullable
    <O> IPredicate ifNotIn(@Nullable Collection<O> parameters);

    @Nullable
    <O> IPredicate ifNotInParam(@Nullable Collection<O> parameters);

    <O> IPredicate notIn(Expression<Collection<O>> values);

    <C> IPredicate notIn(Function<C, ColumnSubQuery> function);

    IPredicate notIn(Supplier<ColumnSubQuery> supplier);

    Expression<E> mod(Expression<?> operator);

    Expression<E> mod(Object parameter);

    /**
     * <p>
     * Equivalence : this.mod({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> modParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.mod({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> modNamed(String paramName);

    Expression<E> mod(String subQueryAlias, String derivedFieldName);

    Expression<E> mod(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> mod(Function<C, Expression<O>> function);

    <O> Expression<E> mod(Supplier<Expression<O>> supplier);

    Expression<E> multiply(Expression<?> multiplicand);

    Expression<E> multiply(Object parameter);

    /**
     * <p>
     * Equivalence : this.multiply({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> multiplyParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.multiply({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> multiplyNamed(String paramName);

    Expression<E> multiply(String subQueryAlias, String derivedFieldName);

    Expression<E> multiply(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> multiply(Function<C, Expression<O>> function);

    <O> Expression<E> multiply(Supplier<Expression<O>> supplier);

    Expression<E> plus(Expression<?> augend);

    Expression<E> plus(Object parameter);

    /**
     * <p>
     * Equivalence : this.plus({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> plusParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.plus({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> plusNamed(String paramName);

    Expression<E> plus(String subQueryAlias, String derivedFieldName);

    Expression<E> plus(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> plus(Function<C, Expression<O>> function);

    <O> Expression<E> plus(Supplier<Expression<O>> supplier);

    Expression<E> minus(Expression<?> subtrahend);

    Expression<E> minus(Object parameter);

    /**
     * <p>
     * Equivalence : this.minus({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> minusParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.minus({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> minusNamed(String paramName);

    Expression<E> minus(String subQueryAlias, String derivedFieldName);

    Expression<E> minus(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> minus(Function<C, Expression<O>> function);

    <O> Expression<E> minus(Supplier<Expression<O>> supplier);

    Expression<E> divide(Expression<?> divisor);

    Expression<E> divide(Object parameter);

    /**
     * <p>
     * Equivalence : this.divide({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> divideParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.divide({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> divideNamed(String paramName);

    Expression<E> divide(String subQueryAlias, String derivedFieldName);

    Expression<E> divide(String tableAlias, FieldMeta<?, ?> field);

    <C, O> Expression<E> divide(Function<C, Expression<O>> function);

    <O> Expression<E> divide(Supplier<Expression<O>> supplier);

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
     * <p>
     * Equivalence : this.bitwiseAnd({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> bitwiseAndParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.bitwiseAnd({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> bitwiseAndNamed(String paramName);

    /**
     * Bitwise AND
     *
     * @param <O> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C, O> Expression<E> bitwiseAnd(Function<C, Expression<O>> function);

    <O> Expression<E> bitwiseAnd(Supplier<Expression<O>> supplier);

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
     * <p>
     * Equivalence : this.bitwiseOr({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> bitwiseOrParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.bitwiseOr({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> bitwiseOrNamed(String paramName);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    <C, O> Expression<E> bitwiseOr(Function<C, Expression<O>> function);

    <O> Expression<E> bitwiseOr(Supplier<Expression<O>> supplier);

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
     * <p>
     * Equivalence : this.xor({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> xorParam(Object parameter);

    /**
     * <p>
     * Equivalence : this.xor({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> xorNamed(String paramName);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    <C, O> Expression<E> xor(Function<C, Expression<O>> function);

    <O> Expression<E> xor(Supplier<Expression<O>> supplier);

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
    Expression<E> rightShift(Number parameter);

    /**
     * <p>
     * Equivalence : this.rightShift({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> rightShiftParam(Number parameter);

    /**
     * <p>
     * Equivalence : this.rightShift({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> rightShiftNamed(String paramName);

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    <N extends Number> Expression<E> rightShift(Expression<N> bitNumber);

    /**
     * Shifts a  number to the right.
     *
     * @param <N> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C, N extends Number> Expression<E> rightShift(Function<C, Expression<N>> function);

    <N extends Number> Expression<E> rightShift(Supplier<Expression<N>> supplier);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> leftShift(Number parameter);

    /**
     * <p>
     * Equivalence : this.leftShift({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> leftShiftParam(Number parameter);

    /**
     * <p>
     * Equivalence : this.leftShift({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> leftShiftNamed(String paramName);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    <N extends Number> Expression<E> leftShift(Expression<N> bitNumber);

    /**
     * Shifts a  number to the left.
     *
     * @param <N> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C, N extends Number> Expression<E> leftShift(Function<C, Expression<N>> function);

    <N extends Number> Expression<E> leftShift(Supplier<Expression<N>> supplier);

    <O> Expression<O> asType(Class<O> convertType);

    <O> Expression<O> asType(Class<O> convertType, MappingType longMapping);

    <O> Expression<O> asType(Class<O> convertType, FieldMeta<?, O> longMapping);

    Expression<E> bracket();

    SortItem asc();

    SortItem desc();

    IPredicate like(String patternParameter);

    @Nullable
    IPredicate ifLike(@Nullable String patternParameter);

    /**
     * <p>
     * Equivalence : this.like({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate likeNamed(String paramName);

    <C> IPredicate like(Function<C, Expression<String>> function);

    IPredicate like(Supplier<Expression<String>> supplier);

    IPredicate notLike(String patternParameter);

    @Nullable
    IPredicate ifNotLike(@Nullable String patternParameter);

    /**
     * <p>
     * Equivalence : this.notLike({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate notLikeNamed(String paramName);

    IPredicate like(Expression<String> pattern);

    IPredicate notLike(Expression<String> pattern);

    <C> IPredicate notLike(Function<C, Expression<String>> function);

    IPredicate notLike(Supplier<Expression<String>> supplier);

}
