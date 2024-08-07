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
import io.army.criteria.dialect.DqlCommand;
import io.army.criteria.dialect.ReturningDelete;
import io.army.criteria.dialect.ReturningUpdate;
import io.army.criteria.postgre.*;
import io.army.mapping.*;

import java.util.function.BiFunction;
import java.util.function.Consumer;

/**
 * <p>
 * This class is Postgre SQL syntax utils.
 *
 * @since 0.6.0
 */
public abstract class Postgres extends PostgreSyntax {


    /**
     * private constructor
     */
    private Postgres() {
    }

    public static final Modifier ALL = PostgreWords.SelectModifier.ALL;
    public static final WordDistinct DISTINCT = PostgreWords.KeyWordDistinct.DISTINCT;
    public static final SQLs.BooleanTestWord FROM_NORMALIZED = PostgreWords.FromNormalizedWord.FROM_NORMALIZED;
    public static final SQLs.BooleanTestWord NORMALIZED = PostgreWords.FromNormalizedWord.NORMALIZED;


    public static final WordName NAME = PostgreWords.KeyWordName.NAME;

    public static final WordVersion VERSION = PostgreWords.KeyWordVersion.VERSION;

    public static final WordStandalone STANDALONE = PostgreWords.KeyWordStandalone.STANDALONE;

    public static final StandaloneOption YES = PostgreWords.KeyWordStandaloneOption.YES;

    public static final StandaloneOption NO = PostgreWords.KeyWordStandaloneOption.NO;

    public static final WordsNoValue NO_VALUE = PostgreWords.KeyWordsNoValue.NO_VALUE;

    public static final SQLs.WordDocument DOCUMENT = SqlWords.KeyWordDocument.DOCUMENT;

    public static final SQLs.WordContent CONTENT = SqlWords.KeyWordContent.CONTENT;

    public static final WordPassing PASSING = PostgreWords.KeyWordPassing.PASSING;

    public static final PassingOption BY_REF = PostgreWords.WordPassingOption.BY_REF;

    public static final PassingOption BY_VALUE = PostgreWords.WordPassingOption.BY_VALUE;

    public static final NullTreatMode RAISE_EXCEPTION = PostgreWords.NullTreatModeExpression.RAISE_EXCEPTION;

    public static final NullTreatMode USE_JSON_NULL = PostgreWords.NullTreatModeExpression.USE_JSON_NULL;

    public static final NullTreatMode DELETE_KEY = PostgreWords.NullTreatModeExpression.DELETE_KEY;

    public static final NullTreatMode RETURN_TARGET = PostgreWords.NullTreatModeExpression.RETURN_TARGET;

    public static final DoubleColon DOUBLE_COLON = PostgreWords.SymbolDoubleColon.DOUBLE_COLON;


