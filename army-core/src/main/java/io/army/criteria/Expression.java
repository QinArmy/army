package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
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
public interface Expression<E> extends SelectionSpec, TypeInfer, SortItem, SetRightItem {


    /**
     * relational operate with {@code =}
     * <p>
     * Operand will be wrapped with optimizing param
     * </p>
     *
     * @param operand right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     */
    IPredicate equal(Object operand);

    /**
     * <p>
     * Equivalence : this.equal({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate equalParam(Object operand);


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
     * @param operand right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     * @return If operand null return null,or return predicate instance.
     * @see Statement.WhereAndClause#ifAnd(IPredicate)
     */
    @Nullable
    IPredicate ifEqual(@Nullable Object operand);

    /**
     * <p>
     * Equivalence : this.equal({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    @Nullable
    IPredicate ifEqualParam(@Nullable Object operand);


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

    IPredicate lessThan(Object operand);

    /**
     * <p>
     * Equivalence : this.lessThan({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate lessThanParam(Object operand);

    /**
     * <p>
     * Equivalence : this.lessThan({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate lessThanNamed(String paramName);

    @Nullable
    IPredicate ifLessThan(@Nullable Object operand);

    @Nullable
    IPredicate ifLessThanParam(@Nullable Object operand);

    <C, O> IPredicate lessThan(Function<C, Expression<O>> function);

    <O> IPredicate lessThan(Supplier<Expression<O>> supplier);

    <C> IPredicate lessThanAny(Function<C, ColumnSubQuery> function);

    IPredicate lessThanAny(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate lessThanSome(Function<C, ColumnSubQuery> function);

    IPredicate lessThanSome(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate lessThanAll(Function<C, ColumnSubQuery> function);

    IPredicate lessThanAll(Supplier<ColumnSubQuery> supplier);

    IPredicate lessEqual(Object operand);

    /**
     * <p>
     * Equivalence : this.lessEqual({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate lessEqualParam(Object operand);

    /**
     * <p>
     * Equivalence : this.lessEqual({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate lessEqualNamed(String paramName);

    @Nullable
    IPredicate ifLessEqual(@Nullable Object operand);

    @Nullable
    IPredicate ifLessEqualParam(@Nullable Object operand);

    <C, O> IPredicate lessEqual(Function<C, Expression<O>> function);

    <O> IPredicate lessEqual(Supplier<Expression<O>> supplier);

    <C> IPredicate lessEqualAny(Function<C, ColumnSubQuery> function);

    IPredicate lessEqualAny(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate lessEqualSome(Function<C, ColumnSubQuery> function);

    IPredicate lessEqualSome(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate lessEqualAll(Function<C, ColumnSubQuery> function);

    IPredicate lessEqualAll(Supplier<ColumnSubQuery> supplier);

    IPredicate greatThan(Object operand);

    /**
     * <p>
     * Equivalence : this.greatThan({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate greatThanParam(Object operand);

    /**
     * <p>
     * Equivalence : this.greatThan({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate greatThanNamed(String paramName);

    @Nullable
    IPredicate ifGreatThan(@Nullable Object operand);

    @Nullable
    IPredicate ifGreatThanParam(@Nullable Object operand);

    <C, O> IPredicate greatThan(Function<C, Expression<O>> function);

    <O> IPredicate greatThan(Supplier<Expression<O>> supplier);

    <C> IPredicate greatThanAny(Function<C, ColumnSubQuery> function);

    IPredicate greatThanAny(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate greatThanSome(Function<C, ColumnSubQuery> function);

    IPredicate greatThanSome(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate greatThanAll(Function<C, ColumnSubQuery> function);

    IPredicate greatThanAll(Supplier<ColumnSubQuery> supplier);

    IPredicate greatEqual(Object operand);

    /**
     * <p>
     * Equivalence : this.greatEqual({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate greatEqualParam(Object operand);

    /**
     * <p>
     * Equivalence : this.greatEqual({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate greatEqualNamed(String operand);

    @Nullable
    IPredicate IfGreatEqual(@Nullable Object operand);

    @Nullable
    IPredicate ifGreatEqualParam(@Nullable Object operand);

    <C, O> IPredicate greatEqual(Function<C, Expression<O>> function);

    <O> IPredicate greatEqual(Supplier<Expression<O>> supplier);

    <C> IPredicate greatEqualAny(Function<C, ColumnSubQuery> function);

    IPredicate greatEqualAny(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate greatEqualSome(Function<C, ColumnSubQuery> function);

    IPredicate greatEqualSome(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate greatEqualAll(Function<C, ColumnSubQuery> function);

    IPredicate greatEqualAll(Supplier<ColumnSubQuery> supplier);

    IPredicate notEqual(Object operand);

    /**
     * <p>
     * Equivalence : this.notEqual({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate notEqualParam(Object operand);

    /**
     * <p>
     * Equivalence : this.notEqual({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate notEqualNamed(String paramName);

    @Nullable
    IPredicate ifNotEqual(@Nullable Object operand);

    @Nullable
    IPredicate ifNotEqualParam(@Nullable Object operand);

    <C, O> IPredicate notEqual(Function<C, Expression<O>> function);

    <O> IPredicate notEqual(Supplier<Expression<O>> supplier);

    <C> IPredicate notEqualAny(Function<C, ColumnSubQuery> function);

    IPredicate notEqualAny(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate notEqualSome(Function<C, ColumnSubQuery> function);

    IPredicate notEqualSome(Supplier<ColumnSubQuery> supplier);

    <C> IPredicate notEqualAll(Function<C, ColumnSubQuery> function);

    IPredicate notEqualAll(Supplier<ColumnSubQuery> supplier);

    IPredicate between(Object firstOperand, Object secondOperand);

    IPredicate betweenParam(Object firstOperand, Object secondOperand);

    @Nullable
    IPredicate ifBetween(@Nullable Object firstOperand, @Nullable Object secondOperand);

    @Nullable
    IPredicate ifBetweenParam(@Nullable Object firstOperand, @Nullable Object secondOperand);

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

    Expression<E> mod(Object operand);

    /**
     * <p>
     * Equivalence : this.mod({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> modParam(Object operand);

    /**
     * <p>
     * Equivalence : this.mod({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> modNamed(String paramName);


    <C, O> Expression<E> mod(Function<C, Expression<O>> function);

    <O> Expression<E> mod(Supplier<Expression<O>> supplier);

    Expression<E> multiply(Object multiplicand);

    /**
     * <p>
     * Equivalence : this.multiply({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> multiplyParam(Object multiplicand);

    /**
     * <p>
     * Equivalence : this.multiply({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> multiplyNamed(String paramName);

    <C, O> Expression<E> multiply(Function<C, Expression<O>> function);

    <O> Expression<E> multiply(Supplier<Expression<O>> supplier);

    Expression<E> plus(Object augend);

    /**
     * <p>
     * Equivalence : this.plus({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> plusParam(Object augend);

    /**
     * <p>
     * Equivalence : this.plus({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> plusNamed(String paramName);

    <C, O> Expression<E> plus(Function<C, Expression<O>> function);

    <O> Expression<E> plus(Supplier<Expression<O>> supplier);

    Expression<E> minus(Object minuend);

    /**
     * <p>
     * Equivalence : this.minus({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> minusParam(Object minuend);

    /**
     * <p>
     * Equivalence : this.minus({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> minusNamed(String paramName);


    <C, O> Expression<E> minus(Function<C, Expression<O>> function);

    <O> Expression<E> minus(Supplier<Expression<O>> supplier);

    Expression<E> divide(Object divisor);

    /**
     * <p>
     * Equivalence : this.divide({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> divideParam(Object divisor);

    /**
     * <p>
     * Equivalence : this.divide({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> divideNamed(String paramName);

    <C, O> Expression<E> divide(Function<C, Expression<O>> function);

    <O> Expression<E> divide(Supplier<Expression<O>> supplier);

    Expression<E> negate();

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression<E> bitwiseAnd(Object operand);

    /**
     * <p>
     * Equivalence : this.bitwiseAnd({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> bitwiseAndParam(Object operand);

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
    Expression<E> bitwiseOr(Object operand);

    /**
     * <p>
     * Equivalence : this.bitwiseOr({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> bitwiseOrParam(Object operand);

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
    Expression<E> xor(Object operand);

    /**
     * <p>
     * Equivalence : this.xor({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> xorParam(Object operand);

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
    Expression<E> rightShift(Object bitNumber);

    /**
     * <p>
     * Equivalence : this.rightShift({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> rightShiftParam(Object bitNumber);

    /**
     * <p>
     * Equivalence : this.rightShift({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> rightShiftNamed(String paramName);

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
    Expression<E> leftShift(Object bitNumber);

    /**
     * <p>
     * Equivalence : this.leftShift({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression<E> leftShiftParam(Object bitNumber);

    /**
     * <p>
     * Equivalence : this.leftShift({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression<E> leftShiftNamed(String paramName);


    /**
     * Shifts a  number to the left.
     *
     * @param <N> the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C, N extends Number> Expression<E> leftShift(Function<C, Expression<N>> function);

    <N extends Number> Expression<E> leftShift(Supplier<Expression<N>> supplier);

    <O> Expression<O> asType(Class<O> convertType);

    <O> Expression<O> asType(Class<O> convertType, ParamMeta paramMeta);

    Expression<E> bracket();

    SortItem asc();

    SortItem desc();

    IPredicate like(Object pattern);

    @Nullable
    IPredicate ifLike(@Nullable Object pattern);

    /**
     * <p>
     * Equivalence : this.like({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate likeNamed(String paramName);

    <C> IPredicate like(Function<C, Expression<String>> function);

    IPredicate like(Supplier<Expression<String>> supplier);

    IPredicate notLike(Object pattern);

    @Nullable
    IPredicate ifNotLike(@Nullable Object pattern);

    /**
     * <p>
     * Equivalence : this.notLike({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate notLikeNamed(String paramName);

    <C> IPredicate notLike(Function<C, Expression<String>> function);

    IPredicate notLike(Supplier<Expression<String>> supplier);

}
