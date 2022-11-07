package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.*;

public abstract class MySQLs extends MySQLFuncSyntax2 {

    /**
     * private constructor
     */
    private MySQLs() {
    }

    public static MySQLInsert._PrimaryOptionSpec singleInsert() {
        return MySQLInserts.primaryInsert();
    }

    public static MySQLReplace._PrimaryOptionSpec singleReplace() {
        return MySQLReplaces.primaryReplace();
    }

    public static MySQLQuery._WithSpec<Select> query() {
        return MySQLQueries.primaryQuery(null, null, SQLs::identity);
    }


    public static MySQLQuery._WithSpec<SubQuery> subQuery() {
        return MySQLQueries.subQuery(null, ContextStack.peek(), SQLs::identity);
    }


    public static MySQLQuery._WithSpec<Expression> scalarSubQuery() {
        return MySQLQueries.subQuery(null, ContextStack.peek(), ScalarExpression::from);
    }


    public static MySQLValues._ValueSpec<Values> primaryValues() {
        return MySQLSimpleValues.primaryValues(null, SQLs::identity);
    }


    public static MySQLValues._ValueSpec<SubValues> subValues() {
        return MySQLSimpleValues.subValues(ContextStack.peek(), SQLs::identity);
    }


    public static MySQLUpdate._SingleWithSpec<Update> singleUpdate() {
        return MySQLSingleUpdate.simple(SQLs::identity);
    }


    public static MySQLUpdate._BatchSingleWithSpec<Update> batchSingleUpdate() {
        return MySQLSingleUpdate.batch(SQLs::identity);
    }

    public static MySQLUpdate._MultiWithSpec<Update> multiUpdate() {
        return MySQLMultiUpdate.simple(SQLs::identity);
    }


    public static MySQLUpdate._BatchMultiWithSpec<Update> batchMultiUpdate() {
        return MySQLMultiUpdate.batch(SQLs::identity);
    }

    public static MySQLDelete._SingleWithSpec<Delete> singleDelete() {
        return MySQLSingleDelete.simple(null, SQLs::identity);
    }


    public static MySQLDelete._BatchSingleWithSpec<Delete> batchSingleDelete() {
        return MySQLSingleDelete.batch(null, SQLs::identity);
    }

    public static MySQLDelete._MultiWithSpec<Delete> multiDelete() {
        return MySQLMultiDelete.simple(SQLs::identity);
    }


    public static MySQLDelete._BatchMultiWithSpec<Delete> batchMultiDelete() {
        return MySQLMultiDelete.batch(SQLs::identity);
    }


    public static MySQLLoadData._LoadDataClause<SQLCommand> loadDataCommand() {
        return MySQLLoads.loadDataCommand(SQLs::identity);
    }


}