    public static final ExtractTimeField CENTURY = PostgreWords.WordExtractTimeField.CENTURY;
    public static final ExtractTimeField DAY = PostgreWords.WordExtractTimeField.DAY;
    public static final ExtractTimeField DECADE = PostgreWords.WordExtractTimeField.DECADE;
    public static final ExtractTimeField DOW = PostgreWords.WordExtractTimeField.DOW;
    public static final ExtractTimeField DOY = PostgreWords.WordExtractTimeField.DOY;
    public static final ExtractTimeField EPOCH = PostgreWords.WordExtractTimeField.EPOCH;
    public static final ExtractTimeField HOUR = PostgreWords.WordExtractTimeField.HOUR;
    public static final ExtractTimeField ISODOW = PostgreWords.WordExtractTimeField.ISODOW;
    public static final ExtractTimeField ISOYEAR = PostgreWords.WordExtractTimeField.ISOYEAR;
    public static final ExtractTimeField JULIAN = PostgreWords.WordExtractTimeField.JULIAN;
    public static final ExtractTimeField MICROSECONDS = PostgreWords.WordExtractTimeField.MICROSECONDS;
    public static final ExtractTimeField MILLENNIUM = PostgreWords.WordExtractTimeField.MILLENNIUM;
    public static final ExtractTimeField MILLISECONDS = PostgreWords.WordExtractTimeField.MILLISECONDS;
    public static final ExtractTimeField MINUTE = PostgreWords.WordExtractTimeField.MINUTE;
    public static final ExtractTimeField MONTH = PostgreWords.WordExtractTimeField.MONTH;
    public static final ExtractTimeField QUARTER = PostgreWords.WordExtractTimeField.QUARTER;
    public static final ExtractTimeField SECOND = PostgreWords.WordExtractTimeField.SECOND;
    public static final ExtractTimeField TIMEZONE = PostgreWords.WordExtractTimeField.TIMEZONE;
    public static final ExtractTimeField TIMEZONE_HOUR = PostgreWords.WordExtractTimeField.TIMEZONE_HOUR;
    public static final ExtractTimeField TIMEZONE_MINUTE = PostgreWords.WordExtractTimeField.TIMEZONE_MINUTE;
    public static final ExtractTimeField WEEK = PostgreWords.WordExtractTimeField.WEEK;
    public static final ExtractTimeField YEAR = PostgreWords.WordExtractTimeField.YEAR;
    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">current_date → date</a>
     */
    public static final Expression CURRENT_DATE = LiteralFunctions.noParensFunc("current_date", LocalDateType.INSTANCE);
    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  OffsetTimeType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">current_time</a>
     */
    public static final Expression CURRENT_TIME = LiteralFunctions.noParensFunc("current_time", OffsetTimeType.INSTANCE);
    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  OffsetDateTimeType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">current_timestamp → timestamp with time zone</a>
     */
    public static final Expression CURRENT_TIMESTAMP = LiteralFunctions.noParensFunc("current_timestamp", OffsetDateTimeType.INSTANCE);
    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalTimeType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">localtime → time</a>
     */
    public static final Expression LOCALTIME = LiteralFunctions.noParensFunc("localtime", LocalTimeType.INSTANCE);
    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-datetime.html#FUNCTIONS-DATETIME-TABLE">localtimestamp → timestamp</a>
     */
    public static final Expression LOCALTIMESTAMP = LiteralFunctions.noParensFunc("localtimestamp", LocalDateTimeType.INSTANCE);

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">current_catalog → name</a>
     */
    public static final Expression CURRENT_CATALOG = LiteralFunctions.noParensFunc("current_catalog", TextType.INSTANCE);

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">current_user → name</a>
     */
    public static final Expression CURRENT_USER = LiteralFunctions.noParensFunc("current_user", TextType.INSTANCE);


    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">session_user → name</a>
     */
    public static final Expression SESSION_USER = LiteralFunctions.noParensFunc("session_user", TextType.INSTANCE);

    /**
     * <p>
     * The {@link MappingType} of function return type: {@link  TextType}
     *
     * @see <a href="https://www.postgresql.org/docs/current/functions-info.html#FUNCTIONS-INFO-SESSION-TABLE">user → name</a>
     */
    public static final Expression USER = LiteralFunctions.noParensFunc("user", TextType.INSTANCE);

    /**
     * <p>create single-table INSERT statement that is primary statement.
     */
    public static PostgreInsert._PrimaryOptionSpec singleInsert() {
        return PostgreInserts.singleInsert();
    }

    /**
     * <p>create simple(non-batch) SELECT statement that is primary statement.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">Postgre SELECT syntax</a>
     * @see <a href="https://www.postgresql.org/docs/current/queries-with.html#QUERIES-WITH-SEARCH">Search Order</a>
     * @see <a href="https://www.postgresql.org/docs/current/queries-with.html#QUERIES-WITH-CYCLE">Cycle Detection</a>
     */
    public static PostgreQuery.WithSpec<Select> query() {
        return PostgreQueries.simpleQuery();
    }

    /**
     * <p>
     * create batch SELECT statement that is primary statement.
     */
    public static PostgreQuery.WithSpec<Statement._BatchSelectParamSpec> batchQuery() {
        return PostgreQueries.batchQuery();
    }


