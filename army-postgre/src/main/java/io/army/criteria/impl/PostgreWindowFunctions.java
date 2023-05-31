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
