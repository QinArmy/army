package io.army.criteria.postgre;

import io.army.criteria.Item;

public interface PgSingleDeleteSpec<I extends Item, Q extends Item> extends PgSingleDeleteClause<I, Q>,
        PostgreStatement._PostgreDynamicWithClause<PgSingleDeleteClause<I, Q>>,
        PostgreQuery._PostgreStaticWithClause<PgSingleDeleteClause<I, Q>> {

}
