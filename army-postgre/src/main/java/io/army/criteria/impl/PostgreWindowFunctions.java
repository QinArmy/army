package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.postgre.PostgreWindow;
import io.army.criteria.standard.SQLFunction;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.array.DoubleArrayType;
import io.army.mapping.array.IntervalArrayType;
import io.army.mapping.optional.IntervalType;
import io.army.meta.TypeMeta;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;


/**
 * <p>
 * Package class,This class hold window function and Aggregate function method.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/tutorial-window.html">Window Functions tutorial</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Window Function synatx</a>
 * @see <a href="https://www.postgresql.org/docs/current/functions-window.html">Window Functions list</a>
 * @see <a href="https://www.postgresql.org/docs/current/queries-table-expressions.html#QUERIES-WINDOW">Window Function Processing</a>
 * @see <a href="https://www.postgresql.org/docs/current/tutorial-agg.html">Aggregate Functions tutorial</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-AGGREGATES">Aggregate function syntax</a>
 * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">General-Purpose Aggregate Functions list</a>
 * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">Aggregate Functions for Statistics list</a>
 * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-ORDEREDSET-TABLE">Ordered-Set Aggregate Functions list</a>
 * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-HYPOTHETICAL-TABLE">Hypothetical-Set Aggregate Functions list</a>
 * @since 1.0
 */
abstract class PostgreWindowFunctions extends PostgreDocumentFunctions {

    PostgreWindowFunctions() {
    }


    /**
     * <p>
     * This interface representing postgre over clause.
     * </p>
     */
    public interface _OverSpec extends Window._OverWindowClause<PostgreWindow._PartitionBySpec> {


    }

    /**
     * <p>
     * This interface representing postgre aggregate function over clause.
     * </p>
     */
    public interface _PgAggWindowFuncSpec extends _OverSpec,
            SQLFunction._OuterClauseBeforeOver,
            SQLFunction.AggregateFunction,
            SimpleExpression {

    }

    /**
     * <p>
     * This interface representing postgre aggregate function filter clause.
     * </p>
     */
    interface _PgAggFuncFilterClause<R extends Item> {

        R filter(Consumer<Statement._SimpleWhereClause> consumer);

        R ifFilter(Consumer<Statement._SimpleWhereClause> consumer);
    }

    /**
     * <p>
     * This interface representing postgre aggregate function.
     * </p>
     */
    public interface _PostgreAggregateFunction<R extends SimpleExpression> extends SimpleExpression,
            _PgAggFuncFilterClause<R>,
            SQLFunction.AggregateFunction {


    }

    /**
     * <p>
     * This interface is base interface of postgre window aggregate function.
     * </p>
     */
    public interface _AggWindowFunc extends _PostgreAggregateFunction<_PgAggWindowFuncSpec>, _PgAggWindowFuncSpec {

    }


    /**
     * <p>
     * This interface is base interface of postgre non-window aggregate function.
     * </p>
     */
    public interface _PgAggFunc extends _PostgreAggregateFunction<SimpleExpression> {

    }

    /**
     * <p>
     * This interface representing postgre  ordered-set aggregate function. This interface couldn't extends {@link  _PgAggFunc}
     * </p>
     */
    public interface _AggWithGroupClause {

        _PgAggFunc withinGroup(Consumer<Statement._SimpleOrderByClause> consumer);

    }

