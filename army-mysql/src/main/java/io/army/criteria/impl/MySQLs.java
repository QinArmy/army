package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SQLCommand;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.mysql.*;

public abstract class MySQLs extends MySQLSyntax {

    /**
     * private constructor
     */
    private MySQLs() {
    }

    public static MySQLInsert._PrimaryOptionSpec<InsertStatement> singleInsert() {
        return MySQLInserts.primaryInsert(SQLs::_identity);
    }

    public static MySQLReplace._PrimaryOptionSpec singleReplace() {
        return MySQLReplaces.primaryReplace();
    }


    public static MySQLQuery._WithSpec<Select> query() {
        return MySQLQueries.primaryQuery(null, ContextStack.peekIfBracket(), SQLs::_identity);
    }


    public static MySQLQuery._WithSpec<SubQuery> subQuery() {
        return MySQLQueries.subQuery(null, ContextStack.peek(), SQLs::_identity);
    }


    public static MySQLQuery._WithSpec<Expression> scalarSubQuery() {
        return MySQLQueries.subQuery(null, ContextStack.peek(), Expressions::scalarExpression);
    }


    public static MySQLValues._ValueSpec<Values> primaryValues() {
        return MySQLSimpleValues.primaryValues(ContextStack.peekIfBracket(), SQLs::_identity);
    }


    public static MySQLValues._ValueSpec<SubValues> subValues() {
        return MySQLSimpleValues.subValues(ContextStack.peek(), SQLs::_identity);
    }


    public static MySQLUpdate._SingleWithSpec<UpdateStatement> singleUpdate() {
        return MySQLSingleUpdate.simple(null, SQLs::_identity);
    }


    public static MySQLUpdate._BatchSingleWithSpec<BatchUpdate> batchSingleUpdate() {
        return MySQLSingleUpdate.batch(SQLs::_batchUpdateIdentity);
    }

    public static MySQLUpdate._MultiWithSpec<UpdateStatement> multiUpdate() {
        return MySQLMultiUpdate.simple(SQLs._UPDATE_IDENTITY);
    }


    public static MySQLUpdate._BatchMultiWithSpec<UpdateStatement> batchMultiUpdate() {
        return MySQLMultiUpdate.batch(SQLs::_identity);
    }

    public static MySQLDelete._SingleWithSpec<DeleteStatement> singleDelete() {
        return MySQLSingleDelete.simple(null, SQLs::_identity);
    }


    public static MySQLDelete._BatchSingleWithSpec<DeleteStatement> batchSingleDelete() {
        return MySQLSingleDelete.batch(SQLs::_identity);
    }

    public static MySQLDelete._MultiWithSpec<DeleteStatement> multiDelete() {
        return MySQLMultiDelete.simple(null, SQLs::_identity);
    }


    public static MySQLDelete._BatchMultiWithSpec<DeleteStatement> batchMultiDelete() {
        return MySQLMultiDelete.batch(SQLs::_identity);
    }


    public static MySQLLoadData._LoadDataClause<SQLCommand> loadDataCommand() {
        return MySQLLoads.loadDataCommand(SQLs::_identity);
    }


}
