package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.dialect.*;
import io.army.criteria.postgre.*;

import java.util.function.Function;

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

    static final Function<ReturningUpdate, ReturningUpdate> _RETURNING_UPDATE_IDENTITY = SQLs._getIdentity();

    static final Function<ReturningDelete, ReturningDelete> _RETURNING_DELETE_IDENTITY = SQLs._getIdentity();

    static final Function<BatchReturningUpdate, BatchReturningUpdate> _BATCH_RETURNING_UPDATE_IDENTITY = SQLs._getIdentity();

    static final Function<BatchReturningDelete, BatchReturningDelete> _BATCH_RETURNING_DELETE_IDENTITY = SQLs._getIdentity();

    public static PostgreInsert._PrimaryOptionSpec singleInsert() {
        return PostgreInserts.singleInsert();
    }

    public static PostgreQuery._WithSpec<Select> query() {
        return PostgreQueries.primaryQuery(null, ContextStack.peekIfBracket(), SQLs::_identity, null);
    }


    public static PostgreQuery._WithSpec<SubQuery> subQuery() {
        return PostgreQueries.subQuery(null, ContextStack.peek(), SQLs::_identity, null);
    }


    public static PostgreQuery._WithSpec<Expression> scalarSubQuery() {
        return PostgreQueries.subQuery(null, ContextStack.peek(), Expressions::scalarExpression, null);
    }

    /**
     * <p>
     * create new simple(non-batch) single-table UPDATE statement that is primary statement.
     * </p>
     */
    public static PostgreUpdate._SingleWithSpec<Update, ReturningUpdate> singleUpdate() {
        return PostgreUpdates.simple();
    }

    /**
     * <p>
     * create new batch single-table UPDATE statement that is primary statement.
     * </p>
     */
    public static PostgreUpdate._BatchSingleWithSpec<BatchUpdate, BatchReturningUpdate> batchSingleUpdate() {
        return PostgreUpdates.batch();
    }

    /**
     * <p>
     * create new simple(non-batch) single-table DELETE statement that is primary statement.
     * </p>
     */
    public static PostgreDelete._SingleWithSpec<Delete, ReturningDelete> singleDelete() {
        return PostgreDeletes.simple();
    }

    /**
     * <p>
     * create new batch single-table DELETE statement that is primary statement.
     * </p>
     */
    public static PostgreDelete._BatchSingleWithSpec<BatchDelete, BatchReturningDelete> batchSingleDelete() {
        return PostgreDeletes.batch();
    }

    public static PostgreValues._WithSpec<Values> primaryValues() {
        return PostgreSimpleValues.primaryValues(null, null, SQLs::_identity);
    }

    public static PostgreValues._WithSpec<SubValues> subValues() {
        return PostgreSimpleValues.subValues(null, ContextStack.peek(), SQLs::_identity);
    }


}
