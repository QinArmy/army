package io.army.criteria.postgre;

import io.army.criteria.DialectStatement;
import io.army.criteria.Query;

public interface PostgreQuery extends Query, DialectStatement {

    interface _SelectSpec<C> {

    }


    interface _PostgreDynamicWithSpec<C, SR>
            extends DialectStatement._DynamicWithCteClause<C, PostgreCteBuilder, SR> {

    }


}
