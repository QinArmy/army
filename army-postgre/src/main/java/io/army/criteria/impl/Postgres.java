package io.army.criteria.impl;


import io.army.criteria.*;
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
        return PostgreInserts.primaryInsert();
    }

    public static PostgreQuery._WithSpec<Select> query() {
        return PostgreQueries.primaryQuery(null, null, SQLs::identity);
    }


    public static PostgreQuery._WithSpec<SubQuery> subQuery() {
        return PostgreQueries.subQuery(null, ContextStack.peek(), SQLs::identity);
    }


    public static PostgreQuery._WithSpec<Expression> scalarSubQuery() {
        return PostgreQueries.subQuery(null, ContextStack.peek(), ScalarExpression::from);
    }

    public static PostgreUpdate._SingleWithSpec<Update, ReturningUpdate> singleUpdate() {
        return PostgreUpdates.single(SQLs::identity, SQLs::identity);
    }

    public static PostgreUpdate._BatchSingleWithSpec<Update, ReturningUpdate> batchSingleUpdate() {
        return PostgreUpdates.batch(SQLs::identity, SQLs::identity);
    }

    public static PostgreDelete._SingleWithSpec<Delete, ReturningDelete> singleDelete() {
        return PostgreDeletes.primarySingle(SQLs::identity, SQLs::identity);
    }

    public static PostgreDelete._BatchSingleWithSpec<Delete, ReturningDelete> batchSingleDelete() {
        return PostgreDeletes.batch(SQLs::identity, SQLs::identity);
    }

    public static PostgreValues._WithSpec<Values> primaryValues() {
        return PostgreSimpleValues.primaryValues(null, null, SQLs::identity);
    }

    public static PostgreValues._WithSpec<SubValues> subValues() {
        return PostgreSimpleValues.subValues(null, ContextStack.peek(), SQLs::identity);
    }


}
