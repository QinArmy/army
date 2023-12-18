package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SQLCommand;
import io.army.criteria.mysql.*;
import io.army.mapping.StringType;

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
    public static final WordUsing USING = MySQLWords.KeyWordUsing.USING;
    public static final SQLs.WordPath PATH = SqlWords.KeyWordPath.PATH;
    @Deprecated
    public static final WordExistsPath EXISTS_PATH = MySQLWords.KeyWordExistsPath.EXISTS_PATH;
    public static final SQLs.WordExists EXISTS = SqlWords.KeyWordExists.EXISTS;
    public static final SQLs.WordColumns COLUMNS = SqlWords.KeyWordColumns.COLUMNS;
    public static final SQLs.WordNested NESTED = SqlWords.KeyWordNested.NESTED;
    public static final SQLs.WordsForOrdinality FOR_ORDINALITY = SqlWords.KeyWordsForOrdinality.FOR_ORDINALITY;
    public static final SQLs.WordError ERROR = SqlWords.KeyWordError.ERROR;
    public static final WordsAtTimeZone AT_TIME_ZONE = MySQLWords.KeyWordsAtTimeZone.AT_TIME_ZONE;
    @Deprecated
    public static final SimpleExpression LITERAL_one = SQLs.literal(StringType.INSTANCE, "one");
    @Deprecated
    public static final SimpleExpression LITERAL_all = SQLs.literal(StringType.INSTANCE, "all");


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

    public interface WordUsing extends SQLWords {

    }

    public interface WordNested extends SQLWords {

    }

    public interface WordExistsPath extends SQLWords {

    }

    public interface WordsAtTimeZone extends SQLWords {

    }

}
