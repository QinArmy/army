package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.dialect.*;
import io.army.criteria.postgre.*;

/**
 * <p>
 * This class is Postgre SQL syntax utils.
 * </p>
 *
 * @since 1.0
 */
public abstract class Postgres extends PostgreFuncSyntax {


    /**
     * private constructor
     */
    private Postgres() {
    }

    /**
     * <p>
     * create single-table INSERT statement that is primary statement.
     * </p>
     */
    public static PostgreInsert._PrimaryOptionSpec singleInsert() {
        return PostgreInserts.singleInsert();
    }

    /**
     * <p>
     * create SELECT statement that is primary statement.
     * </p>
     */
    public static PostgreQuery._WithSpec<Select> query() {
        return PostgreQueries.simpleQuery(ContextStack.peekIfBracket(), SQLs::_identity);
    }

    /**
     * <p>
     * create SUB-SELECT statement that is sub query statement.
     * </p>
     */
    public static PostgreQuery._WithSpec<SubQuery> subQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), SQLs::_identity);
    }

    /**
     * <p>
     * create SUB-SELECT statement that is sub query statement and would be converted to {@link Expression}.
     * </p>
     */
    public static PostgreQuery._WithSpec<Expression> scalarSubQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), Expressions::scalarExpression);
    }

    /**
     * <p>
     * create simple(non-batch) single-table UPDATE statement that is primary statement.
     * </p>
     */
    public static PostgreUpdate._SingleWithSpec<Update, ReturningUpdate> singleUpdate() {
        return PostgreUpdates.simple();
    }

    /**
     * <p>
     * create batch single-table UPDATE statement that is primary statement.
     * </p>
     */
    public static PostgreUpdate._BatchSingleWithSpec<BatchUpdate, BatchReturningUpdate> batchSingleUpdate() {
        return PostgreUpdates.batchUpdate();
    }

    /**
     * <p>
     * create simple(non-batch) single-table DELETE statement that is primary statement.
     * </p>
     */
    public static PostgreDelete._SingleWithSpec<Delete, ReturningDelete> singleDelete() {
        return PostgreDeletes.simpleDelete();
    }

    /**
     * <p>
     * create batch single-table DELETE statement that is primary statement.
     * </p>
     */
    public static PostgreDelete._BatchSingleWithSpec<BatchDelete, BatchReturningDelete> batchSingleDelete() {
        return PostgreDeletes.batchDelete();
    }

    public static PostgreValues._WithSpec<Values> simpleValues() {
        return PostgreSimpleValues.simpleValues(ContextStack.peekIfBracket(), SQLs::_identity);
    }

    public static PostgreValues._WithSpec<SubValues> subValues() {
        return PostgreSimpleValues.subValues(ContextStack.peek(), SQLs::_identity);
    }


}
