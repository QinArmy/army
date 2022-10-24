package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.Select;
import io.army.criteria.SubQuery;
import io.army.criteria.postgre.PostgreInsert;
import io.army.criteria.postgre.PostgreQuery;

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
        return PostgreQueries.primaryQuery();
    }


    public static PostgreQuery._SubWithCteSpec<SubQuery> subQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), SQLs::_identity);
    }


    public static PostgreQuery._SubWithCteSpec<Expression> scalarSubQuery() {
        return PostgreQueries.subQuery(ContextStack.peek(), ScalarExpression::from);
    }


}
