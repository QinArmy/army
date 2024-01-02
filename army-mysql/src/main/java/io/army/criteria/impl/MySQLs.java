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
import io.army.criteria.dialect.SQLCommand;
import io.army.criteria.mysql.*;
import io.army.mapping.LocalDateTimeType;
import io.army.mapping.LocalDateType;
import io.army.mapping.LocalTimeType;
import io.army.mapping.MappingType;

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
    public static final SQLs.WordPath PATH = SqlWords.KeyWordPath.PATH;
    public static final SQLs.WordExists EXISTS = SqlWords.KeyWordExists.EXISTS;

    public static final SQLs.WordColumns COLUMNS = SqlWords.KeyWordColumns.COLUMNS;
    public static final SQLs.WordNested NESTED = SqlWords.KeyWordNested.NESTED;
    public static final SQLs.WordsForOrdinality FOR_ORDINALITY = SqlWords.KeyWordsForOrdinality.FOR_ORDINALITY;
    public static final SQLs.WordError ERROR = SqlWords.KeyWordError.ERROR;
    public static final WordsAtTimeZone AT_TIME_ZONE = MySQLWords.KeyWordsAtTimeZone.AT_TIME_ZONE;


    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalDateType}
     *
     * @see MySQLs#currentDate()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_curdate">CURRENT_DATE()</a>
     */
    public static final Expression CURRENT_DATE = LiteralFunctions.noParensFunc("CURRENT_DATE", LocalDateType.INSTANCE);

    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalTimeType}
     *
     * @see MySQLs#currentTime()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_curdate">CURRENT_TIME()</a>
     */
    public static final Expression CURRENT_TIME = LiteralFunctions.noParensFunc("CURRENT_TIME", LocalTimeType.INSTANCE);

    /**
     * <p>The {@link MappingType} of function return type: {@link  LocalDateTimeType}
     *
     * @see MySQLs#currentTimestamp()
     * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/date-and-time-functions.html#function_curdate">CURRENT_TIME()</a>
     */
    public static final Expression CURRENT_TIMESTAMP = LiteralFunctions.noParensFunc("CURRENT_TIMESTAMP", LocalDateTimeType.INSTANCE);


    /**
     * <p>
     * create single-table INSERT statement that is primary statement and support {@link io.army.meta.ChildTableMeta}.
     */
    public static MySQLInsert._PrimaryOptionSpec singleInsert() {
        return MySQLInserts.singleInsert();
    }


    public static MySQLReplace._PrimaryOptionSpec singleReplace() {
        return MySQLReplaces.singleReplace();
    }


    public static MySQLQuery._WithSpec<Select> query() {
        return MySQLQueries.simpleQuery();
    }

    public static MySQLQuery._WithSpec<Statement._BatchSelectParamSpec> batchQuery() {
        return MySQLQueries.batchQuery();
    }


    public static MySQLQuery._WithSpec<SubQuery> subQuery() {
        return MySQLQueries.subQuery(ContextStack.peek(), SQLs::identity);
    }


    public static MySQLQuery._WithSpec<Expression> scalarSubQuery() {
        return MySQLQueries.subQuery(ContextStack.peek(), Expressions::scalarExpression);
    }


    public static MySQLValues._ValueSpec<Values> primaryValues() {
        return MySQLSimpleValues.simpleValues(SQLs::identity);
    }


    public static MySQLValues._ValueSpec<SubValues> subValues() {
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


    public static MySQLLoadData._LoadDataClause<SQLCommand> loadDataCommand() {
        return MySQLLoads.loadDataCommand(SQLs::identity);
    }


    public interface Modifier extends Query.SelectModifier {

    }

    public interface WordDistinct extends Modifier, SQLs.ArgDistinct {

    }


    public interface WordsAtTimeZone extends SQLWords {

    }

}
