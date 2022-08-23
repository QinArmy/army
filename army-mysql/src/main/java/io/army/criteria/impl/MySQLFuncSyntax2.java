package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.MySQLClause;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.JsonBeanType;
import io.army.mapping.optional.JsonListType;
import io.army.mapping.optional.JsonMapType;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

@SuppressWarnings("unused")
abstract class MySQLFuncSyntax2 extends MySQLFuncSyntax {

    MySQLFuncSyntax2() {
    }



    /*-------------------below Aggregate Function  -------------------*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec avg(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("AVG", null, expr, DoubleType.INSTANCE);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static Expression avg(@Nullable SQLs.Modifier distinct, @Nullable Object exp) {
        final String funcName = "AVG";
        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        final Expression func;
        if (distinct == null) {
            func = SQLFunctions.oneArgFunc(funcName, exp, DoubleType.INSTANCE);
        } else {
            final List<Object> argList = new ArrayList<>(3);
            argList.add(distinct);
            argList.add(SQLFunctions.FuncWord.COMMA);
            argList.add(SQLs._funcParam(exp));
            func = SQLFunctions.safeComplexArgFunc(funcName, argList, DoubleType.INSTANCE);
        }
        return func;
    }

    /**
     * @see #bitAnd(Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitAnd(final @Nullable Object expr) {
        return bitAnd(expr, _bitwiseFuncReturnType(expr));
    }

    /**
     * @see #bitAnd(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitAnd(@Nullable Object expr, MappingType mappingType) {
        return MySQLFunctions.aggregateWindowFunc("BIT_AND", null, expr, mappingType);
    }


    /**
     * @see #bitOr(Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitOr(final @Nullable Object expr) {
        return bitOr(expr, _bitwiseFuncReturnType(expr));
    }

    /**
     * @see #bitOr(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitOr(@Nullable Object expr, MappingType mappingType) {
        return MySQLFunctions.aggregateWindowFunc("BIT_OR", null, expr, mappingType);
    }

    /**
     * @see #bitXor(Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitXor(final @Nullable Object expr) {
        return bitXor(expr, _bitwiseFuncReturnType(expr));
    }

    /**
     * @see #bitXor(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitXor(@Nullable Object expr, MappingType mappingType) {
        return MySQLFunctions.aggregateWindowFunc("BIT_XOR", null, expr, mappingType);
    }

    /**
     * @see #count(Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count() {
        return _count(null, SQLs.star());
    }

    /**
     * @see #count()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count(@Nullable Expression expr) {
        return _count(null, expr);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count(SQLs.Modifier distinct, @Nullable Expression expr) {
        Objects.requireNonNull(distinct);
        return _count(distinct, expr);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count(SQLs.Modifier distinct, List<Expression> list) {
        Objects.requireNonNull(distinct);
        return _count(distinct, list);
    }


    /**
     * @see #groupConcat(SQLs.Modifier, Object, Supplier)
     * @see <a href="">COUNT(expr) [over_clause]</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static MySQLClause._GroupConcatOrderBySpec groupConcatClause() {
        return MySQLFunctions.groupConcatClause();
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param expressions parameter or {@link Expression} or List(element:null or parameter or {@link Expression})
     * @see #groupConcat(SQLs.Modifier, Object)
     * @see #groupConcat(SQLs.Modifier, Object, Supplier)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static Expression groupConcat(@Nullable Object expressions) {
        return _groupConcat(null, expressions, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param distinct    null or {@link  SQLs.Modifier#DISTINCT}
     * @param expressions parameter or {@link Expression} or List(element:null or parameter or {@link Expression})
     * @see #groupConcat(Object)
     * @see #groupConcat(SQLs.Modifier, Object, Supplier)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static Expression groupConcat(@Nullable SQLs.Modifier distinct, @Nullable Object expressions) {
        return _groupConcat(distinct, expressions, (Clause) null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param distinct    null or {@link  SQLs.Modifier#DISTINCT}
     * @param expressions parameter or {@link Expression} or List(element:null or parameter or {@link Expression})
     * @param supplier    supplier of {@link  #groupConcatClause()},allow to return null
     * @see #groupConcat(Object)
     * @see #groupConcat(SQLs.Modifier, Object)
     * @see #groupConcatClause()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static Expression groupConcat(@Nullable SQLs.Modifier distinct, @Nullable Object expressions, Supplier<Clause> supplier) {
        return _groupConcat(distinct, expressions, supplier.get());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonListType}
     * </p>
     *
     * @param expr parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #jsonArrayAgg(Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    public static _AggregateOverSpec jsonArrayAgg(final Object expr) {
        return _jsonArrayAgg(expr, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonListType}
     * </p>
     *
     * @param expr parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #jsonArrayAgg(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    public static _AggregateOverSpec jsonArrayAgg(final Object expr, final MappingType returnType) {
        return _jsonArrayAgg(expr, returnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonMapType}
     * </p>
     *
     * @param key   non-null parameter or {@link Expression},but couldn't be null.
     * @param value non-null parameter or {@link Expression},but couldn't be null.
     * @see #jsonObjectAgg(Object, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-objectagg">JSON_OBJECTAGG(key, value) [over_clause]</a>
     */
    public static _AggregateOverSpec jsonObjectAgg(final Object key, final Object value) {
        return _jsonObjectAgg(key, value, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: returnType
     * </p>
     *
     * @param key        non-null parameter or {@link Expression},but couldn't be null.
     * @param value      non-null parameter or {@link Expression},but couldn't be null.
     * @param returnType function return type,should prefer {@link JsonBeanType} and {@link  JsonMapType}
     * @see #jsonObjectAgg(Object, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-objectagg">JSON_OBJECTAGG(key, value) [over_clause]</a>
     */
    public static _AggregateOverSpec jsonObjectAgg(final Object key, final Object value, MappingType returnType) {
        return _jsonObjectAgg(key, value, returnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #max(SQLs.Modifier, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec max(final Object expr) {
        return _minOrMax("MAX", null, expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param distinct null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #max(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec max(final @Nullable SQLs.Modifier distinct, final Object expr) {
        return _minOrMax("MAX", distinct, expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #min(SQLs.Modifier, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec min(final Object expr) {
        return _minOrMax("MIN", null, expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param distinct null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #min(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec min(final @Nullable SQLs.Modifier distinct, final Object expr) {
        return _minOrMax("MIN", distinct, expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_std">STD(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec std(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("STD", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev">STDDEV(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec stdDev(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("STDDEV", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-pop">STDDEV_POP(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec stdDevPop(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("STDDEV_POP", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-samp">STDDEV_SAMP(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec stdDevSamp(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("STDDEV_SAMP", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #sum(SQLs.Modifier, Object)
     * @see #sum(SQLs.Modifier, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec sum(Object expr) {
        return _sum(null, expr, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param distinct null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #sum(Object)
     * @see #sum(SQLs.Modifier, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec sum(@Nullable SQLs.Modifier distinct, Object expr) {
        return _sum(distinct, expr, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: returnType or the {@link MappingType} of expr.
     * </p>
     *
     * @param distinct   null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr       non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @param returnType nullable,if null ,then {@link MappingType} of function return type is the {@link MappingType} of expr
     * @see #sum(Object)
     * @see #sum(SQLs.Modifier, Object)
     * @see #sum(SQLs.Modifier, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec sum(@Nullable SQLs.Modifier distinct, Object expr
            , @Nullable MappingType returnType) {
        return _sum(distinct, expr, returnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-pop">VAR_POP(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec varPop(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("VAR_POP", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VAR_SAMP(xpr) [over_clause]</a>
     */
    public static _AggregateOverSpec varSamp(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("VAR_SAMP", null, expr, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param expr null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VARIANCE(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec variance(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("VARIANCE", null, expr, DoubleType.INSTANCE);
    }

    /*-------------------below window function -------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_cume-dist">CUME_DIST() over_clause</a>
     */
    public static _OverSpec cumeDist() {
        return MySQLFunctions.noArgWindowFunc("CUME_DIST", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     */
    public static _OverSpec denseRank() {
        return MySQLFunctions.noArgWindowFunc("DENSE_RANK", LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_first-value">FIRST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static _OverSpec firstValue(final Object expr) {
        return _nonNullArgWindowFunc("FIRST_VALUE", expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_last-value">LAST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static _OverSpec lastValue(final Object expr) {
        return _nonNullArgWindowFunc("LAST_VALUE", expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @see #lag(Object, Object, boolean)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(final Object expr) {
        return _lagOrLead("LAG", expr, null, false);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr       non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @param n          nullable,probably is below:
     *                   <ul>
     *                       <li>null</li>
     *                       <li>{@link Long} type</li>
     *                       <li>{@link Integer} type</li>
     *                       <li>{@link SQLs#param(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                       <li>{@link SQLs#literal(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                   </ul>
     * @param useDefault if n is non-nul and useDefault is true,output sql key word {@code DEFAULT}
     * @see #lag(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(final Object expr, final @Nullable Object n, final boolean useDefault) {
        return _lagOrLead("LAG", expr, n, useDefault);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @see #lag(Object, Object, boolean)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(final Object expr) {
        return _lagOrLead("LEAD", expr, null, false);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr       non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#nullWord()}
     * @param n          nullable,probably is below:
     *                   <ul>
     *                       <li>null</li>
     *                       <li>{@link Long} type</li>
     *                       <li>{@link Integer} type</li>
     *                       <li>{@link SQLs#param(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                       <li>{@link SQLs#literal(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                   </ul>
     * @param useDefault if n is non-nul and useDefault is true,output sql key word {@code DEFAULT}
     * @see #lag(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(final Object expr, final @Nullable Object n, final boolean useDefault) {
        return _lagOrLead("LEAD", expr, n, useDefault);
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
    public static _FromFirstLastSpec nthValue(final Expression expr, final long n) {

        final String funcName = "NTH_VALUE";

        if (expr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        } else if (n < 1L) {
            throw CriteriaUtils.funcArgError(funcName, n);
        }
        final List<ArmyExpression> argList;
        argList = Arrays.asList((ArmyExpression) expr, (ArmyExpression) SQLs.literal(LongType.INSTANCE, n));
        return MySQLFunctions.safeMultiArgFromFirstWindowFunc(funcName, null, argList, expr.typeMeta());
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
     *               <li>positive number parameter {@link  Expression},eg:{@link SQLs#param(Object)}</li>
     *               <li>positive number literal {@link  Expression},eg:{@link SQLs#literal(Object)}</li>
     *               <li>variable {@link  Expression}</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_ntile">NTILE(N) over_clause</a>
     */
    public static _OverSpec ntile(final Object n) {
        //TODO a local variable in a stored routine?
        final String funcName = "NTILE";
        if (n instanceof Long) {
            if (((Long) n) < 1L) {
                throw CriteriaUtils.funcArgError(funcName, n);
            }
        } else if (n instanceof Number) {
            if (!(n instanceof Integer || n instanceof Short || n instanceof Byte)) {
                throw CriteriaUtils.funcArgError(funcName, n);
            }
            if (((Number) n).intValue() < 1) {
                throw CriteriaUtils.funcArgError(funcName, n);
            }
        } else if (!(n instanceof Expression)) {
            throw CriteriaUtils.funcArgError(funcName, n);
        }

        final ArmyExpression expression;
        expression = SQLs._funcParam(n);
        if (expression instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, n);
        }
        return MySQLFunctions.oneArgWindowFunc(funcName, null, expression, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">PERCENT_RANK() over_clause</a>
     */
    public static _OverSpec percentRank() {
        return MySQLFunctions.noArgWindowFunc("PERCENT_RANK", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">RANK() over_clause</a>
     */
    public static _OverSpec rank() {
        return MySQLFunctions.noArgWindowFunc("RANK", LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     */
    public static _OverSpec rowNumber() {
        return MySQLFunctions.noArgWindowFunc("ROW_NUMBER", LongType.INSTANCE);
    }


    /*-------------------below XML Functions-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param xmlFrag   non-null
     * @param xpathExpr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xml-functions.html#function_extractvalue">ExtractValue(xml_frag, xpath_expr)</a>
     */
    public static Expression extractValue(final Expression xmlFrag, final Expression xpathExpr) {
        CriteriaContextStack.assertNonNull(xmlFrag);
        CriteriaContextStack.assertNonNull(xpathExpr);

        final List<Object> argList = new ArrayList<>(3);
        argList.add(xmlFrag);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(xpathExpr);
        return SQLFunctions.safeComplexArgFunc("ExtractValue", argList, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param xmlTarget non-null
     * @param xpathExpr non-null
     * @param newXml    non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/xml-functions.html#function_updatexml">UpdateXML(xml_target, xpath_expr, new_xml)</a>
     */
    public static Expression updateXml(final Expression xmlTarget, final Expression xpathExpr, final Expression newXml) {
        CriteriaContextStack.assertNonNull(xmlTarget);
        CriteriaContextStack.assertNonNull(xpathExpr);
        CriteriaContextStack.assertNonNull(newXml);

        final List<Object> argList = new ArrayList<>(5);

        argList.add(xmlTarget);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(xpathExpr);
        argList.add(SQLFunctions.FuncWord.COMMA);

        argList.add(newXml);
        return SQLFunctions.safeComplexArgFunc("UpdateXML", argList, StringType.INSTANCE);
    }

    /*-------------------below Encryption and Compression Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param cryptStr   non-null
     * @param keyStr     non-null
     * @param argExpList non-null,size in [0,4]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_aes-decrypt">AES_DECRYPT(crypt_str,key_str[,init_vector][,kdf_name][,salt][,info | iterations])</a>
     */
    public static Expression aesDecrypt(final Expression cryptStr, final Expression keyStr
            , final List<Expression> argExpList) {
        return _aesEncryptOrDecrypt("AES_DECRYPT", cryptStr, keyStr, argExpList);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str        non-null
     * @param keyStr     non-null
     * @param argExpList non-null,size in [0,4]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_aes-encrypt">AES_ENCRYPT(str,key_str[,init_vector][,kdf_name][,salt][,info | iterations])</a>
     */
    public static Expression aesEncrypt(final Expression str, final Expression keyStr
            , final List<Expression> argExpList) {
        return _aesEncryptOrDecrypt("AES_ENCRYPT", str, keyStr, argExpList);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stringToCompress non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_compress">COMPRESS(string_to_compress)</a>
     */
    public static Expression compress(final Expression stringToCompress) {
        return SQLFunctions.oneArgFunc("COMPRESS", stringToCompress, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stringToUnCompress non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_uncompress">UNCOMPRESS(string_to_uncompress)</a>
     */
    public static Expression unCompress(final Expression stringToUnCompress) {
        return SQLFunctions.oneArgFunc("UNCOMPRESS", stringToUnCompress, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_md5">MD5(str)</a>
     */
    public static Expression md5(final Expression str) {
        return SQLFunctions.oneArgFunc("MD5", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param len non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_md5">RANDOM_BYTES(len)</a>
     */
    public static Expression randomBytes(final Expression len) {
        return SQLFunctions.oneArgFunc("RANDOM_BYTES", len, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_sha1">SHA1(str)</a>
     */
    public static Expression sha1(final Expression str) {
        return SQLFunctions.oneArgFunc("SHA1", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_sha1">SHA(str)</a>
     */
    public static Expression sha(final Expression str) {
        return SQLFunctions.oneArgFunc("SHA", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str        non-null
     * @param hashLength non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_sha2">SHA2(str, hash_length)</a>
     */
    public static Expression sha2(final Expression str, final Expression hashLength) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(str);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(hashLength);
        return SQLFunctions.safeComplexArgFunc("SHA2", argList, StringType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stmt    non-null
     * @param visible non-null
     * @param literal true:output literal
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/bit-functions.html#function_statement-digest">STATEMENT_DIGEST(statement)</a>
     */
    public static Expression statementDigest(final PrimaryStatement stmt, final Visible visible, final boolean literal) {
        return MySQLFunctions.statementDigest(stmt, visible, literal);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stmt    non-null ,sql
     * @param visible non-null
     * @param literal true:output literal
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/bit-functions.html#function_statement-digest">STATEMENT_DIGEST(statement)</a>
     */
    public static Expression statementDigest(final String stmt, final Visible visible, final boolean literal) {
        return MySQLFunctions.statementDigest(stmt, visible, literal);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stmt    non-null
     * @param visible non-null
     * @param literal true:output literal
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/bit-functions.html#function_statement-digest-text">STATEMENT_DIGEST_TEXT(statement)</a>
     */
    public static Expression statementDigestText(final PrimaryStatement stmt, final Visible visible
            , final boolean literal) {
        return MySQLFunctions.statementDigestText(stmt, visible, literal);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param stmt    non-null,sql
     * @param visible non-null
     * @param literal true:output literal
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/bit-functions.html#function_statement-digest-text">STATEMENT_DIGEST_TEXT(statement)</a>
     */
    public static Expression statementDigestText(final String stmt, final Visible visible, final boolean literal) {
        return MySQLFunctions.statementDigestText(stmt, visible, literal);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param compressedString non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_uncompressed-length">UNCOMPRESSED_LENGTH(compressed_string)</a>
     */
    public static Expression unCompressedLength(final Expression compressedString) {
        return SQLFunctions.oneArgFunc("UNCOMPRESSED_LENGTH", compressedString, IntegerType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_validate-password-strength">VALIDATE_PASSWORD_STRENGTH(str)</a>
     */
    public static Expression validatePasswordStrength(final Expression str) {
        return SQLFunctions.oneArgFunc("VALIDATE_PASSWORD_STRENGTH", str, IntegerType.INSTANCE);
    }

    /*-------------------below Locking Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_get-lock">GET_LOCK(str,timeout)</a>
     */
    public static Expression getLock(final Expression str, final Expression timeout) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(str);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(timeout);
        return SQLFunctions.safeComplexArgFunc("GET_LOCK", argList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_is-free-lock">IS_FREE_LOCK(str)</a>
     */
    public static Expression isFreeLock(final Expression str) {
        return SQLFunctions.oneArgFunc("IS_FREE_LOCK", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_is-used-lock">IS_USED_LOCK(str)</a>
     */
    public static Expression isUsedLock(final Expression str) {
        //TODO return Type
        return SQLFunctions.oneArgFunc("IS_USED_LOCK", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_release-all-locks">RELEASE_ALL_LOCKS()</a>
     */
    public static Expression releaseAllLocks() {
        return SQLFunctions.noArgFunc("RELEASE_ALL_LOCKS()", IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_release-lock">RELEASE_LOCK(str)</a>
     */
    public static Expression releaseLock(final Expression str) {
        return SQLFunctions.oneArgFunc("RELEASE_LOCK", str, IntegerType.INSTANCE);
    }


    /*-------------------below Information Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param count non-null
     * @param expr  non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_benchmark">BENCHMARK(count,expr)</a>
     */
    public static Expression benchmark(final Expression count, final Expression expr) {
        final List<Object> argList = new ArrayList<>(3);
        argList.add(count);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(expr);
        return SQLFunctions.safeComplexArgFunc("BENCHMARK", argList, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_charset">CHARSET(str)</a>
     */
    public static Expression charset(final Expression str) {
        return SQLFunctions.oneArgFunc("CHARSET", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link IntegerType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_coercibility">COERCIBILITY(str)</a>
     */
    public static Expression coercibility(final Expression str) {
        return SQLFunctions.oneArgFunc("COERCIBILITY", str, IntegerType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param str non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_collation">COLLATION(str)</a>
     */
    public static Expression collation(final Expression str) {
        return SQLFunctions.oneArgFunc("COLLATION", str, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_connection-id">CONNECTION_ID()</a>
     */
    public static Expression connectionId() {
        return SQLFunctions.noArgFunc("CONNECTION_ID", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_current-role">CURRENT_ROLE()</a>
     */
    public static Expression currentRole() {
        return SQLFunctions.noArgFunc("CURRENT_ROLE", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_current-user">CURRENT_USER()</a>
     */
    public static Expression currentUser() {
        return SQLFunctions.noArgFunc("CURRENT_USER", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_database">DATABASE()</a>
     */
    public static Expression database() {
        return SQLFunctions.noArgFunc("DATABASE", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_icu-version">ICU_VERSION()</a>
     */
    public static Expression icuVersion() {
        return SQLFunctions.noArgFunc("ICU_VERSION", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_last-insert-id">LAST_INSERT_ID()</a>
     */
    public static Expression lastInsertId() {
        return SQLFunctions.noArgFunc("LAST_INSERT_ID", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @param expr non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/encryption-functions.html#function_last-insert-id">LAST_INSERT_ID(expr)</a>
     */
    public static Expression lastInsertId(final Expression expr) {
        return SQLFunctions.oneArgFunc("LAST_INSERT_ID", expr, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_roles-graphml">ROLES_GRAPHML()</a>
     */
    public static Expression rolesGraphml() {
        return SQLFunctions.noArgFunc("ROLES_GRAPHML", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_row-count">ROW_COUNT()</a>
     */
    public static Expression rowCount() {
        return SQLFunctions.noArgFunc("ROW_COUNT", LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_user">USER()</a>
     */
    public static Expression user() {
        return SQLFunctions.noArgFunc("USER", StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/information-functions.html#function_version">VERSION()</a>
     */
    public static Expression version() {
        return SQLFunctions.noArgFunc("VERSION", StringType.INSTANCE);
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
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-mysql-specific-functions.html#function_geomcollection">GeomCollection(g [, g] ...)</a>
     */
    public static Expression geomCollection(final List<Expression> geometryList) {
        return _geometryTypeFunc("GeomCollection", geometryList);
    }

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
        final List<Object> argList = new ArrayList<>(3);
        argList.add(x);
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(y);
        return SQLFunctions.safeComplexArgFunc("Point", argList, ByteArrayType.INSTANCE);
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
    public static Expression mbrContains(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("MBRContains", g1, g2, BooleanType.INSTANCE);
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
    public static Expression mbrCoveredBy(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("MBRCoveredBy", g1, g2, BooleanType.INSTANCE);
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
    public static Expression mbrCovers(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("MBRCovers", g1, g2, BooleanType.INSTANCE);
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
    public static Expression mbrDisjoint(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("MBRDisjoint", g1, g2, BooleanType.INSTANCE);
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
    public static Expression mbrEquals(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("MBREquals", g1, g2, BooleanType.INSTANCE);
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
    public static Expression mbrIntersects(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("MBRIntersects", g1, g2, BooleanType.INSTANCE);
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
    public static Expression mbrOverlaps(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("MBROverlaps", g1, g2, BooleanType.INSTANCE);
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
    public static Expression mbrTouches(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("MBRTouches", g1, g2, BooleanType.INSTANCE);
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
    public static Expression mbrWithin(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("MBRWithin", g1, g2, BooleanType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_Area", polyOrmpoly, DoubleType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_Centroid", polyOrmpoly, ByteArrayType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_ExteriorRing", polyOrmpoly, ByteArrayType.INSTANCE);
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
        return _simpleTowArgFunc("ST_InteriorRingN", poly, n, ByteArrayType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_NumInteriorRing", poly, IntegerType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_NumInteriorRings", poly, IntegerType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_AsBinary", g, ByteArrayType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_AsWKB", g, ByteArrayType.INSTANCE);
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
        return _simpleTowArgFunc("ST_AsWKB", g, options, ByteArrayType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_AsText", g, StringType.INSTANCE);
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
        return _simpleTowArgFunc("ST_AsText", g, options, StringType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_AsWKT", g, StringType.INSTANCE);
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
        return _simpleTowArgFunc("ST_AsWKT", g, options, StringType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_SwapXY", g, ByteArrayType.INSTANCE);
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
                func = SQLFunctions.oneArgFunc(name, expList.get(0), StringType.INSTANCE);
                break;
            case 2:
            case 3:
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList), StringType.INSTANCE);
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
                func = SQLFunctions.oneArgFunc(name, expList.get(0), ByteArrayType.INSTANCE);
                break;
            case 2:
            case 3:
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList)
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
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList)
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
                func = SQLFunctions.oneArgFunc(name, expList.get(0), ByteArrayType.INSTANCE);
                break;
            case 2:
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList)
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
        return SQLFunctions.oneArgFunc("ST_ConvexHull", g, ByteArrayType.INSTANCE);
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
        return _simpleTowArgFunc("ST_Difference", g1, g2, ByteArrayType.INSTANCE);
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
        return _simpleTowArgFunc("ST_Intersection", g1, g2, ByteArrayType.INSTANCE);
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
        return _simpleTowArgFunc("ST_LineInterpolatePoint", ls, fractionalDistance, ByteArrayType.INSTANCE);
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
        return _simpleTowArgFunc("ST_LineInterpolatePoints", ls, fractionalDistance, ByteArrayType.INSTANCE);
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
        return _simpleTowArgFunc("ST_PointAtDistance", ls, distance, ByteArrayType.INSTANCE);
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
        return _simpleTowArgFunc("ST_SymDifference", g1, g2, ByteArrayType.INSTANCE);
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
        return _simpleTowArgFunc("ST_Transform", g, targetSrid, ByteArrayType.INSTANCE);
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
        return _simpleTowArgFunc("ST_Union", g1, g2, ByteArrayType.INSTANCE);
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
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList)
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
    public static Expression stIsValid(final Expression g) {
        return SQLFunctions.oneArgFunc("ST_IsValid", g, BooleanType.INSTANCE);
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
        return _simpleTowArgFunc("ST_MakeEnvelope", pt1, pt2, ByteArrayType.INSTANCE);
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
        return _simpleTowArgFunc("ST_Simplify", g, maxDistance, ByteArrayType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_Validate", g, ByteArrayType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_EndPoint", ls, ByteArrayType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_IsClosed", ls, BooleanType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param expList non-null,size in [1,2].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-length">ST_Length(ls [, unit])</a>
     */
    public static Expression stLength(final List<Expression> expList) {
        final String name = "ST_Length";
        final Expression func;
        switch (expList.size()) {
            case 1:
                func = SQLFunctions.oneArgFunc(name, expList.get(0), DoubleType.INSTANCE);
                break;
            case 2:
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList)
                        , DoubleType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
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
        return SQLFunctions.oneArgFunc("ST_NumPoints", ls, IntegerType.INSTANCE);
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
        return _simpleTowArgFunc("ST_PointN", ls, n, ByteArrayType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_StartPoint", ls, ByteArrayType.INSTANCE);
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
    public static Expression stContains(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("ST_Contains", g1, g2, BooleanType.INSTANCE);
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
    public static Expression stCrosses(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("ST_Crosses", g1, g2, BooleanType.INSTANCE);
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
    public static Expression stDisjoint(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("ST_Disjoint", g1, g2, BooleanType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param expList non-null,size in [2,3].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-distance">ST_Distance(g1, g2 [, unit])</a>
     */
    public static Expression stDistance(final List<Expression> expList) {
        final String name = "ST_Distance";
        final Expression func;
        switch (expList.size()) {
            case 2:
            case 3:
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList), DoubleType.INSTANCE);
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
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-equals">ST_Equals(g1, g2)</a>
     */
    public static Expression stEquals(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("ST_Equals", g1, g2, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param expList non-null,size in [2,3].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-frechetdistance">ST_FrechetDistance(g1, g2 [, unit])</a>
     */
    public static Expression stFrechetDistance(final List<Expression> expList) {
        final String name = "ST_FrechetDistance";
        final Expression func;
        switch (expList.size()) {
            case 2:
            case 3:
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList), DoubleType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param expList non-null,size in [2,3].
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/gis-linestring-property-functions.html#function_st-hausdorffdistance">ST_HausdorffDistance(g1, g2 [, unit])</a>
     */
    public static Expression stHausdorffDistance(final List<Expression> expList) {
        final String name = "ST_HausdorffDistance";
        final Expression func;
        switch (expList.size()) {
            case 2:
            case 3:
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList), DoubleType.INSTANCE);
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
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-intersects">ST_Intersects(g1, g2)</a>
     */
    public static Expression stIntersects(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("ST_Intersects", g1, g2, BooleanType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-overlaps">ST_Overlaps(g1, g2)</a>
     */
    public static Expression stOverlaps(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("ST_Overlaps", g1, g2, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-touches">ST_Touches(g1, g2)</a>
     */
    public static Expression stTouches(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("ST_Touches", g1, g2, BooleanType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link BooleanType}
     * </p>
     *
     * @param g1 non-null
     * @param g2 non-null
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-relation-functions-object-shapes.html#function_st-within">ST_Within(g1, g2)</a>
     */
    public static Expression stWithin(final Expression g1, final Expression g2) {
        return _simpleTowArgFunc("ST_Within", g1, g2, BooleanType.INSTANCE);
    }


    /*-------------------below Spatial Geohash Functions-------------------*/

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param expList non-null,size in [2,3]
     * @throws CriteriaException throw when invoking this method in non-statement context.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/spatial-geohash-functions.html#function_st-geohash">ST_GeoHash(longitude, latitude, max_length), ST_GeoHash(point, max_length)</a>
     */
    public static Expression stGeoHash(final List<Expression> expList) {
        final String name = "ST_GeoHash";
        final Expression func;
        switch (expList.size()) {
            case 2:
            case 3:
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(expList), StringType.INSTANCE);
                break;
            default:
                throw CriteriaUtils.funcArgError(name, expList);
        }
        return func;
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
        return SQLFunctions.oneArgFunc("ST_LatFromGeoHash", geohashStr, DoubleType.INSTANCE);
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
        return SQLFunctions.oneArgFunc("ST_LongFromGeoHash", geohashStr, DoubleType.INSTANCE);
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
        return _simpleTowArgFunc("ST_PointFromGeoHash", geohashStr, srid, ByteArrayType.INSTANCE);
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




    /*-------------------below private method -------------------*/


    /**
     * @see #count()
     * @see #count(Expression)
     * @see #count(SQLs.Modifier, Expression)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _count(final @Nullable SQLs.Modifier distinct
            , final @Nullable Object expressions) {

        final String funcName = "COUNT";

        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        final _AggregateOverSpec func;
        if (!(expressions instanceof List)) {
            if (distinct != null && expressions == null) {
                String m = String.format("function %s option[%s] but expr is null.", funcName, distinct);
                throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), m);
            }
            func = MySQLFunctions.aggregateWindowFunc(funcName, distinct, expressions, LongType.INSTANCE);
        } else if (distinct == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        } else {
            func = MySQLFunctions.multiArgAggregateWindowFunc(funcName, distinct, (List<?>) expressions
                    , null, LongType.INSTANCE);
        }
        return func;
    }

    /**
     * @see #bitAnd(Object)
     * @see #bitOr(Object)
     * @see #bitXor(Object)
     */
    private static MappingType _bitwiseFuncReturnType(final @Nullable Object expr) {
        final MappingType returnType;

        if (expr == null) {
            returnType = LongType.INSTANCE;
        } else if (expr instanceof String) {
            returnType = _StringUtils.isBinary((String) expr) ? StringType.INSTANCE : LongType.INSTANCE;
        } else if (!(expr instanceof Expression)) {
            returnType = LongType.INSTANCE;
        } else {
            final TypeMeta paramMeta = ((Expression) expr).typeMeta();
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
        }
        return returnType;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: returnType or {@link JsonListType}
     * </p>
     *
     * @param expr       parameter or {@link Expression},but couldn't be null.
     * @param returnType if null,then the {@link MappingType} of function return type is {@link JsonListType}.
     * @see #jsonArrayAgg(Object)
     * @see #jsonArrayAgg(Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _jsonArrayAgg(final Object expr, final @Nullable TypeMeta returnType) {
        final String funcName = "JSON_ARRAYAGG";
        if (expr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        final TypeMeta elementType = expression.typeMeta();

        final TypeMeta actualReturnType;
        if (returnType != null) {
            actualReturnType = returnType;
        } else if (elementType instanceof TypeMeta.Delay) {
            actualReturnType = CriteriaSupports.delayParamMeta((TypeMeta.Delay) elementType, JsonListType::from);
        } else {
            actualReturnType = JsonListType.from(elementType.mappingType());
        }
        return MySQLFunctions.aggregateWindowFunc(funcName, null, expression, actualReturnType);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: returnType or {@link  JsonMapType}
     * </p>
     *
     * @param key        non-null parameter or {@link Expression},but couldn't be null.
     * @param value      non-null parameter or {@link Expression},but couldn't be null.
     * @param returnType function return type,if null,then The {@link MappingType} of function return type is {@link  JsonMapType}.
     * @see #jsonObjectAgg(Object, Object)
     * @see #jsonObjectAgg(Object, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-objectagg">JSON_OBJECTAGG(key, value) [over_clause]</a>
     */
    private static _AggregateOverSpec _jsonObjectAgg(final Object key, final Object value
            , final @Nullable TypeMeta returnType) {

        final String funcName = "JSON_OBJECTAGG";

        final ArmyExpression keyExpr, valueExpr;
        keyExpr = SQLs._funcParam(key);
        valueExpr = SQLs._funcParam(value);

        if (keyExpr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, key);
        }
        if (valueExpr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, value);
        }
        final TypeMeta actualReturnType;
        if (returnType != null) {
            actualReturnType = returnType;
        } else {
            actualReturnType = Functions._returnType(keyExpr, valueExpr, JsonMapType::from);
        }
        return MySQLFunctions.safeMultiArgAggregateWindowFunc(funcName, null
                , Arrays.asList(keyExpr, valueExpr), null
                , actualReturnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param funcName MIN or MAX
     * @param distinct null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #min(Object)
     * @see #min(SQLs.Modifier, Object)
     * @see #max(Object)
     * @see #max(SQLs.Modifier, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _minOrMax(final String funcName, final @Nullable SQLs.Modifier distinct
            , final Object expr) {
        if (!(funcName.equals("MAX") || funcName.equals("MIN"))) {
            //no bug,never here
            throw new IllegalArgumentException();
        }
        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expression instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        return MySQLFunctions.aggregateWindowFunc(funcName, distinct, expression, expression.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: returnType or the {@link MappingType} of expr.
     * </p>
     *
     * @param distinct   null or {@link  SQLs.Modifier#DISTINCT}
     * @param expr       non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @param returnType nullable,if null ,then {@link MappingType} of function return type is the {@link MappingType} of expr
     * @see #sum(Object)
     * @see #sum(SQLs.Modifier, Object)
     * @see #sum(SQLs.Modifier, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _sum(final @Nullable SQLs.Modifier distinct, final @Nullable Object expr
            , final @Nullable TypeMeta returnType) {
        if (expr == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        }
        final String funcName = "SUM";
        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expression instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        final TypeMeta actualReturnType;
        if (returnType != null) {
            actualReturnType = returnType;
        } else {
            actualReturnType = expression.typeMeta();
        }
        return MySQLFunctions.aggregateWindowFunc(funcName, distinct, expression, actualReturnType);
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
     *                       <li>{@link SQLs#param(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                       <li>{@link SQLs#literal(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                   </ul>
     * @param useDefault if n is non-nul and useDefault is true,output sql key word {@code DEFAULT}
     * @see #lag(Object)
     * @see #lag(Object, Object, boolean)
     * @see #lead(Object)
     * @see #lead(Object, Object, boolean)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    private static _OverSpec _lagOrLead(final String funcName, final Object expr
            , final @Nullable Object n, final boolean useDefault) {

        assert funcName.equals("LAG") || funcName.equals("LEAD");

        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expr instanceof SQLs.NullWord) {
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
            overSpec = MySQLFunctions.oneArgWindowFunc(funcName, null, expression, expression.typeMeta());
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
            overSpec = MySQLFunctions.safeMultiArgWindowFunc(funcName, null, argList, expression.typeMeta());
        } else {
            final List<ArmyExpression> argList;
            argList = Arrays.asList(expression, nExp);
            overSpec = MySQLFunctions.safeMultiArgWindowFunc(funcName, null, argList, expression.typeMeta());
        }
        return overSpec;
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @see #firstValue(Object)
     * @see #lastValue(Object)
     */
    private static _OverSpec _nonNullArgWindowFunc(final String funcName, final Object expr) {
        final ArmyExpression expression;
        expression = SQLs._funcParam(expr);
        if (expr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        return MySQLFunctions.oneArgWindowFunc(funcName, null, expression, expression.typeMeta());
    }


    /**
     * @see #groupConcat(Object)
     * @see #groupConcat(SQLs.Modifier, Object)
     * @see #groupConcat(SQLs.Modifier, Object, Supplier)
     */
    private static Expression _groupConcat(@Nullable SQLs.Modifier distinct, @Nullable Object expressions
            , @Nullable Clause clause) {

        final String funcName = "GROUP_CONCAT";

        if (distinct != null && distinct != SQLs.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        if (clause != null && !(clause instanceof MySQLFunctions.GroupConcatClause)) {
            throw CriteriaUtils.funcArgError(funcName, clause);
        }
        final Expression func;
        if (expressions instanceof List) {
            func = SQLFunctions.multiArgOptionFunc(funcName, distinct, (List<?>) expressions
                    , clause, StringType.INSTANCE);
        } else {
            func = SQLFunctions.oneArgOptionFunc(funcName, distinct, expressions, clause, StringType.INSTANCE);
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
        argList.add(SQLFunctions.FuncWord.COMMA);
        argList.add(keyStr);

        for (Expression argExp : argExpList) {
            argList.add(SQLFunctions.FuncWord.COMMA);
            argList.add(argExp);
        }
        return SQLFunctions.safeComplexArgFunc(funcName, argList, StringType.INSTANCE);
    }


    /**
     * @see #geomCollection(List)
     * @see #geometryCollection(List)
     * @see #lineString(List)
     */
    private static Expression _geometryTypeFunc(final String name, final List<Expression> geometryList) {
        final Expression func;
        final int geometrySize = geometryList.size();
        switch (geometrySize) {
            case 0:
                func = SQLFunctions.noArgFunc(name, ByteArrayType.INSTANCE);
                break;
            case 1:
                func = SQLFunctions.oneArgFunc(name, geometryList.get(0), ByteArrayType.INSTANCE);
                break;
            default:
                func = SQLFunctions.safeComplexArgFunc(name, _createSimpleMultiArgList(geometryList)
                        , ByteArrayType.INSTANCE);

        }
        return func;
    }


}
