package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.mysql.*;

import java.util.Objects;

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

    public static MySQLQuery._WithCteSpec<Select> query() {
        return MySQLQueries.primaryQuery(SQLs::_identity);
    }


    public static MySQLQuery._WithCteSpec<SubQuery> subQuery() {
        return MySQLQueries.subQuery(ContextStack.peek(), SQLs::_identity);
    }


    public static MySQLQuery._WithCteSpec<Expression> scalarSubQuery() {
        return MySQLQueries.subQuery(ContextStack.peek(), ScalarExpression::from);
    }


    public static MySQLDqlValues._ValuesStmtValuesClause<Void, Values> valuesStmt() {
        return MySQLSimpleValues.primaryValues(null);
    }

    public static <C> MySQLDqlValues._ValuesStmtValuesClause<C, Values> valuesStmt(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSimpleValues.primaryValues(criteria);
    }

    public static MySQLDqlValues._ValuesStmtValuesClause<Void, SubValues> subValues() {
        return MySQLSimpleValues.subValues(null);
    }

    public static <C> MySQLDqlValues._ValuesStmtValuesClause<C, SubValues> subValues(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLSimpleValues.subValues(criteria);
    }


    public static MySQLUpdate._SingleWithSpec<Update> singleUpdate() {
        return MySQLSingleUpdate.simple(SQLs::_identity);
    }


    public static MySQLUpdate._BatchSingleWithSpec<Update> batchSingleUpdate() {
        return MySQLSingleUpdate.batch(SQLs::_identity);
    }

    public static MySQLUpdate._MultiWithSpec<Update> multiUpdate() {
        return MySQLMultiUpdate.simple(SQLs::_identity);
    }


    public static MySQLUpdate._BatchMultiWithSpec<Update> batchMultiUpdate() {
        return MySQLMultiUpdate.batch(SQLs::_identity);
    }

    public static MySQLDelete._SingleWithSpec<Delete> singleDelete() {
        return MySQLSingleDelete.simple(SQLs::_identity);
    }


    public static MySQLDelete._BatchSingleWithSpec<Delete> batchSingleDelete() {
        return MySQLSingleDelete.batch(SQLs::_identity);
    }

    public static MySQLDelete._MultiWithSpec<Delete> multiDelete() {
        return MySQLMultiDelete.simple(SQLs::_identity);
    }


    public static MySQLDelete._BatchMultiWithSpec<Delete> batchMultiDelete() {
        return MySQLMultiDelete.batch(SQLs::_identity);
    }


    public static MySQLLoad._LoadDataClause<Void> loadDataStmt() {
        return MySQLLoads.loadDataStmt(null);
    }

    public static <C> MySQLLoad._LoadDataClause<C> loadDataStmt(C criteria) {
        Objects.requireNonNull(criteria);
        return MySQLLoads.loadDataStmt(criteria);
    }





}
