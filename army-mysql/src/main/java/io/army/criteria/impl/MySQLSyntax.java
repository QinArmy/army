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
import io.army.criteria.dialect.Hint;
import io.army.criteria.dialect.VarExpression;
import io.army.criteria.mysql.HintStrategy;
import io.army.dialect._Constant;
import io.army.util._StringUtils;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

/**
 * <p>
 * This class is base class of below:
 *     <ul>
 *         <li>{@link MySQLStringFunctions}</li>
 *     </ul>
 * package class
 */
@SuppressWarnings("unused")
abstract class MySQLSyntax extends MySQLOtherFunctions {

    /**
     * package constructor
     */
    MySQLSyntax() {
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/charset-mysql.html">String Literals</a>
     */
    public static DefiniteExpression textLiteral(String charsetName, String literal) {
        return MySQLExpressions.textLiteral(charsetName, literal, null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-literals.html">String Literals</a>
     */
    public static DefiniteExpression textLiteral(String charsetName, String literal, SQLs.WordCollate collate, String collationName) {
        ContextStack.assertNonNull(collationName);
        return MySQLExpressions.textLiteral(charsetName, literal, collationName);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-literals.html">String Literals</a>
     */
    public static DefiniteExpression encodingTextLiteral(String charsetName, TableField field, String literal) {
        return MySQLExpressions.encodingTextLiteral(charsetName, field, literal, null);
    }

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/string-literals.html">String Literals</a>
     */
    public static DefiniteExpression encodingTextLiteral(String charsetName, TableField field, String literal, SQLs.WordCollate collate, String collationName) {
        ContextStack.assertNonNull(collationName);
        return MySQLExpressions.encodingTextLiteral(charsetName, field, literal, collationName);
    }


    /**
     * <p>Reference MySQL User-Defined Variables from statement context.
     * <p>Example :
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void rowNumber(final SyncLocalSession session) {
     *        final List&lt;ChinaRegion&lt;?>> regionList = createReginListWithCount(10);
     *        session.batchSave(regionList);
     *
     *        final Select stmt;
     *        stmt = MySQLs.query()
     *                .select(s -> s.space(MySQLs.at("my_row_number").increment().as("rowNumber")) // NOTE : here is defer SELECT clause, so SELECT clause is executed after FROM clause
     *                        .comma("t", PERIOD, ChinaRegion_.T)
     *                )
     *                .from(ChinaRegion_.T, AS, "t")
     *                .crossJoin(SQLs.subQuery()
     *                        .select(MySQLs.at("my_row_number", SQLs.COLON_EQUAL, SQLs.LITERAL_0).as("n"))
     *                        .asQuery()
     *                ).as("s")
     *                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
     *                .orderBy(ChinaRegion_.id)
     *                .asQuery();
     *
     *        final List&lt;Map&lt;String, Object>> rowList;
     *        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
     *        final int rowSize = rowList.size();
     *        Assert.assertEquals(rowSize, regionList.size());
     *
     *        for (int i = 0; i &lt; rowSize; i++) {
     *            Assert.assertEquals(rowList.get(i).get("rowNumber"), i + 1);
     *        }
     *    }
     *
     *     output sql:<br/>
     *            SELECT (&#64;my_row_number := &#64;my_row_number + 1) AS rowNumber,
     *                   t.id                                   AS id,
     *                   t.create_time                          AS createTime,
     *                   t.update_time                          AS updateTime,
     *                   t.version                              AS version,
     *                   t.`visible`                            AS `visible`,
     *                   t.region_type                          AS regionType,
     *                   t.region_gdp                           AS regionGdp,
     *                   t.`name`                               AS `name`,
     *                   t.parent_id                            AS parentId,
     *                   t.population                           AS population
     *            FROM china_region AS t
     *                CROSS JOIN (SELECT (&#64;my_row_number := 0) AS n) AS s
     *            WHERE t.id IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     *              AND t.`visible` = TRUE
     *            ORDER BY t.id
     *     </code>
     * </pre>
     *
     * @throws CriteriaException throw when
     *                           <ul>
     *                               <li>no statement context</li>
     *                               <li>variable not exists</li>
     *                           </ul>
     * @see #at(String, TypeInfer)
     * @see #at(String, SQLs.SymbolColonEqual, Object)
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/user-variables.html">User-Defined Variables</a>
     */
    public static VarExpression at(String varName) {
        return ContextStack.root().refVar(varName);
    }

    /**
     * <p>Assignment MySQL User-Defined Variables and register to statement context.
     * <p>Example :
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void rowNumber(final SyncLocalSession session) {
     *        final List&lt;ChinaRegion&lt;?>> regionList = createReginListWithCount(10);
     *        session.batchSave(regionList);
     *
     *        final Select stmt;
     *        stmt = MySQLs.query()
     *                .select(s -> s.space(MySQLs.at("my_row_number").increment().as("rowNumber")) // NOTE : here is defer SELECT clause, so SELECT clause is executed after FROM clause
     *                        .comma("t", PERIOD, ChinaRegion_.T)
     *                )
     *                .from(ChinaRegion_.T, AS, "t")
     *                .crossJoin(SQLs.subQuery()
     *                        .select(MySQLs.at("my_row_number", SQLs.COLON_EQUAL, SQLs.LITERAL_0).as("n"))
     *                        .asQuery()
     *                ).as("s")
     *                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
     *                .orderBy(ChinaRegion_.id)
     *                .asQuery();
     *
     *        final List&lt;Map&lt;String, Object>> rowList;
     *        rowList = session.queryObjectList(stmt, RowMaps::hashMap);
     *        final int rowSize = rowList.size();
     *        Assert.assertEquals(rowSize, regionList.size());
     *
     *        for (int i = 0; i &lt; rowSize; i++) {
     *            Assert.assertEquals(rowList.get(i).get("rowNumber"), i + 1);
     *        }
     *    }
     *
     *     output sql:<br/>
     *            SELECT (&#64;my_row_number := &#64;my_row_number + 1) AS rowNumber,
     *                   t.id                                   AS id,
     *                   t.create_time                          AS createTime,
     *                   t.update_time                          AS updateTime,
     *                   t.version                              AS version,
     *                   t.`visible`                            AS `visible`,
     *                   t.region_type                          AS regionType,
     *                   t.region_gdp                           AS regionGdp,
     *                   t.`name`                               AS `name`,
     *                   t.parent_id                            AS parentId,
     *                   t.population                           AS population
     *            FROM china_region AS t
     *                CROSS JOIN (SELECT (&#64;my_row_number := 0) AS n) AS s
     *            WHERE t.id IN (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
     *              AND t.`visible` = TRUE
     *            ORDER BY t.id
     *     </code>
     * </pre>
     *
     * @param value literal or {@link Expression}
     * @throws CriteriaException throw when
     *                           <ul>
     *                               <li>no statement context</li>
     *                               <li>value is literal and  no default {@link io.army.mapping.MappingType}</li>
     *                               <li>variable duplication register</li>
     *                           </ul>
     * @see #at(String)
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/user-variables.html">User-Defined Variables</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/assignment-operators.html#operator_assign-value">Assignment Operators</a>
     */
    public static SimpleExpression at(String varName, SQLs.SymbolColonEqual colonEqual, Object value) {
        return UserVarExpression.assignmentVar(varName, colonEqual, value, ContextStack.root());
    }


    /**
     * <p>Create user variable expression that output {@code @user_var_name }
     * <p><strong>NOTE</strong>: this user variable expression never register to statement context.
     *
     * @param name user variable name
     * @param type user variable type
     * @throws CriteriaException throw when name have no text
     * @see #at(String)
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">SET Syntax for Variable Assignment</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/user-variables.html">User-Defined Variables</a>
     */
    public static VarExpression at(String name, TypeInfer type) {
        return UserVarExpression.create(name, type);
    }


    /**
     * <p>Create SESSION system variable expression that output {@code @@SESSION.system_var_name }
     * <p> Example :
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void readSystemVariable(final SyncLocalSession session) {
     *        final Select stmt;
     *        stmt = MySQLs.query()
     *                .select(atAtSession("autocommit").as("autoCommit"), atAtSession("sql_mode").as("sqlMode"))
     *                .asQuery();
     *
     *        final Map&lt;String, Object> row;
     *        row = session.queryOneObject(stmt, RowMaps::hashMap);
     *
     *        Assert.assertNotNull(row);
     *        Assert.assertEquals(row.size(),2);
     *        Assert.assertEquals(row.get("autoCommit"),true);
     *        Assert.assertTrue(row.get("sqlMode") instanceof  String);
     *    }
     *     </code>
     *     output sql : SELECT @@SESSION.autocommit AS autoCommit , @@SESSION.sql_mode AS sqlMode
     *
     * </pre>
     *
     * @throws CriteriaException throw when
     *                           <ul>
     *                               <li>name have no text</li>
     *                               <li>system variable scope isn't SESSION</li>
     *                           </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">SET Syntax for Variable Assignment</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/server-system-variables.html">Server System Variables</a>
     */
    public static SimpleExpression atAtSession(String name) {
        return MySQLExpressions.systemVariable(MySQLs.SESSION, name);
    }

    /**
     * <p>Create GLOBAL system variable expression that output {@code @@GLOBAL.system_var_name }
     * <p> Example :
     * <pre>
     *     <code><br/>
     *    &#64;Test
     *    public void readSystemVariable(final SyncLocalSession session) {
     *        final Select stmt;
     *        stmt = MySQLs.query()
     *                .select(atAtGlobal("autocommit").as("autoCommit"), atAtGlobal("sql_mode").as("sqlMode"))
     *                .asQuery();
     *
     *        final Map&lt;String, Object> row;
     *        row = session.queryOneObject(stmt, RowMaps::hashMap);
     *
     *        Assert.assertNotNull(row);
     *        Assert.assertEquals(row.size(),2);
     *        Assert.assertEquals(row.get("autoCommit"),true);
     *        Assert.assertTrue(row.get("sqlMode") instanceof  String);
     *    }
     *     </code>
     *     output sql : SELECT @@GLOBAL.autocommit AS autoCommit , @@GLOBAL.sql_mode AS sqlMode
     *
     * </pre>
     *
     * @throws CriteriaException throw when
     *                           <ul>
     *                               <li>name have no text</li>
     *                               <li>system variable scope isn't GLOBAL</li>
     *                           </ul>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/set-variable.html">SET Syntax for Variable Assignment</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.3/en/server-system-variables.html">Server System Variables</a>
     */
    public static SimpleExpression atAtGlobal(String name) {
        return MySQLExpressions.systemVariable(MySQLs.GLOBAL, name);
    }





    /*################################## blow join-order hint ##################################*/

    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html#optimizer-hints-join-order">Join-Order Optimizer Hints</a>
     */
    public static Hint joinFixedOrder(@Nullable String queryBlockName) {
        return MySQLHints.joinFixedOrder(queryBlockName);
    }

    public static LogicalPredicate xor(IPredicate left, IPredicate right) {
        throw new UnsupportedOperationException();
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

    /*-------------------below private method -------------------*/


    static String keyWordsToString(Enum<?> words) {
        return _StringUtils.builder()
                .append(MySQLs.class.getSimpleName())
                .append(_Constant.PERIOD)
                .append(words.name())
                .toString();
    }

}
