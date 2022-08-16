package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.mysql.MySQLClause;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.JsonBeanType;
import io.army.mapping.optional.JsonListType;
import io.army.mapping.optional.JsonMapType;
import io.army.meta.ParamMeta;
import io.army.util._StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLs}</li>
 *     </ul>
 * </p>
 * <p>
 *     This class provide MySQL function method.
 * </p>
 * package class
 */
abstract class MySQLFuncSyntax extends MySQLSyntax {

    /**
     * package constructor
     */
    MySQLFuncSyntax() {
    }


    /*-------------------below Aggregate Function  -------------------*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec avg(@Nullable Object expr) {
        return MySQLFunctions.aggregateWindowFunc("AVG", expr, DoubleType.INSTANCE);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_avg">AVG([DISTINCT] expr) [over_clause]</a>
     */
    public static FuncExpression avg(@Nullable SQLModifier distinct, @Nullable Object exp) {
        if (distinct != null && distinct != SQLModifier.DISTINCT) {
            throw CriteriaUtils.funcArgError("AVG", distinct);
        }
        return SQLFunctions.oneArgOptionFunc("AVG", distinct, exp, null, DoubleType.INSTANCE);
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
        return MySQLFunctions.aggregateWindowFunc("BIT_AND", expr, mappingType);
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
        return MySQLFunctions.aggregateWindowFunc("BIT_OR", expr, mappingType);
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
        return MySQLFunctions.aggregateWindowFunc("BIT_XOR", expr, mappingType);
    }

    /**
     * @see #count(Object)
     * @see #countAsInt(Object)
     * @see #countAsInt()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count() {
        return _count(null, SQLs.star(), LongType.INSTANCE);
    }

    /**
     * @see #count(Object)
     * @see #countAsInt(Object)
     * @see #count()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec countAsInt() {
        return _count(null, SQLs.star(), IntegerType.INSTANCE);
    }

    /**
     * @see #countAsInt(Object)
     * @see #count()
     * @see #countAsInt()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count(@Nullable Object expr) {
        return _count(null, expr, LongType.INSTANCE);
    }

    /**
     * @see #count(Object)
     * @see #count()
     * @see #countAsInt()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec countAsInt(@Nullable Object expr) {
        return _count(null, expr, IntegerType.INSTANCE);
    }

    /**
     * @see #countAsInt(SQLModifier, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count(SQLModifier distinct, @Nullable Object expressions) {
        Objects.requireNonNull(distinct);
        return _count(distinct, expressions, LongType.INSTANCE);
    }

    /**
     * @see #count(SQLModifier, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec countAsInt(SQLModifier distinct, @Nullable Object expressions) {
        Objects.requireNonNull(distinct);
        return _count(distinct, expressions, IntegerType.INSTANCE);
    }


    /**
     * @see #groupConcat(SQLModifier, Object, Supplier)
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
     * @see #groupConcat(SQLModifier, Object)
     * @see #groupConcat(SQLModifier, Object, Supplier)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static FuncExpression groupConcat(@Nullable Object expressions) {
        return _groupConcat(null, expressions, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param distinct    null or {@link  SQLModifier#DISTINCT}
     * @param expressions parameter or {@link Expression} or List(element:null or parameter or {@link Expression})
     * @see #groupConcat(Object)
     * @see #groupConcat(SQLModifier, Object, Supplier)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static FuncExpression groupConcat(@Nullable SQLModifier distinct, @Nullable Object expressions) {
        return _groupConcat(distinct, expressions, (Clause) null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param distinct    null or {@link  SQLModifier#DISTINCT}
     * @param expressions parameter or {@link Expression} or List(element:null or parameter or {@link Expression})
     * @param supplier    supplier of {@link  #groupConcatClause()},allow to return null
     * @see #groupConcat(Object)
     * @see #groupConcat(SQLModifier, Object)
     * @see #groupConcatClause()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static FuncExpression groupConcat(@Nullable SQLModifier distinct, @Nullable Object expressions, Supplier<Clause> supplier) {
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
     * @see #max(SQLModifier, Object)
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
     * @param distinct null or {@link  SQLModifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #max(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec max(final @Nullable SQLModifier distinct, final Object expr) {
        return _minOrMax("MAX", distinct, expr);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: the {@link  MappingType} of expr.
     * </p>
     *
     * @param expr non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #min(SQLModifier, Object)
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
     * @param distinct null or {@link  SQLModifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #min(Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec min(final @Nullable SQLModifier distinct, final Object expr) {
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
     * @see #sum(SQLModifier, Object)
     * @see #sum(SQLModifier, Object, MappingType)
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
     * @param distinct null or {@link  SQLModifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #sum(Object)
     * @see #sum(SQLModifier, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec sum(@Nullable SQLModifier distinct, Object expr) {
        return _sum(distinct, expr, null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: returnType or the {@link MappingType} of expr.
     * </p>
     *
     * @param distinct   null or {@link  SQLModifier#DISTINCT}
     * @param expr       non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @param returnType nullable,if null ,then {@link MappingType} of function return type is the {@link MappingType} of expr
     * @see #sum(Object)
     * @see #sum(SQLModifier, Object)
     * @see #sum(SQLModifier, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    public static _AggregateOverSpec sum(@Nullable SQLModifier distinct, Object expr
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
     * @see #denseRankAsInt()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     */
    public static _OverSpec denseRank() {
        return _denseRank(LongType.INSTANCE);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  IntegerType}.
     * </p>
     *
     * @see #denseRank()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     */
    public static _OverSpec denseRankAsInt() {
        return _denseRank(IntegerType.INSTANCE);
    }



