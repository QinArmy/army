package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.OptionalClauseOperator;
import io.army.function.TeNamedOperator;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;

import java.util.Collection;
import java.util.function.BiFunction;

import static io.army.dialect.Database.H2;
import static io.army.dialect.Database.PostgreSQL;

/**
 * <p>
 * This interface representing simple {@link Expression} :
 *     <ul>
 *         <li>{@link DataField}</li>
 *         <li>single-value parameter/literal,for example {@link SQLs#param(TypeInfer, Object)}</li>
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
     * <strong>Node</strong>: The first argument of funcRef always is <strong>this</strong>.
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
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    <T> IPredicate equal(BiFunction<Expression, T, Expression> funcRef, T value);

    /**
     * <p>
     * Operator <strong>&lt;</strong> . This method is similar to {@link #less(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     * </p>
     * <p>
     * <strong>Node</strong>: The first argument of funcRef always is <strong>this</strong>.
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
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    <T> IPredicate less(BiFunction<Expression, T, Expression> funcRef, T value);


    /**
     * <p>
     * Operator <strong>&lt;=</strong> . This method is similar to {@link #lessEqual(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     * </p>
     * <p>
     * <strong>Node</strong>: The first argument of funcRef always is <strong>this</strong>.
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
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    <T> IPredicate lessEqual(BiFunction<Expression, T, Expression> funcRef, T value);

    /**
     * <p>
     * Operator <strong>></strong> . This method is similar to {@link #great(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     * </p>
     * <p>
     * <strong>Node</strong>: The first argument of funcRef always is <strong>this</strong>.
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
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    <T> IPredicate great(BiFunction<Expression, T, Expression> funcRef, T value);

    /**
     * <p>
     * Operator <strong>>=</strong> . This method is similar to {@link #greatEqual(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     * </p>
     * <p>
     * <strong>Node</strong>: The first argument of funcRef always is <strong>this</strong>.
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
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when Operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    <T> IPredicate greatEqual(BiFunction<Expression, T, Expression> funcRef, T value);

    /**
     * <p>
     * Operator <strong>!=</strong> . This method is similar to {@link #notEqual(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     * </p>
     * <p>
     * <strong>Node</strong>: The first argument of funcRef always is <strong>this</strong>.
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
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    <T> IPredicate notEqual(BiFunction<Expression, T, Expression> funcRef, T value);

    /**
     * <p>
     * Operator <strong>BETWEEN AND</strong> . This method is similar to {@link #between(Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     * </p>
     * <p>
     * <strong>Node</strong>: The first argument of funcRef always is <strong>this</strong>.
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
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param first   non-null,it will be passed to funcRef as the second argument of funcRef
     * @param and     {@link SQLs#AND}
     * @param second  non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    <T> IPredicate between(BiFunction<Expression, T, Expression> funcRef, T first, SQLs.WordAnd and, T second);


    /**
     * <p>
     * Operator <strong>NOT BETWEEN AND</strong> . This method is similar to {@link #notBetween(Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     * </p>
     * <p>
     * <strong>Node</strong>: The first argument of funcRef always is <strong>this</strong>.
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
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param first   non-null,it will be passed to funcRef as the second argument of funcRef
     * @param and     {@link SQLs#AND}
     * @param second  non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    <T> IPredicate notBetween(BiFunction<Expression, T, Expression> funcRef, T first, SQLs.WordAnd and, T second);


    /**
     * <p>
     * Operator <strong>BETWEEN [SYMMETRIC/ASYMMETRIC] AND</strong> . This method is similar to {@link #between(SQLs.BetweenModifier, Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     * </p>
     * <p>
     * <strong>Node</strong>: The first argument of funcRef always is <strong>this</strong>.
     * </p>
     *
     * @param modifier {@link SQLs#SYMMETRIC} or {@link SQLs#ASYMMETRIC}
     * @param funcRef  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                 <ul>
     *                     <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>developer custom method</li>
     *                 </ul>.
     *                 The first argument of funcRef always is <strong>this</strong>.
     * @param first    non-null,it will be passed to funcRef as the second argument of funcRef
     * @param and      {@link SQLs#AND}
     * @param second   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    @Support({PostgreSQL, H2})
    <T> IPredicate between(@Nullable SQLs.BetweenModifier modifier, BiFunction<Expression, T, Expression> funcRef, T first, SQLs.WordAnd and, T second);

    /**
     * <p>
     * Operator <strong>NOT BETWEEN [SYMMETRIC/ASYMMETRIC] AND</strong> . This method is similar to {@link #notBetween(SQLs.BetweenModifier, Expression, SQLs.WordAnd, Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     * </p>
     * <p>
     * <strong>Node</strong>: The first argument of funcRef always is <strong>this</strong>.
     * </p>
     *
     * @param modifier {@link SQLs#SYMMETRIC} or {@link SQLs#ASYMMETRIC}
     * @param funcRef  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                 <ul>
     *                     <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                     <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>{@link SQLs#encodingParam(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingLiteral(TypeInfer, Object)},used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true</li>
     *                     <li>{@link SQLs#encodingNamedParam(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete ) syntax</li>
     *                     <li>{@link SQLs#encodingNamedLiteral(TypeInfer, String)} ,used when only <strong>this</strong> is instance of {@link TableField} and {@link TableField#codec()} is true
     *                     and in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                     <li>developer custom method</li>
     *                 </ul>.
     *                 The first argument of funcRef always is <strong>this</strong>.
     * @param first    non-null,it will be passed to funcRef as the second argument of funcRef
     * @param and      {@link SQLs#AND}
     * @param second   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    @Support({PostgreSQL, H2})
    <T> IPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, BiFunction<Expression, T, Expression> funcRef, T first, SQLs.WordAnd and, T second);


    /**
     * <p>
     * Operator <strong>IS </strong> . This method is similar to {@link #notEqual(Expression)},except that the operand
     * {@link Expression} is returned by funcRef.
     * </p>
     * <p>
     * <strong>Node</strong>: The first argument of funcRef always is <strong>this</strong>.
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
     *                The first argument of funcRef always is <strong>this</strong>.
     * @param value   non-null,it will be passed to funcRef as the second argument of funcRef
     * @throws CriteriaException throw when operand isn't operable {@link Expression},for example {@link SQLs#DEFAULT},
     *                           {@link SQLs#multiParam(TypeInfer, Collection)}
     */
    <T> IPredicate is(SQLs.IsComparisonWord operator, BiFunction<Expression, T, Expression> funcRef, @Nullable T value);

    <T> IPredicate isNot(SQLs.IsComparisonWord operator, BiFunction<Expression, T, Expression> funcRef, @Nullable T value);

    <T extends Collection<?>> IPredicate in(BiFunction<Expression, T, Expression> funcRef, T value);

    <T extends Collection<?>> IPredicate notIn(BiFunction<Expression, T, Expression> funcRef, T value);

    IPredicate in(TeNamedOperator<Expression> funcRef, String paramName, int size);

    IPredicate notIn(TeNamedOperator<Expression> funcRef, String paramName, int size);


    <T> IPredicate like(BiFunction<MappingType, T, Expression> funcRef, T value);

    <T> IPredicate like(BiFunction<MappingType, T, Expression> funcRef, T value, SQLs.WordEscape escape, char escapeChar);

    <T> IPredicate notLike(BiFunction<MappingType, T, Expression> funcRef, T value);

    <T> IPredicate notLike(BiFunction<MappingType, T, Expression> funcRef, T value, SQLs.WordEscape escape, char escapeChar);

    <T> Expression mod(BiFunction<Expression, T, Expression> funcRef, T value);

    <T> Expression times(BiFunction<Expression, T, Expression> funcRef, T value);

    <T> Expression plus(BiFunction<Expression, T, Expression> funcRef, T value);

    <T> Expression minus(BiFunction<Expression, T, Expression> funcRef, T value);

    <T> Expression divide(BiFunction<Expression, T, Expression> funcRef, T value);

    <T> Expression bitwiseAnd(BiFunction<Expression, T, Expression> funcRef, T value);

    <T> Expression bitwiseOr(BiFunction<Expression, T, Expression> funcRef, T value);

    <T> Expression bitwiseXor(BiFunction<Expression, T, Expression> funcRef, T value);

    <T> Expression rightShift(BiFunction<Expression, T, Expression> funcRef, T value);

    <T> Expression leftShift(BiFunction<Expression, T, Expression> funcRef, T value);


    <T> Expression apply(BiFunction<Expression, Expression, Expression> operator, BiFunction<Expression, T, Expression> funcRef, T value);

    <M extends SQLWords, T> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, BiFunction<Expression, T, Expression> funcRef, T value, M modifier, Expression optionalExp);

    <M extends SQLWords, T> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, BiFunction<Expression, T, Expression> funcRef, T value, M modifier, char escapeChar);

    <T> IPredicate test(BiFunction<Expression, Expression, IPredicate> operator, BiFunction<Expression, T, Expression> funcRef, T value);

    <M extends SQLWords, T> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, BiFunction<MappingType, T, Expression> funcRef, T value, M modifier, Expression optionalExp);

    <M extends SQLWords, T> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, BiFunction<MappingType, T, Expression> funcRef, T value, M modifier, char escapeChar);


}
