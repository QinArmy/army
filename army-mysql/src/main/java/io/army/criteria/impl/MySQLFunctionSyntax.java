package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TypeInfer;
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
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * <p>
 * package class,this class is base class of {@link MySQLSyntax}
 * </p>
 *
 * @since 1.0
 */
abstract class MySQLFunctionSyntax extends SQLSyntax {

    MySQLFunctionSyntax() {
    }


    public interface _OverSpec extends Window._OverWindowClause {

        Expression over(Consumer<Window._SimplePartitionBySpec> consumer);

        Expression over(@Nullable String windowName, Consumer<Window._SimplePartitionBySpec> consumer);

    }

    public interface _AggregateWindowFunc extends _OverSpec, SQLFunction.AggregateFunction, Expression {

    }

    public interface _ItemAggregateWindowFunc extends _AggregateWindowFunc, _AliasExpression {

    }


    public interface _NullTreatmentOverSpec extends Functions._NullTreatmentClause<_OverSpec>, _OverSpec {


    }

    public interface _FromFirstLastOverSpec extends Functions._FromFirstLastClause<_NullTreatmentOverSpec>,
            _NullTreatmentOverSpec, SQLFunction._OuterClauseBeforeOver {

    }




    /*-------------------below MySQL Aggregate Function Descriptions -------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> avg(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("AVG", exp, DoubleType.INSTANCE, expFunc, endFunc);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param distinct nullable,{@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> avg(
            @Nullable SQLSyntax.ArgDistinct distinct, Expression exp
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("AVG", distinct, exp, DoubleType.INSTANCE, expFunc, endFunc);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> bitAnd(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("BIT_AND", exp, _bitwiseFuncType(exp), expFunc, endFunc);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> bitOr(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("BIT_OR", exp, _bitwiseFuncType(exp), expFunc, endFunc);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> bitXor(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("BIT_XOR", exp, _bitwiseFuncType(exp), expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> countStar(
            Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return count(SQLs._START_EXP, expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> count(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("COUNT", exp, LongType.INSTANCE, expFunc, endFunc);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonListType}
     * </p>
     *
     * @param exp parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> jsonArrayAgg(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        final TypeMeta returnType;//TODO validate JsonListType
        returnType = Functions._returnType((ArmyExpression) exp, JsonListType::from);
        return MySQLFunctionUtils.oneArgAggregate("JSON_ARRAYAGG", exp, returnType, expFunc, endFunc);
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
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> jsonObjectAgg(
            Expression key, Expression value
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        final TypeMeta returnType;//TODO validate JsonMapType
        returnType = Functions._returnType((ArmyExpression) key, (ArmyExpression) value, JsonMapType::from);
        final List<Expression> argList;
        argList = Arrays.asList(key, value);
        final String name = "JSON_OBJECTAGG";
        return MySQLFunctionUtils.multiArgAggregateWindowFunc(name, null, argList, returnType, expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> max(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("MAX", exp, exp.typeMeta(), expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> max(
            @Nullable SQLSyntax.ArgDistinct distinct, Expression exp
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("MAX", distinct, exp, exp.typeMeta(), expFunc, endFunc);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression},but couldn't be {@link SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> min(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("MIN", exp, exp.typeMeta(), expFunc, endFunc);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression},but couldn't be {@link SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> min(
            @Nullable SQLSyntax.ArgDistinct distinct, Expression exp
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("MIN", distinct, exp, exp.typeMeta(), expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_std">STD(xpr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> std(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("STD", exp, DoubleType.INSTANCE, expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev">STDDEV(xpr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> stdDev(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("STDDEV", exp, DoubleType.INSTANCE, expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-pop">STDDEV_POP(xpr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> stdDevPop(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("STDDEV_POP", exp, DoubleType.INSTANCE, expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-samp">STDDEV_SAMP(xpr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> stdDevSamp(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("STDDEV_SAMP", exp, DoubleType.INSTANCE, expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> sum(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("SUM", exp, exp.typeMeta(), expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> sum(
            @Nullable SQLSyntax.ArgDistinct distinct, Expression exp
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("SUM", distinct, exp, exp.typeMeta(), expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-pop">VAR_POP(xpr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> varPop(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("VAR_POP", exp, DoubleType.INSTANCE, expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VAR_SAMP(xpr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> varSamp(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("VAR_SAMP", exp, DoubleType.INSTANCE, expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VARIANCE(expr) [over_clause]</a>
     */
    public static <I extends Item, E extends Expression> _ItemAggregateWindowFunc<I, E> variance(
            Expression exp, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgAggregate("VARIANCE", exp, DoubleType.INSTANCE, expFunc, endFunc);
    }


