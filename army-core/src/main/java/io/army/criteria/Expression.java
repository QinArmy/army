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
    <C> IPredicate equalExp(Function<C, Expression> function);

    IPredicate equalExp(Supplier<Expression> supplier);

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
    IPredicate ifEqual(Supplier<Object> operand);

    @Nullable
    <C> IPredicate ifEqual(Function<C, Object> operand);

    @Nullable
    IPredicate ifEqual(Function<String, Object> operand, String keyName);

    @Nullable
    IPredicate ifEqualLiteral(Supplier<Object> operand);

    @Nullable
    IPredicate ifEqualLiteral(Function<String, Object> operand, String keyName);


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

    <C> IPredicate lessThanExp(Function<C, Expression> function);

    IPredicate lessThanExp(Supplier<Expression> supplier);

    @Nullable
    IPredicate ifLessThan(Supplier<Object> operand);

    @Nullable
    <C> IPredicate ifLessThan(Function<C, Object> operand);

    @Nullable
    IPredicate ifLessThan(Function<String, Object> function, String keyName);

    @Nullable
    IPredicate ifLessThanLiteral(Supplier<Object> supplier);

    @Nullable
    IPredicate ifLessThanLiteral(Function<String, Object> function, String keyName);


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

    <C> IPredicate lessEqualExp(Function<C, Expression> function);

    IPredicate lessEqualExp(Supplier<Expression> supplier);

    @Nullable
    IPredicate ifLessEqual(Supplier<Object> operand);

    @Nullable
    <C> IPredicate ifLessEqual(Function<C, Object> operand);

    @Nullable
    IPredicate ifLessEqual(Function<String, Object> operand, String keyName);

    @Nullable
    IPredicate ifLessEqualLiteral(Supplier<Object> operand);

    @Nullable
    IPredicate ifLessEqualLiteral(Function<String, Object> operand, String keyName);

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


    /**
     * relational operate with {@code =}
     */
    <C> IPredicate greatThanExp(Function<C, Expression> function);

    IPredicate greatThanExp(Supplier<Expression> supplier);

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
    IPredicate ifGreatThan(Supplier<Object> operand);

    @Nullable
    <C> IPredicate ifGreatThan(Function<C, Object> operand);

    @Nullable
    IPredicate ifGreatThan(Function<String, Object> operand, String keyName);

    @Nullable
    IPredicate ifGreatThanLiteral(Supplier<Object> operand);

    @Nullable
    IPredicate ifGreatThanLiteral(Function<String, Object> operand, String keyName);


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


    /**
     * relational operate with {@code =}
     */
    <C> IPredicate greatEqualExp(Function<C, Expression> function);

    IPredicate greatEqualExp(Supplier<Expression> supplier);

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
    IPredicate ifGreatEqual(Supplier<Object> operand);

    @Nullable
    <C> IPredicate ifGreatEqual(Function<C, Object> operand);

    @Nullable
    IPredicate ifGreatEqual(Function<String, Object> operand, String keyName);

    @Nullable
    IPredicate ifGreatEqualLiteral(Supplier<Object> operand);

    @Nullable
    IPredicate ifGreatEqualLiteral(Function<String, Object> operand, String keyName);


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


    /**
     * relational operate with {@code =}
     */
    <C> IPredicate notEqualExp(Function<C, Expression> function);

    IPredicate notEqualExp(Supplier<Expression> supplier);

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
    IPredicate ifNotEqual(Supplier<Object> operand);

    @Nullable
    <C> IPredicate ifNotEqual(Function<C, Object> operand);

    @Nullable
    IPredicate ifNotEqual(Function<String, Object> operand, String keyName);

    @Nullable
    IPredicate ifNotEqualLiteral(Supplier<Object> operand);

    @Nullable
    IPredicate ifNotEqualLiteral(Function<String, Object> operand, String keyName);

    <C> IPredicate notEqualAny(Function<C, SubQuery> function);

    IPredicate notEqualAny(Supplier<SubQuery> supplier);

    <C> IPredicate notEqualSome(Function<C, SubQuery> function);

    IPredicate notEqualSome(Supplier<SubQuery> supplier);

    <C> IPredicate notEqualAll(Function<C, SubQuery> function);

    IPredicate notEqualAll(Supplier<SubQuery> supplier);

    IPredicate between(Object firstOperand, Object secondOperand);

    IPredicate betweenLiteral(Object firstOperand, Object secondOperand);

    @Nullable
    IPredicate ifBetween(Supplier<Object> firstOperand, Supplier<Object> secondOperand);

    @Nullable
    IPredicate ifBetween(Function<String, Object> function, String firstKey, String secondKey);

    @Nullable
    IPredicate ifBetweenLiteral(Supplier<Object> firstOperand, Supplier<Object> secondOperand);

    @Nullable
    IPredicate ifBetweenLiteral(Function<String, Object> function, String firstKey, String secondKey);

    <C> IPredicate between(Function<C, ExpressionPair> function);

    @Nullable
    <C> IPredicate ifBetween(Function<C, ExpressionPair> function);

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

    <C> IPredicate in(Function<C, Expression> function);

    IPredicate in(Supplier<Expression> supplier);

    /**
     * <p>
     * If parameters non-null parameters will be wrapped with {@link SQLs#optimizingParams(ParamMeta, Collection)}.
     * </p>
     *
     * @param <O> java type of element of parameters,the element is weak weakly instance, because sql is weakly typed.
     */
    @Nullable
    <O> IPredicate ifIn(Supplier<Collection<O>> supplier);

    @Nullable
    <O> IPredicate ifInParam(Supplier<Collection<O>> supplier);

    @Nullable
    IPredicate ifInExp(Supplier<Expression> supplier);

    @Nullable
    <C> IPredicate ifInExp(Function<C, Expression> function);

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

    <C> IPredicate notInExp(Function<C, Expression> function);

    IPredicate notInExp(Supplier<Expression> supplier);

    @Nullable
    <O> IPredicate ifNotIn(Supplier<Collection<O>> supplier);

    @Nullable
    <O> IPredicate ifNotInParam(Supplier<Collection<O>> supplier);

    @Nullable
    IPredicate ifNotInExp(Supplier<Expression> supplier);

    @Nullable
    <C> IPredicate ifNotInExp(Function<C, Expression> function);


    IPredicate like(Object pattern);

    /**
     * <p>
     * Equivalence : this.like({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate likeNamed(String paramName);


    <C> IPredicate likeExp(Function<C, Expression> function);

    IPredicate likeExp(Supplier<Expression> supplier);

    @Nullable
    IPredicate ifLike(Supplier<Object> pattern);

    @Nullable
    <C> IPredicate ifLike(Function<C, Object> pattern);

    @Nullable
    IPredicate ifLike(Function<String, Object> function, String keyName);


    IPredicate notLike(Object pattern);


    /**
     * <p>
     * Equivalence : this.notLike({@link SQLs#namedParam(String, ParamMeta)})
     * </p>
     */
    IPredicate notLikeNamed(String paramName);

    <C> IPredicate notLikeExp(Function<C, Expression> function);

    IPredicate notLikeExp(Supplier<Expression> supplier);

    @Nullable
    IPredicate ifNotLike(Supplier<Object> pattern);

    @Nullable
    <C> IPredicate ifNotLike(Function<C, Object> pattern);

    @Nullable
    IPredicate ifNotLike(Function<String, Object> function, String keyName);


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


    <C> Expression modExp(Function<C, Expression> function);

    Expression modExp(Supplier<Expression> supplier);

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

    <C> Expression multiplyExp(Function<C, Expression> function);

    Expression multiplyExp(Supplier<Expression> supplier);

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

    <C> Expression plusExp(Function<C, Expression> function);

    Expression plusExp(Supplier<Expression> supplier);

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


    <C> Expression minusExp(Function<C, Expression> function);

    Expression minusExp(Supplier<Expression> supplier);

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

    <C> Expression divideExp(Function<C, Expression> function);

    Expression divideExp(Supplier<Expression> supplier);

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
    <C> Expression bitwiseAndExp(Function<C, Expression> function);

    Expression bitwiseAndExp(Supplier<Expression> supplier);

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
    <C> Expression bitwiseOrExp(Function<C, Expression> function);

    Expression bitwiseOrExp(Supplier<Expression> supplier);

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
    <C> Expression xorExp(Function<C, Expression> function);

    Expression xorExp(Supplier<Expression> supplier);

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
    <C> Expression rightShiftExp(Function<C, Expression> function);

    Expression rightShiftExp(Supplier<Expression> supplier);

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
    <C> Expression leftShiftExp(Function<C, Expression> function);

    Expression leftShiftExp(Supplier<Expression> supplier);

    Expression asType(Class<?> convertType);

    Expression asType(ParamMeta paramMeta);

    Expression bracket();

    SortItem asc();

    SortItem desc();


}