    /*-------------------below window function-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">row_number () → bigint<br/>
     * Returns the number of the current row within its partition, counting from 1.
     * </a>
     */
    public static _OverSpec rowNumber() {
        return PostgreFunctionUtils.zeroArgWindowFunc("row_number", LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">rank () → bigint<br/>
     * Returns the rank of the current row, with gaps; that is, the row_number of the first row in its peer group.
     * </a>
     */
    public static _OverSpec rank() {
        return PostgreFunctionUtils.zeroArgWindowFunc("rank", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">dense_rank () → bigint<br/>
     * Returns the rank of the current row, without gaps; this function effectively counts peer groups.
     * </a>
     */
    public static _OverSpec denseRank() {
        return PostgreFunctionUtils.zeroArgWindowFunc("dense_rank", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">percent_rank () → double precision<br/>
     * Returns the relative rank of the current row, that is (rank - 1) / (total partition rows - 1). The value thus ranges from 0 to 1 inclusive.
     * </a>
     */
    public static _OverSpec percentRank() {
        return PostgreFunctionUtils.zeroArgWindowFunc("percent_rank", DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  DoubleType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">cume_dist () → double precision<br/>
     * Returns the cumulative distribution, that is (number of partition rows preceding or peers with current row) / (total partition rows). The value thus ranges from 1/N to 1.
     * </a>
     */
    public static _OverSpec cumeDist() {
        return PostgreFunctionUtils.zeroArgWindowFunc("cume_dist", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType#INSTANCE}
     * </p>
     *
     * @param func  the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *              <ul>
     *                  <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                  <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                  <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                  <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                  <li>developer custom method</li>
     *              </ul>.
     *              The first argument of func always is {@link IntegerType#INSTANCE}.
     * @param value non-null,it will be passed to func as the second argument of func
     * @see #ntile(Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">ntile ( num_buckets integer ) → integer<br/>
     * Returns an integer ranging from 1 to the argument value, dividing the partition as equally as possible.
     * </a>
     */
    public static <T> _OverSpec ntile(BiFunction<IntegerType, T, Expression> func, T value) {
        return ntile(func.apply(IntegerType.INSTANCE, value));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">ntile ( num_buckets integer ) → integer<br/>
     * Returns an integer ranging from 1 to the argument value, dividing the partition as equally as possible.
     * </a>
     */
    public static _OverSpec ntile(Expression numBuckets) {
        return PostgreFunctionUtils.oneArgWindowFunc("ntile", numBuckets, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of value
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">lag ( value anycompatible [, offset integer [, default anycompatible ]] ) → anycompatible<br/>
     * Returns value evaluated at the row that is offset rows before the current row within the partition; if there is no such row,<br/>
     * instead returns default (which must be of a type compatible with value).<br/>
     * Both offset and default are evaluated with respect to the current row. If omitted, offset defaults to 1 and default to NULL.
     * </a>
     */
    public static _OverSpec lag(Expression value) {
        return PostgreFunctionUtils.oneArgWindowFunc("lag", value, _returnType(value, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of value
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">lag ( value anycompatible [, offset integer [, default anycompatible ]] ) → anycompatible<br/>
     * Returns value evaluated at the row that is offset rows before the current row within the partition; if there is no such row,<br/>
     * instead returns default (which must be of a type compatible with value).<br/>
     * Both offset and default are evaluated with respect to the current row. If omitted, offset defaults to 1 and default to NULL.
     * </a>
     */
    public static _OverSpec lag(Expression value, Expression offset) {
        return PostgreFunctionUtils.twoArgWindowFunc("lag", value, offset, _returnType(value, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of value
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">lag ( value anycompatible [, offset integer [, default anycompatible ]] ) → anycompatible<br/>
     * Returns value evaluated at the row that is offset rows before the current row within the partition; if there is no such row,<br/>
     * instead returns default (which must be of a type compatible with value).<br/>
     * Both offset and default are evaluated with respect to the current row. If omitted, offset defaults to 1 and default to NULL.
     * </a>
     */
    public static _OverSpec lag(Expression value, Expression offset, Expression defaultValue) {
        return PostgreFunctionUtils.threeArgWindowFunc("lag", value, offset, defaultValue,
                _returnType(value, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of value
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">lead ( value anycompatible [, offset integer [, default anycompatible ]] ) → anycompatible<br/>
     * Returns value evaluated at the row that is offset rows after the current row within the partition;<br/>
     * if there is no such row, instead returns default (which must be of a type compatible with value).<br/>
     * Both offset and default are evaluated with respect to the current row. If omitted, offset defaults to 1 and default to NULL.
     * </a>
     */
    public static _OverSpec lead(Expression value) {
        return PostgreFunctionUtils.oneArgWindowFunc("lead", value, _returnType(value, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of value
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">lead ( value anycompatible [, offset integer [, default anycompatible ]] ) → anycompatible<br/>
     * Returns value evaluated at the row that is offset rows after the current row within the partition;<br/>
     * if there is no such row, instead returns default (which must be of a type compatible with value).<br/>
     * Both offset and default are evaluated with respect to the current row. If omitted, offset defaults to 1 and default to NULL.
     * </a>
     */
    public static _OverSpec lead(Expression value, Expression offset) {
        return PostgreFunctionUtils.twoArgWindowFunc("lead", value, offset, _returnType(value, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of value
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">lead ( value anycompatible [, offset integer [, default anycompatible ]] ) → anycompatible<br/>
     * Returns value evaluated at the row that is offset rows after the current row within the partition;<br/>
     * if there is no such row, instead returns default (which must be of a type compatible with value).<br/>
     * Both offset and default are evaluated with respect to the current row. If omitted, offset defaults to 1 and default to NULL.
     * </a>
     */
    public static _OverSpec lead(Expression value, Expression offset, Expression defaultValue) {
        return PostgreFunctionUtils.threeArgWindowFunc("lead", value, offset, defaultValue,
                _returnType(value, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of value
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">first_value ( value anyelement ) → anyelement<br/>
     * Returns value evaluated at the row that is the first row of the window frame.
     * </a>
     */
    public static _OverSpec firstValue(Expression value) {
        return PostgreFunctionUtils.oneArgWindowFunc("first_value", value, _returnType(value, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of value
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">last_value ( value anyelement ) → anyelement<br/>
     * Returns value evaluated at the row that is the last row of the window frame.
     * </a>
     */
    public static _OverSpec lastValue(Expression value) {
        return PostgreFunctionUtils.oneArgWindowFunc("last_value", value, _returnType(value, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of value
     * </p>
     *
     * @param func the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *             <ul>
     *                 <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                 <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                 <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                 <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                 <li>developer custom method</li>
     *             </ul>.
     *             The first argument of func always is {@link IntegerType#INSTANCE}.
     * @param n    non-null,it will be passed to func as the second argument of func
     * @see #nthValue(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">nth_value ( value anyelement, n integer ) → anyelement<br/>
     * Returns value evaluated at the row that is the n'th row of the window frame (counting from 1); returns NULL if there is no such row.
     * </a>
     */
    public static <T> _OverSpec nthValue(Expression value, BiFunction<IntegerType, T, Expression> func, T n) {
        return nthValue(value, func.apply(IntegerType.INSTANCE, n));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of value
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">nth_value ( value anyelement, n integer ) → anyelement<br/>
     * Returns value evaluated at the row that is the n'th row of the window frame (counting from 1); returns NULL if there is no such row.
     * </a>
     */
    public static _OverSpec nthValue(Expression value, Expression n) {
        return PostgreFunctionUtils.twoArgWindowFunc("nth_value", value, n, _returnType(value, Expressions::identityType));
    }

    /*-------------------below general-purpose Aggregate Functions-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type: the array {@link  MappingType} of any
     * </p>
     *
     * @param modifier see {@link SQLs#DISTINCT} or {@link Postgres#DISTINCT}
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">array_agg ( anynonarray ) → anyarray<br/>
     * Collects all the input values, including nulls, into an array.
     * </a>
     */
    public static _PgAggFunc arrayAgg(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("array_agg", modifier, any, null, _returnType(any, MappingType::arrayTypeOfThis));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the array {@link  MappingType} of any
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">array_agg ( anynonarray ) → anyarray<br/>
     * Collects all the input values, including nulls, into an array.
     * </a>
     */
    public static _PgAggFunc arrayAgg(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("array_agg", null, any, consumer, _returnType(any, MappingType::arrayTypeOfThis));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the array {@link  MappingType} of any
     * </p>
     *
     * @param modifier see {@link SQLs#DISTINCT} or {@link Postgres#DISTINCT}
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">array_agg ( anynonarray ) → anyarray<br/>
     * Collects all the input values, including nulls, into an array.
     * </a>
     */
    public static _PgAggFunc arrayAgg(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                      Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("array_agg", modifier, any, consumer, _returnType(any, MappingType::arrayTypeOfThis));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the array {@link  MappingType} of any
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">array_agg ( anynonarray ) → anyarray<br/>
     * Collects all the input values, including nulls, into an array.
     * </a>
     */
    public static _AggWindowFunc arrayAgg(Expression any) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("array_agg", any,
                _returnType(any, MappingType::arrayTypeOfThis)
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → {@link BigDecimalType#INSTANCE}</li>
     * <li>exp is decimal type  → {@link BigDecimalType#INSTANCE}</li>
     * <li>exp is float type  → {@link DoubleType#INSTANCE}</li>
     * <li>exp is interval type  → {@link IntervalType#TEXT}</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">avg ( smallint ) → numeric<br/>
     * avg ( integer ) → numeric<br/>
     * avg ( bigint ) → numeric<br/>
     * avg ( numeric ) → numeric<br/>
     * avg ( real ) → double precision<br/>
     * avg ( double precision ) → double precision<br/>
     * avg ( interval ) → interval<br/>
     * </a>
     */
    public static _PgAggFunc avg(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("avg", modifier, any, null, _returnType(any, PostgreWindowFunctions::_avgType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → {@link BigDecimalType#INSTANCE}</li>
     * <li>exp is decimal type  → {@link BigDecimalType#INSTANCE}</li>
     * <li>exp is float type  → {@link DoubleType#INSTANCE}</li>
     * <li>exp is interval type  → {@link IntervalType#TEXT}</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">avg ( smallint ) → numeric<br/>
     * avg ( integer ) → numeric<br/>
     * avg ( bigint ) → numeric<br/>
     * avg ( numeric ) → numeric<br/>
     * avg ( real ) → double precision<br/>
     * avg ( double precision ) → double precision<br/>
     * avg ( interval ) → interval<br/>
     * </a>
     */
    public static _PgAggFunc avg(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("avg", null, any, consumer, _returnType(any, PostgreWindowFunctions::_avgType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → {@link BigDecimalType#INSTANCE}</li>
     * <li>exp is decimal type  → {@link BigDecimalType#INSTANCE}</li>
     * <li>exp is float type  → {@link DoubleType#INSTANCE}</li>
     * <li>exp is interval type  → {@link IntervalType#TEXT}</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">avg ( smallint ) → numeric<br/>
     * avg ( integer ) → numeric<br/>
     * avg ( bigint ) → numeric<br/>
     * avg ( numeric ) → numeric<br/>
     * avg ( real ) → double precision<br/>
     * avg ( double precision ) → double precision<br/>
     * avg ( interval ) → interval<br/>
     * </a>
     */
    public static _PgAggFunc avg(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                 Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("avg", modifier, any, consumer, _returnType(any, PostgreWindowFunctions::_avgType));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → {@link BigDecimalType#INSTANCE}</li>
     * <li>exp is decimal type  → {@link BigDecimalType#INSTANCE}</li>
     * <li>exp is float type  → {@link DoubleType#INSTANCE}</li>
     * <li>exp is interval type  → {@link IntervalType#TEXT}</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">avg ( smallint ) → numeric<br/>
     * avg ( integer ) → numeric<br/>
     * avg ( bigint ) → numeric<br/>
     * avg ( numeric ) → numeric<br/>
     * avg ( real ) → double precision<br/>
     * avg ( double precision ) → double precision<br/>
     * avg ( interval ) → interval<br/>
     * </a>
     */
    public static _AggWindowFunc avg(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("avg", exp, _returnType(exp, PostgreWindowFunctions::_avgType));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_and ( smallint ) → smallint<br/>
     * bit_and ( integer ) → integer<br/>
     * bit_and ( bigint ) → bigint<br/>
     * bit_and ( bit ) → bit<br/>
     * Computes the bitwise AND of all non-null input values.
     * </a>
     */
    public static _PgAggFunc bitAnd(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("bit_and", modifier, any, null, _returnType(any, PostgreWindowFunctions::_bitOpeType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_and ( smallint ) → smallint<br/>
     * bit_and ( integer ) → integer<br/>
     * bit_and ( bigint ) → bigint<br/>
     * bit_and ( bit ) → bit<br/>
     * Computes the bitwise AND of all non-null input values.
     * </a>
     */
    public static _PgAggFunc bitAnd(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("bit_and", null, any, consumer, _returnType(any, PostgreWindowFunctions::_bitOpeType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_and ( smallint ) → smallint<br/>
     * bit_and ( integer ) → integer<br/>
     * bit_and ( bigint ) → bigint<br/>
     * bit_and ( bit ) → bit<br/>
     * Computes the bitwise AND of all non-null input values.
     * </a>
     */
    public static _PgAggFunc bitAnd(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                    Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("bit_and", modifier, any, consumer, _returnType(any, PostgreWindowFunctions::_bitOpeType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_and ( smallint ) → smallint<br/>
     * bit_and ( integer ) → integer<br/>
     * bit_and ( bigint ) → bigint<br/>
     * bit_and ( bit ) → bit<br/>
     * Computes the bitwise AND of all non-null input values.
     * </a>
     */
    public static _AggWindowFunc bitAnd(Expression exp) {
        return _bitWindowFunc("bit_and", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_or ( smallint ) → smallint<br/>
     * bit_or ( integer ) → integer<br/>
     * bit_or ( bigint ) → bigint<br/>
     * bit_or ( bit ) → bit<br/>
     * Computes the bitwise OR of all non-null input values.
     * </a>
     */
    public static _PgAggFunc bitOr(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("bit_or", modifier, any, null, _returnType(any, PostgreWindowFunctions::_bitOpeType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_or ( smallint ) → smallint<br/>
     * bit_or ( integer ) → integer<br/>
     * bit_or ( bigint ) → bigint<br/>
     * bit_or ( bit ) → bit<br/>
     * Computes the bitwise OR of all non-null input values.
     * </a>
     */
    public static _PgAggFunc bitOr(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("bit_or", null, any, consumer, _returnType(any, PostgreWindowFunctions::_bitOpeType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_or ( smallint ) → smallint<br/>
     * bit_or ( integer ) → integer<br/>
     * bit_or ( bigint ) → bigint<br/>
     * bit_or ( bit ) → bit<br/>
     * Computes the bitwise OR of all non-null input values.
     * </a>
     */
    public static _PgAggFunc bitOr(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                   Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("bit_or", modifier, any, consumer, _returnType(any, PostgreWindowFunctions::_bitOpeType));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_or ( smallint ) → smallint<br/>
     * bit_or ( integer ) → integer<br/>
     * bit_or ( bigint ) → bigint<br/>
     * bit_or ( bit ) → bit<br/>
     * Computes the bitwise OR of all non-null input values.
     * </a>
     */
    public static _AggWindowFunc bitOr(Expression exp) {
        return _bitWindowFunc("bit_or", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_xor ( smallint ) → smallint<br/>
     * bit_xor ( integer ) → integer<br/>
     * bit_xor ( bigint ) → bigint<br/>
     * bit_xor ( bit ) → bit<br/>
     * Computes the bitwise OR of all non-null input values.
     * </a>
     */
    public static _PgAggFunc bitXor(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("bit_xor", modifier, any, null, _returnType(any, PostgreWindowFunctions::_bitOpeType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_xor ( smallint ) → smallint<br/>
     * bit_xor ( integer ) → integer<br/>
     * bit_xor ( bigint ) → bigint<br/>
     * bit_xor ( bit ) → bit<br/>
     * Computes the bitwise OR of all non-null input values.
     * </a>
     */
    public static _PgAggFunc bitXor(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("bit_xor", null, any, consumer, _returnType(any, PostgreWindowFunctions::_bitOpeType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_xor ( smallint ) → smallint<br/>
     * bit_xor ( integer ) → integer<br/>
     * bit_xor ( bigint ) → bigint<br/>
     * bit_xor ( bit ) → bit<br/>
     * Computes the bitwise OR of all non-null input values.
     * </a>
     */
    public static _PgAggFunc bitXor(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                    Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("bit_xor", modifier, any, consumer, _returnType(any, PostgreWindowFunctions::_bitOpeType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:<ul>
     * <li>exp is integer type  → the {@link MappingType} of exp</li>
     * <li>exp is bit type  → the {@link MappingType} of exp</li>
     * <li>else → {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bit_xor ( smallint ) → smallint<br/>
     * bit_xor ( integer ) → integer<br/>
     * bit_xor ( bigint ) → bigint<br/>
     * bit_xor ( bit ) → bit<br/>
     * Computes the bitwise OR of all non-null input values.
     * </a>
     */
    public static _AggWindowFunc bitXor(Expression exp) {
        return _bitWindowFunc("bit_xor", exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bool_and ( boolean ) → boolean<br/>
     * Returns true if all non-null input values are true, otherwise false.
     * </a>
     */
    public static _PgAggFunc boolAnd(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("bool_and", modifier, any, null, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bool_and ( boolean ) → boolean<br/>
     * Returns true if all non-null input values are true, otherwise false.
     * </a>
     */
    public static _PgAggFunc boolAnd(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("bool_and", null, any, consumer, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bool_and ( boolean ) → boolean<br/>
     * Returns true if all non-null input values are true, otherwise false.
     * </a>
     */
    public static _PgAggFunc boolAnd(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                     Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("bool_and", modifier, any, consumer, BooleanType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bool_and ( boolean ) → boolean<br/>
     * Returns true if all non-null input values are true, otherwise false.
     * </a>
     */
    public static _AggWindowFunc boolAnd(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("bool_and", exp, BooleanType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bool_or ( boolean ) → boolean<br/>
     * Returns true if any non-null input value is true, otherwise false.
     * </a>
     */
    public static _PgAggFunc boolOr(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("bool_or", modifier, any, null, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bool_or ( boolean ) → boolean<br/>
     * Returns true if any non-null input value is true, otherwise false.
     * </a>
     */
    public static _PgAggFunc boolOr(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("bool_or", null, any, consumer, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bool_or ( boolean ) → boolean<br/>
     * Returns true if any non-null input value is true, otherwise false.
     * </a>
     */
    public static _PgAggFunc boolOr(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                    Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("bool_or", modifier, any, consumer, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">bool_or ( boolean ) → boolean<br/>
     * Returns true if any non-null input value is true, otherwise false.
     * </a>
     */
    public static _AggWindowFunc boolOr(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("bool_or", exp, BooleanType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">count ( * ) → bigint<br/>
     * Computes the number of input rows.
     * </a>
     * @see StandardFunctions#countAsterisk()
     */
    public static _AggWindowFunc countAsterisk() {
        return PostgreFunctionUtils.oneArgAggWindowFunc("count", SQLs._ASTERISK_EXP, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">count ( "any" ) → bigint<br/>
     * Computes the number of input rows in which the input value is not null.
     * </a>
     * @see StandardFunctions#count(Expression)
     */
    public static _PgAggFunc count(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("count", modifier, any, null, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">count ( "any" ) → bigint<br/>
     * Computes the number of input rows in which the input value is not null.
     * </a>
     * @see StandardFunctions#count(Expression)
     */
    public static _PgAggFunc count(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("count", null, any, consumer, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">count ( "any" ) → bigint<br/>
     * Computes the number of input rows in which the input value is not null.
     * </a>
     * @see StandardFunctions#count(Expression)
     */
    public static _PgAggFunc count(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                   Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("count", modifier, any, consumer, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LongType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">count ( "any" ) → bigint<br/>
     * Computes the number of input rows in which the input value is not null.
     * </a>
     * @see StandardFunctions#count(Expression)
     */
    public static _AggWindowFunc count(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("count", exp, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">every ( boolean ) → boolean<br/>
     * This is the SQL standard's equivalent to bool_and.
     * </a>
     * @see #boolAnd(Expression)
     */
    public static _PgAggFunc every(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("every", modifier, any, null, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">every ( boolean ) → boolean<br/>
     * This is the SQL standard's equivalent to bool_and.
     * </a>
     * @see #boolAnd(Expression)
     */
    public static _PgAggFunc every(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("every", null, any, consumer, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">every ( boolean ) → boolean<br/>
     * This is the SQL standard's equivalent to bool_and.
     * </a>
     * @see #boolAnd(Expression)
     */
    public static _PgAggFunc every(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                   Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("every", modifier, any, consumer, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  BooleanType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">every ( boolean ) → boolean<br/>
     * This is the SQL standard's equivalent to bool_and.
     * </a>
     * @see #boolAnd(Expression)
     */
    public static _AggWindowFunc every(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("every", exp, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">json_agg ( anyelement ) → json<br/>
     * Collects all the input values, including nulls, into a JSON array. Values are converted to JSON as per to_json or to_jsonb.
     * </a>
     */
    public static _PgAggFunc jsonAgg(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("json_agg", modifier, any, null, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">json_agg ( anyelement ) → json<br/>
     * Collects all the input values, including nulls, into a JSON array. Values are converted to JSON as per to_json or to_jsonb.
     * </a>
     */
    public static _PgAggFunc jsonAgg(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("json_agg", null, any, consumer, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">json_agg ( anyelement ) → json<br/>
     * Collects all the input values, including nulls, into a JSON array. Values are converted to JSON as per to_json or to_jsonb.
     * </a>
     */
    public static _PgAggFunc jsonAgg(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                     Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("json_agg", modifier, any, consumer, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">json_agg ( anyelement ) → json<br/>
     * Collects all the input values, including nulls, into a JSON array. Values are converted to JSON as per to_json or to_jsonb.
     * </a>
     */
    public static _AggWindowFunc jsonAgg(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("json_agg", exp, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonbType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">jsonb_agg ( anyelement ) → jsonb_agg<br/>
     * Collects all the input values, including nulls, into a JSON array. Values are converted to JSON as per to_json or to_jsonb.
     * </a>
     */
    public static _PgAggFunc jsonbAgg(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("jsonb_agg", modifier, any, null, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonbType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">jsonb_agg ( anyelement ) → jsonb_agg<br/>
     * Collects all the input values, including nulls, into a JSON array. Values are converted to JSON as per to_json or to_jsonb.
     * </a>
     */
    public static _PgAggFunc jsonbAgg(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("jsonb_agg", null, any, consumer, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonbType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">jsonb_agg ( anyelement ) → jsonb_agg<br/>
     * Collects all the input values, including nulls, into a JSON array. Values are converted to JSON as per to_json or to_jsonb.
     * </a>
     */
    public static _PgAggFunc jsonbAgg(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                      Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("jsonb_agg", modifier, any, consumer, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonbType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">jsonb_agg ( anyelement ) → jsonb_agg<br/>
     * Collects all the input values, including nulls, into a JSON array. Values are converted to JSON as per to_json or to_jsonb.
     * </a>
     */
    public static _AggWindowFunc jsonbAgg(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("jsonb_agg", exp, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">json_object_agg ( key "any", value "any" ) → json<br/>
     * Collects all the key/value pairs into a JSON object. Key arguments are coerced to text; value arguments are converted as per to_json or to_jsonb. Values can be null, but not keys.
     * </a>
     */
    public static _PgAggFunc jsonObjectAgg(@Nullable SQLs.ArgDistinct modifier, Expression key, Expression value) {
        return PostgreFunctionUtils.twoArgAggFunc("json_object_agg", modifier, key, value, null, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">json_object_agg ( key "any", value "any" ) → json<br/>
     * Collects all the key/value pairs into a JSON object. Key arguments are coerced to text; value arguments are converted as per to_json or to_jsonb. Values can be null, but not keys.
     * </a>
     */
    public static _PgAggFunc jsonObjectAgg(Expression key, Expression value, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("json_object_agg", null, key, value, consumer, JsonType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">json_object_agg ( key "any", value "any" ) → json<br/>
     * Collects all the key/value pairs into a JSON object. Key arguments are coerced to text; value arguments are converted as per to_json or to_jsonb. Values can be null, but not keys.
     * </a>
     */
    public static _PgAggFunc jsonObjectAgg(@Nullable SQLs.ArgDistinct modifier, Expression key, Expression value,
                                           Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("json_object_agg", modifier, key, value, consumer, JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonType#TEXT}.
     * </p>
     *
     * @param keyFunc   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                      <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of keyFunc always is {@link TextType#INSTANCE}.
     * @param key       non-null,it will be passed to keyFunc as the second argument of keyFunc
     * @param valueFunc the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                      <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of valueFunc always is {@link TextType#INSTANCE}.
     * @param value     non-null,it will be passed to valueFunc as the second argument of valueFunc
     * @see #jsonObjectAgg(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">json_object_agg ( key "any", value "any" ) → json<br/>
     * Collects all the key/value pairs into a JSON object. Key arguments are coerced to text; value arguments are converted as per to_json or to_jsonb. Values can be null, but not keys.
     * </a>
     */
    public static <K, V> _AggWindowFunc jsonObjectAgg(BiFunction<TextType, K, Expression> keyFunc, K key,
                                                      BiFunction<TextType, V, Expression> valueFunc, V value) {
        return jsonObjectAgg(keyFunc.apply(TextType.INSTANCE, key), valueFunc.apply(TextType.INSTANCE, value));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">json_object_agg ( key "any", value "any" ) → json<br/>
     * Collects all the key/value pairs into a JSON object. Key arguments are coerced to text; value arguments are converted as per to_json or to_jsonb. Values can be null, but not keys.
     * </a>
     */
    public static _AggWindowFunc jsonObjectAgg(Expression key, Expression value) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("json_object_agg", key, value, JsonType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonbType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">jsonb_object_agg ( key "any", value "any" ) → json<br/>
     * Collects all the key/value pairs into a JSON object. Key arguments are coerced to text; value arguments are converted as per to_json or to_jsonb. Values can be null, but not keys.
     * </a>
     */
    public static _PgAggFunc jsonbObjectAgg(@Nullable SQLs.ArgDistinct modifier, Expression key, Expression value) {
        return PostgreFunctionUtils.twoArgAggFunc("jsonb_object_agg", modifier, key, value, null, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonbType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">jsonb_object_agg ( key "any", value "any" ) → json<br/>
     * Collects all the key/value pairs into a JSON object. Key arguments are coerced to text; value arguments are converted as per to_json or to_jsonb. Values can be null, but not keys.
     * </a>
     */
    public static _PgAggFunc jsonbObjectAgg(Expression key, Expression value, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("jsonb_object_agg", null, key, value, consumer, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonbType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">jsonb_object_agg ( key "any", value "any" ) → json<br/>
     * Collects all the key/value pairs into a JSON object. Key arguments are coerced to text; value arguments are converted as per to_json or to_jsonb. Values can be null, but not keys.
     * </a>
     */
    public static _PgAggFunc jsonbObjectAgg(@Nullable SQLs.ArgDistinct modifier, Expression key, Expression value,
                                            Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("jsonb_object_agg", modifier, key, value, consumer, JsonbType.TEXT);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonbType#TEXT}.
     * </p>
     *
     * @param keyFunc   the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                      <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of keyFunc always is {@link TextType#INSTANCE}.
     * @param key       non-null,it will be passed to keyFunc as the second argument of keyFunc
     * @param valueFunc the reference of method,Note: it's the reference of method,not lambda. Valid method:
     *                  <ul>
     *                      <li>{@link SQLs#param(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#literal(TypeInfer, Object)}</li>
     *                      <li>{@link SQLs#namedParam(TypeInfer, String)} ,used only in INSERT( or batch update/delete ) syntax</li>
     *                      <li>{@link SQLs#namedLiteral(TypeInfer, String)} ,used only in INSERT( or batch update/delete in multi-statement) syntax</li>
     *                      <li>developer custom method</li>
     *                  </ul>.
     *                  The first argument of valueFunc always is {@link TextType#INSTANCE}.
     * @param value     non-null,it will be passed to valueFunc as the second argument of valueFunc
     * @see #jsonbObjectAgg(Expression, Expression)
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">json_object_agg ( key "any", value "any" ) → json<br/>
     * Collects all the key/value pairs into a JSON object. Key arguments are coerced to text; value arguments are converted as per to_json or to_jsonb. Values can be null, but not keys.
     * </a>
     */
    public static <K, V> _AggWindowFunc jsonbObjectAgg(BiFunction<TextType, K, Expression> keyFunc, K key,
                                                       BiFunction<TextType, V, Expression> valueFunc, V value) {
        return jsonbObjectAgg(keyFunc.apply(TextType.INSTANCE, key), valueFunc.apply(TextType.INSTANCE, value));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  JsonbType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">jsonb_object_agg ( key "any", value "any" ) → json<br/>
     * Collects all the key/value pairs into a JSON object. Key arguments are coerced to text; value arguments are converted as per to_json or to_jsonb. Values can be null, but not keys.
     * </a>
     */
    public static _AggWindowFunc jsonbObjectAgg(Expression key, Expression value) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("jsonb_object_agg", key, value, JsonbType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">max ( see text ) → same as input type<br/>
     * Computes the maximum of the non-null input values. Available for any numeric, string, date/time, or enum type, as well as inet, interval, money, oid, pg_lsn, tid, xid8, and arrays of any of these types.
     * </a>
     */
    public static _PgAggFunc max(@Nullable SQLs.ArgDistinct modifier, Expression exp) {
        return PostgreFunctionUtils.oneArgAggFunc("max", modifier, exp, null, _returnType(exp, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">max ( see text ) → same as input type<br/>
     * Computes the maximum of the non-null input values. Available for any numeric, string, date/time, or enum type, as well as inet, interval, money, oid, pg_lsn, tid, xid8, and arrays of any of these types.
     * </a>
     */
    public static _AggWindowFunc max(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("max", exp, _returnType(exp, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">min ( see text ) → same as input type<br/>
     * Computes the minimum of the non-null input values. Available for any numeric, string, date/time, or enum type, as well as inet, interval, money, oid, pg_lsn, tid, xid8, and arrays of any of these types.
     * </a>
     */
    public static _PgAggFunc min(@Nullable SQLs.ArgDistinct modifier, Expression exp) {
        return PostgreFunctionUtils.oneArgAggFunc("min", modifier, exp, null, _returnType(exp, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">min ( see text ) → same as input type<br/>
     * Computes the minimum of the non-null input values. Available for any numeric, string, date/time, or enum type, as well as inet, interval, money, oid, pg_lsn, tid, xid8, and arrays of any of these types.
     * </a>
     */
    public static _AggWindowFunc min(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("min", exp, _returnType(exp, Expressions::identityType));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">range_agg ( value anyrange ) → anymultirange<br/>
     * range_agg ( value anymultirange ) → anymultirange
     * Computes the union of the non-null input values.
     * </a>
     */
    public static _PgAggFunc rangeAgg(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("range_agg", modifier, any, null,
                _returnType(any, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">range_agg ( value anyrange ) → anymultirange<br/>
     * range_agg ( value anymultirange ) → anymultirange
     * Computes the union of the non-null input values.
     * </a>
     */
    public static _PgAggFunc rangeAgg(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("range_agg", null, any, consumer,
                _returnType(any, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">range_agg ( value anyrange ) → anymultirange<br/>
     * range_agg ( value anymultirange ) → anymultirange
     * Computes the union of the non-null input values.
     * </a>
     */
    public static _PgAggFunc rangeAgg(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                      Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("range_agg", modifier, any, consumer,
                _returnType(any, Expressions::identityType)
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">range_agg ( value anyrange ) → anymultirange<br/>
     * range_agg ( value anymultirange ) → anymultirange
     * Computes the union of the non-null input values.
     * </a>
     */
    public static _AggWindowFunc rangeAgg(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("range_agg", exp, _returnType(exp, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">range_intersect_agg ( value anyrange ) → anymultirange<br/>
     * range_intersect_agg ( value anymultirange ) → anymultirange
     * Computes the intersection of the non-null input values.
     * </a>
     */
    public static _PgAggFunc rangeIntersectAgg(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("range_intersect_agg", modifier, any, null,
                _returnType(any, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">range_intersect_agg ( value anyrange ) → anymultirange<br/>
     * range_intersect_agg ( value anymultirange ) → anymultirange
     * Computes the intersection of the non-null input values.
     * </a>
     */
    public static _PgAggFunc rangeIntersectAgg(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("range_intersect_agg", null, any, consumer,
                _returnType(any, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">range_intersect_agg ( value anyrange ) → anymultirange<br/>
     * range_intersect_agg ( value anymultirange ) → anymultirange
     * Computes the intersection of the non-null input values.
     * </a>
     */
    public static _PgAggFunc rangeIntersectAgg(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                               Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("range_intersect_agg", modifier, any, consumer,
                _returnType(any, Expressions::identityType)
        );
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">range_intersect_agg ( value anyrange ) → anymultirange<br/>
     * range_intersect_agg ( value anymultirange ) → anymultirange
     * Computes the intersection of the non-null input values.
     * </a>
     */
    public static _AggWindowFunc rangeIntersectAgg(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("range_intersect_agg", exp, _returnType(exp, Expressions::identityType));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">string_agg ( value text, delimiter text ) → text<br/>
     * string_agg ( value bytea, delimiter bytea ) → bytea
     * Concatenates the non-null input values into a string. Each value after the first is preceded by the corresponding delimiter (if it's not null).
     * </a>
     */
    public static _PgAggFunc stringAgg(@Nullable SQLs.ArgDistinct modifier, Expression value, Expression delimiter) {
        return PostgreFunctionUtils.twoArgAggFunc("string_agg", modifier, value, delimiter, null,
                _returnType(value, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">string_agg ( value text, delimiter text ) → text<br/>
     * string_agg ( value bytea, delimiter bytea ) → bytea
     * Concatenates the non-null input values into a string. Each value after the first is preceded by the corresponding delimiter (if it's not null).
     * </a>
     */
    public static _PgAggFunc stringAgg(Expression value, Expression delimiter, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("string_agg", null, value, delimiter, consumer,
                _returnType(value, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">string_agg ( value text, delimiter text ) → text<br/>
     * string_agg ( value bytea, delimiter bytea ) → bytea
     * Concatenates the non-null input values into a string. Each value after the first is preceded by the corresponding delimiter (if it's not null).
     * </a>
     */
    public static _PgAggFunc stringAgg(@Nullable SQLs.ArgDistinct modifier, Expression value, Expression delimiter,
                                       Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("string_agg", modifier, value, delimiter, consumer,
                _returnType(value, Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of exp.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">string_agg ( value text, delimiter text ) → text<br/>
     * string_agg ( value bytea, delimiter bytea ) → bytea
     * Concatenates the non-null input values into a string. Each value after the first is preceded by the corresponding delimiter (if it's not null).
     * </a>
     */
    public static _AggWindowFunc stringAgg(Expression value, Expression delimiter) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("string_agg", value, delimiter, _returnType(value, Expressions::identityType));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If exp is {@link ByteType},then {@link ShortType}</li>
     *     <li>Else if exp is {@link ShortType},then {@link IntegerType}</li>
     *     <li>Else if exp is {@link MediumIntType},then {@link IntegerType}</li>
     *     <li>Else if exp is {@link LongType},then {@link BigIntegerType}</li>
     *     <li>Else if exp is {@link BigDecimalType},then {@link BigDecimalType}</li>
     *     <li>Else if exp is {@link FloatType},then {@link FloatType}</li>
     *     <li>Else if exp is sql float type,then {@link DoubleType}</li>
     *     <li>Else he {@link MappingType} of exp</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">sum ( smallint ) → bigint<br/>
     * sum ( integer ) → bigint<br/>
     * sum ( bigint ) → numeric<br/>
     * sum ( numeric ) → numeric<br/>
     * sum ( real ) → real<br/>
     * sum ( double precision ) → double precision<br/>
     * sum ( interval ) → interval<br/>
     * sum ( money ) → money<br/>
     * Computes the sum of the non-null input values.
     * </a>
     */
    public static _PgAggFunc sum(@Nullable SQLs.ArgDistinct modifier, Expression exp) {
        return PostgreFunctionUtils.oneArgAggFunc("sum", modifier, exp, null, _returnType(exp, Functions::_sumType));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:
     * <ul>
     *     <li>If exp is {@link ByteType},then {@link ShortType}</li>
     *     <li>Else if exp is {@link ShortType},then {@link IntegerType}</li>
     *     <li>Else if exp is {@link MediumIntType},then {@link IntegerType}</li>
     *     <li>Else if exp is {@link LongType},then {@link BigIntegerType}</li>
     *     <li>Else if exp is {@link BigDecimalType},then {@link BigDecimalType}</li>
     *     <li>Else if exp is {@link FloatType},then {@link FloatType}</li>
     *     <li>Else if exp is sql float type,then {@link DoubleType}</li>
     *     <li>Else he {@link MappingType} of exp</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">sum ( smallint ) → bigint<br/>
     * sum ( integer ) → bigint<br/>
     * sum ( bigint ) → numeric<br/>
     * sum ( numeric ) → numeric<br/>
     * sum ( real ) → real<br/>
     * sum ( double precision ) → double precision<br/>
     * sum ( interval ) → interval<br/>
     * sum ( money ) → money<br/>
     * Computes the sum of the non-null input values.
     * </a>
     */
    public static _AggWindowFunc sum(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("sum", exp, _returnType(exp, Functions::_sumType));
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link XmlType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">xmlagg ( xml ) → xml<br/>
     * Concatenates the non-null XML input values
     * </a>
     */
    public static _PgAggFunc xmlAgg(@Nullable SQLs.ArgDistinct modifier, Expression any) {
        return PostgreFunctionUtils.oneArgAggFunc("xmlagg", modifier, any, null, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link XmlType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">xmlagg ( xml ) → xml<br/>
     * Concatenates the non-null XML input values
     * </a>
     */
    public static _PgAggFunc xmlAgg(Expression any, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("xmlagg", null, any, consumer, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link XmlType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">xmlagg ( xml ) → xml<br/>
     * Concatenates the non-null XML input values
     * </a>
     */
    public static _PgAggFunc xmlAgg(@Nullable SQLs.ArgDistinct modifier, Expression any,
                                    Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("xmlagg", modifier, any, consumer, XmlType.TEXT);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link XmlType#TEXT}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">xmlagg ( xml ) → xml<br/>
     * Concatenates the non-null XML input values
     * </a>
     */
    public static _AggWindowFunc xmlAgg(Expression xml) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("xmlagg", xml, XmlType.TEXT);
    }

    /*-------------------below Aggregate Functions for Statistics-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">corr ( Y double precision, X double precision ) → double precision<br/>
     * Computes the correlation coefficient.
     * </a>
     */
    public static _PgAggFunc corr(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("corr", modifier, y, x, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">corr ( Y double precision, X double precision ) → double precision<br/>
     * Computes the correlation coefficient.
     * </a>
     */
    public static _PgAggFunc corr(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("corr", null, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">corr ( Y double precision, X double precision ) → double precision<br/>
     * Computes the correlation coefficient.
     * </a>
     */
    public static _PgAggFunc corr(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                  Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("corr", modifier, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">corr ( Y double precision, X double precision ) → double precision<br/>
     * Computes the correlation coefficient.
     * </a>
     */
    public static _AggWindowFunc corr(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("corr", y, x, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">covar_pop ( Y double precision, X double precision ) → double precision<br/>
     * Computes the population covariance.
     * </a>
     */
    public static _PgAggFunc covarPop(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("covar_pop", modifier, y, x, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">covar_pop ( Y double precision, X double precision ) → double precision<br/>
     * Computes the population covariance.
     * </a>
     */
    public static _PgAggFunc covarPop(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("covar_pop", null, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">covar_pop ( Y double precision, X double precision ) → double precision<br/>
     * Computes the population covariance.
     * </a>
     */
    public static _PgAggFunc covarPop(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                      Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("covar_pop", modifier, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">covar_pop ( Y double precision, X double precision ) → double precision<br/>
     * Computes the population covariance.
     * </a>
     */
    public static _AggWindowFunc covarPop(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("covar_pop", y, x, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">covar_samp ( Y double precision, X double precision ) → double precision<br/>
     * Computes the sample covariance.
     * </a>
     */
    public static _PgAggFunc covarSamp(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("covar_samp", modifier, y, x, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">covar_samp ( Y double precision, X double precision ) → double precision<br/>
     * Computes the sample covariance.
     * </a>
     */
    public static _PgAggFunc covarSamp(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("covar_samp", null, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">covar_samp ( Y double precision, X double precision ) → double precision<br/>
     * Computes the sample covariance.
     * </a>
     */
    public static _PgAggFunc covarSamp(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                       Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("covar_samp", modifier, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">covar_samp ( Y double precision, X double precision ) → double precision<br/>
     * Computes the sample covariance.
     * </a>
     */
    public static _AggWindowFunc covarSamp(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("covar_samp", y, x, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_avgx ( Y double precision, X double precision ) → double precision<br/>
     * Computes the average of the independent variable, sum(X)/N.
     * </a>
     */
    public static _PgAggFunc regrAvgx(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("regr_avgx", modifier, y, x, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_avgx ( Y double precision, X double precision ) → double precision<br/>
     * Computes the average of the independent variable, sum(X)/N.
     * </a>
     */
    public static _PgAggFunc regrAvgx(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_avgx", null, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_avgx ( Y double precision, X double precision ) → double precision<br/>
     * Computes the average of the independent variable, sum(X)/N.
     * </a>
     */
    public static _PgAggFunc regrAvgx(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                      Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_avgx", modifier, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_avgx ( Y double precision, X double precision ) → double precision<br/>
     * Computes the average of the independent variable, sum(X)/N.
     * </a>
     */
    public static _AggWindowFunc regrAvgx(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("regr_avgx", y, x, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_avgy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the average of the dependent variable, sum(Y)/N.
     * </a>
     */
    public static _PgAggFunc regrAvgy(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("regr_avgy", modifier, y, x, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_avgy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the average of the dependent variable, sum(Y)/N.
     * </a>
     */
    public static _PgAggFunc regrAvgy(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_avgy", null, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_avgy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the average of the dependent variable, sum(Y)/N.
     * </a>
     */
    public static _PgAggFunc regrAvgy(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                      Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_avgy", modifier, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_avgy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the average of the dependent variable, sum(Y)/N.
     * </a>
     */
    public static _AggWindowFunc regrAvgy(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("regr_avgy", y, x, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_count ( Y double precision, X double precision ) → bigint<br/>
     * Computes the number of rows in which both inputs are non-null.
     * </a>
     */
    public static _PgAggFunc regrCount(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("regr_count", modifier, y, x, null, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_count ( Y double precision, X double precision ) → bigint<br/>
     * Computes the number of rows in which both inputs are non-null.
     * </a>
     */
    public static _PgAggFunc regrCount(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_count", null, y, x, consumer, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_count ( Y double precision, X double precision ) → bigint<br/>
     * Computes the number of rows in which both inputs are non-null.
     * </a>
     */
    public static _PgAggFunc regrCount(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                       Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_count", modifier, y, x, consumer, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link LongType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_count ( Y double precision, X double precision ) → bigint<br/>
     * Computes the number of rows in which both inputs are non-null.
     * </a>
     */
    public static _AggWindowFunc regrCount(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("regr_count", y, x, LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_intercept ( Y double precision, X double precision ) → double precision<br/>
     * Computes the y-intercept of the least-squares-fit linear equation determined by the (X, Y) pairs.
     * </a>
     */
    public static _PgAggFunc regrIntercept(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("regr_intercept", modifier, y, x, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_intercept ( Y double precision, X double precision ) → double precision<br/>
     * Computes the y-intercept of the least-squares-fit linear equation determined by the (X, Y) pairs.
     * </a>
     */
    public static _PgAggFunc regrIntercept(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_intercept", null, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_intercept ( Y double precision, X double precision ) → double precision<br/>
     * Computes the y-intercept of the least-squares-fit linear equation determined by the (X, Y) pairs.
     * </a>
     */
    public static _PgAggFunc regrIntercept(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                           Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_intercept", modifier, y, x, consumer, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_intercept ( Y double precision, X double precision ) → double precision<br/>
     * Computes the y-intercept of the least-squares-fit linear equation determined by the (X, Y) pairs.
     * </a>
     */
    public static _AggWindowFunc regrIntercept(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("regr_intercept", y, x, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_r2 ( Y double precision, X double precision ) → double precision<br/>
     * Computes the square of the correlation coefficient.
     * </a>
     */
    public static _PgAggFunc regrR2(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("regr_r2", modifier, y, x, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_r2 ( Y double precision, X double precision ) → double precision<br/>
     * Computes the square of the correlation coefficient.
     * </a>
     */
    public static _PgAggFunc regrR2(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_r2", null, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_r2 ( Y double precision, X double precision ) → double precision<br/>
     * Computes the square of the correlation coefficient.
     * </a>
     */
    public static _PgAggFunc regrR2(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                    Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_r2", modifier, y, x, consumer, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_r2 ( Y double precision, X double precision ) → double precision<br/>
     * Computes the square of the correlation coefficient.
     * </a>
     */
    public static _AggWindowFunc regrR2(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("regr_r2", y, x, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_slope ( Y double precision, X double precision ) → double precision<br/>
     * Computes the slope of the least-squares-fit linear equation determined by the (X, Y) pairs.
     * </a>
     */
    public static _PgAggFunc regrSlope(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("regr_slope", modifier, y, x, null, DoubleType.INSTANCE);
    }

    /**
     * /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_slope ( Y double precision, X double precision ) → double precision<br/>
     * Computes the slope of the least-squares-fit linear equation determined by the (X, Y) pairs.
     * </a>
     */
    public static _PgAggFunc regrSlope(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_slope", null, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_slope ( Y double precision, X double precision ) → double precision<br/>
     * Computes the slope of the least-squares-fit linear equation determined by the (X, Y) pairs.
     * </a>
     */
    public static _PgAggFunc regrSlope(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                       Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_slope", modifier, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_slope ( Y double precision, X double precision ) → double precision<br/>
     * Computes the slope of the least-squares-fit linear equation determined by the (X, Y) pairs.
     * </a>
     */
    public static _AggWindowFunc regrSlope(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("regr_slope", y, x, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_sxx ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of squares” of the independent variable, sum(X^2) - sum(X)^2/N.
     * </a>
     */
    public static _PgAggFunc regrSxx(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("regr_sxx", modifier, y, x, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_sxx ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of squares” of the independent variable, sum(X^2) - sum(X)^2/N.
     * </a>
     */
    public static _PgAggFunc regrSxx(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_sxx", null, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_sxx ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of squares” of the independent variable, sum(X^2) - sum(X)^2/N.
     * </a>
     */
    public static _PgAggFunc regrSxx(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                     Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_sxx", modifier, y, x, consumer, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_sxx ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of squares” of the independent variable, sum(X^2) - sum(X)^2/N.
     * </a>
     */
    public static _AggWindowFunc regrSxx(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("regr_sxx", y, x, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_sxy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of products” of independent times dependent variables, sum(X*Y) - sum(X) * sum(Y)/N.
     * </a>
     */
    public static _PgAggFunc regrSxy(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("regr_sxy", modifier, y, x, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_sxy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of products” of independent times dependent variables, sum(X*Y) - sum(X) * sum(Y)/N.
     * </a>
     */
    public static _PgAggFunc regrSxy(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_sxy", null, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_sxy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of products” of independent times dependent variables, sum(X*Y) - sum(X) * sum(Y)/N.
     * </a>
     */
    public static _PgAggFunc regrSxy(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                     Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_sxy", modifier, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_sxy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of products” of independent times dependent variables, sum(X*Y) - sum(X) * sum(Y)/N.
     * </a>
     */
    public static _AggWindowFunc regrSxy(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("regr_sxy", y, x, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_syy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of squares” of the dependent variable, sum(Y^2) - sum(Y)^2/N.
     * </a>
     */
    public static _PgAggFunc regrSyy(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggFunc("regr_syy", modifier, y, x, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_syy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of squares” of the dependent variable, sum(Y^2) - sum(Y)^2/N.
     * </a>
     */
    public static _PgAggFunc regrSyy(Expression y, Expression x, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_syy", null, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_syy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of squares” of the dependent variable, sum(Y^2) - sum(Y)^2/N.
     * </a>
     */
    public static _PgAggFunc regrSyy(@Nullable SQLs.ArgDistinct modifier, Expression y, Expression x,
                                     Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.twoArgAggFunc("regr_syy", modifier, y, x, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">regr_syy ( Y double precision, X double precision ) → double precision<br/>
     * Computes the “sum of squares” of the dependent variable, sum(Y^2) - sum(Y)^2/N.
     * </a>
     */
    public static _AggWindowFunc regrSyy(Expression y, Expression x) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("regr_syy", y, x, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * This is a historical alias for stddev_samp.
     * </a>
     */
    public static _PgAggFunc stdDev(@Nullable SQLs.ArgDistinct modifier, Expression exp) {
        return PostgreFunctionUtils.oneArgAggFunc("stddev", modifier, exp, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * This is a historical alias for stddev_samp.
     * </a>
     */
    public static _PgAggFunc stdDev(Expression exp, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("stddev", null, exp, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * This is a historical alias for stddev_samp.
     * </a>
     */
    public static _PgAggFunc stdDev(@Nullable SQLs.ArgDistinct modifier, Expression exp,
                                    Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("stddev", modifier, exp, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * This is a historical alias for stddev_samp.
     * </a>
     */
    public static _AggWindowFunc stdDev(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("stddev", exp, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev_pop ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the population standard deviation of the input values.
     * </a>
     */
    public static _PgAggFunc stdDevPop(@Nullable SQLs.ArgDistinct modifier, Expression exp) {
        return PostgreFunctionUtils.oneArgAggFunc("stddev_pop", modifier, exp, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev_pop ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the population standard deviation of the input values.
     * </a>
     */
    public static _PgAggFunc stdDevPop(Expression exp, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("stddev_pop", null, exp, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev_pop ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the population standard deviation of the input values.
     * </a>
     */
    public static _PgAggFunc stdDevPop(@Nullable SQLs.ArgDistinct modifier, Expression exp,
                                       Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("stddev_pop", modifier, exp, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev_pop ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the population standard deviation of the input values.
     * </a>
     */
    public static _AggWindowFunc stdDevPop(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("stddev_pop", exp, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev_samp ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the sample standard deviation of the input values.
     * </a>
     */
    public static _PgAggFunc stdDevSamp(@Nullable SQLs.ArgDistinct modifier, Expression exp) {
        return PostgreFunctionUtils.oneArgAggFunc("stddev_samp", modifier, exp, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev_samp ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the sample standard deviation of the input values.
     * </a>
     */
    public static _PgAggFunc stdDevSamp(Expression exp, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("stddev_samp", null, exp, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev_samp ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the sample standard deviation of the input values.
     * </a>
     */
    public static _PgAggFunc stdDevSamp(@Nullable SQLs.ArgDistinct modifier, Expression exp,
                                        Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("stddev_samp", modifier, exp, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">stddev_samp ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the sample standard deviation of the input values.
     * </a>
     */
    public static _AggWindowFunc stdDevSamp(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("stddev_samp", exp, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">variance ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * This is a historical alias for var_samp.
     * </a>
     */
    public static _PgAggFunc variance(@Nullable SQLs.ArgDistinct modifier, Expression exp) {
        return PostgreFunctionUtils.oneArgAggFunc("variance", modifier, exp, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">variance ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * This is a historical alias for var_samp.
     * </a>
     */
    public static _PgAggFunc variance(Expression exp, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("variance", null, exp, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">variance ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * This is a historical alias for var_samp.
     * </a>
     */
    public static _PgAggFunc variance(@Nullable SQLs.ArgDistinct modifier, Expression exp,
                                      Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("variance", modifier, exp, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">variance ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * This is a historical alias for var_samp.
     * </a>
     */
    public static _AggWindowFunc variance(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("variance", exp, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">var_pop ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the population variance of the input values (square of the population standard deviation).
     * </a>
     */
    public static _PgAggFunc varPop(@Nullable SQLs.ArgDistinct modifier, Expression exp) {
        return PostgreFunctionUtils.oneArgAggFunc("var_pop", modifier, exp, null, DoubleType.INSTANCE);
    }

    /**
     * /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">var_pop ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the population variance of the input values (square of the population standard deviation).
     * </a>
     */
    public static _PgAggFunc varPop(Expression exp, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("var_pop", null, exp, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">var_pop ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the population variance of the input values (square of the population standard deviation).
     * </a>
     */
    public static _PgAggFunc varPop(@Nullable SQLs.ArgDistinct modifier, Expression exp,
                                    Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("var_pop", modifier, exp, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">var_pop ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the population variance of the input values (square of the population standard deviation).
     * </a>
     */
    public static _AggWindowFunc varPop(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("var_pop", exp, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">var_samp ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the sample variance of the input values (square of the sample standard deviation).
     * </a>
     */
    public static _PgAggFunc varSamp(@Nullable SQLs.ArgDistinct modifier, Expression exp) {
        return PostgreFunctionUtils.oneArgAggFunc("var_samp", modifier, exp, null, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">var_samp ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the sample variance of the input values (square of the sample standard deviation).
     * </a>
     */
    public static _PgAggFunc varSamp(Expression exp, Consumer<Statement._SimpleOrderByClause> consumer) {

        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("var_samp", null, exp, consumer, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">var_samp ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the sample variance of the input values (square of the sample standard deviation).
     * </a>
     */
    public static _PgAggFunc varSamp(@Nullable SQLs.ArgDistinct modifier, Expression exp,
                                     Consumer<Statement._SimpleOrderByClause> consumer) {
        ContextStack.assertNonNull(consumer);
        return PostgreFunctionUtils.oneArgAggFunc("var_samp", modifier, exp, consumer, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link DoubleType#INSTANCE}.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-STATISTICS-TABLE">var_samp ( numeric_type ) → double precision for real or double precision, otherwise numeric<br/>
     * Computes the sample variance of the input values (square of the sample standard deviation).
     * </a>
     */
    public static _AggWindowFunc varSamp(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("var_samp", exp, DoubleType.INSTANCE);
    }




    /*-------------------below Ordered-Set Aggregate Functions -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  the {@link MappingType} of order by clause first item.
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-ORDEREDSET-TABLE">mode () WITHIN GROUP ( ORDER BY anyelement ) → anyelement<br/>
     * Computes the mode, the most frequent value of the aggregated argument (arbitrarily choosing the first one if there are multiple equally-frequent values). The aggregated argument must be of a sortable type.
     * </a>
     */
    public static _AggWithGroupClause mode() {
        return PostgreFunctionUtils.zeroArgWithGroupAggFunc("mode",
                PostgreFunctionUtils.unaryOrderSetType(Expressions::identityType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: <ul>
     * <li>If fraction is double array type and order by clause first item is sql double type type,then {@link DoubleType#INSTANCE}</li>
     * <li>If fraction is sql double array type and order by clause first item is sql interval type,then {@link IntervalType#TEXT}</li>
     * <li>If fraction is sql double type and order by clause first item is sql double type type,then {@link DoubleArrayType#LINEAR}</li>
     * <li>If fraction is sql double type and order by clause first item is sql interval type,then {@link IntervalArrayType#LINEAR}</li>
     * <li>Else {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-ORDEREDSET-TABLE">percentile_cont ( fraction double precision ) WITHIN GROUP ( ORDER BY double precision ) → double precision<br/>
     * </a>
     */
    public static _AggWithGroupClause percentileCont(final Expression fraction) {
        return PostgreFunctionUtils.oneArgWithGroupAggFunc("percentile_cont", fraction,
                PostgreFunctionUtils.biOrderedSetType(fraction, PostgreWindowFunctions::_percentileType)
        );
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: <ul>
     * <li>If fraction is double array type ,then it is the {@link MappingType} of order by clause first item</li>
     * <li>If fraction is double array type ,then it is the array {@link MappingType} of order by clause first item</li>
     * <li>Else {@link TextType#INSTANCE}</li>
     * </ul>
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-ORDEREDSET-TABLE">percentile_disc ( fraction double precision ) WITHIN GROUP ( ORDER BY anyelement ) → anyelement<br/>
     * percentile_disc ( fractions double precision[] ) WITHIN GROUP ( ORDER BY anyelement ) → anyarray
     * </a>
     */
    public static _AggWithGroupClause percentileDisc(final Expression fraction) {
        return PostgreFunctionUtils.oneArgWithGroupAggFunc("percentile_disc", fraction,
                PostgreFunctionUtils.biOrderedSetType(fraction, PostgreWindowFunctions::_percentileType)
        );
    }


    /*-------------------below Hypothetical-Set Aggregate Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-HYPOTHETICAL-TABLE">rank ( args ) WITHIN GROUP ( ORDER BY sorted_args ) → bigint<br/>
     * </a>
     */
    public static _AggWithGroupClause rank(Expression args) {
        return PostgreFunctionUtils.oneArgWithGroupAggFunc("rank", args, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-HYPOTHETICAL-TABLE">dense_rank ( args ) WITHIN GROUP ( ORDER BY sorted_args ) → bigint<br/>
     * </a>
     */
    public static _AggWithGroupClause denseRank(Expression args) {
        return PostgreFunctionUtils.oneArgWithGroupAggFunc("dense_rank", args, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-HYPOTHETICAL-TABLE">percent_rank ( args ) WITHIN GROUP ( ORDER BY sorted_args ) → double precision<br/>
     * </a>
     */
    public static _AggWithGroupClause percentRank(Expression args) {
        return PostgreFunctionUtils.oneArgWithGroupAggFunc("percent_rank", args, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType#INSTANCE}
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-HYPOTHETICAL-TABLE">cume_dist ( args ) WITHIN GROUP ( ORDER BY sorted_args ) → double precision<br/>
     * </a>
     */
    public static _AggWithGroupClause cumeDist(Expression args) {
        return PostgreFunctionUtils.oneArgWithGroupAggFunc("cume_dist", args, DoubleType.INSTANCE);
    }

    /*-------------------below user-defined WITH GROUP aggregate function-------------------*/

    /**
     * user-defined WITH GROUP aggregate function
     */
    public static _AggWithGroupClause myWithGroupAggFunc(String name, TypeMeta returnType) {
        return PostgreFunctionUtils.zeroArgMyWithGroupAggFunc(name, returnType);
    }


    /**
     * user-defined WITH GROUP aggregate function
     */
    public static _AggWithGroupClause myWithGroupAggFunc(String name, Expression one, TypeMeta returnType) {
        return PostgreFunctionUtils.oneArgMyWithGroupAggFunc(name, one, returnType);
    }

    /**
     * user-defined WITH GROUP aggregate function
     */
    public static _AggWithGroupClause myWithGroupAggFunc(String name, List<Expression> argList, TypeMeta returnType) {
        return PostgreFunctionUtils.multiArgMyWithGroupAggFunc(name, FunctionUtils.expList(name, argList), returnType);
    }


    /*-------------------below private method-------------------*/


    /**
     * @see #bitAnd(Expression)
     * @see #bitOr(Expression)
     * @see #bitXor(Expression)
     */
    private static _AggWindowFunc _bitWindowFunc(String name, Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc(name, exp,
                _returnType(exp, PostgreWindowFunctions::_bitOpeType)
        );
    }


    /**
     * @see #avg(Expression)
     */
    private static MappingType _avgType(final MappingType type) {
        final MappingType returnType;
        if (type instanceof MappingType.SqlIntegerType || type instanceof MappingType.SqlDecimalType) {
            returnType = BigDecimalType.INSTANCE;
        } else if (type instanceof MappingType.SqlFloatType) {
            returnType = DoubleType.INSTANCE;
        } else if (type instanceof MappingType.SqlIntervalType) {
            returnType = IntervalType.TEXT;
        } else {
            returnType = TextType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see #bitAnd(Expression)
     * @see #bitOr(Expression)
     */
    private static MappingType _bitOpeType(final MappingType type) {
        final MappingType returnType;
        if (type instanceof MappingType.SqlIntegerType || type instanceof MappingType.SqlBitType) {
            returnType = type;
        } else {
            returnType = TextType.INSTANCE;
        }
        return returnType;
    }

    /**
     * @see #percentileCont(Expression)
     */
    private static MappingType _percentileType(final MappingType fractionType, final MappingType type) {
        final MappingType returnType;
        if (fractionType instanceof MappingType.SqlArrayType) {
            returnType = type.arrayTypeOfThis();
        } else if (type instanceof MappingType.SqlFloatType) {
            returnType = type;
        } else if (type instanceof MappingType.SqlIntervalType) {
            returnType = type;
        } else {
            returnType = TextType.INSTANCE;
        }
        return returnType;
    }


}
