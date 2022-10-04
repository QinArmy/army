package io.army.criteria.postgre;

import io.army.criteria.DialectStatement;
import io.army.criteria.Item;
import io.army.criteria.Query;

public interface PostgreQuery extends Query, DialectStatement {

    interface _SelectSpec<C, Q extends Item> {

    }

    interface _PostgreDynamicWithClause<C, SR>
            extends DialectStatement._DynamicWithCteClause<C, PostgreCteBuilder, SR> {

    }

    interface _SubWithCteSpec<C, Q extends Item> {

    }


}
