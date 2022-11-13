package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.mysql.MySQLCastType;
import io.army.criteria.mysql.MySQLFunction;
import io.army.criteria.standard.SQLFunction;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.JsonListType;
import io.army.mapping.optional.JsonMapType;
import io.army.mapping.optional.JsonType;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unused")
abstract class MySQLFunctionSyntax extends MySQLSyntax {

    MySQLFunctionSyntax() {
    }


    public interface _OverSpec<R extends Item, I extends Item>
            extends Window._OverClause<Window._SimpleLeftParenClause<R>, R> {

    }

    public interface _AggregateWindowFunc<R extends Item, I extends Item>
            extends _OverSpec<R, I>, _AliasExpression<I>, SQLFunction.AggregateFunction {

    }

    public interface _NullTreatmentOverSpec<R extends Item, I extends Item>
            extends Functions._NullTreatmentClause<_OverSpec<R, I>>, _OverSpec<R, I> {


    }

    public interface _FromFirstLastOverSpec<R extends Item, I extends Item>
            extends Functions._FromFirstLastClause<_NullTreatmentOverSpec<R, I>>, _NullTreatmentOverSpec<R, I>
            , SQLFunction._OuterClauseBeforeOver {

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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> avg(@Nullable SQLSyntax.ArgDistinct distinct
            , Expression exp, Function<_ItemWindow<I>, W> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("AVG", distinct, exp, DoubleType.INSTANCE, expFunc, endFunc);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> bitAnd(Expression exp
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("BIT_AND", exp, _bitwiseFuncType(exp), endFunction, asFunction);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> bitOr(Expression exp
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("BIT_OR", exp, _bitwiseFuncType(exp), endFunction, asFunction);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> bitXor(Expression exp
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("BIT_XOR", exp, _bitwiseFuncType(exp), endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> countStar(Function<_ItemWindow<I>, W> endFunction
            , Function<TypeInfer, I> asFunction) {
        return count(SQLs._START_EXP, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> count(Expression exp
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregateWindow("COUNT", exp, LongType.INSTANCE, endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> count(@Nullable SQLSyntax.ArgDistinct distinct
            , List<Expression> argList, Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> jsonArrayAgg(final Expression exp
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> jsonObjectAgg(final Expression key
            , Expression value, Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> max(@Nullable ArgDistinct distinct
            , Expression expr, Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> min(@Nullable ArgDistinct distinct
            , Expression expr, Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> std(Expression expr
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> stdDev(Expression expr
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> stdDevPop(Expression expr
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> stdDevSamp(Expression expr
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> sum(@Nullable ArgDistinct distinct
            , Expression expr, Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        return MySQLFunctionUtils.oneArgAggregate("SUM", distinct, expr, expr.typeMeta(), endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-pop">VAR_POP(xpr) [over_clause]</a>
     */
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> varPop(Expression expr
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> varSamp(Expression expr
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _AggregateWindowFunc<W, I> variance(Expression expr
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _OverSpec<W, I> cumeDist(
            Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        return MySQLFunctionUtils.noArgWindowFunc("CUME_DIST", DoubleType.INSTANCE, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     */
    public static <W extends Item, I extends Item> _OverSpec<W, I> denseRank(
            Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _OverSpec<W, I> firstValue(final Expression expr
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        return MySQLFunctionUtils.oneArgWindowFunc("FIRST_VALUE", expr, expr.typeMeta(), endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_last-value">LAST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static <W extends Item, I extends Item> _OverSpec<W, I> lastValue(final Expression expr
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _OverSpec<W, I> lag(final Expression expr
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        return leadOrLog("LAG", expr, null, null, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
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
    public static <W extends Item, I extends Item> _OverSpec<W, I> lag(Expression expr, @Nullable Expression n
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _OverSpec<W, I> lag(final Expression expr
            , final @Nullable Expression n, final @Nullable SQLs.WordDefault defaultWord
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _OverSpec<W, I> lead(final Expression expr
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _OverSpec<W, I> lead(Expression expr, @Nullable Expression n
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _OverSpec<W, I> lead(Expression expr, @Nullable Expression n
            , final @Nullable SQLs.WordDefault defaultWord, Function<_ItemWindow<I>, W> endFunction
            , Function<TypeInfer, I> asFunction) {
        return leadOrLog("LEAD", expr, n, defaultWord, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null {@link  Expression}
     * @param n    positive.output literal.
     * @param <W>  representing {@link _ItemWindow} or supper
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_nth-value">NTH_VALUE(expr, N) [from_first_last] [null_treatment] over_clause</a>
     */
    public static <W extends Item, I extends Item> _FromFirstLastOverSpec<W, I> nthValue(Expression expr, Expression n
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _OverSpec<W, I> ntile(final Expression n
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
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
    public static <W extends Item, I extends Item> _OverSpec<W, I> percentRank(
            Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        return MySQLFunctionUtils.noArgWindowFunc("PERCENT_RANK", DoubleType.INSTANCE, endFunction, asFunction);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">RANK() over_clause</a>
     */
    public static <W extends Item, I extends Item> _OverSpec<W, I> rank(
            Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        return MySQLFunctionUtils.noArgWindowFunc("RANK", LongType.INSTANCE, endFunction, asFunction);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     */
    public static <W extends Item, I extends Item> _OverSpec<W, I> rowNumber(
            Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        return MySQLFunctionUtils.noArgWindowFunc("ROW_NUMBER", LongType.INSTANCE, endFunction, asFunction);
    }

    /*-------------------below JSON function -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:
     *      <ul>
     *          <li>If don't specified RETURNING clause then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#BINARY }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#CHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#NCHAR }then {@link StringType}</li>
     *          <li>Else if type is {@link MySQLCastType#TIME }then {@link LocalTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATE }then {@link LocalDateType}</li>
     *          <li>Else if type is {@link MySQLCastType#YEAR }then {@link YearType}</li>
     *          <li>Else if type is {@link MySQLCastType#DATETIME }then {@link LocalDateTimeType}</li>
     *          <li>Else if type is {@link MySQLCastType#SIGNED_INTEGER }then {@link LongType}</li>
     *          <li>Else if type is {@link MySQLCastType#UNSIGNED_INTEGER }then {@link UnsignedBigIntegerType}</li>
     *          <li>Else if type is {@link MySQLCastType#DECIMAL }then {@link BigDecimalType}</li>
     *          <li>Else if type is {@link MySQLCastType#FLOAT }then {@link FloatType}</li>
     *          <li>Else if type is {@link MySQLCastType#REAL }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#DOUBLE }then {@link DoubleType}</li>
     *          <li>Else if type is {@link MySQLCastType#JSON }then {@link JsonType}</li>
     *          <li>Else if type is {@link MySQLCastType#Point }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPoint }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiLineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#LineString }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#Polygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#MultiPolygon }then {@link ByteArrayType}</li>
     *          <li>Else if type is {@link MySQLCastType#GeometryCollection }then {@link ByteArrayType}</li>
     *      </ul>
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/json-search-functions.html#function_json-value">JSON_VALUE(json_doc, path)</a>
     */
    public static <E extends Expression, I extends Item> MySQLFunction._JsonValueLeftParenClause<E> jsonValue(
            Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.jsonValueFunc(expFunc, endFunc);
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

    /**
     * @param <W> representing {@link _ItemWindow} or supper
     * @see #lag(Expression, Expression, SQLs.WordDefault, Function, Function)
     * @see #lead(Expression, Expression, SQLs.WordDefault, Function, Function)
     */
    private static <W extends Item, I extends Item> MySQLFunctionSyntax._OverSpec<W, I> leadOrLog(
            final String name, final @Nullable Expression expr
            , final @Nullable Expression n, final @Nullable SQLs.WordDefault defaultWord
            , Function<_ItemWindow<I>, W> endFunction, Function<TypeInfer, I> asFunction) {
        if (expr == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        assert defaultWord == null || defaultWord == SQLs.DEFAULT;
        final MySQLFunctionSyntax._OverSpec<W, I> spec;
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