    /*-------------------below window function-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_cume-dist">CUME_DIST() over_clause</a>
     */
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> cumeDist(
            Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.noArgWindowFunc("CUME_DIST", DoubleType.INSTANCE, expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     */
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> denseRank(
            Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.noArgWindowFunc("DENSE_RANK", LongType.INSTANCE, expFunc, endFunc);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_first-value">FIRST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> firstValue(Expression expr
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.noArgWindowFunc("FIRST_VALUE", expr.typeMeta(), expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_last-value">LAST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> lastValue(Expression expr
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.noArgWindowFunc("LAST_VALUE", expr.typeMeta(), expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> lag(Expression expr
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgWindowFunc("LAG", expr, expr.typeMeta(), expFunc, endFunc);
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
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> lag(Expression expr
            , Expression n, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.twoArgWindowFunc("LAG", expr, n, expr.typeMeta(), expFunc, endFunc);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr         non-null parameter or {@link  Expression},but couldn't be {@link SQLs#NULL}
     * @param n            nullable,probably is below:
     *                     <ul>
     *                         <li>null</li>
     *                         <li>{@link Long} type</li>
     *                         <li>{@link Integer} type</li>
     *                         <li>{@link SQLs#paramFrom(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                         <li>{@link SQLs#literalFrom(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                     </ul>
     * @param defaultValue non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> lag(Expression expr
            , Expression n, Expression defaultValue
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.threeArgWindow("LAG", expr, n, defaultValue, expr.typeMeta(), expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> lead(Expression expr
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgWindowFunc("LEAD", expr, expr.typeMeta(), expFunc, endFunc);
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
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> lead(Expression expr
            , Expression n, Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.twoArgWindowFunc("LEAD", expr, n, expr.typeMeta(), expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr         non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#NULL}
     * @param n            nullable,probably is below:
     *                     <ul>
     *                         <li>null</li>
     *                         <li>{@link Long} type</li>
     *                         <li>{@link Integer} type</li>
     *                         <li>{@link SQLs#paramFrom(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                         <li>{@link SQLs#literalFrom(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                     </ul>
     * @param defaultValue non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> lead(Expression expr
            , Expression n, Expression defaultValue
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.threeArgWindow("LEAD", expr, n, defaultValue, expr.typeMeta(), expFunc, endFunc);
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
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._FromFirstLastOverSpec<E> nthValue(
            Expression expr, Expression n
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.twoArgFromFirstWindowFunc("NTH_VALUE", expr, n, expr.typeMeta(), expFunc, endFunc);
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
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> ntile(Expression n
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.oneArgWindowFunc("NTILE", n, LongType.INSTANCE, expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">PERCENT_RANK() over_clause</a>
     */
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> percentRank(
            Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.noArgWindowFunc("PERCENT_RANK", DoubleType.INSTANCE, expFunc, endFunc);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">RANK() over_clause</a>
     */
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> rank(
            Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.noArgWindowFunc("RANK", LongType.INSTANCE, expFunc, endFunc);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     */
    public static <I extends Item, E extends Expression> MySQLFunctionSyntax._OverSpec<E> rowNumber(
            Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        return MySQLFunctionUtils.noArgWindowFunc("ROW_NUMBER", LongType.INSTANCE, expFunc, endFunc);
    }


    /*-------------------below Flow Control Functions-------------------*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/flow-control-functions.html">Flow Control Functions</a>
     */
    public static <I extends Item, E extends Expression> SQLFunction._CaseFuncWhenClause<E> cases(Expression exp
            , Function<_ItemExpression<I>, E> expFunc, Function<TypeInfer, I> endFunc) {
        if (!(exp instanceof ArmyExpression)) {
            throw CriteriaUtils.funcArgError("CASE", exp);
        }
        return FunctionUtils.caseFunction(exp);
    }


    /*-------------------below private methos -------------------*/

    /**
     * @see #bitAnd(Expression, Function, Function)
     * @see #bitOr(Expression, Function, Function)
     * @see #bitXor(Expression, Function, Function)
     */
    private static MappingType _bitwiseFuncType(final Expression expr) {
        final MappingType returnType;

        final TypeMeta paramMeta = expr.typeMeta();
        if (paramMeta instanceof TypeMeta.Delay) {
            returnType = StringType.INSTANCE; //TODO optimize unknown,compatibility
        } else if (!(paramMeta.mappingType() instanceof StringType)) {
            returnType = LongType.INSTANCE;
        } else if (!(expr instanceof SqlValueParam.SingleNonNamedValue)) {
            returnType = StringType.INSTANCE; //ODO optimize unknown,compatibility
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
