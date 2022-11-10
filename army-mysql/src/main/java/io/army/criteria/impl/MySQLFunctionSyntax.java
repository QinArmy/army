package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.standard.SQLFunction;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.JsonListType;
import io.army.mapping.optional.JsonMapType;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
abstract class MySQLFunctionSyntax extends MySQLSyntax {

    MySQLFunctionSyntax() {
    }


    public interface _OverSpec<R extends Expression, I extends Item>
            extends Window._OverClause<Window._SimpleLeftParenClause<R>, R> {

    }

    public interface _AggregateWindowFunc<R extends Expression, I extends Item>
            extends _OverSpec<R, I>, _ItemExpression<I>, SQLFunction.AggregateFunction {

    }

    public interface _NullTreatmentSpec<R extends Expression, I extends Item>
            extends Functions._NullTreatmentClause<_OverSpec<R, I>>, _OverSpec<R, I> {


    }

    public interface _FromFirstLastSpec<R extends Expression, I extends Item>
            extends Functions._FromFirstLastClause<_NullTreatmentSpec<R, I>>, _NullTreatmentSpec<R, I>
            , SQLFunction._OuterOptionalClause {

    }


    /*-------------------below Aggregate Function  -------------------*/

    /**
     * @param distinct nullable,{@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> avg(@Nullable Functions.FuncDistinct distinct
            , Expression exp, Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgOptionAggregateWindow("AVG", distinct, exp, DoubleType.INSTANCE, endFunction, asFunction);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> bitAnd(Expression exp
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("BIT_AND", exp, _bitwiseFuncReturnType(exp), endFunction, asFunction);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> bitOr(Expression exp
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("BIT_OR", exp, _bitwiseFuncReturnType(exp), endFunction, asFunction);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> bitXor(Expression exp
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("BIT_XOR", exp, _bitwiseFuncReturnType(exp), endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> countStar(Function<_ItemExpression<I>, R> endFunction
            , Function<Selection, I> asFunction) {
        return count(SQLs._START_EXP, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> count(Expression exp
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("COUNT", exp, LongType.INSTANCE, endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> count(@Nullable Functions.FuncDistinct distinct
            , List<Expression> argList, Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.multiArgAggregateWindowFunc("COUNT", distinct, argList, LongType.INSTANCE, endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonListType}
     * </p>
     *
     * @param exp parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> jsonArrayAgg(final Expression exp
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        final TypeMeta returnType;//TODO validate JsonListType
        returnType = Functions._returnType((ArmyExpression) exp, JsonListType::from);
        return MySQLFunctionUtils.oneArgAggregateWindow("JSON_ARRAYAGG", exp, returnType, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonMapType}
     * </p>
     *
     * @param key   non-null parameter or {@link Expression},but couldn't be null.
     * @param value non-null parameter or {@link Expression},but couldn't be null.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-objectagg">JSON_OBJECTAGG(key, value) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> jsonObjectAgg(final Expression key
            , Expression value, Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        final TypeMeta returnType;//TODO validate JsonMapType
        returnType = Functions._returnType((ArmyExpression) key, (ArmyExpression) value, JsonMapType::from);
        final List<Expression> argList;
        argList = Arrays.asList(key, value);
        return MySQLFunctionUtils.multiArgAggregateWindowFunc("JSON_OBJECTAGG", null, argList, returnType, endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> max(@Nullable SQLs.FuncDistinct distinct
            , Expression expr, Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("MAX", expr, expr.typeMeta(), endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link Expression},but couldn't be {@link SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> min(@Nullable SQLs.FuncDistinct distinct
            , Expression expr, Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("MIN", expr, expr.typeMeta(), endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_std">STD(xpr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> std(Expression expr
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("STD", expr, DoubleType.INSTANCE, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev">STDDEV(xpr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> stdDev(Expression expr
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("STDDEV", expr, DoubleType.INSTANCE, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-pop">STDDEV_POP(xpr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> stdDevPop(Expression expr
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("STDDEV_POP", expr, DoubleType.INSTANCE, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-samp">STDDEV_SAMP(xpr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> stdDevSamp(Expression expr
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("STDDEV_SAMP", expr, DoubleType.INSTANCE, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> sum(@Nullable SQLs.FuncDistinct distinct
            , Expression expr, Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgOptionAggregateWindow("SUM", distinct, expr, expr.typeMeta(), endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-pop">VAR_POP(xpr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> varPop(Expression expr
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("VAR_POP", expr, DoubleType.INSTANCE, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VAR_SAMP(xpr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> varSamp(Expression expr
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("VAR_SAMP", expr, DoubleType.INSTANCE, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VARIANCE(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> variance(Expression expr
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("VARIANCE", expr, DoubleType.INSTANCE, endFunction, asFunction);
    }

    /*-------------------below window function -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_cume-dist">CUME_DIST() over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> cumeDist(
            Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.noArgWindowFunc("CUME_DIST", DoubleType.INSTANCE, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> denseRank(
            Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.noArgWindowFunc("DENSE_RANK", LongType.INSTANCE, endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_first-value">FIRST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> firstValue(final Expression expr
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgWindowFunc("FIRST_VALUE", expr, expr.typeMeta(), endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_last-value">LAST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> lastValue(final Expression expr
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgWindowFunc("LAST_VALUE", expr, expr.typeMeta(), endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> lag(final Expression expr
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return leadOrLog("LAG", expr, null, null, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @param n    nullable,probably is below:
     *             <ul>
     *                 <li>null</li>
     *                 <li>{@link Long} type</li>
     *                 <li>{@link Integer} type</li>
     *                 <li>{@link SQLs#paramFrom(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                 <li>{@link SQLs#literalFrom(Object) },argument type is {@link Long} or {@link Integer}</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> lag(Expression expr, @Nullable Expression n
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return leadOrLog("LAG", expr, n, null, endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr        non-null parameter or {@link  Expression},but couldn't be {@link SQLs#NULL}
     * @param n           nullable,probably is below:
     *                    <ul>
     *                        <li>null</li>
     *                        <li>{@link Long} type</li>
     *                        <li>{@link Integer} type</li>
     *                        <li>{@link SQLs#paramFrom(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                        <li>{@link SQLs#literalFrom(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                    </ul>
     * @param defaultWord {@link  SQLs#DEFAULT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> lag(final Expression expr
            , final @Nullable Expression n, final @Nullable SQLs.WordDefault defaultWord
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return leadOrLog("LAG", expr, n, defaultWord, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> lead(final Expression expr
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return leadOrLog("LEAD", expr, null, null, endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#NULL}
     * @param n    nullable,probably is below:
     *             <ul>
     *                 <li>null</li>
     *                 <li>{@link Long} type</li>
     *                 <li>{@link Integer} type</li>
     *                 <li>{@link SQLs#paramFrom(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                 <li>{@link SQLs#literalFrom(Object) },argument type is {@link Long} or {@link Integer}</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> lead(Expression expr, @Nullable Expression n
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return leadOrLog("LEAD", expr, n, null, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr        non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#NULL}
     * @param n           nullable,probably is below:
     *                    <ul>
     *                        <li>null</li>
     *                        <li>{@link Long} type</li>
     *                        <li>{@link Integer} type</li>
     *                        <li>{@link SQLs#paramFrom(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                        <li>{@link SQLs#literalFrom(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                    </ul>
     * @param defaultWord {@link  SQLs#DEFAULT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> lead(Expression expr, @Nullable Expression n
            , final @Nullable SQLs.WordDefault defaultWord, Function<_ItemExpression<I>, R> endFunction
            , Function<Selection, I> asFunction) {
        return leadOrLog("LEAD", expr, n, defaultWord, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null {@link  Expression}
     * @param n    positive.output literal.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_nth-value">NTH_VALUE(expr, N) [from_first_last] [null_treatment] over_clause</a>
     */
    public static <R extends Expression, I extends Item> _FromFirstLastSpec<R, I> nthValue(Expression expr, Expression n
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.twoArgFromFirstWindowFunc("NTH_VALUE", expr, n, expr.typeMeta(), endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}
     * </p>
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
     *               <li>positive number parameter {@link  Expression},eg:{@link SQLs#paramFrom(Object)}</li>
     *               <li>positive number literal {@link  Expression},eg:{@link SQLs#literalFrom(Object)}</li>
     *               <li>variable {@link  Expression}</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_ntile">NTILE(N) over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> ntile(final Expression n
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        //TODO a local variable in a stored routine?
        return MySQLFunctionUtils.oneArgWindowFunc("NTILE", n, LongType.INSTANCE, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">PERCENT_RANK() over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> percentRank(
            Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.noArgWindowFunc("PERCENT_RANK", DoubleType.INSTANCE, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">RANK() over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> rank(
            Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.noArgWindowFunc("RANK", LongType.INSTANCE, endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     */
    public static <R extends Expression, I extends Item> _OverSpec<R, I> rowNumber(
            Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.noArgWindowFunc("ROW_NUMBER", LongType.INSTANCE, endFunction, asFunction);
    }



    /*-------------------below MySQL-Specific Functions-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param geometryList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_geometrycollection">GeometryCollection(g [, g] ...)</a>
     */
    public static Expression geometryCollection(final List<Expression> geometryList) {
        return _geometryTypeFunc("GeometryCollection", geometryList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param ptList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_linestring">LineString(pt [, pt] ...)</a>
     */
    public static Expression lineString(final List<Expression> ptList) {
        return _geometryTypeFunc("LineString", ptList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param ptList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_multilinestring">MultiLineString(ls [, ls] ...)</a>
     */
    public static Expression multiLineString(final List<Expression> ptList) {
        return _geometryTypeFunc("MultiLineString", ptList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param ptList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_multipoint">MultiPoint(pt [, pt2] ...)</a>
     */
    public static Expression multiPoint(final List<Expression> ptList) {
        return _geometryTypeFunc("MultiPoint", ptList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param ptList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_multipolygon">MultiPolygon(poly [, poly] ...)</a>
     */
    public static Expression multiPolygon(final List<Expression> ptList) {
        return _geometryTypeFunc("MultiPolygon", ptList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param lsList non-null,empty list or list
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_polygon">Polygon(ls [, ls] ...)</a>
     */
    public static Expression polygon(final List<Expression> lsList) {
        return _geometryTypeFunc("Polygon", lsList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param x non-null
     * @param y non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_point">Point(x, y)</a>
     */
    public static Expression point(final Expression x, final Expression y) {
        return FunctionUtils.twoArgFunc("Point", x, y, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrcontains">MBRContains(g1, g2)</a>
     */
    public static IPredicate mbrContains(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRContains", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrcoveredby">MBRCoveredBy(g1, g2)</a>
     */
    public static IPredicate mbrCoveredBy(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRCoveredBy", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrcovers">MBRCovers(g1, g2)</a>
     */
    public static IPredicate mbrCovers(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRCovers", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrdisjoint">MBRDisjoint(g1, g2)</a>
     */
    public static IPredicate mbrDisjoint(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRDisjoint", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrequals">MBREquals(g1, g2)</a>
     */
    public static IPredicate mbrEquals(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBREquals", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrintersects">MBRIntersects(g1, g2)</a>
     */
    public static IPredicate mbrIntersects(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRIntersects", g1, g2);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbroverlaps">MBROverlaps(g1, g2)</a>
     */
    public static IPredicate mbrOverlaps(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBROverlaps", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrtouches">MBRTouches(g1, g2)</a>
     */
    public static IPredicate mbrTouches(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRTouches", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-mbr.html#function_mbrwithin">MBRWithin(g1, g2)</a>
     */
    public static IPredicate mbrWithin(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("MBRWithin", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param polyOrmpoly non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-area">ST_Area({poly|mpoly})</a>
     */
    public static Expression stArea(final Expression polyOrmpoly) {
        return FunctionUtils.oneArgFunc("ST_Area", polyOrmpoly, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param polyOrmpoly non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-centroid">ST_Centroid({poly|mpoly})</a>
     */
    public static Expression stCentroid(final Expression polyOrmpoly) {
        return FunctionUtils.oneArgFunc("ST_Centroid", polyOrmpoly, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param polyOrmpoly non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-exteriorring">ST_ExteriorRing(poly)</a>
     */
    public static Expression stExteriorRing(final Expression polyOrmpoly) {
        return FunctionUtils.oneArgFunc("ST_ExteriorRing", polyOrmpoly, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param poly non-null
     * @param n    non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-interiorringn">ST_InteriorRingN(poly, N)</a>
     */
    public static Expression stInteriorRingN(final Expression poly, final Expression n) {
        return FunctionUtils.twoArgFunc("ST_InteriorRingN", poly, n, ByteArrayType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param poly non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-numinteriorrings">ST_NumInteriorRing(poly)</a>
     */
    public static Expression stNumInteriorRing(final Expression poly) {
        return FunctionUtils.oneArgFunc("ST_NumInteriorRing", poly, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param poly non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-polygon-property-functions.html#function_st-numinteriorrings">ST_NumInteriorRings(poly)</a>
     */
    public static Expression stNumInteriorRings(final Expression poly) {
        return FunctionUtils.oneArgFunc("ST_NumInteriorRings", poly, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-asbinary">ST_AsBinary(g [, options])</a>
     */
    public static Expression stAsBinary(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_AsBinary", g, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g       non-null
     * @param options non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-asbinary">ST_AsBinary(g [, options])</a>
     */
    public static Expression stAsBinary(final Expression g, final Expression options) {
        return _simpleTowArgFunc("ST_AsBinary", g, options, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-asbinary">ST_AsWKB(g [, options])</a>
     */
    public static Expression stAsWKB(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_AsWKB", g, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g       non-null
     * @param options non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-asbinary">ST_AsWKB(g [, options])</a>
     */
    public static Expression stAsWKB(final Expression g, final Expression options) {
        return FunctionUtils.twoArgFunc("ST_AsWKB", g, options, ByteArrayType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-astext">ST_AsText(g [, options])</a>
     */
    public static Expression stAsText(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_AsText", g, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param g       non-null
     * @param options non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-astext">ST_AsText(g [, options])</a>
     */
    public static Expression stAsText(final Expression g, final Expression options) {
        return FunctionUtils.twoArgFunc("ST_AsText", g, options, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-astext">ST_AsWKT(g [, options])</a>
     */
    public static Expression stAsWKT(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_AsWKT", g, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param g       non-null
     * @param options non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-astext">ST_AsWKT(g [, options])</a>
     */
    public static Expression stAsWKT(final Expression g, final Expression options) {
        return FunctionUtils.twoArgFunc("ST_AsWKT", g, options, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-format-conversion-functions.html#function_st-swapxy">ST_SwapXY(g)</a>
     */
    public static Expression stSwapXY(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_SwapXY", g, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param expList non-null ,the list that size in [1,3].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geojson-functions.html#function_st-asgeojson">ST_AsGeoJSON(g [, max_dec_digits [, options]])</a>
     */
    public static Expression stAsGeoJson(final List<Expression> expList) {
        final String name = "ST_AsGeoJSON";
        final Expression func;
        switch (expList.size()) {
            case 1:
                func = FunctionUtils.oneArgFunc(name, expList.get(0), StringType.INSTANCE);
                break;
            case 2:
            case 3:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList), StringType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null ,the list that size in [1,3].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geojson-functions.html#function_st-geomfromgeojson">ST_GeomFromGeoJSON(str [, options [, srid]])</a>
     */
    public static Expression stGeomFromGeoJson(final List<Expression> expList) {
        final String name = "ST_GeomFromGeoJSON";
        final Expression func;
        switch (expList.size()) {
            case 1:
                func = FunctionUtils.oneArgFunc(name, expList.get(0), ByteArrayType.INSTANCE);
                break;
            case 2:
            case 3:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList)
                        , ByteArrayType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null ,the list that size in [2,5].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-buffer">ST_Buffer(g, d [, strategy1 [, strategy2 [, strategy3]]])</a>
     */
    public static Expression stBuffer(final List<Expression> expList) {
        final String name = "ST_Buffer";
        final Expression func;
        switch (expList.size()) {
            case 2:
            case 3:
            case 4:
            case 5:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList)
                        , ByteArrayType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType}
     * </p>
     *
     * @param expList non-null ,the list that size in [1,2].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#unction_st-buffer-strategy">ST_Buffer_Strategy(strategy [, points_per_circle])</a>
     */
    public static Expression stBufferStrategy(final List<Expression> expList) {
        final String name = "ST_Buffer_Strategy";
        final Expression func;
        switch (expList.size()) {
            case 1:
                func = FunctionUtils.oneArgFunc(name, expList.get(0), ByteArrayType.INSTANCE);
                break;
            case 2:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList)
                        , ByteArrayType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-convexhull">ST_ConvexHull(g)</a>
     */
    public static Expression stConvexHull(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_ConvexHull", g, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-difference">ST_Difference(g1, g2)</a>
     */
    public static Expression stDifference(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_Difference", g1, g2, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-intersection">ST_Intersection(g1, g2)</a>
     */
    public static Expression stIntersection(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_Intersection", g1, g2, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param ls                 non-null
     * @param fractionalDistance non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-lineinterpolatepoint">ST_LineInterpolatePoint(ls, fractional_distance)</a>
     */
    public static Expression stLineInterpolatePoint(final Expression ls, final Expression fractionalDistance) {
        return FunctionUtils.twoArgFunc("ST_LineInterpolatePoint", ls, fractionalDistance, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param ls                 non-null
     * @param fractionalDistance non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-lineinterpolatepoints">ST_LineInterpolatePoints(ls, fractional_distance)</a>
     */
    public static Expression stLineInterpolatePoints(final Expression ls, final Expression fractionalDistance) {
        return FunctionUtils.twoArgFunc("ST_LineInterpolatePoints", ls, fractionalDistance, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param ls       non-null
     * @param distance non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-pointatdistance">ST_PointAtDistance(ls, distance)</a>
     */
    public static Expression stPointAtDistance(final Expression ls, final Expression distance) {
        return FunctionUtils.twoArgFunc("ST_PointAtDistance", ls, distance, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-symdifference">ST_SymDifference(g1, g2)</a>
     */
    public static Expression stSymDifference(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_SymDifference", g1, g2, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g          non-null
     * @param targetSrid non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-transform">ST_Transform(g, target_srid)</a>
     */
    public static Expression stTransform(final Expression g, final Expression targetSrid) {
        return FunctionUtils.twoArgFunc("ST_Transform", g, targetSrid, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-union">ST_Union(g1, g2)</a>
     */
    public static Expression stUnion(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_Union", g1, g2, ByteArrayType.INSTANCE);
    }

    /*-------------------below Spatial Convenience Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param expList non-null,size in [2,3].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-distance-sphere">ST_Distance_Sphere(g1, g2 [, radius])</a>
     */
    public static Expression stDistanceSphere(final List<Expression> expList) {
        final String name = "ST_Distance_Sphere";
        final Expression func;
        switch (expList.size()) {
            case 2:
            case 3:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(expList)
                        , DoubleType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-isvalid">ST_IsValid(g)</a>
     */
    public static IPredicate stIsValid(final Expression g) {
        return FunctionUtils.oneArgFuncPredicate("ST_IsValid", g);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param pt1 non-null
     * @param pt2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-makeenvelope">ST_MakeEnvelope(pt1, pt2)</a>
     */
    public static Expression stMakeEnvelope(final Expression pt1, final Expression pt2) {
        return FunctionUtils.twoArgFunc("ST_MakeEnvelope", pt1, pt2, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g           non-null
     * @param maxDistance non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-operator-functions.html#function_st-simplify">ST_Simplify(g, max_distance)</a>
     */
    public static Expression stSimplify(final Expression g, final Expression maxDistance) {
        return FunctionUtils.twoArgFunc("ST_Simplify", g, maxDistance, ByteArrayType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-convenience-functions.html#function_st-validate">ST_Validate(g)</a>
     */
    public static Expression stValidate(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_Validate", g, ByteArrayType.INSTANCE);
    }


    /*-------------------below LineString and MultiLineString Property Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param ls non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-endpoint">ST_EndPoint(ls)</a>
     */
    public static Expression stEndPoint(final Expression ls) {
        return FunctionUtils.oneArgFunc("ST_EndPoint", ls, ByteArrayType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param ls non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-isclosed">ST_IsClosed(ls)</a>
     */
    public static Expression stIsClosed(final Expression ls) {
        return FunctionUtils.oneArgFunc("ST_IsClosed", ls, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param ls non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-length">ST_Length(ls [, unit])</a>
     */
    public static Expression stLength(Expression ls) {
        return FunctionUtils.oneArgFunc("ST_Length", ls, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param ls   non-null
     * @param unit non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-length">ST_Length(ls [, unit])</a>
     */
    public static Expression stLength(final Expression ls, Expression unit) {
        return FunctionUtils.twoArgFunc("ST_Length", ls, unit, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param ls non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-numpoints">ST_NumPoints(ls)</a>
     */
    public static Expression stNumPoints(final Expression ls) {
        return FunctionUtils.oneArgFunc("ST_NumPoints", ls, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param ls non-null
     * @param n  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-pointn">ST_PointN(ls, N)</a>
     */
    public static Expression stPointN(final Expression ls, final Expression n) {
        return FunctionUtils.twoArgFunc("ST_PointN", ls, n, ByteArrayType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param ls non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-startpoint">ST_StartPoint(ls)</a>
     */
    public static Expression stStartPoint(final Expression ls) {
        return FunctionUtils.oneArgFunc("ST_StartPoint", ls, ByteArrayType.INSTANCE);
    }


    /*-------------------below Spatial Relation Functions That Use Object Shapes-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-contains">ST_Contains(g1, g2)</a>
     */
    public static IPredicate stContains(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Contains", g1, g2);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-crosses">ST_Crosses(g1, g2)</a>
     */
    public static IPredicate stCrosses(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Crosses", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-disjoint">ST_Disjoint(g1, g2)</a>
     */
    public static IPredicate stDisjoint(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Disjoint", g1, g2);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #stDistance(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-distance">ST_Distance(g1, g2 [, unit])</a>
     */
    public static Expression stDistance(final Expression g1, Expression g2) {
        return FunctionUtils.twoArgFunc("ST_Distance", g1, g2, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param g1   non-null
     * @param g2   non-null
     * @param unit non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see #stDistance(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-distance">ST_Distance(g1, g2 [, unit])</a>
     */
    public static Expression stDistance(final Expression g1, Expression g2, Expression unit) {
        return FunctionUtils.threeArgFunc("ST_Distance", g1, g2, unit, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-equals">ST_Equals(g1, g2)</a>
     */
    public static IPredicate stEquals(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Equals", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-frechetdistance">ST_FrechetDistance(g1, g2 [, unit])</a>
     */
    public static Expression stFrechetDistance(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_FrechetDistance", g1, g2, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param g1   non-null
     * @param g2   non-null
     * @param unit non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-frechetdistance">ST_FrechetDistance(g1, g2 [, unit])</a>
     */
    public static Expression stFrechetDistance(final Expression g1, final Expression g2, final Expression unit) {
        return FunctionUtils.threeArgFunc("ST_FrechetDistance", g1, g2, unit, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-hausdorffdistance">ST_HausdorffDistance(g1, g2 [, unit])</a>
     */
    public static Expression stHausdorffDistance(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgFunc("ST_HausdorffDistance", g1, g2, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param g1   non-null
     * @param g2   non-null
     * @param unit non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-hausdorffdistance">ST_HausdorffDistance(g1, g2 [, unit])</a>
     */
    public static Expression stHausdorffDistance(final Expression g1, final Expression g2, final Expression unit) {
        return FunctionUtils.threeArgFunc("ST_HausdorffDistance", g1, g2, unit, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-intersects">ST_Intersects(g1, g2)</a>
     */
    public static IPredicate stIntersects(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Intersects", g1, g2);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when g1 or g2 is multi parameter or literal
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-overlaps">ST_Overlaps(g1, g2)</a>
     */
    public static IPredicate stOverlaps(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Overlaps", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when g1 or g2 is multi parameter or literal
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-touches">ST_Touches(g1, g2)</a>
     */
    public static IPredicate stTouches(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Touches", g1, g2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when g1 or g2 is multi parameter or literal
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-within">ST_Within(g1, g2)</a>
     */
    public static IPredicate stWithin(final Expression g1, final Expression g2) {
        return FunctionUtils.twoArgPredicateFunc("ST_Within", g1, g2);
    }


    /*-------------------below Spatial Geohash Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param point     non-null
     * @param maxLength non-null
     * @throws CriteriaException throw when any argument is multi parameter or literal
     * @see #stGeoHash(Expression, Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geohash-functions.html#function_st-geohash">ST_GeoHash(longitude, latitude, max_length), ST_GeoHash(point, max_length)</a>
     */
    public static Expression stGeoHash(final Expression point, final Expression maxLength) {
        return FunctionUtils.twoArgFunc("ST_GeoHash", point, maxLength, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param longitude non-null
     * @param latitude  non-null
     * @param maxLength non-null
     * @throws CriteriaException throw when any argument is multi parameter or literal
     * @see #stGeoHash(Expression, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geohash-functions.html#function_st-geohash">ST_GeoHash(longitude, latitude, max_length), ST_GeoHash(point, max_length)</a>
     */
    public static Expression stGeoHash(final Expression longitude, final Expression latitude, final Expression maxLength) {
        return FunctionUtils.threeArgFunc("ST_GeoHash", longitude, latitude, maxLength, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param geohashStr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geohash-functions.html#function_st-latfromgeohash">ST_LatFromGeoHash(geohash_str)</a>
     */
    public static Expression stLatFromGeoHash(final Expression geohashStr) {
        return FunctionUtils.oneArgFunc("ST_LatFromGeoHash", geohashStr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param geohashStr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geohash-functions.html#function_st-longfromgeohash">ST_LongFromGeoHash(geohash_str)</a>
     */
    public static Expression stLongFromGeoHash(final Expression geohashStr) {
        return FunctionUtils.oneArgFunc("ST_LongFromGeoHash", geohashStr, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType}
     * </p>
     *
     * @param geohashStr non-null
     * @param srid       non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geohash-functions.html#function_st-pointfromgeohash">ST_PointFromGeoHash(geohash_str, srid)</a>
     */
    public static Expression stPointFromGeoHash(final Expression geohashStr, final Expression srid) {
        return FunctionUtils.twoArgFunc("ST_PointFromGeoHash", geohashStr, srid, ByteArrayType.INSTANCE);
    }

    /*-------------------below Functions That Create Geometry Values from WKT Values-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-geomcollfromtext">ST_GeomCollFromText(wkt [, srid [, options]])</a>
     */
    public static Expression stGeomCollFromText(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_GeomCollFromText", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-geomfromtext">ST_GeomFromText(wkt [, srid [, options]])</a>
     */
    public static Expression stGeomFromText(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_GeomFromText", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-linefromtext">ST_LineStringFromText(wkt [, srid [, options]])</a>
     */
    public static Expression stLineStringFromText(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_LineStringFromText", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mlinefromtext">ST_MultiLineStringFromText(wkt [, srid [, options]])</a>
     */
    public static Expression stMultiLineStringFromText(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_MultiLineStringFromText", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mpointfromtext">ST_MultiPointFromText(wkt [, srid [, options]])</a>
     */
    public static Expression stMultiPointFromText(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_MultiPointFromText", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-mpolyfromtext">ST_MultiPolygonFromText(wkt [, srid [, options]])</a>
     */
    public static Expression stMultiPolygonFromText(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_MultiPolygonFromText", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-pointfromtext">ST_PointFromText(wkt [, srid [, options]])</a>
     */
    public static Expression stPointFromText(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_PointFromText", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkt-functions.html#function_st-polyfromtext">ST_PolygonFromText(wkt [, srid [, options]])</a>
     */
    public static Expression stPolygonFromText(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_PolygonFromText", expList, ByteArrayType.INSTANCE);
    }

    /*-------------------below Functions That Create Geometry Values from WKB Values-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-geomcollfromwkb">ST_GeomCollFromWKB(wkb [, srid [, options]])</a>
     */
    public static Expression stGeomCollFromWKB(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_GeomCollFromWKB", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-geomfromwkb">ST_GeomFromWKB(wkb [, srid [, options]])</a>
     */
    public static Expression stGeomFromWKB(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_GeomFromWKB", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-linefromwkb">ST_LineStringFromWKB(wkb [, srid [, options]])</a>
     */
    public static Expression stLineStringFromWKB(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_LineStringFromWKB", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-mlinefromwkb">ST_MultiLineStringFromWKB(wkb [, srid [, options]])</a>
     */
    public static Expression stMultiLineStringFromWKB(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_MultiLineStringFromWKB", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-mpolyfromwkb">ST_MultiPolygonFromWKB(wkb [, srid [, options]])</a>
     */
    public static Expression stMultiPolygonFromWKB(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_MultiPolygonFromWKB", expList, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-pointfromwkb">ST_PointFromWKB(wkb [, srid [, options]])</a>
     */
    public static Expression stPointFromWKB(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_PointFromWKB", expList, ByteArrayType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param expList non-null,size in [1,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-wkb-functions.html#function_st-polyfromwkb">ST_PolygonFromWKB(wkb [, srid [, options]])</a>
     */
    public static Expression stPolygonFromWKB(final List<Expression> expList) {
        return _simpleMaxThreeArgFunc("ST_PolygonFromWKB", expList, ByteArrayType.INSTANCE);
    }

    /*-------------------below GeometryCollection Property Functions-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param gc non-null
     * @param n  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-geometrycollection-property-functions.html#function_st-geometryn">ST_GeometryN(gc, N)</a>
     */
    public static Expression stGeometryN(final Expression gc, final Expression n) {
        return FunctionUtils.twoArgFunc("ST_GeometryN", gc, n, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param gc non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-geometrycollection-property-functions.html#function_st-numgeometries">ST_NumGeometries(gc)</a>
     */
    public static Expression stNumGeometries(final Expression gc) {
        return FunctionUtils.oneArgFunc("ST_NumGeometries", gc, IntegerType.INSTANCE);
    }

    /*-------------------below General Geometry Property Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-dimension">ST_Dimension(g)</a>
     */
    public static Expression stDimension(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_Dimension", g, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-envelope">ST_Envelope(g)</a>
     */
    public static Expression stEnvelope(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_Envelope", g, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-geometrytype">ST_GeometryType(g)</a>
     */
    public static Expression stGeometryType(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_GeometryType", g, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-isempty">ST_IsEmpty(g)</a>
     */
    public static Expression stIsEmpty(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_IsEmpty", g, BooleanType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-issimple">ST_IsSimple(g)</a>
     */
    public static Expression stIsSimple(final Expression g) {
        return FunctionUtils.oneArgFunc("ST_IsSimple", g, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-srid">ST_SRID(g [, srid])</a>
     */
    public static Expression stSRID(final Expression p) {
        return FunctionUtils.oneArgFunc("ST_SRID", p, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-general-property-functions.html#function_st-srid">ST_SRID(g [, srid])</a>
     */
    public static Expression stSRID(final Expression p, final Expression srid) {
        return FunctionUtils.twoArgFunc("ST_SRID", p, srid, ByteArrayType.INSTANCE);
    }

    /*-------------------below Point Property Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-latitude">ST_Latitude(p [, new_latitude_val])</a>
     */
    public static Expression stLatitude(final Expression p) {
        return FunctionUtils.oneArgFunc("ST_Latitude", p, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param p              non-null
     * @param newLatitudeVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-latitude">ST_Latitude(p [, new_latitude_val])</a>
     */
    public static Expression stLatitude(final Expression p, final Expression newLatitudeVal) {
        return FunctionUtils.twoArgFunc("ST_Latitude", p, newLatitudeVal, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-longitude">ST_Longitude(p [, new_longitude_val])</a>
     */
    public static Expression stLongitude(final Expression p) {
        return FunctionUtils.oneArgFunc("ST_Longitude", p, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param p               non-null
     * @param newLongitudeVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-longitude">ST_Longitude(p [, new_longitude_val])</a>
     */
    public static Expression stLongitude(final Expression p, final Expression newLongitudeVal) {
        return FunctionUtils.twoArgFunc("ST_Longitude", p, newLongitudeVal, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-x">ST_X(p [, new_x_val])</a>
     */
    public static Expression stX(final Expression p) {
        return FunctionUtils.oneArgFunc("ST_X", p, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param p       non-null
     * @param newXVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-x">ST_X(p [, new_x_val])</a>
     */
    public static Expression stX(final Expression p, final Expression newXVal) {
        return FunctionUtils.twoArgFunc("ST_X", p, newXVal, ByteArrayType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param p non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-y">ST_Y(p [, new_y_val])</a>
     */
    public static Expression stY(final Expression p) {
        return FunctionUtils.oneArgFunc("ST_Y", p, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link ByteArrayType},Well-Known Binary (WKB) format
     * , not Internal Geometry Storage Format,that is converted by {@link io.army.stmt.Stmt} executor.
     * </p>
     *
     * @param p       non-null
     * @param newYVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-point-property-functions.html#function_st-y">ST_Y(p [, new_y_val])</a>
     */
    public static Expression stY(final Expression p, final Expression newYVal) {
        return FunctionUtils.twoArgFunc("ST_Y", p, newYVal, ByteArrayType.INSTANCE);
    }

    /*-------------------below Performance Schema Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param count non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/performance-schema-functions.html#function_format-bytes">FORMAT_BYTES(count)</a>
     */
    public static Expression formatBytes(final Expression count) {
        return FunctionUtils.oneArgFunc("FORMAT_BYTES", count, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param timeVal non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/performance-schema-functions.html#function_format-pico-time">FORMAT_PICO_TIME(time_val)</a>
     */
    public static Expression formatPicoTime(final Expression timeVal) {
        return FunctionUtils.oneArgFunc("FORMAT_PICO_TIME", timeVal, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/performance-schema-functions.html#function_ps-current-thread-id">PS_CURRENT_THREAD_ID()</a>
     */
    public static Expression psCurrentThreadId() {
        return FunctionUtils.noArgFunc("PS_CURRENT_THREAD_ID", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @param connectionId non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/performance-schema-functions.html#function_ps-thread-id">PS_THREAD_ID(connection_id)</a>
     */
    public static Expression psThreadId(final Expression connectionId) {
        return FunctionUtils.oneArgFunc("PS_THREAD_ID", connectionId, LongType.INSTANCE);
    }


    /*-------------------below Functions Used with Global Transaction Identifiers-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param set1 non-null
     * @param set2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_gtid-subset">GTID_SUBSET(set1,set2)</a>
     */
    public static IPredicate gtidSubset(final Expression set1, final Expression set2) {
        return FunctionUtils.twoArgPredicateFunc("GTID_SUBSET", set1, set2);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:the {@link MappingType} set1
     * </p>
     *
     * @param set1 non-null
     * @param set2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_gtid-subtract">GTID_SUBTRACT(set1,set2)</a>
     */
    public static Expression gtidSubtract(final Expression set1, final Expression set2) {
        return FunctionUtils.twoArgFunc("GTID_SUBTRACT", set1, set2, set1.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param gtidSet non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_wait-for-executed-gtid-set">WAIT_FOR_EXECUTED_GTID_SET(gtid_set[, timeout])</a>
     */
    public static Expression waitForExecutedGtidSet(final Expression gtidSet) {
        return FunctionUtils.oneArgFunc("WAIT_FOR_EXECUTED_GTID_SET", gtidSet, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param gtidSet non-null
     * @param timeout non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_wait-for-executed-gtid-set">WAIT_FOR_EXECUTED_GTID_SET(gtid_set[, timeout])</a>
     */
    public static Expression waitForExecutedGtidSet(final Expression gtidSet, final Expression timeout) {
        return FunctionUtils.twoArgFunc("WAIT_FOR_EXECUTED_GTID_SET", gtidSet, timeout, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param gtidSet non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_wait-for-executed-gtid-set">WAIT_UNTIL_SQL_THREAD_AFTER_GTIDS(gtid_set[, timeout][,channel])</a>
     */
    static Expression waitUntilSqlThreadAfterGtids(final Expression gtidSet) {
        return FunctionUtils.oneArgFunc("WAIT_UNTIL_SQL_THREAD_AFTER_GTIDS", gtidSet, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link IntegerType}
     * </p>
     *
     * @param gtidSet non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gtid-functions.html#function_wait-for-executed-gtid-set">WAIT_UNTIL_SQL_THREAD_AFTER_GTIDS(gtid_set[, timeout][,channel])</a>
     */
    static Expression waitUntilSqlThreadAfterGtids(Expression gtidSet, Expression timeout) {
        return FunctionUtils.twoArgFunc("WAIT_UNTIL_SQL_THREAD_AFTER_GTIDS", gtidSet, timeout, IntegerType.INSTANCE);
    }


    /*-------------------below private method -------------------*/


    /**
     * @see #bitAnd(Expression, Function, Function)
     * @see #bitOr(Expression, Function, Function)
     * @see #bitOr(Expression, Function, Function)
     */
    static MappingType _bitwiseFuncReturnType(final Expression expr) {
        final MappingType returnType;

        final TypeMeta paramMeta = expr.typeMeta();
        if (paramMeta instanceof TypeMeta.Delay) {
            returnType = StringType.INSTANCE; //unknown,compatibility
        } else if (!(paramMeta.mappingType() instanceof StringType)) {
            returnType = LongType.INSTANCE;
        } else if (!(expr instanceof SqlValueParam.SingleNonNamedValue)) {
            returnType = StringType.INSTANCE; //unknown,compatibility
        } else {
            final Object value;
            value = ((SqlValueParam.SingleNonNamedValue) expr).value();
            if (value instanceof String && _StringUtils.isBinary((String) value)) {
                returnType = StringType.INSTANCE;
            } else {
                returnType = LongType.INSTANCE;
            }
        }
        return returnType;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param name     MIN or MAX
     * @param distinct null or {@link  StandardSyntax.WordAll#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #min(Expression)
     * @see #min(StandardSyntax.WordAll, Expression)
     * @see #max(Expression)
     * @see #max(StandardSyntax.WordAll, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _distinctOneAggregateWindow(final String name, final @Nullable StandardSyntax.WordAll distinct
            , final Expression expr, final TypeMeta returnType) {
        final _AggregateOverSpec func;
        if (distinct == null) {
            func = MySQLFunctionUtils.oneArgAggregateWindow(name, expr, returnType);
        } else if (distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(name, distinct);
        } else if (expr instanceof SqlValueParam.MultiValue) {
            throw CriteriaUtils.funcArgError(name, expr);
        } else {
            final List<Object> argList = new ArrayList<>(2);
            argList.add(distinct);
            argList.add(expr);
            func = MySQLFunctionUtils.complexAggregateWindow(name, argList, returnType);
        }
        return func;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param funcName   LAG or LEAD
     * @param expr       non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @param n          nullable,probably is below:
     *                   <ul>
     *                       <li>null</li>
     *                       <li>{@link Long} type</li>
     *                       <li>{@link Integer} type</li>
     *                       <li>{@link SQLs#paramFrom(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                       <li>{@link SQLs#literalFrom(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                   </ul>
     * @param useDefault if n is non-nul and useDefault is true,output sql key word {@code DEFAULT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    private static _OverSpec _lagOrLead(final String funcName, final Object expr
            , final @Nullable Object n, final boolean useDefault) {

        assert funcName.equals("LAG") || funcName.equals("LEAD");

        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expr == SQLs.NULL) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }

        final ArmyExpression nExp;
        final TypeMeta nType;
        if (n == null) {
            nExp = null;
            nType = null;
        } else {
            nExp = SQLs._funcParam(n);
            nType = nExp.typeMeta();
        }

        final _OverSpec overSpec;
        if (nExp == null) {
            overSpec = MySQLFunctionUtils.oneArgWindowFunc(funcName, null, expression, expression.typeMeta());
        } else if (!(nExp instanceof ParamExpression.SingleParamExpression
                || nExp instanceof LiteralExpression.SingleLiteral)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        } else if (nExp.isNullValue()) {
            throw CriteriaUtils.funcArgError(funcName, n);
        } else if (!(nType instanceof LongType || nType instanceof IntegerType)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        } else if (useDefault) {
            final List<ArmyExpression> argList;
            argList = Arrays.asList(expression, nExp, (ArmyExpression) SQLs.defaultWord());
            overSpec = MySQLFunctionUtils.safeMultiArgWindowFunc(funcName, null, argList, expression.typeMeta());
        } else {
            final List<ArmyExpression> argList;
            argList = Arrays.asList(expression, nExp);
            overSpec = MySQLFunctionUtils.safeMultiArgWindowFunc(funcName, null, argList, expression.typeMeta());
        }
        return overSpec;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @see #firstValue(Expression)
     * @see #lastValue(Expression)
     */
    private static _OverSpec _nonNullArgWindowFunc(final String funcName, final Object expr) {
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expr == SQLs.NULL) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        return MySQLFunctionUtils.oneArgWindowFunc(funcName, null, expression, expression.typeMeta());
    }


    /**
     * @see #groupConcat(Object)
     * @see #groupConcat(StandardSyntax.WordAll, Object)
     */
    private static Expression _groupConcat(@Nullable StandardSyntax.WordAll distinct, @Nullable Object expressions
            , @Nullable Clause clause) {

        final String funcName = "GROUP_CONCAT";

        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        if (clause != null && !(clause instanceof MySQLFunctionUtils.GroupConcatClause)) {
            throw CriteriaUtils.funcArgError(funcName, clause);
        }
        final Expression func;
        if (expressions instanceof List) {
            func = FunctionUtils.multiArgOptionFunc(funcName, distinct, (List<?>) expressions
                    , clause, StringType.INSTANCE);
        } else {
            func = FunctionUtils.oneArgOptionFunc(funcName, distinct, expressions, clause, StringType.INSTANCE);
        }
        return func;
    }

    /**
     * @see #aesEncrypt(Expression, Expression, List)
     * @see #aesDecrypt(Expression, Expression, List)
     */
    private static Expression _aesEncryptOrDecrypt(final String funcName, final Expression str, final Expression keyStr
            , final List<Expression> argExpList) {
        if (argExpList.size() > 4) {
            throw CriteriaUtils.funcArgError(funcName, argExpList);
        }
        final List<Object> argList = new ArrayList<>();

        argList.add(str);
        argList.add(FunctionUtils.FuncWord.COMMA);
        argList.add(keyStr);

        for (Expression argExp : argExpList) {
            argList.add(FunctionUtils.FuncWord.COMMA);
            argList.add(argExp);
        }
        return FunctionUtils.complexArgFunc(funcName, argList, StringType.INSTANCE);
    }


    /**
     * @see #geometryCollection(List)
     * @see #lineString(List)
     */
    private static Expression _geometryTypeFunc(final String name, final List<Expression> geometryList) {
        final Expression func;
        final int geometrySize = geometryList.size();
        switch (geometrySize) {
            case 0:
                func = FunctionUtils.noArgFunc(name, ByteArrayType.INSTANCE);
                break;
            case 1:
                func = FunctionUtils.oneArgFunc(name, geometryList.get(0), ByteArrayType.INSTANCE);
                break;
            default:
                func = FunctionUtils.complexArgFunc(name, _createSimpleMultiArgList(geometryList)
                        , ByteArrayType.INSTANCE);

        }
        return func;
    }

    /**
     * @see #lag(Expression, Expression, SQLs.WordDefault, Function, Function)
     * @see #lead(Expression, Expression, SQLs.WordDefault, Function, Function)
     */
    private static <R extends Expression, I extends Item> _OverSpec<R, I> leadOrLog(
            final String name, final @Nullable Expression expr
            , final @Nullable Expression n, final @Nullable SQLs.WordDefault defaultWord
            , Function<_ItemExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        if (expr == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        assert defaultWord == null || defaultWord == SQLs.DEFAULT;
        final _OverSpec<R, I> spec;
        if (n == null) {
            spec = MySQLFunctionUtils.oneArgWindowFunc(name, expr, expr.typeMeta(), endFunction, asFunction);
        } else if (defaultWord == null) {
            spec = MySQLFunctionUtils.twoArgWindowFunc(name, expr, n, expr.typeMeta(), endFunction, asFunction);
        } else {
            spec = MySQLFunctionUtils.threeArgWindow(name, expr, n, defaultWord, expr.typeMeta(), endFunction, asFunction);
        }
        return spec;
    }


}
