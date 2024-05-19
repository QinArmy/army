/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.army.criteria.standard;

import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.Support;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.Expressions;
import io.army.criteria.impl.FuncExpUtils;
import io.army.criteria.impl.Functions;
import io.army.mapping.*;
import io.army.mapping.optional.IntervalType;

import java.util.Arrays;

import static io.army.dialect.Database.H2;
import static io.army.dialect.Database.MySQL;


/**
 * <p>This class provide standard window function.
 * <p><strong>NOTE</strong>: You shouldn't static import these window function method , because you shouldn't avoid the conflict with dialect window function methods.
 *
 * @since 0.6.4
 */
public abstract class Windows {

    private Windows() {
        throw new UnsupportedOperationException();
    }

    public interface _OverSpec extends Window._OverWindowClause<Window._StandardPartitionBySpec> {


    }

    public interface _WindowAggSpec extends _OverSpec, SQLFunction.AggregateFunction, SimpleExpression {

    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  DoubleType}.
     *
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_cume-dist">CUME_DIST() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">cume_dist () → double precision<br/>
     * Returns the cumulative distribution, that is (number of partition rows preceding or peers with current row) / (total partition rows). The value thus ranges from 1/N to 1.
     * </a>
     */
    public static _OverSpec cumeDist() {
        return StandardWindowFunctions.zeroArgWindowFunc("CUME_DIST", DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  LongType}.
     *
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">row_number () → bigint<br/>
     * Returns the number of the current row within its partition, counting from 1.
     * </a>
     */
    public static _OverSpec rowNumber() {
        return StandardWindowFunctions.zeroArgWindowFunc("ROW_NUMBER", LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  LongType}.
     *
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">dense_rank () → bigint<br/>
     * Returns the rank of the current row, without gaps; this function effectively counts peer groups.
     * </a>
     */
    public static _OverSpec denseRank() {
        return StandardWindowFunctions.zeroArgWindowFunc("DENSE_RANK", LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  MappingType} or exp.
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_first-value">FIRST_VALUE() over_clause</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-window.html#FUNCTIONS-WINDOW-TABLE">first_value () → anyelement</a>
     */
    public static _OverSpec firstValue(Expression exp) {
        return StandardWindowFunctions.oneArgWindowFunc("FIRST_VALUE", exp, exp.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_last-value">LAST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static _OverSpec lastValue(Expression expr) {
        return StandardWindowFunctions.zeroArgWindowFunc("LAST_VALUE", expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(Expression expr) {
        return StandardWindowFunctions.oneArgWindowFunc("LAG", expr, expr.typeMeta());
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
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(Expression expr, Expression n) {
        return StandardWindowFunctions.twoArgWindowFunc("LAG", expr, n, expr.typeMeta());
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
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(Expression expr, Expression n, Expression defaultValue) {
        return StandardWindowFunctions.compositeWindowFunc("LAG", Arrays.asList(expr, SQLs.COMMA, n, SQLs.COMMA, defaultValue), expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(Expression expr) {
        return StandardWindowFunctions.oneArgWindowFunc("LEAD", expr, expr.typeMeta());
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
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(Expression expr, Expression n) {
        return StandardWindowFunctions.twoArgWindowFunc("LEAD", expr, n, expr.typeMeta());
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
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(Expression expr, Expression n, Expression defaultValue) {
        return StandardWindowFunctions.compositeWindowFunc("LEAD", Arrays.asList(expr, SQLs.COMMA, n, SQLs.COMMA, defaultValue), expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null {@link  Expression}
     * @param n    positive.output literal.
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_nth-value">NTH_VALUE(expr, N) [from_first_last] [null_treatment] over_clause</a>
     */
    public static _OverSpec nthValue(Expression expr, Expression n) {
        return StandardWindowFunctions.twoArgWindowFunc("NTH_VALUE", expr, n, expr.typeMeta());
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
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_ntile">NTILE(N) over_clause</a>
     */
    public static _OverSpec ntile(Expression n) {
        return StandardWindowFunctions.oneArgWindowFunc("NTILE", n, LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}.
     *
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">PERCENT_RANK() over_clause</a>
     */
    public static _OverSpec percentRank() {
        return StandardWindowFunctions.zeroArgWindowFunc("PERCENT_RANK", DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link LongType}.
     *
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">RANK() over_clause</a>
     */
    public static _OverSpec rank() {
        return StandardWindowFunctions.zeroArgWindowFunc("RANK", LongType.INSTANCE);
    }



    /*-------------------below Aggregate Function-------------------*/

    /**
     * <p>The {@link MappingType} of function return type: {@link  LongType}
     *
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _WindowAggSpec countAsterisk() {
        return count(Expressions._ASTERISK_EXP);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  LongType}
     *
     * @param expr non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _WindowAggSpec count(Expression expr) {
        return StandardWindowFunctions.oneArgWindowAggFunc("COUNT", expr, LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  LongType}
     *
     * @param expr non-null
     * @throws io.army.criteria.CriteriaException throw when
     *                                            <ul>
     *                                                <li>distinct isn't {@link SQLs#DISTINCT}</li>
     *                                                <li>not in statement context</li>
     *                                            </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _WindowAggSpec count(SQLs.ArgDistinct distinct, Expression expr) {
        FuncExpUtils.assertDistinct(distinct, SQLs.DISTINCT);
        return StandardWindowFunctions.compositeWindowAggFunc("COUNT", Arrays.asList(distinct, expr), LongType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link  MappingType} of exp
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN(expr) [over_clause]</a>
     */
    public static _WindowAggSpec min(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("MIN", exp, exp.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link  MappingType} of exp
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX(expr) [over_clause]</a>
     */
    public static _WindowAggSpec max(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("MAX", exp, exp.typeMeta());
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
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">MySQL SUM([DISTINCT] expr)</a>
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#sum">H2 SUM([DISTINCT] expr)</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">Postgre SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _WindowAggSpec sum(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("SUM", exp, Functions._returnType(exp, Functions::_sumType));
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
     * @throws io.army.criteria.CriteriaException throw when
     *                                            <ul>
     *                                                <li>distinct isn't {@link SQLs#DISTINCT}</li>
     *                                                <li>not in statement context</li>
     *                                            </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">MySQL SUM([DISTINCT] expr) [over_clause]</a>
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#sum">H2 SUM([DISTINCT] expr)</a>
     */
    @Support({MySQL, H2})
    public static _WindowAggSpec sum(SQLs.ArgDistinct distinct, Expression exp) {
        FuncExpUtils.assertDistinct(distinct, SQLs.DISTINCT);
        return StandardWindowFunctions.compositeWindowAggFunc("SUM", Arrays.asList(distinct, exp), Functions._returnType(exp, Functions::_sumType));
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>sql float type : {@link DoubleType}</li>
     *     <li>sql integer/decimal type : {@link BigDecimalType}</li>
     *     <li>sql interval : {@link IntervalType}</li>
     *     <li>else : {@link TextType}</li>
     * </ul>
     *
     * @param expr non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static _WindowAggSpec avg(Expression expr) {
        return StandardWindowFunctions.oneArgWindowAggFunc("AVG", expr, Functions._returnType(expr, Functions::_avgType));
    }


    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>sql float type : {@link DoubleType}</li>
     *     <li>sql integer/decimal type : {@link BigDecimalType}</li>
     *     <li>sql interval : {@link IntervalType}</li>
     *     <li>else : {@link TextType}</li>
     * </ul>
     *
     * @param distinct see {@link SQLs#DISTINCT}
     * @param expr     non-null
     * @throws io.army.criteria.CriteriaException throw when
     *                                            <ul>
     *                                                <li>distinct isn't {@link SQLs#DISTINCT}</li>
     *                                                <li>not in statement context</li>
     *                                            </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static _WindowAggSpec avg(SQLs.ArgDistinct distinct, Expression expr) {
        FuncExpUtils.assertDistinct(distinct, SQLs.DISTINCT);
        return StandardWindowFunctions.compositeWindowAggFunc("AVG", Arrays.asList(distinct, expr), Functions._returnType(expr, Functions::_avgType));
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">MySQL JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#json_arrayagg">H2 JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">Postgre JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    public static _WindowAggSpec jsonArrayAgg(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("JSON_ARRAYAGG", exp, JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link JsonType#TEXT}
     *
     * @param key   non-null
     * @param value non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-objectagg">MySQL JSON_OBJECTAGG(col_or_expr) [over_clause]</a>
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#json_objectagg">H2 JSON_OBJECTAGG(col_or_expr) [over_clause]</a>
     * @see <a href="https://www.postgresql.org/docs/current/functions-aggregate.html#FUNCTIONS-AGGREGATE-TABLE">Postgre JSON_OBJECTAGG(col_or_expr) [over_clause]</a>
     */
    public static _WindowAggSpec jsonObjectAgg(Expression key, Expression value) {
        return StandardWindowFunctions.twoArgAggWindow("JSON_OBJECTAGG", key, value, JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_std">STD(expr) [over_clause]</a>
     */
    public static _WindowAggSpec std(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("STD", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev">STDDEV(expr) [over_clause]</a>
     */
    public static _WindowAggSpec stdDev(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("STDDEV", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-pop">STDDEV_POP(expr) [over_clause]</a>
     */
    public static _WindowAggSpec stdDevPop(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("STDDEV_POP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-samp">STDDEV_SAMP(expr) [over_clause]</a>
     */
    public static _WindowAggSpec stdDevSamp(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("STDDEV_SAMP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-pop">VAR_POP(expr) [over_clause]</a>
     */
    public static _WindowAggSpec varPop(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("VAR_POP", exp, DoubleType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VAR_SAMP(expr) [over_clause]</a>
     */
    public static _WindowAggSpec varSamp(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("VAR_SAMP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_variance">VARIANCE(expr) [over_clause]</a>
     */
    public static _WindowAggSpec variance(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("VARIANCE", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static _WindowAggSpec bitAnd(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("BIT_AND", exp, Functions._bitwiseFuncType(exp));
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static _WindowAggSpec bitOr(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("BIT_OR", exp, Functions._bitwiseFuncType(exp));
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static _WindowAggSpec bitXor(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("BIT_XOR", exp, Functions._bitwiseFuncType(exp));
    }


    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#bit_and_agg">BIT_AND_AGG(expr) [over_clause]</a>
     */
    @Support({H2})
    public static _WindowAggSpec bitAndAgg(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("BIT_AND_AGG", exp, Functions._bitwiseFuncType(exp));
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#bit_and_agg">BIT_OR_AGG(expr) [over_clause]</a>
     */
    @Support({H2})
    public static _WindowAggSpec bitOrAgg(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("BIT_OR_AGG", exp, Functions._bitwiseFuncType(exp));
    }

    /**
     * <p>The {@link MappingType} of function return type:
     * <ul>
     *     <li>If expr is integer number type ,then {@link UnsignedLongType}</li>
     *     <li>else {@link VarBinaryType}</li>
     * </ul>
     *
     * @param exp non-null
     * @throws io.army.criteria.CriteriaException throw when not in statement context
     * @see <a href="https://www.h2database.com/html/functions-aggregate.html#bit_and_agg">BIT_XOR_AGG(expr) [over_clause]</a>
     */
    @Support({H2})
    public static _WindowAggSpec bitXorAgg(Expression exp) {
        return StandardWindowFunctions.oneArgWindowAggFunc("BIT_XOR_AGG", exp, Functions._bitwiseFuncType(exp));
    }


}
