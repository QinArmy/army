package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.HintStrategy;
import io.army.criteria.mysql.MySQLClause;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.JsonListType;
import io.army.meta.ParamMeta;
import io.army.util._StringUtils;

import java.util.EnumSet;
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
 * package class
 */
@SuppressWarnings("unused")
abstract class MySQLSyntax extends Syntax {

    /**
     * package constructor
     */
    MySQLSyntax() {
    }

    public interface _OverSpec extends Window._OverClause {

        Window._SimpleOverLestParenSpec over();

    }

    public interface _AggregateOverSpec extends _OverSpec, FuncExpression {

    }


    /*################################## blow join-order hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static Hint joinFixedOrder(@Nullable String queryBlockName) {
        return MySQLHints.joinFixedOrder(queryBlockName);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static Hint joinOrder(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.joinOrderHint(MySQLHints.HintType.JOIN_ORDER, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static Hint joinPrefix(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.joinOrderHint(MySQLHints.HintType.JOIN_PREFIX, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static Hint joinSuffix(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.joinOrderHint(MySQLHints.HintType.JOIN_SUFFIX, queryBlockName, tableNameList);
    }

    /*################################## blow table-level hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint bka(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.BKA, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint noBka(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.NO_BKA, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint bnl(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.BNL, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint noBnl(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.NO_BNL, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint derivedConditionPushdown(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.DERIVED_CONDITION_PUSHDOWN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint noDerivedConditionPushdown(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.NO_DERIVED_CONDITION_PUSHDOWN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint hashJoin(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.HASH_JOIN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint noHashJoin(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.NO_HASH_JOIN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint merge(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.MERGE, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static Hint noMerge(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.HintType.NO_MERGE, queryBlockName, tableNameList);
    }

    /*################################## blow index-level hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint groupIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.GROUP_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noGroupIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_GROUP_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint index(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint indexMerge(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.INDEX_MERGE, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noIndexMerge(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_INDEX_MERGE, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint joinIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.JOIN_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noJoinIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_JOIN_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint mrr(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.MRR, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noMrr(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_MRR, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noIcp(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_ICP, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noRangeOptimization(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_RANGE_OPTIMIZATION, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint orderIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.ORDER_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noOrderIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_ORDER_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint skipScan(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.SKIP_SCAN, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static Hint noSkipScan(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.HintType.NO_SKIP_SCAN, queryBlockName, tableName, indexNameList);
    }

    /*################################## blow SubQuery hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-subquery">Subquery Optimizer Hints</a>
     */
    public static Hint semijoin(@Nullable String queryBlockName, EnumSet<HintStrategy> strategySet) {
        return MySQLHints.subQueryHint(MySQLHints.HintType.SEMIJOIN, queryBlockName, strategySet);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-subquery">Subquery Optimizer Hints</a>
     */
    public static Hint noSemijoin(@Nullable String queryBlockName, EnumSet<HintStrategy> strategySet) {
        return MySQLHints.subQueryHint(MySQLHints.HintType.NO_SEMIJOIN, queryBlockName, strategySet);
    }

    /*################################## blow Statement Execution Time Optimizer hint ##################################*/

    /**
     * @param millis null or non-negative
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-execution-time">Statement Execution Time Optimizer Hints</a>
     */
    public static Hint maxExecutionTime(@Nullable Long millis) {
        return MySQLHints.maxExecutionTime(millis);
    }

    /*################################## blow Variable-Setting Hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-set-var">Variable-Setting Hint Syntax</a>
     */
    public static Hint setVar(String varValuePair) {
        return MySQLHints.setVar(varValuePair);
    }

    /*################################## blow Resource Group Hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-resource-group">Resource Group Hint Syntax</a>
     */
    public static Hint resourceGroup(String groupName) {
        return MySQLHints.resourceGroup(groupName);
    }

    /*################################## blow Optimizer Hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-query-block-naming">Optimizer Hints for Naming Query Blocks</a>
     */
    public static Hint qbName(String name) {
        return MySQLHints.qbName(name);
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
    public static FuncExpression avg(@Nullable Distinct distinct, @Nullable Object exp) {
        if (distinct != null && distinct != Distinct.DISTINCT) {
            throw CriteriaUtils.funcArgError("AVG", distinct);
        }
        return SQLFunctions.oneArgOptionFunc("AVG", distinct, exp, null, DoubleType.INSTANCE);
    }

    /**
     * @see #bitAnd(Object, MappingType)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_bit-and">BIT_AND(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec bitAnd(final @Nullable Object expr) {
        return bitAnd(expr, bitwiseAggregateFuncReturnType(expr));
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
        return bitOr(expr, bitwiseAggregateFuncReturnType(expr));
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
        return bitXor(expr, bitwiseAggregateFuncReturnType(expr));
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
        return count(null, SQLs.star(), LongType.INSTANCE);
    }

    /**
     * @see #count(Object)
     * @see #countAsInt(Object)
     * @see #count()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec countAsInt() {
        return count(null, SQLs.star(), IntegerType.INSTANCE);
    }

    /**
     * @see #countAsInt(Object)
     * @see #count()
     * @see #countAsInt()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count(@Nullable Object expr) {
        return count(null, expr, LongType.INSTANCE);
    }

    /**
     * @see #count(Object)
     * @see #count()
     * @see #countAsInt()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec countAsInt(@Nullable Object expr) {
        return count(null, expr, IntegerType.INSTANCE);
    }

    /**
     * @see #countAsInt(Distinct, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec count(Distinct distinct, @Nullable Object expressions) {
        Objects.requireNonNull(distinct);
        return count(distinct, expressions, LongType.INSTANCE);
    }

    /**
     * @see #count(Distinct, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    public static _AggregateOverSpec countAsInt(Distinct distinct, @Nullable Object expressions) {
        Objects.requireNonNull(distinct);
        return count(distinct, expressions, IntegerType.INSTANCE);
    }


    /**
     * @see #groupConcat(Distinct, Object, Supplier)
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
     * @see #groupConcat(Distinct, Object)
     * @see #groupConcat(Distinct, Object, Supplier)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static FuncExpression groupConcat(@Nullable Object expressions) {
        return groupConcat(null, expressions, (Clause) null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param distinct    null or {@link  Distinct#DISTINCT}
     * @param expressions parameter or {@link Expression} or List(element:null or parameter or {@link Expression})
     * @see #groupConcat(Object)
     * @see #groupConcat(Distinct, Object, Supplier)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static FuncExpression groupConcat(@Nullable Distinct distinct, @Nullable Object expressions) {
        return groupConcat(distinct, expressions, (Clause) null);
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link StringType}
     * </p>
     *
     * @param distinct    null or {@link  Distinct#DISTINCT}
     * @param expressions parameter or {@link Expression} or List(element:null or parameter or {@link Expression})
     * @param supplier    supplier of {@link  #groupConcatClause()},allow to return null
     * @see #groupConcat(Object)
     * @see #groupConcat(Distinct, Object)
     * @see #groupConcatClause()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_group-concat">GROUP_CONCAT(expr)</a>
     */
    public static FuncExpression groupConcat(@Nullable Distinct distinct, @Nullable Object expressions, Supplier<Clause> supplier) {
        return groupConcat(distinct, expressions, supplier.get());
    }

    /**
     * <p>
     * The {@link MappingType} of function return type:{@link JsonListType}
     * </p>
     *
     * @param expr parameter or {@link Expression},but couldn't be {@link SQLs#nullWord()}.
     */
    public static _AggregateOverSpec jsonArrayAgg(final Object expr) {
        final String funcName = "JSON_ARRAYAGG";
        if (expr instanceof SQLs.NullWord) {
            throw CriteriaUtils.funcArgError(funcName, expr);
        }
        final ArmyExpression expression;
        expression = SQLFunctions.funcParam(expr);
        final ParamMeta elementType = expression.paramMeta();

        final ParamMeta returnType;
        if (elementType instanceof ParamMeta.Delay) {
            returnType = CriteriaSupports.delayParamMeta((ParamMeta.Delay) elementType, JsonListType::from);
        } else {
            returnType = JsonListType.from(elementType.mappingType());
        }
        return MySQLFunctions.optionAggregateWindowFunc(funcName, null, expression, returnType);
    }



    /*-------------------below private method -------------------*/

    /**
     * @see #count(Object)
     * @see #count()
     * @see #countAsInt()
     * @see #count(Distinct, Object)
     * @see #countAsInt(Distinct, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/aggregate-functions.html#function_count">COUNT(expr) [over_clause]</a>
     */
    private static _AggregateOverSpec count(@Nullable Distinct distinct, @Nullable Object expressions
            , MappingType returnType) {

        final String funcName = "COUNT";

        if (distinct != null && distinct != Distinct.DISTINCT) {
            throw CriteriaUtils.funcArgError(funcName, distinct);
        }
        final _AggregateOverSpec func;
        if (!(expressions instanceof List)) {
            func = MySQLFunctions.optionAggregateWindowFunc(funcName, distinct, expressions, returnType);
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
    private static MappingType bitwiseAggregateFuncReturnType(final @Nullable Object expr) {
        final MappingType returnType;
        if (expr == null) {
            returnType = LongType.INSTANCE;
        } else if (expr instanceof String && _StringUtils.isBinary((String) expr)) {
            returnType = StringType.INSTANCE;
        } else if (expr instanceof SqlValueParam.SingleNonNamedValue) {
            final Object paramValue;
            paramValue = ((SqlValueParam.SingleNonNamedValue) expr).value();
            if (paramValue instanceof String && _StringUtils.isBinary((String) paramValue)) {
                returnType = StringType.INSTANCE;
            } else {
                returnType = LongType.INSTANCE;
            }
        } else if (expr instanceof TypeInfer) {
            final Class<?> javaType;
            if (expr instanceof TypeInfer.DelayTypeInfer) {
                final TypeInfer.DelayTypeInfer typeInfer = (TypeInfer.DelayTypeInfer) expr;
                if (typeInfer.isPrepared()) {
                    javaType = typeInfer.paramMeta().mappingType().javaType();
                } else {
                    javaType = null;
                }
            } else {
                javaType = ((TypeInfer) expr).paramMeta().mappingType().javaType();
            }
            if (javaType != null && Number.class.isAssignableFrom(javaType)) {
                returnType = LongType.INSTANCE;
            } else {
                returnType = StringType.INSTANCE;
            }
        } else {
            returnType = LongType.INSTANCE;
        }
        return returnType;
    }

    private static FuncExpression groupConcat(@Nullable Distinct distinct, @Nullable Object expressions
            , @Nullable Clause clause) {

        final String funcName = "GROUP_CONCAT";

        if (distinct != null && distinct != Distinct.DISTINCT) {
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


}
