package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.postgre.PostgreInsert;
import io.army.criteria.postgre.PostgreQuery;
import io.army.criteria.postgre.PostgreUpdate;

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
        return PostgreQueries.primaryQuery(null, SQLs::_identity);
    }


    public static PostgreQuery._WithSpec<SubQuery> subQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), SQLs::_identity);
    }


    public static PostgreQuery._WithSpec<Expression> scalarSubQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), ScalarExpression::from);
    }

    static PostgreUpdate._SingleWithSpec<Update, ReturningUpdate> singleUpdate() {
        return PostgreUpdates.single(SQLs::_identity, SQLs::_identity);
    }

    static PostgreUpdate._BatchSingleWithSpec<Update, ReturningUpdate> batchSingleUpdate() {
        return PostgreUpdates.batch(SQLs::_identity, SQLs::_identity);
    }


}
