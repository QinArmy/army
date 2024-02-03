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
import io.army.criteria.dialect.DmlCommand;
import io.army.criteria.mysql.*;
import io.army.mapping.LocalDateTimeType;
import io.army.mapping.LocalDateType;
import io.army.mapping.LocalTimeType;
import io.army.mapping.MappingType;
import io.army.session.Session;
import io.army.session.StmtOption;

public abstract class MySQLs extends MySQLSyntax {

    /**
     * private constructor
     */
    private MySQLs() {
    }

    public static final Modifier ALL = MySQLWords.MySQLModifier.ALL;
    public static final WordDistinct DISTINCT = MySQLWords.KeyWordDistinct.DISTINCT;
    public static final Modifier DISTINCTROW = MySQLWords.MySQLModifier.DISTINCTROW;
    public static final Modifier HIGH_PRIORITY = MySQLWords.MySQLModifier.HIGH_PRIORITY;
    public static final Modifier STRAIGHT_JOIN = MySQLWords.MySQLModifier.STRAIGHT_JOIN;
    public static final Modifier SQL_SMALL_RESULT = MySQLWords.MySQLModifier.SQL_SMALL_RESULT;
    public static final Modifier SQL_BIG_RESULT = MySQLWords.MySQLModifier.SQL_BIG_RESULT;
    public static final Modifier SQL_BUFFER_RESULT = MySQLWords.MySQLModifier.SQL_BUFFER_RESULT;
    public static final Modifier SQL_NO_CACHE = MySQLWords.MySQLModifier.SQL_NO_CACHE;
    public static final Modifier SQL_CALC_FOUND_ROWS = MySQLWords.MySQLModifier.SQL_CALC_FOUND_ROWS;
    public static final Modifier LOW_PRIORITY = MySQLWords.MySQLModifier.LOW_PRIORITY;
    public static final Modifier DELAYED = MySQLWords.MySQLModifier.DELAYED;
    public static final Modifier QUICK = MySQLWords.MySQLModifier.QUICK;
    public static final Modifier IGNORE = MySQLWords.MySQLModifier.IGNORE;
    public static final Modifier CONCURRENT = MySQLWords.MySQLModifier.CONCURRENT;
    public static final Modifier LOCAL = MySQLWords.MySQLModifier.LOCAL;

