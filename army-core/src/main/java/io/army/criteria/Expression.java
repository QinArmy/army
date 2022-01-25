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
 * @see FieldMeta
 * @since 1.0
 */
@SuppressWarnings("unused")
public interface Expression extends SelectionSpec, TypeInfer, SortItem, SetRightItem {


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
    IPredicate equalLiteral(Object operand);


    /**
     * <p>
     * Equivalence : this.equal({@link SQLs#namedParam(String, ParamMeta)}})
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
    IPredicate ifEqualLiteral(@Nullable Object operand);


    /**
     * relational operate with {@code =}
     */
    <C> IPredicate equal(Function<C, Object> function);

    IPredicate equal(Supplier<Object> supplier);

    /**
     * relational operate with {@code = ANY}
     */
    <C> IPredicate equalAny(Function<C, SubQuery> supplier);

    /**
     * relational operate with {@code = ANY}
     */
    IPredicate equalAny(Supplier<SubQuery> supplier);

    /**
     * relational operate with {@code = SOME}
     */
    <C> IPredicate equalSome(Function<C, SubQuery> function);

    /**
     * relational operate with {@code = SOME}
     */
    IPredicate equalSome(Supplier<SubQuery> subQuery);

    IPredicate lessThan(Object operand);

    /**
     * <p>
     * Equivalence : this.lessThan({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate lessThanLiteral(Object operand);

    /**
     * <p>
     * Equivalence : this.lessThan({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate lessThanNamed(String paramName);

    @Nullable
    IPredicate ifLessThan(@Nullable Object operand);

    @Nullable
    IPredicate ifLessThanLiteral(@Nullable Object operand);

    <C> IPredicate lessThan(Function<C, Object> function);

    IPredicate lessThan(Supplier<Object> supplier);

    <C> IPredicate lessThanAny(Function<C, SubQuery> function);

    IPredicate lessThanAny(Supplier<SubQuery> supplier);

    <C> IPredicate lessThanSome(Function<C, SubQuery> function);

    IPredicate lessThanSome(Supplier<SubQuery> supplier);

    <C> IPredicate lessThanAll(Function<C, SubQuery> function);

    IPredicate lessThanAll(Supplier<SubQuery> supplier);

    IPredicate lessEqual(Object operand);

    /**
     * <p>
     * Equivalence : this.lessEqual({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate lessEqualLiteral(Object operand);

    /**
     * <p>
     * Equivalence : this.lessEqual({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate lessEqualNamed(String paramName);

    @Nullable
    IPredicate ifLessEqual(@Nullable Object operand);

    @Nullable
    IPredicate ifLessEqualLiteral(@Nullable Object operand);

    <C> IPredicate lessEqual(Function<C, Object> function);

    IPredicate lessEqual(Supplier<Object> supplier);

    <C> IPredicate lessEqualAny(Function<C, SubQuery> function);

    IPredicate lessEqualAny(Supplier<SubQuery> supplier);

    <C> IPredicate lessEqualSome(Function<C, SubQuery> function);

    IPredicate lessEqualSome(Supplier<SubQuery> supplier);

    <C> IPredicate lessEqualAll(Function<C, SubQuery> function);

    IPredicate lessEqualAll(Supplier<SubQuery> supplier);

    IPredicate greatThan(Object operand);

    /**
     * <p>
     * Equivalence : this.greatThan({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate greatThanLiteral(Object operand);

    /**
     * <p>
     * Equivalence : this.greatThan({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate greatThanNamed(String paramName);

    @Nullable
    IPredicate ifGreatThan(@Nullable Object operand);

    @Nullable
    IPredicate ifGreatThanLiteral(@Nullable Object operand);

    <C> IPredicate greatThan(Function<C, Object> function);

    IPredicate greatThan(Supplier<Object> supplier);

    <C> IPredicate greatThanAny(Function<C, SubQuery> function);

    IPredicate greatThanAny(Supplier<SubQuery> supplier);

    <C> IPredicate greatThanSome(Function<C, SubQuery> function);

    IPredicate greatThanSome(Supplier<SubQuery> supplier);

    <C> IPredicate greatThanAll(Function<C, SubQuery> function);

    IPredicate greatThanAll(Supplier<SubQuery> supplier);

    IPredicate greatEqual(Object operand);

    /**
     * <p>
     * Equivalence : this.greatEqual({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate greatEqualLiteral(Object operand);

    /**
     * <p>
     * Equivalence : this.greatEqual({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate greatEqualNamed(String operand);

    @Nullable
    IPredicate ifGreatEqual(@Nullable Object operand);

    @Nullable
    IPredicate ifGreatEqualLiteral(@Nullable Object operand);

    <C> IPredicate greatEqual(Function<C, Object> function);

    IPredicate greatEqual(Supplier<Object> supplier);

    <C> IPredicate greatEqualAny(Function<C, SubQuery> function);

    IPredicate greatEqualAny(Supplier<SubQuery> supplier);

    <C> IPredicate greatEqualSome(Function<C, SubQuery> function);

    IPredicate greatEqualSome(Supplier<SubQuery> supplier);

    <C> IPredicate greatEqualAll(Function<C, SubQuery> function);

    IPredicate greatEqualAll(Supplier<SubQuery> supplier);

    IPredicate notEqual(Object operand);

    /**
     * <p>
     * Equivalence : this.notEqual({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    IPredicate notEqualLiteral(Object operand);

    /**
     * <p>
     * Equivalence : this.notEqual({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate notEqualNamed(String paramName);

    @Nullable
    IPredicate ifNotEqual(@Nullable Object operand);

    @Nullable
    IPredicate ifNotEqualLiteral(@Nullable Object operand);

    <C> IPredicate notEqual(Function<C, Object> function);

    IPredicate notEqual(Supplier<Object> supplier);

    <C> IPredicate notEqualAny(Function<C, SubQuery> function);

    IPredicate notEqualAny(Supplier<SubQuery> supplier);

    <C> IPredicate notEqualSome(Function<C, SubQuery> function);

    IPredicate notEqualSome(Supplier<SubQuery> supplier);

    <C> IPredicate notEqualAll(Function<C, SubQuery> function);

    IPredicate notEqualAll(Supplier<SubQuery> supplier);

    IPredicate between(Object firstOperand, Object secondOperand);

    IPredicate betweenLiteral(Object firstOperand, Object secondOperand);

    @Nullable
    IPredicate ifBetween(@Nullable Object firstOperand, @Nullable Object secondOperand);

    @Nullable
    IPredicate ifBetweenLiteral(@Nullable Object firstOperand, @Nullable Object secondOperand);

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

    <O> IPredicate in(Expression parameters);

    <C> IPredicate in(Function<C, SubQuery> function);

    IPredicate in(Supplier<SubQuery> supplier);

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

    IPredicate notIn(Expression values);

    <C> IPredicate notIn(Function<C, SubQuery> function);

    IPredicate notIn(Supplier<SubQuery> supplier);

    Expression mod(Object operand);

    /**
     * <p>
     * Equivalence : this.mod({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression modLiteral(Object operand);

    /**
     * <p>
     * Equivalence : this.mod({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression modNamed(String paramName);


    <C> Expression mod(Function<C, Object> function);

    Expression mod(Supplier<Object> supplier);

    Expression multiply(Object multiplicand);

    /**
     * <p>
     * Equivalence : this.multiply({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression multiplyLiteral(Object multiplicand);

    /**
     * <p>
     * Equivalence : this.multiply({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression multiplyNamed(String paramName);

    <C> Expression multiply(Function<C, Object> function);

    Expression multiply(Supplier<Object> supplier);

    Expression plus(Object augend);

    /**
     * <p>
     * Equivalence : this.plus({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression plusLiteral(Object augend);

    /**
     * <p>
     * Equivalence : this.plus({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression plusNamed(String paramName);

    <C> Expression plus(Function<C, Object> function);

    Expression plus(Supplier<Object> supplier);

    Expression minus(Object minuend);

    /**
     * <p>
     * Equivalence : this.minus({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression minusLiteral(Object minuend);

    /**
     * <p>
     * Equivalence : this.minus({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression minusNamed(String paramName);


    <C> Expression minus(Function<C, Object> function);

    Expression minus(Supplier<Object> supplier);

    Expression divide(Object divisor);

    /**
     * <p>
     * Equivalence : this.divide({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression divideLiteral(Object divisor);

    /**
     * <p>
     * Equivalence : this.divide({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression divideNamed(String paramName);

    <C> Expression divide(Function<C, Object> function);

    Expression divide(Supplier<Object> supplier);

    Expression negate();

    /**
     * Bitwise AND
     *
     * @return {@link BigInteger} expression
     */
    Expression bitwiseAnd(Object operand);

