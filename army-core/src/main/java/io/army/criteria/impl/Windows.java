package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.Support;
import io.army.criteria.dialect.Window;
import io.army.criteria.standard.SQLFunction;
import io.army.mapping.*;

import java.util.Arrays;

import static io.army.dialect.Database.H2;

public abstract class Windows {

    private Windows() {
        throw new UnsupportedOperationException();
    }

    public interface _OverSpec extends Window._OverWindowClause<Window._StandardPartitionBySpec> {


    }

    public interface _AggWindowFunc extends _OverSpec, SQLFunction.AggregateFunction, SimpleExpression {

    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  DoubleType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_cume-dist">CUME_DIST() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">cume_dist () → double precision<br/>
     * Returns the cumulative distribution, that is (number of partition rows preceding or peers with current row) / (total partition rows). The value thus ranges from 1/N to 1.
     * </a>
     */
    public static _OverSpec cumeDist() {
        return WindowFunctions.zeroArgWindowFunc("CUME_DIST", DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  LongType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">row_number () → bigint<br/>
     * Returns the number of the current row within its partition, counting from 1.
     * </a>
     */
    public static _OverSpec rowNumber() {
        return WindowFunctions.zeroArgWindowFunc("ROW_NUMBER", LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  LongType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">dense_rank () → bigint<br/>
     * Returns the rank of the current row, without gaps; this function effectively counts peer groups.
     * </a>
     */
    public static _OverSpec denseRank() {
        return WindowFunctions.zeroArgWindowFunc("DENSE_RANK", LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  MappingType} or exp.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_first-value">FIRST_VALUE() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">first_value () → anyelement</a>
     */
    public static _OverSpec firstValue(Expression exp) {
        return WindowFunctions.oneArgWindowFunc("FIRST_VALUE", exp, exp.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_last-value">LAST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static _OverSpec lastValue(Expression expr) {
        return WindowFunctions.zeroArgWindowFunc("LAST_VALUE", expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(Expression expr) {
        return WindowFunctions.oneArgWindowFunc("LAG", expr, expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null parameter or {@link  Expression}
     * @param n    nullable,probably is below:
     *             <ul>
     *                 <li>null</li>
     *                 <li>{@link Long} type</li>
     *                 <li>{@link Integer} type</li>
     *                 <li>{@link SQLs#paramValue(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                 <li>{@link SQLs#literalValue(Object) },argument type is {@link Long} or {@link Integer}</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(Expression expr, Expression n) {
        return WindowFunctions.twoArgWindowFunc("LAG", expr, n, expr.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr         non-null parameter or {@link  Expression},but couldn't be {@link SQLs#NULL}
     * @param n            nullable,probably is below:
     *                     <ul>
     *                         <li>null</li>
     *                         <li>{@link Long} type</li>
     *                         <li>{@link Integer} type</li>
     *                         <li>{@link SQLs#paramValue(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                         <li>{@link SQLs#literalValue(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                     </ul>
     * @param defaultValue non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(Expression expr, Expression n, Expression defaultValue) {
        return WindowFunctions.compositeWindowFunc("LAG", Arrays.asList(expr, SQLs.COMMA, n, SQLs.COMMA, defaultValue), expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(Expression expr) {
        return WindowFunctions.oneArgWindowFunc("LEAD", expr, expr.typeMeta());
    }


    /**
     * <p>The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#NULL}
     * @param n    nullable,probably is below:
     *             <ul>
     *                 <li>null</li>
     *                 <li>{@link Long} type</li>
     *                 <li>{@link Integer} type</li>
     *                 <li>{@link SQLs#paramValue(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                 <li>{@link SQLs#literalValue(Object) },argument type is {@link Long} or {@link Integer}</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(Expression expr, Expression n) {
        return WindowFunctions.twoArgWindowFunc("LEAD", expr, n, expr.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr         non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#NULL}
     * @param n            nullable,probably is below:
     *                     <ul>
     *                         <li>null</li>
     *                         <li>{@link Long} type</li>
     *                         <li>{@link Integer} type</li>
     *                         <li>{@link SQLs#paramValue(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                         <li>{@link SQLs#literalValue(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                     </ul>
     * @param defaultValue non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(Expression expr, Expression n, Expression defaultValue) {
        return WindowFunctions.compositeWindowFunc("LEAD", Arrays.asList(expr, SQLs.COMMA, n, SQLs.COMMA, defaultValue), expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null {@link  Expression}
     * @param n    positive.output literal.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_nth-value">NTH_VALUE(expr, N) [from_first_last] [null_treatment] over_clause</a>
     */
    public static _OverSpec nthValue(Expression expr, Expression n) {
        return WindowFunctions.twoArgWindowFunc("NTH_VALUE", expr, n, expr.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link LongType}
     *
     * @param n positive number or {@link  Expression}.in any of the following forms:
     *          <ul>
     *               <li>positive number:
     *                      <ul>
     *                           <li>{@link  Long}</li>
     *                           <li>{@link  Integer}</li>
     *                           <li>{@link  Short}</li>
     *                           <li>{@link  Byte}</li>
     *                      </ul>
     *               </li>
     *               <li>positive number parameter {@link  Expression},eg:{@link SQLs#paramValue(Object)}</li>
     *               <li>positive number literal {@link  Expression},eg:{@link SQLs#literalValue(Object)}</li>
     *               <li>variable {@link  Expression}</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_ntile">NTILE(N) over_clause</a>
     */
    public static _OverSpec ntile(Expression n) {
        return WindowFunctions.oneArgWindowFunc("NTILE", n, LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">PERCENT_RANK() over_clause</a>
     */
    public static _OverSpec percentRank() {
        return WindowFunctions.zeroArgWindowFunc("PERCENT_RANK", DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link LongType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">RANK() over_clause</a>
     */
    public static _OverSpec rank() {
        return WindowFunctions.zeroArgWindowFunc("RANK", LongType.INSTANCE);
    }



    /*-------------------below Aggregate Function-------------------*/

    /**
     * <p>The {@link MappingType} of function return type: {@link  LongType}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggWindowFunc countAsterisk() {
        return count(SQLs._ASTERISK_EXP);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  LongType}
     *
     * @param expr non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggWindowFunc count(Expression expr) {
        return WindowFunctions.oneArgWindowAggFunc("COUNT", expr, LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  LongType}
     *
     * @param expr non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggWindowFunc count(SQLs.ArgDistinct distinct, Expression expr) {
        FuncExpUtils.assertDistinct(distinct, SQLs.DISTINCT);
        return WindowFunctions.compositeWindowAggFunc("COUNT", Arrays.asList(distinct, expr), LongType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link  MappingType} of exp
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN(expr) [over_clause]</a>
     */
    public static _AggWindowFunc min(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("MIN", exp, exp.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  MappingType} of exp
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX(expr) [over_clause]</a>
     */
    public static _AggWindowFunc max(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("MAX", exp, exp.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If exp is following types :
     *          <ul>
     *              <li>tiny int</li>
     *              <li>small int</li>
     *              <li>medium int</li>
     *          </ul>
     *     ,then {@link IntegerType}
     *     </li>
     *     <li>Else if exp is int,then {@link LongType}</li>
     *     <li>Else if exp is bigint,then {@link BigIntegerType}</li>
     *     <li>Else if exp is decimal,then {@link BigDecimalType}</li>
     *     <li>Else if exp is float type ,then {@link DoubleType}</li>
     *     <li>Else he {@link MappingType} of exp</li>
     * </ul>
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">MySQL SUM([DISTINCT] expr)</a>
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#sum">H2 SUM([DISTINCT] expr)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">Postgre SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggWindowFunc sum(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("SUM", exp, Functions._returnType(exp, Functions::_sumType));
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If exp is following types :
     *          <ul>
     *              <li>tiny int</li>
     *              <li>small int</li>
     *              <li>medium int</li>
     *          </ul>
     *     ,then {@link IntegerType}
     *     </li>
     *     <li>Else if exp is int,then {@link LongType}</li>
     *     <li>Else if exp is bigint,then {@link BigIntegerType}</li>
     *     <li>Else if exp is decimal,then {@link BigDecimalType}</li>
     *     <li>Else if exp is float type ,then {@link DoubleType}</li>
     *     <li>Else he {@link MappingType} of exp</li>
     * </ul>
     *
     * @param distinct see {@link SQLs#DISTINCT}
     * @param exp      non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">MySQL SUM([DISTINCT] expr) [over_clause]</a>
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#sum">H2 SUM([DISTINCT] expr)</a>
     */
    public static _AggWindowFunc sum(SQLs.ArgDistinct distinct, Expression exp) {
        FuncExpUtils.assertDistinct(distinct, SQLs.DISTINCT);
        return WindowFunctions.compositeWindowAggFunc("SUM", Arrays.asList(distinct, exp), Functions._returnType(exp, Functions::_sumType));
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param expr non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggWindowFunc avg(Expression expr) {
        return WindowFunctions.oneArgWindowAggFunc("AVG", expr, DoubleType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param distinct see {@link SQLs#DISTINCT}
     * @param expr     non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggWindowFunc avg(SQLs.ArgDistinct distinct, Expression expr) {
        FuncExpUtils.assertDistinct(distinct, SQLs.DISTINCT);
        return WindowFunctions.compositeWindowAggFunc("AVG", Arrays.asList(distinct, expr), DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">MySQL JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#json_arrayagg">H2 JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">Postgre JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    public static _AggWindowFunc jsonArrayAgg(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("JSON_ARRAYAGG", exp, JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param key   non-null
     * @param value non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-objectagg">MySQL JSON_OBJECTAGG(col_or_expr) [over_clause]</a>
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#json_objectagg">H2 JSON_OBJECTAGG(col_or_expr) [over_clause]</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">Postgre JSON_OBJECTAGG(col_or_expr) [over_clause]</a>
     */
    public static _AggWindowFunc jsonObjectAgg(Expression key, Expression value) {
        return WindowFunctions.twoArgAggWindow("JSON_OBJECTAGG", key, value, JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_std">STD(expr) [over_clause]</a>
     */
    public static _AggWindowFunc std(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("STD", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev">STDDEV(expr) [over_clause]</a>
     */
    public static _AggWindowFunc stdDev(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("STDDEV", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-pop">STDDEV_POP(expr) [over_clause]</a>
     */
    public static _AggWindowFunc stdDevPop(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("STDDEV_POP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-samp">STDDEV_SAMP(expr) [over_clause]</a>
     */
    public static _AggWindowFunc stdDevSamp(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("STDDEV_SAMP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-pop">VAR_POP(expr) [over_clause]</a>
     */
    public static _AggWindowFunc varPop(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("VAR_POP", exp, DoubleType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VAR_SAMP(expr) [over_clause]</a>
     */
    public static _AggWindowFunc varSamp(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("VAR_SAMP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_variance">VARIANCE(expr) [over_clause]</a>
     */
    public static _AggWindowFunc variance(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("VARIANCE", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static _AggWindowFunc bitAnd(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("BIT_AND", exp, Functions._bitwiseFuncType(exp));
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static _AggWindowFunc bitOr(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("BIT_OR", exp, Functions._bitwiseFuncType(exp));
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static _AggWindowFunc bitXor(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("BIT_XOR", exp, Functions._bitwiseFuncType(exp));
    }


    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#bit_and_agg">BIT_AND_AGG(expr) [over_clause]</a>
     */
    @Support({H2})
    public static _AggWindowFunc bitAndAgg(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("BIT_AND_AGG", exp, Functions._bitwiseFuncType(exp));
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#bit_and_agg">BIT_OR_AGG(expr) [over_clause]</a>
     */
    @Support({H2})
    public static _AggWindowFunc bitOrAgg(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("BIT_OR_AGG", exp, Functions._bitwiseFuncType(exp));
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#bit_and_agg">BIT_XOR_AGG(expr) [over_clause]</a>
     */
    @Support({H2})
    public static _AggWindowFunc bitXorAgg(Expression exp) {
        return WindowFunctions.oneArgWindowAggFunc("BIT_XOR_AGG", exp, Functions._bitwiseFuncType(exp));
    }


}
