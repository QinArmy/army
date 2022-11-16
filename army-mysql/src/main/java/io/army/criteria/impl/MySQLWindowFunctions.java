package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.TypeInfer;
import io.army.criteria.mysql.MySQLFunction;
import io.army.lang.Nullable;
import io.army.mapping.DoubleType;
import io.army.mapping.LongType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.mapping.optional.JsonListType;
import io.army.mapping.optional.JsonMapType;

import java.util.ArrayList;
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


    /*-------------------below MySQL Aggregate Function Descriptions -------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> avg(Expression exp) {
        return MySQLs.avg(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:{@link DoubleType}
     * </p>
     *
     * @param distinct nullable,{@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> avg(@Nullable SQLSyntax.ArgDistinct distinct
            , Expression exp) {
        return MySQLs.avg(distinct, exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> bitAnd(Expression exp) {
        return MySQLs.bitAnd(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> bitOr(Expression exp) {
        return MySQLs.bitOr(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> bitXor(Expression exp) {
        return MySQLs.bitXor(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> countStar() {
        return MySQLs.countStar(SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> count(Expression exp) {
        return MySQLs.count(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(DISTINCT expr,[expr...])</a>
     */
    public static Expression count(final @Nullable SQLSyntax.ArgDistinct distinct, final List<Expression> expList) {
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
                argList.add(FuncWord.COMMA);
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
     * @see SQLs#multiParams(TypeInfer, Collection)
     * @see SQLs#multiLiterals(TypeInfer, Collection)
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
     * @see SQLs#multiParams(TypeInfer, Collection)
     * @see SQLs#multiLiterals(TypeInfer, Collection)
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
    public static Expression groupConcat(@Nullable SQLSyntax.ArgDistinct distinct
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
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> jsonArrayAgg(Expression exp) {
        return MySQLs.jsonArrayAgg(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
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
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> jsonObjectAgg(Expression key, Expression value) {
        return MySQLs.jsonObjectAgg(key, value, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> max(Expression exp) {
        return MySQLs.max(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> max(@Nullable SQLSyntax.ArgDistinct distinct
            , Expression exp) {
        return MySQLs.max(distinct, exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression},but couldn't be {@link SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> min(Expression exp) {
        return MySQLs.min(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression},but couldn't be {@link SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> min(@Nullable SQLSyntax.ArgDistinct distinct
            , Expression exp) {
        return MySQLs.min(distinct, exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_std">STD(xpr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> std(Expression exp) {
        return MySQLs.std(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev">STDDEV(xpr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> stdDev(Expression exp) {
        return MySQLs.stdDev(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-pop">STDDEV_POP(xpr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> stdDevPop(Expression exp) {
        return MySQLs.stdDevPop(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-samp">STDDEV_SAMP(xpr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> stdDevSamp(Expression exp) {
        return MySQLs.stdDevSamp(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> sum(Expression exp) {
        return MySQLs.sum(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> sum(@Nullable SQLSyntax.ArgDistinct distinct
            , Expression exp) {
        return MySQLs.sum(distinct, exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-pop">VAR_POP(xpr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> varPop(Expression exp) {
        return MySQLs.varPop(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VAR_SAMP(xpr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> varSamp(Expression exp) {
        return MySQLs.varSamp(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VARIANCE(expr) [over_clause]</a>
     */
    public static MySQLFunctionSyntax._AggregateWindowFunc<Expression> variance(Expression exp) {
        return MySQLs.variance(exp, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }


    /*-------------------below window function-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_cume-dist">CUME_DIST() over_clause</a>
     */
    public static MySQLFunctionSyntax._OverSpec<Expression> cumeDist() {
        return MySQLs.cumeDist(SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     */
    public static MySQLFunctionSyntax._OverSpec<Expression> denseRank() {
        return MySQLs.denseRank(SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_first-value">FIRST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static MySQLFunctionSyntax._OverSpec<Expression> firstValue(final Expression expr) {
        return MySQLs.firstValue(expr, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_last-value">LAST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static MySQLFunctionSyntax._OverSpec<Expression> lastValue(final Expression expr) {
        return MySQLs.lastValue(expr, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static MySQLFunctionSyntax._OverSpec<Expression> lag(final Expression expr) {
        return MySQLs.lag(expr, SQLs._getAsExpFunc(), SQLs._IDENTITY);
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
    public static MySQLFunctionSyntax._OverSpec<Expression> lag(Expression expr, Expression n) {
        return MySQLs.lag(expr, n, SQLs._getAsExpFunc(), SQLs._IDENTITY);
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
     * @param defaultValue non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static MySQLFunctionSyntax._OverSpec<Expression> lag(final Expression expr, final Expression n
            , final Expression defaultValue) {
        return MySQLs.lag(expr, n, defaultValue, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static MySQLFunctionSyntax._OverSpec<Expression> lead(final Expression expr) {
        return MySQLs.lead(expr, SQLs._getAsExpFunc(), SQLs._IDENTITY);
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
    public static MySQLFunctionSyntax._OverSpec<Expression> lead(Expression expr, Expression n) {
        return MySQLs.lead(expr, n, SQLs._getAsExpFunc(), SQLs._IDENTITY);
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
     * @param defaultValue non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static MySQLFunctionSyntax._OverSpec<Expression> lead(Expression expr, Expression n
            , Expression defaultValue) {
        return MySQLs.lead(expr, n, defaultValue, SQLs._getAsExpFunc(), SQLs._IDENTITY);
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
    public static MySQLFunctionSyntax._FromFirstLastOverSpec<Expression> nthValue(Expression expr, Expression n) {
        return MySQLs.nthValue(expr, n, SQLs._getAsExpFunc(), SQLs._IDENTITY);
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
    public static MySQLFunctionSyntax._OverSpec<Expression> ntile(final Expression n) {
        return MySQLs.ntile(n, SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link DoubleType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">PERCENT_RANK() over_clause</a>
     */
    public static MySQLFunctionSyntax._OverSpec<Expression> percentRank() {
        return MySQLs.percentRank(SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">RANK() over_clause</a>
     */
    public static MySQLFunctionSyntax._OverSpec<Expression> rank() {
        return MySQLs.rank(SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link LongType}.
     * </p>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     */
    public static MySQLFunctionSyntax._OverSpec<Expression> rowNumber() {
        return MySQLs.rowNumber(SQLs._getAsExpFunc(), SQLs._IDENTITY);
    }




}
