package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.dialect.ReturningDelete;
import io.army.criteria.dialect.ReturningUpdate;
import io.army.criteria.dialect.SubQuery;
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


    public static PostgreInsert._PrimaryOptionSpec singleInsert() {
        return PostgreInserts.primaryInsert(null);
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

    public static PostgreUpdate._SingleWithSpec<Update, ReturningUpdate> singleUpdate() {
        return PostgreUpdates.simple(null, SQLs::_identity, SQLs::_identity);
    }

    public static PostgreUpdate._BatchSingleWithSpec<Update, ReturningUpdate> batchSingleUpdate() {
        return PostgreUpdates.batch(SQLs::_identity, SQLs::_identity);
    }

    public static PostgreDelete._SingleWithSpec<Delete, ReturningDelete> singleDelete() {
        return PostgreDeletes.primarySingle(SQLs::_identity, SQLs::_identity);
    }

    public static PostgreDelete._BatchSingleWithSpec<Delete, ReturningDelete> batchSingleDelete() {
        return PostgreDeletes.batch(SQLs::_identity, SQLs::_identity);
    }

    public static PostgreValues._WithSpec<Values> primaryValues() {
        return PostgreSimpleValues.primaryValues(null, null, SQLs::_identity);
    }

    public static PostgreValues._WithSpec<SubValues> subValues() {
        return PostgreSimpleValues.subValues(null, ContextStack.peek(), SQLs::_identity);
    }


}
