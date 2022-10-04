package io.army.criteria.impl;


import io.army.criteria.ScalarExpression;
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


    public static PostgreInsert._PrimaryOptionSpec<Void> singleInsert() {
        return PostgreInserts.primaryInsert(null);
    }

    /**
     * @param criteria non-null criteria instance,java bean or {@link java.util.Map}.
     */
    public static <C> PostgreInsert._PrimaryOptionSpec<C> singleInsert(C criteria) {
        return PostgreInserts.primaryInsert(criteria);
    }

    public static PostgreQuery._SubWithCteSpec<Void, SubQuery> subQuery() {
        return PostgreSimpleQuery.subQuery(null, ContextStack.peek(), Postgres::_thisSubQuery);
    }

    /**
     * @param criteria non-null criteria instance,java bean or {@link java.util.Map}.
     */
    public static <C> PostgreQuery._SubWithCteSpec<C, SubQuery> subQuery(C criteria) {
        return PostgreSimpleQuery.subQuery(criteria, ContextStack.peek(criteria), Postgres::_thisSubQuery);
    }

    public static PostgreQuery._SubWithCteSpec<Void, ScalarExpression> scalarSubQuery() {
        return PostgreSimpleQuery.subQuery(null, ContextStack.peek(), ScalarQueryExpression::from);
    }

    /**
     * @param criteria non-null criteria instance,java bean or {@link java.util.Map}.
     */
    public static <C> PostgreQuery._SubWithCteSpec<C, SubQuery> scalarSubQuery(C criteria) {
        return PostgreSimpleQuery.subQuery(criteria, ContextStack.peek(criteria), ScalarQueryExpression::from);
    }


}