    /**
     * <p>create SUB-SELECT statement that is sub query statement.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">Postgre SELECT syntax</a>
     * @see <a href="https://www.postgresql.org/docs/current/queries-with.html#QUERIES-WITH-SEARCH">Search Order</a>
     * @see <a href="https://www.postgresql.org/docs/current/queries-with.html#QUERIES-WITH-CYCLE">Cycle Detection</a>
     */
    public static PostgreQuery.WithSpec<SubQuery> subQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), SQLs::identity);
    }

    /**
     * <p>create SUB-SELECT statement that is sub query statement and would be converted to {@link Expression}.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-select.html">Postgre SELECT syntax</a>
     */
    public static PostgreQuery.WithSpec<Expression> scalarSubQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), Expressions::scalarExpression);
    }

    /**
     * <p>
     * create simple(non-batch) single-table UPDATE statement that is primary statement.
     */
    public static PostgreUpdate._SingleWithSpec<Update, ReturningUpdate> singleUpdate() {
        return PostgreUpdates.simple();
    }

    /**
     * <p>
     * create batch single-table UPDATE statement that is primary statement.
     */
    public static PostgreUpdate._SingleWithSpec<Statement._BatchUpdateParamSpec, Statement._BatchReturningUpdateParamSpec> batchSingleUpdate() {
        return PostgreUpdates.batchUpdate();
    }

    /**
     * <p>
     * create simple(non-batch) single-table DELETE statement that is primary statement.
     */
    public static PgSingleDeleteSpec<Delete, ReturningDelete> singleDelete() {
        return PostgreDeletes.simpleDelete();
    }

    /**
     * <p>
     * create batch single-table DELETE statement that is primary statement.
     */
    public static PgSingleDeleteSpec<Statement._BatchDeleteParamSpec, Statement._BatchReturningDeleteParamSpec> batchSingleDelete() {
        return PostgreDeletes.batchDelete();
    }

    public static PostgreValues.ValuesSpec<Values> valuesStmt() {
        return PostgreSimpleValues.simpleValues();
    }

    public static PostgreValues.ValuesSpec<SubValues> subValues() {
        return PostgreSimpleValues.subValues(ContextStack.peek(), SQLs::identity);
    }


    /**
     * <p>Create postgre DECLARE statement.
     * <pre>
     *     <code><br/>
     *    &#64;Transactional(readOnly = true)
     *    &#64;Test
     *    public void readOnlyCursor(final SyncLocalSession session) {
     *        final List&lt;ChinaRegion&lt;?>> regionList = createReginListWithCount(300);
     *        session.batchSave(regionList);
     *
     *        final DeclareCursor stmt;
     *        stmt = Postgres.declareStmt()
     *                .declare("my_china_region_cursor").cursor()
     *                .forSpace()
     *                .select("c", PERIOD, ChinaRegion_.T)
     *                .from(ChinaRegion_.T, AS, "c")
     *                .where(ChinaRegion_.id.in(SQLs::rowParam, extractRegionIdList(regionList)))
     *                .orderBy(ChinaRegion_.id)
     *                .limit(SQLs::literal, regionList.size())
     *                .asQuery()
     *                .asCommand();
     *
     *        final ResultStates states;
     *        states = session.updateAsStates(stmt);
     *
     *        try (SyncStmtCursor cursor = states.nonNullOf(SyncStmtCursor.SYNC_STMT_CURSOR)) {
     *            ChinaRegion&lt;?> region, firstRow;
     *            int rowCount = 0;
     *            while ((region = cursor.next(ChinaRegion_.CLASS)) != null) {
     *                LOG.debug("region : {}", region);
     *                rowCount++;
     *                if (rowCount > 200) {
     *                    break;
     *                }
     *            }
     *            firstRow = cursor.fetchOneObject(Direction.FIRST, ChinaRegion_::constructor, ResultStates.IGNORE_STATES);
     *            LOG.debug("{} firstRow : {}", session.name(), firstRow);
     *            cursor.move(Direction.LAST);
     *
     *            cursor.fetch(Direction.FORWARD_ALL, ChinaRegion_.CLASS, ResultStates.IGNORE_STATES)
     *                    .forEach(System.out::println);
     *        }
     *
     *    }
     *     </code>
     * </pre>
     *
     * @see #closeCursor(String)
     * @see #closeAllCursor()
     * @see <a href="https://www.postgresql.org/docs/current/sql-declare.html">DECLARE — define a cursor</a>
     */
    public static PostgreCursor._PostgreDeclareClause declareStmt() {
        return PostgreDeclareCursors.declare();
    }

    /**
     * <p>Create postgre CLOSE statement.
     *
     * @see #closeCursor(String)
     * @see #declareStmt()
     * @see <a href="https://www.postgresql.org/docs/current/sql-close.html">CLOSE — close a cursor</a>
     */
    public static SimpleDmlStatement closeCursor(String name) {
        return PostgreSupports.closeCursor(name);
    }

    /**
     * <p>Create postgre CLOSE statement.
     *
     * @see #declareStmt()
     * @see #closeAllCursor()
     * @see <a href="https://www.postgresql.org/docs/current/sql-close.html">CLOSE — close a cursor</a>
     */
    public static SimpleDmlStatement closeAllCursor() {
        return PostgreSupports.closeAllCursor();
    }


    /**
     * <p>Create postgre single-table MERGE statement.
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-merge.html">MERGE — conditionally insert, update, or delete rows of a table</a>
     */
    public static PostgreMerge._WithSpec singleMerge() {
        return PostgreMerges.mergeStmt(null);
    }

    /**
     * <p>Set statement
     *
     * @see <a href="https://www.postgresql.org/docs/current/sql-set.html">SET — change a run-time parameter</a>
     * @see <a href="https://www.postgresql.org/docs/16/multibyte.html#CHARSET-TABLE">PostgreSQL Character Sets</a>
     * @see <a href="https://www.postgresql.org/docs/16/runtime-config-client.html">PostgreSQL Client Connection Defaults</a>
     */
    public static PostgreCommand._SetClause setStmt() {
        return PostgreSets.setStmt();
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-show.html">SHOW — show the value of a run-time parameter</a>
     */
    public static DqlCommand show(String name) {
        return PostgreCommands.show(name);
    }

    /**
     * @see <a href="https://www.postgresql.org/docs/current/sql-show.html">SHOW — show the value of a run-time parameter</a>
     */
    public static DqlCommand showAll() {
        return PostgreCommands.showAll();
    }


    public interface _XmlNamedElementClause {

        _XmlNamedElementClause accept(Expression value, SQLs.WordAs as, String name);

        _XmlNamedElementClause accept(BiFunction<MappingType, String, Expression> funcRef, String value, SQLs.WordAs as, String name);

    }

    public interface _XmlNamedElementFieldClause extends _XmlNamedElementClause {

        _XmlNamedElementFieldClause accept(SqlField field);

        _XmlNamedElementFieldClause accept(Expression value, SQLs.WordAs as, String name);

        _XmlNamedElementFieldClause accept(BiFunction<MappingType, String, Expression> funcRef, String value, SQLs.WordAs as, String name);

    }


    public interface _XmlTableColumnsClause {

        XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path, Expression columnExp, SQLs.WordDefault wordDefault, Expression defaultExp, SQLs.NullOption nullOption);

        XmlTableCommaClause columns(String name, MappingType type, SQLs.WordDefault wordDefault, Expression defaultExp, SQLs.NullOption nullOption);

        XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path, Expression columnExp, SQLs.NullOption nullOption);

        XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path, Expression columnExp, SQLs.WordDefault wordDefault, Expression defaultExp);

        XmlTableCommaClause columns(String name, MappingType type, SQLs.NullOption nullOption);

        XmlTableCommaClause columns(String name, MappingType type, SQLs.WordDefault wordDefault, Expression defaultExp);

        XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path, Expression columnExp);

        XmlTableCommaClause columns(String name, MappingType type);

        XmlTableCommaClause columns(String name, SQLs.WordsForOrdinality forOrdinality);


        XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, SQLs.WordDefault wordDefault, Expression defaultExp, SQLs.NullOption nullOption);


        XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, SQLs.NullOption nullOption);

        XmlTableCommaClause columns(String name, MappingType type, SQLs.WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, SQLs.WordDefault wordDefault, Expression defaultExp);


    }

    /**
     * <p>
     * This interface not start with underscore, so this interface can present in application developer code.
     *
     * @since 0.6.0
     */
    public interface XmlTableCommaClause {

        XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path, Expression columnExp, SQLs.WordDefault wordDefault, Expression defaultExp, SQLs.NullOption nullOption);

        XmlTableCommaClause comma(String name, MappingType type, SQLs.WordDefault wordDefault, Expression defaultExp, SQLs.NullOption nullOption);

        XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path, Expression columnExp, SQLs.NullOption nullOption);

        XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path, Expression columnExp, SQLs.WordDefault wordDefault, Expression defaultExp);

        XmlTableCommaClause comma(String name, MappingType type, SQLs.NullOption nullOption);

        XmlTableCommaClause comma(String name, MappingType type, SQLs.WordDefault wordDefault, Expression defaultExp);

        XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path, Expression columnExp);

        XmlTableCommaClause comma(String name, MappingType type);

        XmlTableCommaClause comma(String name, SQLs.WordsForOrdinality forOrdinality);


        XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, SQLs.WordDefault wordDefault, Expression defaultExp, SQLs.NullOption nullOption);


        XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, SQLs.NullOption nullOption);

        XmlTableCommaClause comma(String name, MappingType type, SQLs.WordPath path, BiFunction<MappingType, String, Expression> funcRefForColumnExp, String columnExp, SQLs.WordDefault wordDefault, Expression defaultExp);


    }


    public interface _RowsFromAsClause {

        _RowsFromCommaClause as(Consumer<PostgreStatement._FuncColumnDefinitionSpaceClause> consumer);

    }

    public interface _RowsFromSpaceClause {

        _RowsFromCommaClause space(SimpleExpression func);

        _RowsFromCommaClause space(SimplePredicate func);

        _RowsFromCommaClause space(_TabularFunction func);

        _RowsFromAsClause space(UndoneFunction func);

    }


    /**
     * <p>
     * This interface not start with underscore, so this interface can present in application developer code.
     *
     * @since 0.6.0
     */
    public interface _RowsFromCommaClause {

        _RowsFromCommaClause comma(SimpleExpression func);

        _RowsFromCommaClause comma(SimplePredicate func);

        _RowsFromCommaClause comma(_TabularFunction func);

        _RowsFromAsClause comma(UndoneFunction func);

    }

    public interface _RowsFromConsumerAsClause {

        RowFromConsumer as(Consumer<PostgreStatement._FuncColumnDefinitionSpaceClause> consumer);

    }

    public interface RowFromConsumer {

        RowFromConsumer accept(SimpleExpression func);

        RowFromConsumer accept(SimplePredicate func);

        RowFromConsumer accept(_TabularFunction func);

        _RowsFromConsumerAsClause accept(UndoneFunction func);

    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface WordDistinct extends Modifier, SQLs.ArgDistinct {

    }

    public interface WordName extends SQLWords {

    }

    public interface DoubleColon {

    }

    public interface _PeriodOverlapsClause {

        IPredicate overlaps(Expression start, Expression endOrLength);

        <T> IPredicate overlaps(Expression start, BiFunction<Expression, T, Expression> valueOperator, T value);

        <T> IPredicate overlaps(BiFunction<Expression, T, Expression> valueOperator, T value, Expression endOrLength);

        IPredicate overlaps(TypeInfer type, BiFunction<TypeInfer, Object, Expression> valueOperator, Object start, Object endOrLength);


    }
}
