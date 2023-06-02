package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.SimpleExpression;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TypeInfer;
import io.army.criteria.dialect.Window;
import io.army.criteria.mysql.MySQLFunction;
import io.army.criteria.mysql.MySQLWindow;
import io.army.criteria.standard.SQLFunction;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

/**
 * package class
 *
 * @since 1.0
 */
@SuppressWarnings("unused")
abstract class MySQLWindowFunctions extends MySQLJsonFunctions {

    MySQLWindowFunctions() {
    }


    public interface _OverSpec extends Window._OverWindowClause<MySQLWindow._PartitionBySpec> {


    }

    public interface _AggregateWindowFunc extends _OverSpec, SQLFunction.AggregateFunction, SimpleExpression {

    }

    public interface _ItemAggregateWindowFunc extends _AggregateWindowFunc {

    }

    public interface _NullTreatmentOverSpec extends _NullTreatmentClause<_OverSpec>, _OverSpec {


    }

    public interface _FromFirstLastOverSpec extends _FromFirstLastClause<_NullTreatmentOverSpec>,
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
    public static _AggregateWindowFunc avg(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("AVG", exp, DoubleType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param distinct nullable,{@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc avg(@Nullable SqlSyntax.ArgDistinct distinct, Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("AVG", distinct, exp, DoubleType.INSTANCE);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc bitAnd(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("BIT_AND", exp, _bitwiseFuncType(exp));
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc bitOr(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("BIT_OR", exp, _bitwiseFuncType(exp));
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc bitXor(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("BIT_XOR", exp, _bitwiseFuncType(exp));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc countAsterisk() {
        return count(SQLs._ASTERISK_EXP);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc count(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("COUNT", exp, LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(DISTINCT expr,[expr...])</a>
     */
    public static Expression count(final @Nullable SqlSyntax.ArgDistinct distinct, final List<?> expList) {//TODO
        assertDistinct(distinct);
        final String name = "COUNT";
        final int size = expList.size();
        if (size == 0) {
            throw CriteriaUtils.funcArgError(name, expList);
        }
        final List<Object> argList = new ArrayList<>(((1 + size) << 1) - 1);
        if (distinct != null) {
            argList.add(distinct);
        }
        for (int i = 0; i < size; i++) {
            if (i > 0) {
                argList.add(Functions.FuncWord.COMMA);
            }
            argList.add(expList.get(i));
        }
        return FunctionUtils.complexArgFunc(name, argList, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT([DISTINCT] expr [,expr ...]
     * [ORDER BY {unsigned_integer | col_name | expr}
     * [ASC | DESC] [,col_name ...]]
     * [SEPARATOR str_val])</a>
     */
    public static Expression groupConcat(Expression exp) {
        return FunctionUtils.oneArgFunc("GROUP_CONCAT", exp, StringType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param distinct {@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @param exp      expression or multi-value expression
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT([DISTINCT] expr [,expr ...]
     * [ORDER BY {unsigned_integer | col_name | expr}
     * [ASC | DESC] [,col_name ...]]
     * [SEPARATOR str_val])</a>
     * @see SQLs#rowParam(TypeInfer, Collection)
     * @see SQLs#rowLiteral(TypeInfer, Collection)
     */
    public static Expression groupConcat(@Nullable SQLs.ArgDistinct distinct, Expression exp) {
        assertDistinct(distinct);
        return FunctionUtils.complexArgFunc("GROUP_CONCAT", StringType.INSTANCE, distinct, exp);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @param distinct {@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @param exp      expression or multi-value expression
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT([DISTINCT] expr [,expr ...]
     * [ORDER BY {unsigned_integer | col_name | expr}
     * [ASC | DESC] [,col_name ...]]
     * [SEPARATOR str_val])</a>
     * @see SQLs#rowParam(TypeInfer, Collection)
     * @see SQLs#rowLiteral(TypeInfer, Collection)
     */
    public static Expression groupConcat(@Nullable SQLs.ArgDistinct distinct, Expression exp
            , Consumer<MySQLFunction._GroupConcatOrderBySpec> consumer) {
        assertDistinct(distinct);

        final MySQLFunctionUtils.GroupConcatInnerClause clause;
        clause = MySQLFunctionUtils.groupConcatClause();
        consumer.accept(clause);

        return MySQLFunctionUtils.groupConcatFunc(distinct, exp, clause);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link StringType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT([DISTINCT] expr [,expr ...]
     * [ORDER BY {unsigned_integer | col_name | expr}
     * [ASC | DESC] [,col_name ...]]
     * [SEPARATOR str_val])</a>
     */
    public static Expression groupConcat(@Nullable SqlSyntax.ArgDistinct distinct
            , List<Expression> argList, Consumer<MySQLFunction._GroupConcatOrderBySpec> consumer) {

        assertDistinct(distinct);
        final MySQLFunctionUtils.GroupConcatInnerClause clause;
        clause = MySQLFunctionUtils.groupConcatClause();
        consumer.accept(clause);
        return MySQLFunctionUtils.groupConcatFunc(distinct, argList, clause);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonListType}
     * </p>
     *
     * @param exp parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc jsonArrayAgg(Expression exp) {
        final TypeMeta returnType;//TODO validate JsonListType
        returnType = Functions._returnType((ArmyExpression) exp, JsonListType::from);
        return MySQLFunctionUtils.oneArgAggregate("JSON_ARRAYAGG", exp, returnType);
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
    public static _AggregateWindowFunc jsonObjectAgg(Expression key, Expression value) {
        final TypeMeta returnType;//TODO validate JsonMapType
        returnType = Functions._returnType((ArmyExpression) key, (ArmyExpression) value, JsonMapType::from);
        final List<Expression> argList;
        argList = Arrays.asList(key, value);
        final String name = "JSON_OBJECTAGG";
        return MySQLFunctionUtils.multiArgAggregateWindowFunc(name, null, argList, returnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc max(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("MAX", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc max(@Nullable SqlSyntax.ArgDistinct distinct, Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("MAX", distinct, exp, exp.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression},but couldn't be {@link SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc min(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("MIN", exp, exp.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression},but couldn't be {@link SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc min(@Nullable SqlSyntax.ArgDistinct distinct, Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("MIN", distinct, exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_std">STD(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc std(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("STD", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev">STDDEV(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc stdDev(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("STDDEV", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-pop">STDDEV_POP(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc stdDevPop(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("STDDEV_POP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-samp">STDDEV_SAMP(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc stdDevSamp(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("STDDEV_SAMP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc sum(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("SUM", exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc sum(@Nullable SqlSyntax.ArgDistinct distinct, Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("SUM", distinct, exp, exp.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-pop">VAR_POP(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc varPop(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("VAR_POP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VAR_SAMP(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc varSamp(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("VAR_SAMP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VARIANCE(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc variance(Expression exp) {
        return MySQLFunctionUtils.oneArgAggregate("VARIANCE", exp, DoubleType.INSTANCE);
    }


    /*-------------------below window function-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_cume-dist">CUME_DIST() over_clause</a>
     */
    public static _OverSpec cumeDist() {
        return MySQLFunctionUtils.noArgWindowFunc("CUME_DIST", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     */
    public static _OverSpec denseRank() {
        return MySQLFunctionUtils.noArgWindowFunc("DENSE_RANK", LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_first-value">FIRST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static _OverSpec firstValue(Expression expr) {
        return MySQLFunctionUtils.noArgWindowFunc("FIRST_VALUE", expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_last-value">LAST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static _OverSpec lastValue(Expression expr) {
        return MySQLFunctionUtils.noArgWindowFunc("LAST_VALUE", expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(Expression expr) {
        return MySQLFunctionUtils.oneArgWindowFunc("LAG", expr, expr.typeMeta());
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
     *                 <li>{@link SQLs#paramValue(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                 <li>{@link SQLs#literalValue(Object) },argument type is {@link Long} or {@link Integer}</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(Expression expr, Expression n) {
        return MySQLFunctionUtils.twoArgWindowFunc("LAG", expr, n, expr.typeMeta());
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
     *                         <li>{@link SQLs#paramValue(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                         <li>{@link SQLs#literalValue(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                     </ul>
     * @param defaultValue non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(Expression expr, Expression n, Expression defaultValue) {
        return MySQLFunctionUtils.threeArgWindow("LAG", expr, n, defaultValue, expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(Expression expr) {
        return MySQLFunctionUtils.oneArgWindowFunc("LEAD", expr, expr.typeMeta());
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
     *                 <li>{@link SQLs#paramValue(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                 <li>{@link SQLs#literalValue(Object) },argument type is {@link Long} or {@link Integer}</li>
     *             </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(Expression expr, Expression n) {
        return MySQLFunctionUtils.twoArgWindowFunc("LEAD", expr, n, expr.typeMeta());
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
     *                         <li>{@link SQLs#paramValue(Object)},argument type is {@link Long} or {@link Integer}</li>
     *                         <li>{@link SQLs#literalValue(Object) },argument type is {@link Long} or {@link Integer}</li>
     *                     </ul>
     * @param defaultValue non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(Expression expr, Expression n, Expression defaultValue) {
        return MySQLFunctionUtils.threeArgWindow("LEAD", expr, n, defaultValue, expr.typeMeta());
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
    public static _FromFirstLastOverSpec nthValue(Expression expr, Expression n) {
        return MySQLFunctionUtils.twoArgFromFirstWindowFunc("NTH_VALUE", expr, n, expr.typeMeta());
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
     *               <li>positive number parameter {@link  Expression},eg:{@link SQLs#paramValue(Object)}</li>
     *               <li>positive number literal {@link  Expression},eg:{@link SQLs#literalValue(Object)}</li>
     *               <li>variable {@link  Expression}</li>
     *          </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_ntile">NTILE(N) over_clause</a>
     */
    public static _OverSpec ntile(Expression n) {
        return MySQLFunctionUtils.oneArgWindowFunc("NTILE", n, LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">PERCENT_RANK() over_clause</a>
     */
    public static _OverSpec percentRank() {
        return MySQLFunctionUtils.noArgWindowFunc("PERCENT_RANK", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">RANK() over_clause</a>
     */
    public static _OverSpec rank() {
        return MySQLFunctionUtils.noArgWindowFunc("RANK", LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     */
    public static _OverSpec rowNumber() {
        return MySQLFunctionUtils.noArgWindowFunc("ROW_NUMBER", LongType.INSTANCE);
    }




    /*-------------------below private methos -------------------*/

    /**
     * @see #bitAnd(Expression)
     * @see #bitOr(Expression)
     * @see #bitXor(Expression)
     */
    private static MappingType _bitwiseFuncType(final Expression expr) {
        final MappingType returnType;

        final TypeMeta paramMeta = expr.typeMeta();
        if (paramMeta instanceof TypeMeta.DelayTypeMeta) {
            returnType = StringType.INSTANCE; //TODO optimize unknown,compatibility
        } else if (!(paramMeta.mappingType() instanceof StringType)) {
            returnType = LongType.INSTANCE;
        } else if (!(expr instanceof SqlValueParam.SingleAnonymousValue)) {
            returnType = StringType.INSTANCE; //ODO optimize unknown,compatibility
        } else {
            final Object value;
            value = ((SqlValueParam.SingleAnonymousValue) expr).value();
            if (value instanceof String && _StringUtils.isBinary((String) value)) {
                returnType = StringType.INSTANCE;
            } else {
                returnType = LongType.INSTANCE;
            }
        }
        return returnType;
    }


}
