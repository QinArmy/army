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

package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.dialect.Window;
import io.army.criteria.mysql.MySQLFunction;
import io.army.criteria.mysql.MySQLWindow;
import io.army.criteria.standard.SQLFunction;
import io.army.mapping.*;
import io.army.meta.TypeMeta;
import io.army.util._Collections;

import java.util.*;
import java.util.function.Consumer;

/**
 * package class
 *
 * @since 0.6.0
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
     * <p>The {@link MappingType} of function return type:{@link DoubleType}
     *
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc avg(Expression exp) {
        return MySQLFunctions.oneArgAggregate("AVG", exp, DoubleType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link DoubleType}
     *
     * @param distinct non-null ,see {@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @param exp      non-null
     * @throws CriteriaException throw when argument error
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc avg(SQLs.ArgDistinct distinct, Expression exp) {
        FuncExpUtils.assertDistinct(distinct, MySQLs.DISTINCT);
        return MySQLFunctions.compositeAggWindowFunc("AVG", Arrays.asList(distinct, exp), DoubleType.INSTANCE);
    }

    /**
     * @param exp non-null
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc bitAnd(Expression exp) {
        return MySQLFunctions.oneArgAggregate("BIT_AND", exp, _bitwiseFuncType(exp));
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-or">BIT_OR(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc bitOr(Expression exp) {
        return MySQLFunctions.oneArgAggregate("BIT_OR", exp, _bitwiseFuncType(exp));
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-xor">BIT_XOR(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc bitXor(Expression exp) {
        return MySQLFunctions.oneArgAggregate("BIT_XOR", exp, _bitwiseFuncType(exp));
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link LongType}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc countAsterisk() {
        return count(SQLs._ASTERISK_EXP);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link LongType}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc count(Expression exp) {
        return MySQLFunctions.oneArgAggregate("COUNT", exp, LongType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link LongType}
     *
     * @param distinct see {@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(DISTINCT expr,[expr...])</a>
     */
    public static SimpleExpression count(SQLs.ArgDistinct distinct, Expression expr) {
        FuncExpUtils.assertDistinct(distinct, MySQLs.DISTINCT);
        return LiteralFunctions.compositeFunc("COUNT", Arrays.asList(distinct, expr), LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link LongType}
     *
     * @param distinct see {@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(DISTINCT expr,[expr...])</a>
     */
    public static SimpleExpression count(SQLs.ArgDistinct distinct, Expression expr1, Expression expr2, Expression... expVariadic) {
        FuncExpUtils.assertDistinct(distinct, MySQLs.DISTINCT);
        final List<Object> argList = _Collections.arrayList(3 + expVariadic.length);

        argList.add(distinct);
        argList.add(expr1);
        argList.add(expr2);

        Collections.addAll(argList, expr1);

        return LiteralFunctions.compositeFunc("COUNT", argList, LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link LongType}
     *
     * @param distinct see {@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(DISTINCT expr,[expr...])</a>
     */
    public static SimpleExpression count(SQLs.ArgDistinct distinct, Consumer<Consumer<Expression>> consumer) {
        FuncExpUtils.assertDistinct(distinct, MySQLs.DISTINCT);

        final List<Object> argList = _Collections.arrayList(3);
        argList.add(distinct);

        try {
            consumer.accept(argList::add);
        } catch (Exception e) {
            throw ContextStack.clearStackAndCause(e, "Invoking Expression consumer error");
        } catch (Error e) {
            throw ContextStack.clearStackAndError(e);
        }
        return LiteralFunctions.compositeFunc("COUNT", argList, LongType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link StringType}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT([DISTINCT] expr [,expr ...]
     * [ORDER BY {unsigned_integer | col_name | expr}
     * [ASC | DESC] [,col_name ...]]
     * [SEPARATOR str_val])</a>
     */
    public static SimpleExpression groupConcat(Expression exp) {
        return LiteralFunctions.oneArgFunc("GROUP_CONCAT", exp, StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link StringType}
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
    public static SimpleExpression groupConcat(SQLs.ArgDistinct distinct, Expression exp) {
        FuncExpUtils.assertDistinct(distinct, MySQLs.DISTINCT);
        return LiteralFunctions.compositeFunc("GROUP_CONCAT", Arrays.asList(distinct, exp), StringType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link StringType}
     *
     * @param exp expression or multi-value expression
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT([DISTINCT] expr [,expr ...]
     * [ORDER BY {unsigned_integer | col_name | expr}
     * [ASC | DESC] [,col_name ...]]
     * [SEPARATOR str_val])</a>
     * @see SQLs#rowParam(TypeInfer, Collection)
     * @see SQLs#rowLiteral(TypeInfer, Collection)
     */
    public static SimpleExpression groupConcat(Expression exp, Consumer<MySQLFunction._GroupConcatOrderBySpec> consumer) {

        return LiteralFunctions.compositeFunc("GROUP_CONCAT", Arrays.asList(exp, MySQLFunctions.groupConcatClause(consumer)), StringType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link StringType}
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
    public static SimpleExpression groupConcat(SQLs.ArgDistinct distinct, Expression exp,
                                         Consumer<MySQLFunction._GroupConcatOrderBySpec> consumer) {
        FuncExpUtils.assertDistinct(distinct, MySQLs.DISTINCT);

        return LiteralFunctions.compositeFunc("GROUP_CONCAT", Arrays.asList(distinct, exp, MySQLFunctions.groupConcatClause(consumer)),
                StringType.INSTANCE
        );
    }

    /**
     * <p>GROUP_CONCAT function static method.
     * <p>The {@link MappingType} of function return type: {@link StringType}
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT([DISTINCT] expr [,expr ...]
     * [ORDER BY {unsigned_integer | col_name | expr}
     * [ASC | DESC] [,col_name ...]]
     * [SEPARATOR str_val])</a>
     * @see SQLs#rowParam(TypeInfer, Collection)
     * @see SQLs#rowLiteral(TypeInfer, Collection)
     */
    public static SimpleExpression groupConcat(Consumer<Clause._VariadicExprSpaceClause> expConsumer,
                                         Consumer<MySQLFunction._GroupConcatOrderBySpec> consumer) {


        final ArrayList<Object> argList = _Collections.arrayList(3);

        CriteriaUtils.invokeConsumer(FuncExpUtils.variadicExpressionClause(true, SQLs.COMMA, argList), expConsumer);

        if (argList.size() == 0) {
            throw CriteriaUtils.dontAddAnyItem();
        }

        argList.add(MySQLFunctions.groupConcatClause(consumer));

        return LiteralFunctions.compositeFunc("GROUP_CONCAT", argList, StringType.INSTANCE);
    }


    /**
     * <p>GROUP_CONCAT function static method.
     * <p>The {@link MappingType} of function return type: {@link StringType}
     *
     * @param distinct {@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT([DISTINCT] expr [,expr ...]
     * [ORDER BY {unsigned_integer | col_name | expr}
     * [ASC | DESC] [,col_name ...]]
     * [SEPARATOR str_val])</a>
     * @see SQLs#rowParam(TypeInfer, Collection)
     * @see SQLs#rowLiteral(TypeInfer, Collection)
     */
    public static SimpleExpression groupConcat(SQLs.ArgDistinct distinct, Consumer<Clause._VariadicExprSpaceClause> expConsumer,
                                         Consumer<MySQLFunction._GroupConcatOrderBySpec> consumer) {
        FuncExpUtils.assertDistinct(distinct, MySQLs.DISTINCT);

        final ArrayList<Object> argList = _Collections.arrayList(4);

        argList.add(distinct);

        CriteriaUtils.invokeConsumer(FuncExpUtils.variadicExpressionClause(true, SQLs.COMMA, argList), expConsumer);

        if (argList.size() < 2) {
            throw CriteriaUtils.dontAddAnyItem();
        }

        argList.add(MySQLFunctions.groupConcatClause(consumer));

        return LiteralFunctions.compositeFunc("GROUP_CONCAT", argList, StringType.INSTANCE);
    }

    /**
     * <p>GROUP_CONCAT function dynamic method.
     * <p>The {@link MappingType} of function return type: {@link StringType}
     *
     * @param space see {@link SQLs#SPACE}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT([DISTINCT] expr [,expr ...]
     * [ORDER BY {unsigned_integer | col_name | expr}
     * [ASC | DESC] [,col_name ...]]
     * [SEPARATOR str_val])</a>
     * @see SQLs#rowParam(TypeInfer, Collection)
     * @see SQLs#rowLiteral(TypeInfer, Collection)
     */
    public static SimpleExpression groupConcat(SQLs.SymbolSpace space, Consumer<Consumer<Expression>> expConsumer,
                                         Consumer<MySQLFunction._GroupConcatOrderBySpec> consumer) {


        final ArrayList<Object> argList = _Collections.arrayList(4);

        final Consumer<Expression> expAcceptor;
        expAcceptor = exp -> {
            if (argList.size() > 0) {
                argList.add(SQLs.COMMA);
            }
            argList.add(exp);
        };

        CriteriaUtils.invokeConsumer(expAcceptor, expConsumer);


        if (argList.size() == 0) {
            throw CriteriaUtils.dontAddAnyItem();
        }

        argList.add(MySQLFunctions.groupConcatClause(consumer));

        return LiteralFunctions.compositeFunc("GROUP_CONCAT", argList, StringType.INSTANCE);
    }

    /**
     * <p>GROUP_CONCAT function dynamic method.
     * <p>The {@link MappingType} of function return type: {@link StringType}
     *
     * @param space    see {@link SQLs#SPACE}
     * @param distinct {@link SQLs#DISTINCT} or {@link MySQLs#DISTINCT}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT([DISTINCT] expr [,expr ...]
     * [ORDER BY {unsigned_integer | col_name | expr}
     * [ASC | DESC] [,col_name ...]]
     * [SEPARATOR str_val])</a>
     * @see SQLs#rowParam(TypeInfer, Collection)
     * @see SQLs#rowLiteral(TypeInfer, Collection)
     */
    public static SimpleExpression groupConcat(SQLs.ArgDistinct distinct, SQLs.SymbolSpace space, Consumer<Consumer<Expression>> expConsumer,
                                         Consumer<MySQLFunction._GroupConcatOrderBySpec> consumer) {
        FuncExpUtils.assertDistinct(distinct, MySQLs.DISTINCT);

        final ArrayList<Object> argList = _Collections.arrayList(4);

        argList.add(distinct);

        final Consumer<Expression> expAcceptor;
        expAcceptor = exp -> {
            if (argList.size() > 1) {
                argList.add(SQLs.COMMA);
            }
            argList.add(exp);
        };

        CriteriaUtils.invokeConsumer(expAcceptor, expConsumer);

        if (argList.size() < 2) {
            throw CriteriaUtils.dontAddAnyItem();
        }

        argList.add(MySQLFunctions.groupConcatClause(consumer));

        return LiteralFunctions.compositeFunc("GROUP_CONCAT", argList, StringType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type:{@link JsonType#TEXT}
     *
     * @param exp parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-arrayagg">JSON_ARRAYAGG(col_or_expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc jsonArrayAgg(Expression exp) {
        return MySQLFunctions.oneArgAggregate("JSON_ARRAYAGG", exp, JsonType.TEXT);
    }

    /**
     * <p>The {@link MappingType} of function return type:{@link JsonType#TEXT}
     *
     * @param key   non-null parameter or {@link Expression},but couldn't be null.
     * @param value non-null parameter or {@link Expression},but couldn't be null.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_json-objectagg">JSON_OBJECTAGG(key, value) [over_clause]</a>
     */
    public static _AggregateWindowFunc jsonObjectAgg(Expression key, Expression value) {
        return MySQLFunctions.multiArgAggregateWindowFunc("JSON_OBJECTAGG", Arrays.asList(key, value), JsonType.TEXT);
    }


    /**
     * <p>The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc max(Expression exp) {
        return MySQLFunctions.oneArgAggregate("MAX", exp, exp.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc max(SQLs.ArgDistinct distinct, Expression exp) {
        FuncExpUtils.assertDistinct(distinct, MySQLs.DISTINCT);
        return MySQLFunctions.compositeAggWindowFunc("MAX", Arrays.asList(distinct, exp), exp.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     *
     * @param exp non-null parameter or {@link Expression},but couldn't be {@link SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc min(Expression exp) {
        return MySQLFunctions.oneArgAggregate("MIN", exp, exp.typeMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     *
     * @param exp non-null parameter or {@link Expression},but couldn't be {@link SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc min(SQLs.ArgDistinct distinct, Expression exp) {
        FuncExpUtils.assertDistinct(distinct, MySQLs.DISTINCT);
        return MySQLFunctions.compositeAggWindowFunc("MIN", Arrays.asList(distinct, exp), exp.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  DoubleType}.
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_std">STD(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc std(Expression exp) {
        return MySQLFunctions.oneArgAggregate("STD", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  DoubleType}.
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev">STDDEV(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc stdDev(Expression exp) {
        return MySQLFunctions.oneArgAggregate("STDDEV", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  DoubleType}.
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-pop">STDDEV_POP(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc stdDevPop(Expression exp) {
        return MySQLFunctions.oneArgAggregate("STDDEV_POP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  DoubleType}.
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_stddev-samp">STDDEV_SAMP(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc stdDevSamp(Expression exp) {
        return MySQLFunctions.oneArgAggregate("STDDEV_SAMP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc sum(Expression exp) {
        return MySQLFunctions.oneArgAggregate("SUM", exp, exp.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param exp non-null parameter or {@link Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc sum(SQLs.ArgDistinct distinct, Expression exp) {
        FuncExpUtils.assertDistinct(distinct, MySQLs.DISTINCT);
        return MySQLFunctions.compositeAggWindowFunc("SUM", Arrays.asList(distinct, exp), exp.typeMeta());
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  DoubleType}.
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-pop">VAR_POP(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc varPop(Expression exp) {
        return MySQLFunctions.oneArgAggregate("VAR_POP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  DoubleType}.
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VAR_SAMP(xpr) [over_clause]</a>
     */
    public static _AggregateWindowFunc varSamp(Expression exp) {
        return MySQLFunctions.oneArgAggregate("VAR_SAMP", exp, DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type:  {@link  DoubleType}.
     *
     * @param exp null or parameter or {@link Expression}.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_var-samp">VARIANCE(expr) [over_clause]</a>
     */
    public static _AggregateWindowFunc variance(Expression exp) {
        return MySQLFunctions.oneArgAggregate("VARIANCE", exp, DoubleType.INSTANCE);
    }


    /*-------------------below window function-------------------*/


    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  DoubleType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_cume-dist">CUME_DIST() over_clause</a>
     */
    public static _OverSpec cumeDist() {
        return MySQLFunctions.noArgWindowFunc("CUME_DIST", DoubleType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:  {@link  LongType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     */
    public static _OverSpec denseRank() {
        return MySQLFunctions.noArgWindowFunc("DENSE_RANK", LongType.INSTANCE);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_first-value">FIRST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static _OverSpec firstValue(Expression expr) {
        return MySQLFunctions.noArgWindowFunc("FIRST_VALUE", expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_last-value">LAST_VALUE(expr) [null_treatment] over_clause</a>
     */
    public static _OverSpec lastValue(Expression expr) {
        return MySQLFunctions.noArgWindowFunc("LAST_VALUE", expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null parameter or {@link  Expression}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lag">LAG(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lag(Expression expr) {
        return MySQLFunctions.oneArgWindowFunc("LAG", expr, expr.typeMeta());
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
        return MySQLFunctions.twoArgWindowFunc("LAG", expr, n, expr.typeMeta());
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
        return MySQLFunctions.threeArgWindow("LAG", expr, n, defaultValue, expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null parameter or {@link  Expression},but couldn't be {@link  SQLs#NULL}
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_lead">LEAD(expr [, N[, default]]) [null_treatment] over_clause</a>
     */
    public static _OverSpec lead(Expression expr) {
        return MySQLFunctions.oneArgWindowFunc("LEAD", expr, expr.typeMeta());
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
        return MySQLFunctions.twoArgWindowFunc("LEAD", expr, n, expr.typeMeta());
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
        return MySQLFunctions.threeArgWindow("LEAD", expr, n, defaultValue, expr.typeMeta());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link MappingType} of expr.
     *
     * @param expr non-null {@link  Expression}
     * @param n    positive.output literal.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_nth-value">NTH_VALUE(expr, N) [from_first_last] [null_treatment] over_clause</a>
     */
    public static _FromFirstLastOverSpec nthValue(Expression expr, Expression n) {
        return MySQLFunctions.twoArgFromFirstWindowFunc("NTH_VALUE", expr, n, expr.typeMeta());
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
        return MySQLFunctions.oneArgWindowFunc("NTILE", n, LongType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link DoubleType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">PERCENT_RANK() over_clause</a>
     */
    public static _OverSpec percentRank() {
        return MySQLFunctions.noArgWindowFunc("PERCENT_RANK", DoubleType.INSTANCE);
    }

    /**
     * <p>The {@link MappingType} of function return type: {@link LongType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_percent-rank">RANK() over_clause</a>
     */
    public static _OverSpec rank() {
        return MySQLFunctions.noArgWindowFunc("RANK", LongType.INSTANCE);
    }


    /**
     * <p>The {@link MappingType} of function return type: {@link LongType}.
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_row-number">ROW_NUMBER() over_clause</a>
     */
    public static _OverSpec rowNumber() {
        return MySQLFunctions.noArgWindowFunc("ROW_NUMBER", LongType.INSTANCE);
    }




    /*-------------------below private methos -------------------*/

    /**
     * @see #bitAnd(Expression)
     * @see #bitOr(Expression)
     * @see #bitXor(Expression)
     */
    private static MappingType _bitwiseFuncType(final Expression expr) {
        final MappingType returnType;

        TypeMeta paramMeta = expr.typeMeta();
        if (!(paramMeta instanceof MappingType)) {
            paramMeta = paramMeta.mappingType();
        }

        if (paramMeta instanceof MappingType.SqlIntegerType || paramMeta instanceof MappingType.SqlBitType) {
            returnType = UnsignedLongType.INSTANCE;
        } else {
            returnType = VarBinaryType.INSTANCE;
        }
        return returnType;
    }


}
