package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.Selection;
import io.army.criteria.mysql.MySQLClause;
import io.army.lang.Nullable;
import io.army.mapping.DoubleType;
import io.army.mapping.MappingType;
import io.army.mapping.optional.JsonListType;
import io.army.mapping.optional.JsonMapType;

import java.util.List;
import java.util.function.Function;

public abstract class MySQLFunctions extends Functions {

    /**
     * private constructor
     */
    private MySQLFunctions() {
    }

    /*-------------------below MySQL Aggregate Function Descriptions -------------------*/


    /**
     * @see MySQLs#avg(FuncDistinct, Expression, Function, Function)
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> avg(Expression exp) {
        return MySQLs.avg(null, exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * @see MySQLs#avg(FuncDistinct, Expression, Function, Function)
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> avg(@Nullable Functions.FuncDistinct distinct
            , Expression exp) {
        return MySQLs.avg(distinct, exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * @see MySQLs#bitAnd(Expression, Function, Function)
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> bitAnd(Expression exp) {
        return MySQLs.bitAnd(exp, SQLs::_asExp, SQLs::_identity);
    }


    /**
     * @see MySQLs#bitOr(Expression, Function, Function)
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> bitOr(Expression exp) {
        return MySQLs.bitOr(exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * @see MySQLs#bitXor(Expression, Function, Function)
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> bitXor(Expression exp) {
        return MySQLs.bitXor(exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> countStar() {
        return MySQLs.countStar(SQLs::_asExp, SQLs::_identity);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> count(Expression exp) {
        return MySQLs.count(exp, SQLs::_asExp, SQLs::_identity);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> count(@Nullable SQLs.FuncDistinct distinct
            , List<Expression> argList) {
        return MySQLs.count(distinct, argList, SQLs::_asExp, SQLs::_identity);
    }

    public static MySQLClause._GroupConcatOrderBySpec groupConcat(Expression exp) {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static MySQLClause._GroupConcatOrderBySpec groupConcat(@Nullable SQLs.FuncDistinct distinct, Expression exp) {
        //TODO
        throw new UnsupportedOperationException();
    }

    public static MySQLClause._GroupConcatOrderBySpec groupConcat(@Nullable SQLs.FuncDistinct distinct
            , List<Expression> argList) {
        //TODO
        throw new UnsupportedOperationException();
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonListType}
     * </p>
     *
     * @param exp parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> jsonArrayAgg(Expression exp) {
        return MySQLs.jsonArrayAgg(exp, SQLs::_asExp, SQLs::_identity);
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
    public static MySQLs._AggregateWindowFunc<Expression, Selection> jsonObjectAgg(Expression key
            , Expression value) {
        return MySQLs.jsonObjectAgg(key, value, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> max(Expression exp) {
        return MySQLs.max(null, exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> max(@Nullable SQLs.FuncDistinct distinct
            , Expression exp) {
        return MySQLs.max(distinct, exp, SQLs::_asExp, SQLs::_identity);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression},but couldn't be {@link SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> min(Expression exp) {
        return MySQLs.min(null, exp, SQLs::_asExp, SQLs::_identity);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression},but couldn't be {@link SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> min(@Nullable SQLs.FuncDistinct distinct
            , Expression exp) {
        return MySQLs.min(distinct, exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_std">STD(xpr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> std(Expression exp) {
        return MySQLs.std(exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev">STDDEV(xpr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> stdDev(Expression exp) {
        return MySQLs.stdDev(exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-pop">STDDEV_POP(xpr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> stdDevPop(Expression exp) {
        return MySQLs.stdDevPop(exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-samp">STDDEV_SAMP(xpr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> stdDevSamp(Expression exp) {
        return MySQLs.stdDevSamp(exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> sum(Expression exp) {
        return MySQLs.sum(null, exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     * </p>
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> sum(@Nullable SQLs.FuncDistinct distinct
            , Expression exp) {
        return MySQLs.sum(distinct, exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-pop">VAR_POP(xpr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> varPop(Expression exp) {
        return MySQLs.varPop(exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VAR_SAMP(xpr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> varSamp(Expression exp) {
        return MySQLs.varSamp(exp, SQLs::_asExp, SQLs::_identity);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     * </p>
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VARIANCE(expr) [over_clause]</a>
     */
    public static MySQLs._AggregateWindowFunc<Expression, Selection> variance(Expression exp) {
        return MySQLs.variance(exp, SQLs::_asExp, SQLs::_identity);
    }











}