    /**
     * <p>
     * Equivalence : this.bitwiseAnd({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression bitwiseAndLiteral(Object operand);

    /**
     * <p>
     * Equivalence : this.bitwiseAnd({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression bitwiseAndNamed(String paramName);

    /**
     * Bitwise AND
     *
     * @param function the type maybe different from this.
     * @return {@link BigInteger} expression
     */
    <C> Expression bitwiseAnd(Function<C, Object> function);

    Expression bitwiseAnd(Supplier<Object> supplier);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    Expression bitwiseOr(Object operand);

    /**
     * <p>
     * Equivalence : this.bitwiseOr({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression bitwiseOrLiteral(Object operand);

    /**
     * <p>
     * Equivalence : this.bitwiseOr({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression bitwiseOrNamed(String paramName);

    /**
     * Bitwise OR
     *
     * @return {@link BigInteger} expression
     */
    <C> Expression bitwiseOr(Function<C, Object> function);

    Expression bitwiseOr(Supplier<Object> supplier);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    Expression xor(Object operand);

    /**
     * <p>
     * Equivalence : this.xor({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression xorLiteral(Object operand);

    /**
     * <p>
     * Equivalence : this.xor({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression xorNamed(String paramName);

    /**
     * Bitwise XOR
     *
     * @return {@link BigInteger} expression
     */
    <C> Expression xor(Function<C, Object> function);

