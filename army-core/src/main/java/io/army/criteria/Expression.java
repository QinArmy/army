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
     */
    <C> IPredicate equalExp(Function<C, ? extends Expression> function);

    IPredicate equalExp(Supplier<? extends Expression> supplier);


    /**
     * relational operate with {@code =}
     * <p>
     * If operand non-null than operand will be wrapped with optimizing param.
     * </p>
     *
     * @param operand right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     * @return If operand null return null,or return predicate instance.
     * @see Statement._WhereAndClause#ifAnd(IPredicate)
     */
    @Nullable
    IPredicate ifEqual(Supplier<?> operand);

    @Nullable
    IPredicate ifEqual(Function<String, ?> function, String keyName);

    @Nullable
    IPredicate ifEqualLiteral(Supplier<?> operand);

    @Nullable
    IPredicate ifEqualLiteral(Function<String, ?> function, String keyName);


    /**
     * relational operate with {@code = ANY}
     */
    <C> IPredicate equalAny(Function<C, ? extends SubQuery> supplier);

    /**
     * relational operate with {@code = ANY}
     */
    IPredicate equalAny(Supplier<? extends SubQuery> supplier);

    /**
     * relational operate with {@code = SOME}
     */
    <C> IPredicate equalSome(Function<C, ? extends SubQuery> function);

    /**
     * relational operate with {@code = SOME}
     */
    IPredicate equalSome(Supplier<? extends SubQuery> subQuery);

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

    <C> IPredicate lessThanExp(Function<C, ? extends Expression> function);

    IPredicate lessThanExp(Supplier<? extends Expression> supplier);

    @Nullable
    IPredicate ifLessThan(Supplier<?> operand);

    @Nullable
    IPredicate ifLessThan(Function<String, ?> function, String keyName);

    @Nullable
    IPredicate ifLessThanLiteral(Supplier<?> supplier);

    @Nullable
    IPredicate ifLessThanLiteral(Function<String, ?> function, String keyName);


    <C> IPredicate lessThanAny(Function<C, ? extends SubQuery> function);

    IPredicate lessThanAny(Supplier<? extends SubQuery> supplier);

    <C> IPredicate lessThanSome(Function<C, ? extends SubQuery> function);

    IPredicate lessThanSome(Supplier<? extends SubQuery> supplier);

    <C> IPredicate lessThanAll(Function<C, ? extends SubQuery> function);

    IPredicate lessThanAll(Supplier<? extends SubQuery> supplier);

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

    <C> IPredicate lessEqualExp(Function<C, ? extends Expression> function);

    IPredicate lessEqualExp(Supplier<? extends Expression> supplier);

    @Nullable
    IPredicate ifLessEqual(Supplier<?> operand);

    @Nullable
    IPredicate ifLessEqual(Function<String, ?> function, String keyName);

    @Nullable
    IPredicate ifLessEqualLiteral(Supplier<?> operand);

    @Nullable
    IPredicate ifLessEqualLiteral(Function<String, ?> function, String keyName);

    <C> IPredicate lessEqualAny(Function<C, ? extends SubQuery> function);

    IPredicate lessEqualAny(Supplier<? extends SubQuery> supplier);

    <C> IPredicate lessEqualSome(Function<C, ? extends SubQuery> function);

    IPredicate lessEqualSome(Supplier<? extends SubQuery> supplier);

    <C> IPredicate lessEqualAll(Function<C, ? extends SubQuery> function);

    IPredicate lessEqualAll(Supplier<? extends SubQuery> supplier);

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


    /**
     * relational operate with {@code =}
     */
    <C> IPredicate greatThanExp(Function<C, ? extends Expression> function);

    IPredicate greatThanExp(Supplier<? extends Expression> supplier);

    /**
     * Relational operate with {@code =}
     * <p>
     * If operand non-null than operand will be wrapped with optimizing param.
     * </p>
     *
     * @param operand right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     * @return If operand null return null,or return predicate instance.
     * @see Statement._WhereAndClause#ifAnd(IPredicate)
     */
    @Nullable
    IPredicate ifGreatThan(Supplier<?> operand);

    @Nullable
    IPredicate ifGreatThan(Function<String, ?> function, String keyName);

    @Nullable
    IPredicate ifGreatThanLiteral(Supplier<?> operand);

    @Nullable
    IPredicate ifGreatThanLiteral(Function<String, ?> function, String keyName);


    <C> IPredicate greatThanAny(Function<C, ? extends SubQuery> function);

    IPredicate greatThanAny(Supplier<? extends SubQuery> supplier);

    <C> IPredicate greatThanSome(Function<C, ? extends SubQuery> function);

    IPredicate greatThanSome(Supplier<? extends SubQuery> supplier);

    <C> IPredicate greatThanAll(Function<C, ? extends SubQuery> function);

    IPredicate greatThanAll(Supplier<? extends SubQuery> supplier);

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


    /**
     * relational operate with {@code =}
     */
    <C> IPredicate greatEqualExp(Function<C, ? extends Expression> function);

    IPredicate greatEqualExp(Supplier<? extends Expression> supplier);

    /**
     * relational operate with {@code =}
     * <p>
     * If operand non-null than operand will be wrapped with optimizing param.
     * </p>
     *
     * @param operand right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     * @return If operand null return null,or return predicate instance.
     * @see Statement._WhereAndClause#ifAnd(IPredicate)
     */
    @Nullable
    IPredicate ifGreatEqual(Supplier<?> operand);

    @Nullable
    IPredicate ifGreatEqual(Function<String, ?> function, String keyName);

    @Nullable
    IPredicate ifGreatEqualLiteral(Supplier<?> operand);

    @Nullable
    IPredicate ifGreatEqualLiteral(Function<String, ?> function, String keyName);


    <C> IPredicate greatEqualAny(Function<C, ? extends SubQuery> function);

    IPredicate greatEqualAny(Supplier<? extends SubQuery> supplier);

    <C> IPredicate greatEqualSome(Function<C, ? extends SubQuery> function);

    IPredicate greatEqualSome(Supplier<? extends SubQuery> supplier);

    <C> IPredicate greatEqualAll(Function<C, ? extends SubQuery> function);

    IPredicate greatEqualAll(Supplier<? extends SubQuery> supplier);

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


    /**
     * relational operate with {@code =}
     */
    <C> IPredicate notEqualExp(Function<C, ? extends Expression> function);

    IPredicate notEqualExp(Supplier<? extends Expression> supplier);

    /**
     * relational operate with {@code =}
     * <p>
     * If operand non-null than operand will be wrapped with optimizing param.
     * </p>
     *
     * @param operand right operand of {@code =},operand is weak weakly instance, because sql is weakly typed.
     * @return If operand null return null,or return predicate instance.
     * @see Statement._WhereAndClause#ifAnd(IPredicate)
     */
    @Nullable
    IPredicate ifNotEqual(Supplier<?> operand);

    @Nullable
    IPredicate ifNotEqual(Function<String, ?> function, String keyName);

    @Nullable
    IPredicate ifNotEqualLiteral(Supplier<?> operand);

    @Nullable
    IPredicate ifNotEqualLiteral(Function<String, ?> function, String keyName);

    <C> IPredicate notEqualAny(Function<C, ? extends SubQuery> function);

    IPredicate notEqualAny(Supplier<? extends SubQuery> supplier);

    <C> IPredicate notEqualSome(Function<C, ? extends SubQuery> function);

    IPredicate notEqualSome(Supplier<? extends SubQuery> supplier);

    <C> IPredicate notEqualAll(Function<C, ? extends SubQuery> function);

    IPredicate notEqualAll(Supplier<? extends SubQuery> supplier);

    IPredicate between(Object firstOperand, Object secondOperand);

    IPredicate betweenLiteral(Object firstOperand, Object secondOperand);

    @Nullable
    IPredicate ifBetween(Supplier<?> firstOperand, Supplier<?> secondOperand);

    @Nullable
    IPredicate ifBetween(Function<String, ?> function, String firstKey, String secondKey);

    @Nullable
    IPredicate ifBetweenLiteral(Supplier<?> firstOperand, Supplier<?> secondOperand);

    @Nullable
    IPredicate ifBetweenLiteral(Function<String, ?> function, String firstKey, String secondKey);

    <C> IPredicate between(Function<C, ExpressionPair> function);

    @Nullable
    <C> IPredicate ifBetween(Function<C, ExpressionPair> function);

    IPredicate isNull();

    IPredicate isNotNull();

    /**
     * <p>
     * Parameters will be wrapped with {@link SQLs#optimizingParams(ParamMeta, Collection)}.
     * </p>
     */
    IPredicate in(Object operand);

    /**
     * <p>
     * Equivalence : this.in({@link SQLs#params(ParamMeta, Collection)})
     * </p>
     */
    IPredicate inParam(Object operand);

    /**
     * <p>
     * Equivalence : this.in({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate inNamed(String paramName);

    <C> IPredicate inExp(Function<C, ? extends Expression> function);

    IPredicate inExp(Supplier<? extends Expression> supplier);

    /**
     * <p>
     * If parameters non-null parameters will be wrapped with {@link SQLs#optimizingParams(ParamMeta, Collection)}.
     * </p>
     **/
    @Nullable
    IPredicate ifIn(Supplier<?> supplier);

    @Nullable
    IPredicate ifIn(Function<String, ?> function, String keyName);

    @Nullable
    IPredicate ifInParam(Supplier<?> supplier);

    @Nullable
    IPredicate ifInParam(Function<String, ?> function, String keyName);

    /**
     * <p>
     * Parameters will be wrapped with {@link SQLs#optimizingParams(ParamMeta, Collection)}.
     * </p>
     */
    IPredicate notIn(Object operand);

    /**
     * <p>
     * Equivalence : this.notIn({@link SQLs#params(ParamMeta, Collection)})
     * </p>
     */
    IPredicate notInParam(Object operand);

    /**
     * <p>
     * Equivalence : this.notIn({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate notInNamed(String paramName);

    <C> IPredicate notInExp(Function<C, ? extends Expression> function);

    IPredicate notInExp(Supplier<? extends Expression> supplier);

    @Nullable
    IPredicate ifNotIn(Supplier<?> supplier);

    @Nullable
    IPredicate ifNotIn(Function<String, ?> function, String keyName);

    @Nullable
    IPredicate ifNotInParam(Supplier<?> operand);

    @Nullable
    IPredicate ifNotInParam(Function<String, ?> function, String keyName);


    IPredicate like(Object pattern);

    /**
     * <p>
     * Equivalence : this.like({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate likeNamed(String paramName);

    <C> IPredicate likeExp(Function<C, ? extends Expression> function);

    IPredicate likeExp(Supplier<? extends Expression> supplier);

    @Nullable
    IPredicate ifLike(Supplier<?> supplier);

    @Nullable
    IPredicate ifLike(Function<String, ?> function, String keyName);


    IPredicate notLike(Object pattern);


    /**
     * <p>
     * Equivalence : this.notLike({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate notLikeNamed(String paramName);

    <C> IPredicate notLikeExp(Function<C, ? extends Expression> function);

    IPredicate notLikeExp(Supplier<? extends Expression> supplier);

    @Nullable
    IPredicate ifNotLike(Supplier<?> supplier);

    @Nullable
    IPredicate ifNotLike(Function<String, ?> function, String keyName);


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


    <C> Expression modExp(Function<C, ? extends Expression> function);

    Expression modExp(Supplier<? extends Expression> supplier);

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

    <C> Expression multiplyExp(Function<C, ? extends Expression> function);

    Expression multiplyExp(Supplier<? extends Expression> supplier);

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

    <C> Expression plusExp(Function<C, ? extends Expression> function);

    Expression plusExp(Supplier<? extends Expression> supplier);

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


    <C> Expression minusExp(Function<C, ? extends Expression> function);

    Expression minusExp(Supplier<? extends Expression> supplier);

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

    <C> Expression divideExp(Function<C, ? extends Expression> function);

    Expression divideExp(Supplier<? extends Expression> supplier);

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
    <C> Expression bitwiseAndExp(Function<C, ? extends Expression> function);

    Expression bitwiseAndExp(Supplier<? extends Expression> supplier);

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
    <C> Expression bitwiseOrExp(Function<C, ? extends Expression> function);

    Expression bitwiseOrExp(Supplier<? extends Expression> supplier);

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
    <C> Expression xorExp(Function<C, ? extends Expression> function);

    Expression xorExp(Supplier<? extends Expression> supplier);

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
    <C> Expression rightShiftExp(Function<C, ? extends Expression> function);

    Expression rightShiftExp(Supplier<? extends Expression> supplier);

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
    <C> Expression leftShiftExp(Function<C, ? extends Expression> function);

    Expression leftShiftExp(Supplier<? extends Expression> supplier);

    Expression asType(Class<?> convertType);

    Expression asType(ParamMeta paramMeta);

    Expression bracket();

    SortItem asc();

    SortItem desc();


}
