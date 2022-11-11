package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.standard.SQLFunction;
import io.army.lang.Nullable;
import io.army.mapping.DoubleType;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.mapping.optional.JsonListType;
import io.army.mapping.optional.JsonMapType;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

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
            extends _OverSpec<R, I>, _AliasExpression<I>, SQLFunction.AggregateFunction {

    }

    public interface _NullTreatmentSpec<R extends Expression, I extends Item>
            extends Functions._NullTreatmentClause<_OverSpec<R, I>>, _OverSpec<R, I> {


    }

    public interface _FromFirstLastSpec<R extends Expression, I extends Item>
            extends Functions._FromFirstLastClause<_NullTreatmentSpec<R, I>>, _NullTreatmentSpec<R, I>
            , SQLFunction._OuterOptionalClause {

    }

    public static <I extends Item, E extends Expression> SQLFunction._CaseFuncWhenClause<E> Case(Expression exp
            , Function<_ItemExpression<I>, E> endFunc, Function<TypeInfer, I> asFunc) {
        if (!(exp instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError("CASE", exp);
        }
        return FunctionUtils.caseFunction(exp, endFunc, asFunc);
    }


    /*-------------------below Aggregate Function  -------------------*/

    /**
     * @param distinct nullable,{@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> avg(@Nullable SQLSyntax.ArgDistinct distinct
            , Expression exp, Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgOptionAggregateWindow("AVG", distinct, exp, DoubleType.INSTANCE, endFunction, asFunction);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> bitAnd(Expression exp
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("BIT_AND", exp, _bitwiseFuncType(exp), endFunction, asFunction);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> bitOr(Expression exp
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("BIT_OR", exp, _bitwiseFuncType(exp), endFunction, asFunction);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> bitXor(Expression exp
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("BIT_XOR", exp, _bitwiseFuncType(exp), endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> countStar(Function<_AliasExpression<I>, R> endFunction
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("COUNT", exp, LongType.INSTANCE, endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> count(@Nullable SQLSyntax.ArgDistinct distinct
            , List<Expression> argList, Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Expression value, Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> max(@Nullable ArgDistinct distinct
            , Expression expr, Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> min(@Nullable ArgDistinct distinct
            , Expression expr, Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
    public static <R extends Expression, I extends Item> _AggregateWindowFunc<R, I> sum(@Nullable ArgDistinct distinct
            , Expression expr, Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , final @Nullable SQLs.WordDefault defaultWord, Function<_AliasExpression<I>, R> endFunction
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            , Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
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
            Function<_AliasExpression<I>, R> endFunction, Function<Selection, I> asFunction) {
        return MySQLFunctionUtils.noArgWindowFunc("ROW_NUMBER", LongType.INSTANCE, endFunction, asFunction);
    }


    /*-------------------below private method-------------------*/

    /**
     * @see #bitAnd(Expression, Function, Function)
     * @see #bitOr(Expression, Function, Function)
     * @see #bitOr(Expression, Function, Function)
     */
    private static MappingType _bitwiseFuncType(final Expression expr) {
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


}