    Expression xor(Supplier<Object> supplier);

    /**
     * Bitwise Inversion
     *
     * @return {@link BigInteger} expression
     */
    Expression inversion();

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    Expression rightShift(Object bitNumber);

    /**
     * <p>
     * Equivalence : this.rightShift({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression rightShiftLiteral(Object bitNumber);

    /**
     * <p>
     * Equivalence : this.rightShift({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression rightShiftNamed(String paramName);

    /**
     * Shifts a  number to the right.
     *
     * @return {@link BigInteger} expression
     */
    <C> Expression rightShift(Function<C, Object> function);

    Expression rightShift(Supplier<Object> supplier);

    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    Expression leftShift(Object bitNumber);

    /**
     * <p>
     * Equivalence : this.leftShift({@link SQLs#param(ParamMeta, Object)})
     * </p>
     */
    Expression leftShiftLiteral(Object bitNumber);

    /**
     * <p>
     * Equivalence : this.leftShift({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    Expression leftShiftNamed(String paramName);


    /**
     * Shifts a  number to the left.
     *
     * @return {@link BigInteger} expression
     */
    <C> Expression leftShift(Function<C, Object> function);

    Expression leftShift(Supplier<Object> supplier);

    Expression asType(Class<?> convertType);

    Expression asType(ParamMeta paramMeta);

    Expression bracket();

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

    <C> IPredicate like(Function<C, Object> function);

    IPredicate like(Supplier<Object> supplier);

    IPredicate notLike(Object pattern);

    @Nullable
    IPredicate ifNotLike(@Nullable Object pattern);

    /**
     * <p>
     * Equivalence : this.notLike({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate notLikeNamed(String paramName);

    <C> IPredicate notLike(Function<C, Object> function);

    IPredicate notLike(Supplier<Object> supplier);

}
