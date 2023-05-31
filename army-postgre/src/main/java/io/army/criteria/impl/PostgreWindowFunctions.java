package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.Statement;
import io.army.criteria.TypeInfer;
import io.army.criteria.dialect.Window;
import io.army.criteria.postgre.PostgreWindow;
import io.army.criteria.standard.SQLFunction;
import io.army.mapping.*;
import io.army.mapping.optional.IntervalType;

import java.util.function.BiFunction;
import java.util.function.Consumer;


/**
 * <p>
 * Package class,This class hold window function and Aggregate function method.
 * </p>
 *
 * @see <a href="https://www.postgresql.org/docs/current/functions-window.html">Window Functions</a>
 * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html">Aggregate Functions</a>
 * @see <a href="https://www.postgresql.org/docs/current/functions-window.html">Window Functions</a>
 * @see <a href="https://www.postgresql.org/docs/current/tutorial-window.html">Window Functions tutorial</a>
 * @see <a href="https://www.postgresql.org/docs/current/sql-expressions.html#SYNTAX-WINDOW-FUNCTIONS">Window Function Calls</a>
 * @since 1.0
 */
abstract class PostgreWindowFunctions extends PostgreDocumentFunctions {

    PostgreWindowFunctions() {
    }


    public interface _OverSpec extends Window._OverWindowClause<PostgreWindow._PartitionBySpec> {


    }

    public interface _AggregateWindowFunc extends _OverSpec, SQLFunction.AggregateFunction,
            SQLFunction._OuterClauseBeforeOver, SimpleExpression {

        _OverSpec filter(Consumer<Statement._SimpleWhereClause> consumer);

        _OverSpec ifFilter(Consumer<Statement._SimpleWhereClause> consumer);


    }

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

    /*-------------------below Aggregate Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type: the array {@link  MappingType} of any
     * </p>
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">array_agg ( anynonarray ) → anyarray<br/>
     * Collects all the input values, including nulls, into an array.
     * </a>
     */
    public static _AggregateWindowFunc arrayAgg(Expression any) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("array_agg", any, _returnType(any, MappingType::arrayTypeOfThis));
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
    public static _AggregateWindowFunc avg(Expression exp) {
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
    public static _AggregateWindowFunc bitAnd(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("bit_and", exp, _returnType(exp, PostgreWindowFunctions::_bitOpeType));
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
    public static _AggregateWindowFunc bitOr(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("bit_or", exp, _returnType(exp, PostgreWindowFunctions::_bitOpeType));
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
    public static _AggregateWindowFunc bitXor(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("bit_xor", exp, _returnType(exp, PostgreWindowFunctions::_bitOpeType));
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
    public static _AggregateWindowFunc boolAnd(Expression exp) {
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
    public static _AggregateWindowFunc boolOr(Expression exp) {
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
     * @see SQLs#countAsterisk()
     */
    public static _AggregateWindowFunc countAsterisk() {
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
     * @see SQLs#count(Expression)
     */
    public static _AggregateWindowFunc count(Expression exp) {
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
    public static _AggregateWindowFunc every(Expression exp) {
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
    public static _AggregateWindowFunc jsonAgg(Expression exp) {
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
    public static _AggregateWindowFunc jsonbAgg(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("jsonb_agg", exp, JsonbType.TEXT);
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
    public static <K, V> _AggregateWindowFunc jsonObjectAgg(BiFunction<TextType, K, Expression> keyFunc, K key,
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
    public static _AggregateWindowFunc jsonObjectAgg(Expression key, Expression value) {
        return PostgreFunctionUtils.twoArgAggWindowFunc("json_object_agg", key, value, JsonType.TEXT);
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
    public static <K, V> _AggregateWindowFunc jsonbObjectAgg(BiFunction<TextType, K, Expression> keyFunc, K key,
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
    public static _AggregateWindowFunc jsonbObjectAgg(Expression key, Expression value) {
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
    public static _AggregateWindowFunc max(Expression exp) {
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
    public static _AggregateWindowFunc min(Expression exp) {
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
    public static _AggregateWindowFunc rangeAgg(Expression exp) {
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
    public static _AggregateWindowFunc rangeIntersectAgg(Expression exp) {
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
    public static _AggregateWindowFunc stringAgg(Expression value, Expression delimiter) {
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
    public static _AggregateWindowFunc sum(Expression exp) {
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
    public static _AggregateWindowFunc xmlAgg(Expression xml) {
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
    public static _AggregateWindowFunc corr(Expression y, Expression x) {
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
    public static _AggregateWindowFunc covarPop(Expression y, Expression x) {
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
    public static _AggregateWindowFunc covarSamp(Expression y, Expression x) {
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
    public static _AggregateWindowFunc regrAvgx(Expression y, Expression x) {
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
    public static _AggregateWindowFunc regrAvgy(Expression y, Expression x) {
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
    public static _AggregateWindowFunc regrCount(Expression y, Expression x) {
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
    public static _AggregateWindowFunc regrIntercept(Expression y, Expression x) {
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
    public static _AggregateWindowFunc regrR2(Expression y, Expression x) {
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
    public static _AggregateWindowFunc regrSlope(Expression y, Expression x) {
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
    public static _AggregateWindowFunc regrSxx(Expression y, Expression x) {
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
    public static _AggregateWindowFunc regrSxy(Expression y, Expression x) {
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
    public static _AggregateWindowFunc regrSyy(Expression y, Expression x) {
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
    public static _AggregateWindowFunc stdDev(Expression exp) {
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
    public static _AggregateWindowFunc stdDevPop(Expression exp) {
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
    public static _AggregateWindowFunc stdDevSamp(Expression exp) {
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
    public static _AggregateWindowFunc variance(Expression exp) {
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
    public static _AggregateWindowFunc varPop(Expression exp) {
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
    public static _AggregateWindowFunc varSamp(Expression exp) {
        return PostgreFunctionUtils.oneArgAggWindowFunc("var_samp", exp, DoubleType.INSTANCE);
    }


    /*-------------------below Ordered-Set Aggregate Functions -------------------*/




    /*-------------------below private method-------------------*/

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


}
