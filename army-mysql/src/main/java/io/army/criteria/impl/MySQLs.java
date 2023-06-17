package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SQLCommand;
import io.army.criteria.mysql.*;

public abstract class MySQLs extends MySQLSyntax {

    /**
     * private constructor
     */
    private MySQLs() {
    }


    /**
     * <p>
     * create single-table INSERT statement that is primary statement and support {@link io.army.meta.ChildTableMeta}.
     * </p>
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


}
