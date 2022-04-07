package io.army.criteria.impl;

import io.army.lang.Nullable;

import java.util.EnumSet;
import java.util.List;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLs}</li>
 *         <li>{@link MySQLs80}</li>
 *     </ul>
 * </p>
 * package class
 */
@SuppressWarnings("unused")
abstract class MySQLSyntax extends StandardFunctions {

    /**
     * package constructor
     */
    MySQLSyntax() {
    }

    /*################################## blow join-order hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static io.army.criteria.Hint joinFixedOrder(@Nullable String queryBlockName) {
        return MySQLHints.joinFixedOrder(queryBlockName);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static io.army.criteria.Hint joinOrder(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.joinOrderHint(MySQLHints.Hint.JOIN_ORDER, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static io.army.criteria.Hint joinPrefix(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.joinOrderHint(MySQLHints.Hint.JOIN_PREFIX, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static io.army.criteria.Hint joinSuffix(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.joinOrderHint(MySQLHints.Hint.JOIN_SUFFIX, queryBlockName, tableNameList);
    }

    /*################################## blow table-level hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint bka(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.Hint.BKA, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noBka(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.Hint.NO_BKA, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint bnl(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.Hint.BNL, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noBnl(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.Hint.NO_BNL, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint derivedConditionPushdown(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.Hint.DERIVED_CONDITION_PUSHDOWN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noDerivedConditionPushdown(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.Hint.NO_DERIVED_CONDITION_PUSHDOWN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint hashJoin(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.Hint.HASH_JOIN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noHashJoin(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.Hint.NO_HASH_JOIN, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint merge(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.Hint.MERGE, queryBlockName, tableNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-table-level">Table-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noMerge(@Nullable String queryBlockName, List<String> tableNameList) {
        return MySQLHints.tableLevelHint(MySQLHints.Hint.NO_MERGE, queryBlockName, tableNameList);
    }

    /*################################## blow index-level hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint groupIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.GROUP_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noGroupIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.NO_GROUP_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint index(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.NO_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint indexMerge(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.INDEX_MERGE, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noIndexMerge(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.NO_INDEX_MERGE, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint joinIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.JOIN_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noJoinIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.NO_JOIN_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint mrr(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.MRR, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noMrr(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.NO_MRR, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noIcp(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.NO_ICP, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noRangeOptimization(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.NO_RANGE_OPTIMIZATION, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint orderIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.ORDER_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noOrderIndex(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.NO_ORDER_INDEX, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint skipScan(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.SKIP_SCAN, queryBlockName, tableName, indexNameList);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-index-level">Index-Level Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noSkipScan(@Nullable String queryBlockName, String tableName, List<String> indexNameList) {
        return MySQLHints.indexLevelHint(MySQLHints.Hint.NO_SKIP_SCAN, queryBlockName, tableName, indexNameList);
    }

    /*################################## blow SubQuery hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-subquery">Subquery Optimizer Hints</a>
     */
    public static io.army.criteria.Hint semijoin(@Nullable String queryBlockName, EnumSet<HintStrategy> strategySet) {
        return MySQLHints.subQueryHint(MySQLHints.Hint.SEMIJOIN, queryBlockName, strategySet);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-subquery">Subquery Optimizer Hints</a>
     */
    public static io.army.criteria.Hint noSemijoin(@Nullable String queryBlockName, EnumSet<HintStrategy> strategySet) {
        return MySQLHints.subQueryHint(MySQLHints.Hint.NO_SEMIJOIN, queryBlockName, strategySet);
    }

    /*################################## blow Statement Execution Time Optimizer hint ##################################*/

    /**
     * @param millis null or non-negative
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-execution-time">Statement Execution Time Optimizer Hints</a>
     */
    public static io.army.criteria.Hint maxExecutionTime(@Nullable Long millis) {
        return MySQLHints.maxExecutionTime(millis);
    }

    /*################################## blow Variable-Setting Hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-set-var">Variable-Setting Hint Syntax</a>
     */
    public static io.army.criteria.Hint setVar(String varValuePair) {
        return MySQLHints.setVar(varValuePair);
    }

    /*################################## blow Resource Group Hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-resource-group">Resource Group Hint Syntax</a>
     */
    public static io.army.criteria.Hint resourceGroup(String groupName) {
        return MySQLHints.resourceGroup(groupName);
    }

    /*################################## blow Optimizer Hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-query-block-naming">Optimizer Hints for Naming Query Blocks</a>
     */
    public static io.army.criteria.Hint qbName(String name) {
        return MySQLHints.qbName(name);
    }


}