    /*-------------------below private method -------------------*/

    /**
     * @see #count(Object)
     * @see #count()
     * @see #countAsInt()
     * @see #count(SQLModifier, Object)
     * @see #countAsInt(SQLModifier, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _count(@Nullable SQLModifier distinct, @Nullable Object expressions
            , MappingType returnType) {

        final String funcName = "COUNT";

        if (distinct != null && distinct != SQLModifier.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        final _AggregateOverSpec func;
        if (!(expressions instanceof List)) {
            if (distinct != null && expressions == null) {
                String m = String.format("function %s option[%s] but expr is null.", funcName, distinct);
                throw CriteriaContextStack.criteriaError(CriteriaContextStack.peek(), m);
            }
            func = MySQLFunctions.aggregateWindowFunc(funcName, distinct, expressions, returnType);
        } else if (distinct == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        } else {
            func = MySQLFunctions.multiArgAggregateWindowFunc(funcName, distinct, (List<?>) expressions
                    , null, returnType);
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
            final ParamMeta paramMeta = ((Expression) expr).paramMeta();
            if (paramMeta instanceof ParamMeta.Delay) {
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


    private static FuncExpression _groupConcat(@Nullable SQLModifier distinct, @Nullable Object expressions
            , @Nullable Clause clause) {

        final String funcName = "GROUP_CONCAT";

        if (distinct != null && distinct != SQLModifier.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        if (clause != null && !(clause instanceof MySQLFunctions.GroupConcatClause)) {
            throw CriteriaUtils.funcArgError(funcName, clause);
        }
        final FuncExpression func;
        if (expressions instanceof List) {
            func = SQLFunctions.multiArgOptionFunc(funcName, distinct, (List<?>) expressions
                    , clause, StringType.INSTANCE);
        } else {
            func = SQLFunctions.oneArgOptionFunc(funcName, distinct, expressions, clause, StringType.INSTANCE);
        }
        return func;
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
    private static _AggregateOverSpec _jsonArrayAgg(final Object expr, final @Nullable ParamMeta returnType) {
        final String funcName = "JSON_ARRAYAGG";
        if (expr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        final ArmyExpression expression;
        expression = SQLFunctions.funcParam(expr);
        final ParamMeta elementType = expression.paramMeta();

        final ParamMeta actualReturnType;
        if (returnType != null) {
            actualReturnType = returnType;
        } else if (elementType instanceof ParamMeta.Delay) {
            actualReturnType = CriteriaSupports.delayParamMeta((ParamMeta.Delay) elementType, JsonListType::from);
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
            , final @Nullable ParamMeta returnType) {

        final String funcName = "JSON_OBJECTAGG";

        final ArmyExpression keyExpr, valueExpr;
        keyExpr = SQLFunctions.funcParam(key);
        valueExpr = SQLFunctions.funcParam(value);

        if (keyExpr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, key);
        }
        if (valueExpr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, value);
        }
        final ParamMeta actualReturnType;
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
     * @param distinct null or {@link  SQLModifier#DISTINCT}
     * @param expr     non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @see #min(Object)
     * @see #min(SQLModifier, Object)
     * @see #max(Object)
     * @see #max(SQLModifier, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_min">MIN([DISTINCT] expr) [over_clause]</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_max">MAX([DISTINCT] expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _minOrMax(final String funcName, final @Nullable SQLModifier distinct
            , final Object expr) {
        if (!(funcName.equals("MAX") || funcName.equals("MIN"))) {
            //no bug,never here
            throw new IllegalArgumentException();
        }
        if (distinct != null && distinct != SQLModifier.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        final ArmyExpression expression;
        expression = SQLFunctions.funcParam(expr);
        if (expression instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        return MySQLFunctions.aggregateWindowFunc(funcName, distinct, expression, expression.paramMeta());
    }


    /**
     * <p>
     * The {@link MappingType} of function return type: returnType or the {@link MappingType} of expr.
     * </p>
     *
     * @param distinct   null or {@link  SQLModifier#DISTINCT}
     * @param expr       non-null parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     * @param returnType nullable,if null ,then {@link MappingType} of function return type is the {@link MappingType} of expr
     * @see #sum(Object)
     * @see #sum(SQLModifier, Object)
     * @see #sum(SQLModifier, Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_sum">SUM([DISTINCT] expr) [over_clause]</a>
     */
    private static _AggregateOverSpec _sum(final @Nullable SQLModifier distinct, final @Nullable Object expr
            , final @Nullable ParamMeta returnType) {
        if (expr == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        }
        final String funcName = "SUM";
        if (distinct != null && distinct != SQLModifier.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        final ArmyExpression expression;
        expression = SQLFunctions.funcParam(expr);
        if (expression instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        final ParamMeta actualReturnType;
        if (returnType != null) {
            actualReturnType = returnType;
        } else {
            actualReturnType = expression.paramMeta();
        }
        return MySQLFunctions.aggregateWindowFunc(funcName, distinct, expression, actualReturnType);
    }


    /**
     * <p>
     * The {@link MappingType} of function return type:  returnType
     * </p>
     *
     * @param returnType {@link  LongType} or {@link  IntegerType}
     * @see #denseRank()
     * @see #denseRankAsInt()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-function-descriptions.html#function_dense-rank">DENSE_RANK() over_clause</a>
     */
    private static _OverSpec _denseRank(MappingType returnType) {
        return MySQLFunctions.noArgWindowFunc("DENSE_RANK", returnType);
    }


}
