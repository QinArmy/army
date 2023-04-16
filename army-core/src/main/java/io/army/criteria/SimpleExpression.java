package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.OptionalClauseOperator;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * <p>
 * This interface representing simple {@link Expression} :
 *     <ul>
 *         <li>{@link DataField}</li>
 *         <li>single-value parameter/literal,for example {@link SQLs#param(TypeInfer, Object)}</li>
 *         <li>parentheses expression,for example (1+ 2)</li>
 *         <li>sql function,for example {@link SQLs#countAsterisk()}</li>
 *         <li>sql variable</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface SimpleExpression extends Expression {


    /**
     * <p>
     * <strong>=</strong> operator. This method is similar to {@link #equal(Expression)},except that the operand
     * of {@link #equal(Expression)} is returned by funcRef.
     * </p>
     * <p>
     * <strong>Node</strong>: The left operand of funcRef always is <strong>this</strong> for <strong>=</strong> operator.
     * </p>
     *
     * @param funcRef the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                <ul>
     *                    <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                    <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                    <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete ) syntax</li>
     *                    <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                    and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                    <li>developer custom method</li>
     *                </ul>.
     *                The left operand of funcRef always is this for <strong>=</strong> operator.
     * @param operand non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when <ul>
     *                           <li>The {@link Expression} returned by funcRef isn't operable {@link Expression},for example {@link SQLs#DEFAULT}</li>
     *                           <li>The {@link Expression} returned by funcRef is multi-value {@link Expression},for example {@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                           </ul>
     */
    <T> IPredicate equal(BiFunction<Expression, T, Expression> funcRef, T operand);

    /**
     * <p>
     * <strong>&lt;</strong> . This method is similar to {@link #less(Expression)},except that the operand
     * {@link Expression} is returned by valueFunc.
     * </p>
     * <p>
     * <strong>Node</strong>: The left operand of valueFunc always is this for <strong>=</strong> valueFunc.
     * </p>
     *
     * @param valueFunc the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                      <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                      <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                      <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                      <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                      and in INSERT( or batch update/delete ) syntax</li>
     *                      <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                      and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The left operand of valueFunc always is this for <strong>=</strong> operator.
     * @param operand   non-null,it will pass to valueFunc as the right valueFunc of valueFunc
     * @throws CriteriaException throw when <ul>
     *                           <li>The {@link Expression} returned by valueFunc isn't operable {@link Expression},for example {@link SQLs#DEFAULT}</li>
     *                           <li>The {@link Expression} returned by valueFunc is multi-value {@link Expression},for example {@link SQLs#multiParam(TypeInfer, Collection)}</li>
     *                           </ul>
     */
    <T> IPredicate less(BiFunction<Expression, T, Expression> valueFunc, T operand);


    <T> IPredicate lessEqual(BiFunction<Expression, T, Expression> operator, T operand);

    <T> IPredicate great(BiFunction<Expression, T, Expression> operator, T operand);

    <T> IPredicate greatEqual(BiFunction<Expression, T, Expression> operator, T operand);


    <T> IPredicate notEqual(BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * @param and {@link SQLs#AND}
     */
    <T> IPredicate between(BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);


    /**
     * @param and {@link SQLs#AND}
     */
    <T> IPredicate notBetween(BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);


    /**
     * @param and {@link SQLs#AND}
     */
    <T> IPredicate between(@Nullable SQLs.BetweenModifier modifier, BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);

    /**
     * @param and {@link SQLs#AND}
     */
    <T> IPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);


    <T> IPredicate is(SQLs.IsComparisonWord operator, BiFunction<Expression, T, Expression> valueOperator, @Nullable T value);

    <T> IPredicate isNot(SQLs.IsComparisonWord operator, BiFunction<Expression, T, Expression> valueOperator, @Nullable T value);

    <T extends Collection<?>> IPredicate in(BiFunction<Expression, T, Expression> operator, T operand);

    <T extends Collection<?>> IPredicate notIn(BiFunction<Expression, T, Expression> operator, T operand);


    <T> IPredicate like(BiFunction<MappingType, T, Expression> operator, T operand);

    <T> IPredicate like(BiFunction<MappingType, T, Expression> operator, T operand, SQLs.WordEscape escape, char escapeChar);

    <T> IPredicate notLike(BiFunction<MappingType, T, Expression> operator, T operand);

    <T> IPredicate notLike(BiFunction<MappingType, T, Expression> operator, T operand, SQLs.WordEscape escape, char escapeChar);

    <T> Expression mod(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression times(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression plus(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression minus(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression divide(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression bitwiseXor(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression rightShift(BiFunction<Expression, T, Expression> operator, T operand);

    <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, T operand);


    <T> Expression apply(BiFunction<Expression, Expression, Expression> operator, BiFunction<Expression, T, Expression> valueOperator, T value);

    <M extends SQLWords, T> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, BiFunction<Expression, T, Expression> valueOperator, T value, M modifier, Expression optionalExp);

    <M extends SQLWords, T> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, BiFunction<Expression, T, Expression> valueOperator, T value, M modifier, char escapeChar);

    <T> IPredicate test(BiFunction<Expression, Expression, IPredicate> operator, BiFunction<Expression, T, Expression> valueOperator, T value);

    <M extends SQLWords, T> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, BiFunction<MappingType, T, Expression> valueOperator, T value, M modifier, Expression optionalExp);

    <M extends SQLWords, T> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, BiFunction<MappingType, T, Expression> valueOperator, T value, M modifier, char escapeChar);


}