    public static final WordsAtTimeZone AT_TIME_ZONE = MySQLWords.KeyWordsAtTimeZone.AT_TIME_ZONE;


    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalDateType}
     *
     * @see MySQLs#currentDate()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_curdate">CURRENT_DATE()</a>
     */
    public static final SimpleExpression CURRENT_DATE = LiteralFunctions.noParensFunc("CURRENT_DATE", LocalDateType.INSTANCE);

    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalTimeType}
     *
     * @see MySQLs#currentTime()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_curdate">CURRENT_TIME()</a>
     */
    public static final SimpleExpression CURRENT_TIME = LiteralFunctions.noParensFunc("CURRENT_TIME", LocalTimeType.INSTANCE);

    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     *
     * @see MySQLs#currentTimestamp()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_curdate">CURRENT_TIME()</a>
     */
    public static final SimpleExpression CURRENT_TIMESTAMP = LiteralFunctions.noParensFunc("CURRENT_TIMESTAMP", LocalDateTimeType.INSTANCE);


    /**
     * <p>create single-table INSERT statement that is primary statement and support {@link io.army.meta.ChildTableMeta}.
     * <p><strong>NOTE</strong> : if you specify ON DUPLICATE KEY UPDATE ,then the affected-rows value per row is
     * <ul>
     *     <li>1 if the row is inserted as a new row</li>
     *     <li>2 if an existing row is updated</li>
     *     <li>0 if an existing row is set to its current values,but due to army auto set 'updateTime' field,so actually never 0</li>
     * </ul>
     * <p><strong>Limitations</strong> of MySQL INSERT ON DUPLICATE KEY UPDATE clause :
     * <ul>
     *     <li>If target table contain 'visible' field,then ON DUPLICATE KEY UPDATE clause can only be used in {@link Visible#BOTH} mode,see {@link Session#visible()} and {@link io.army.env.ArmyKey#VISIBLE_MODE}</li>
     *     <li>If target table contain the field whose {@link io.army.annotation.UpdateMode} is {@link io.army.annotation.UpdateMode#ONLY_NULL} or {@link io.army.annotation.UpdateMode#ONLY_DEFAULT},then you couldn't use ON DUPLICATE KEY UPDATE clause .</li>
     *     <li>If target table primary key is auto increment and you insert multi-row {@link io.army.meta.ChildTableMeta} ,then you couldn't use ON DUPLICATE KEY UPDATE clause ,because database server will couldn't return correct multi-row primary key value if conflict occur.</li>
     *     <li>If target table primary key is auto increment and you insert multi-row with "domain" syntax (see Example 3),then database will couldn't return correct multi-row primary key value , because of conflict. So you have to use ignoreReturnIds() option clause before insert into clause.</li>
     * </ul>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/insert.html">INSERT statement</a>
     */
    public static MySQLInsert._PrimaryOptionSpec singleInsert() {
        return MySQLInserts.singleInsert();
    }


    /**
     * <p>Create single-table MySQL REPLACE statement api instance.
     * <p><strong>Limitations</strong> of MySQL REPLACE statement :
     * <ul>
     *     <li>If target table contain 'visible' field,then MySQL REPLACE can only be used in {@link Visible#BOTH} mode,see {@link Session#visible()} and {@link io.army.env.ArmyKey#VISIBLE_MODE}</li>
     *     <li>If target table contain the field whose {@link io.army.annotation.UpdateMode} is {@link io.army.annotation.UpdateMode#ONLY_NULL} or {@link io.army.annotation.UpdateMode#ONLY_DEFAULT},then you couldn't use MySQL REPLACE .</li>
     *     <li>If target table primary key is auto increment and you replace multi-row with "domain" syntax (see Example 3),then database will couldn't return correct multi-row primary key value , because of conflict. So you have to use ignoreReturnIds() option clause before replace into clause.</li>
     * </ul>
     * <pre>
     *     <code><br/>
     *    Example 3 :
     *
     *    &#64;VisibleMode(Visible.BOTH)
     *    &#64;Test(invocationCount = 3)
     *    public void domainReplaceParent(final SyncLocalSession session) {
     *
     *        assert ChinaRegion_.id.generatorType() == GeneratorType.POST; // primary key is auto increment
     *
     *        final List&lt;ChinaRegion&lt;?>> regionList = createReginListWithCount(3);
     *
     *        final long startNanoSecond = System.nanoTime();
     *
     *        final Insert stmt;
     *        stmt = MySQLs.singleReplace()
     *                .ignoreReturnIds() // due to you use "domain" api replace multi-row , so you have to use ignoreReturnIds() option clause,because database couldn't return correct multi-row primary key value.
     *                .replaceInto(ChinaRegion_.T)
     *                .parens(s -> s.space(ChinaRegion_.name, ChinaRegion_.regionGdp)
     *                        .comma(ChinaRegion_.parentId)
     *                )
     *                .defaultValue(ChinaRegion_.regionGdp, SQLs::param, "88888.88")
     *                .defaultValue(ChinaRegion_.visible, SQLs::param, true)
     *                .defaultValue(ChinaRegion_.parentId, SQLs::param, 0)
     *                .values(regionList)  // here , "domain" api
     *                .asInsert();
     *
     *        statementCostTimeLog(session, LOG, startNanoSecond);
     *
     *        Assert.assertEquals(session.update(stmt), regionList.size());
     *
     *        for (ChinaRegion&lt;?> region : regionList) {
     *            Assert.assertNull(region.getId()); // because ignoreReturnIds() option clause.
     *        }
     *
     *    }
     *     </code>
     * </pre>
     *
     * @return MySQL REPLACE statement api instance.
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/replace.html">REPLACE Statement</a>
     */
    public static MySQLReplace._PrimaryOptionSpec singleReplace() {
        return MySQLReplaces.singleReplace();
    }


    public static MySQLQuery.WithSpec<Select> query() {
        return MySQLQueries.simpleQuery();
    }

    public static MySQLQuery.WithSpec<Statement._BatchSelectParamSpec> batchQuery() {
        return MySQLQueries.batchQuery();
    }


    public static MySQLQuery.WithSpec<SubQuery> subQuery() {
        return MySQLQueries.subQuery(ContextStack.peek(), SQLs::identity);
    }


    public static MySQLQuery.WithSpec<Expression> scalarSubQuery() {
        return MySQLQueries.subQuery(ContextStack.peek(), Expressions::scalarExpression);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/values.html">VALUES Statement</a>
     */
    public static MySQLValues.ValuesSpec<Values> valuesStmt() {
        return MySQLSimpleValues.simpleValues(SQLs::identity);
    }


    /**
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/values.html">VALUES Statement</a>
     */
    public static MySQLValues.ValuesSpec<SubValues> subValues() {
        return MySQLSimpleValues.subValues(ContextStack.peek(), SQLs::identity);
    }


    public static MySQLUpdate._SingleWithSpec<Update> singleUpdate() {
        return MySQLSingleUpdates.simple();
    }


    public static MySQLUpdate._SingleWithSpec<Statement._BatchUpdateParamSpec> batchSingleUpdate() {
        return MySQLSingleUpdates.batch();
    }

    public static MySQLUpdate._MultiWithSpec<Update> multiUpdate() {
        return MySQLMultiUpdates.simple();
    }


    public static MySQLUpdate._MultiWithSpec<Statement._BatchUpdateParamSpec> batchMultiUpdate() {
        return MySQLMultiUpdates.batch();
    }

    public static MySQLDelete._SingleWithSpec<Delete> singleDelete() {
        return MySQLSingleDeletes.simple();
    }


    public static MySQLDelete._SingleWithSpec<Statement._BatchDeleteParamSpec> batchSingleDelete() {
        return MySQLSingleDeletes.batch();
    }

    public static MySQLDelete._MultiWithSpec<Delete> multiDelete() {
        return MySQLMultiDeletes.simple();
    }


    public static MySQLDelete._MultiWithSpec<Statement._BatchDeleteParamSpec> batchMultiDelete() {
        return MySQLMultiDeletes.batch();
    }

    /**
     * <p>Create MySQL LOAD DATA statement instance.
     * <p><strong>Limitations</strong> of MySQL LOAD DATA statement :
     * <ol>
     *     <li>Server local_infile system variables must be true</li>
     *     <li>Client allowLoadLocalInfile property(JDBC/JDBD) must be true</li>
     *     <li>You have to use client-prepared statement or static statement,see {@link StmtOption#isPreferServerPrepare()} ,see following LOCAL INFILE Request</li>
     *     <li>Due to the literal(in COLUMNS/LINES clause) use {@link io.army.env.EscapeMode#BACK_SLASH} and ignore {@link io.army.env.ArmyKey#LITERAL_ESCAPE_MODE},so you should guarantee sql mode NO_BACKSLASH_ESCAPES is disabled.</li>
     * </ol>
     *
     * <pre>
     *     <code><br/>
     *
     *    // Example 01 :
     *
     *    &#64;Test
     *    public void singleLoadData(final SyncLocalSession session) {
     *        final Path csvFile;
     *        csvFile = MyPaths.myLocal("china_region.csv");
     *        if (Files.notExists(csvFile)) {
     *            return;
     *        }
     *
     *        final DmlCommand stmt;
     *        stmt = MySQLs.loadDataStmt()
     *                .loadData(MySQLs.LOCAL)
     *                .infile(csvFile)
     *                .ignore()
     *                .intoTable(ChinaRegion_.T)
     *                .characterSet("utf8mb4")
     *                .columns(s -> s.terminatedBy(","))
     *                .lines(s -> s.terminatedBy("\n"))
     *                .ignore(1, SQLs.LINES)
     *                .set(ChinaRegion_.visible, SQLs::literal, true)
     *                .set(ChinaRegion_.regionType, SQLs::literal, RegionType.NONE)
     *                .asCommand();
     *
     *        final long rows;
     *        rows = session.update(stmt, SyncStmtOption.preferServerPrepare(false));
     *        LOG.debug("session[name : {}] rows {}", session.name(), rows);
     *    }
     *
     *    // Example 02 :
     *
     *    &#64;Test
     *    public void childLoadData(final SyncLocalSession session) {
     *        final Path parentTempFile, childTempFile;
     *        parentTempFile = MyPaths.myLocal("china_region_parent.csv");
     *        childTempFile = MyPaths.myLocal("china_province.csv");
     *
     *        final DmlCommand stmt;
     *        stmt = MySQLs.loadDataStmt()
     *                .loadData(MySQLs.LOCAL)
     *                .infile(parentTempFile)
     *                .ignore()
     *                .intoTable(ChinaRegion_.T)
     *                .characterSet("utf8mb4")
     *                .columns(s -> s.terminatedBy(","))
     *                .lines(s -> s.terminatedBy("\n"))
     *                .ignore(1, SQLs.LINES)
     *                .parens(s -> s.space(ChinaRegion_.name))
     *                .set(ChinaRegion_.regionType, SQLs::literal, RegionType.PROVINCE)
     *                .asCommand()
     *
     *                .child()
     *
     *                .loadData(MySQLs.LOCAL)
     *                .infile(childTempFile)
     *                .ignore()
     *                .intoTable(ChinaProvince_.T)
     *                .characterSet("utf8mb4")
     *                .columns(s -> s.terminatedBy(","))
     *                .lines(s -> s.terminatedBy("\n"))
     *                .ignore(1, SQLs.LINES)
     *                .asCommand();
     *
     *        final long rows;
     *        rows = session.update(stmt, SyncStmtOption.preferServerPrepare(false));
     *        LOG.debug("session[name : {}] rows {}", session.name(), rows);
     *
     *    }
     *
     *     </code>
     * </pre>
     *
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/load-data.html">LOAD DATA Statement</a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/server-system-variables.html#sysvar_local_infile">Server local_infile system variables</a>
     * @see <a href="https://dev.mysql.com/doc/connector-j/en/connector-j-connp-props-security.html">client allowLoadLocalInfile property </a>
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/sql-mode.html#sqlmode_no_backslash_escapes">NO_BACKSLASH_ESCAPES</a>
     * @see <a href="https://dev.mysql.com/doc/dev/mysql-server/latest/page_protocol_com_query_response_local_infile_request.html">LOCAL INFILE Request</a>
     */
    public static MySQLLoadData._LoadDataClause<DmlCommand> loadDataStmt() {
        return MySQLLoads.loadDataCommand(SQLs::identity);
    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface WordDistinct extends Modifier, SQLs.ArgDistinct {

    }


    public interface WordsAtTimeZone extends SQLWords {

    }

}
