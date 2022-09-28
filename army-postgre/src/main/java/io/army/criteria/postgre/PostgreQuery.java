package io.army.criteria.postgre;

import io.army.criteria.DialectStatement;
import io.army.criteria.Query;

public interface PostgreQuery extends Query, DialectStatement {


    interface _PostgreComplexCommandSpec<C> {

    }

    interface _PostgreDynamicWithSpec<C>
            extends DialectStatement._DynamicWithCteClause<C, PostgreCteBuilder, _PostgreComplexCommandSpec<C>> {

    }


}
