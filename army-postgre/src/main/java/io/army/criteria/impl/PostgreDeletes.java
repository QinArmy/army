package io.army.criteria.impl;

import io.army.criteria.Item;
import io.army.criteria.SubStatement;
import io.army.criteria.impl.inner.postgre._PostgreDelete;
import io.army.criteria.postgre.PostgreCteBuilder;
import io.army.criteria.postgre.PostgreDelete;

import java.util.function.Function;

abstract class PostgreDeletes<I extends Item, Q extends Item, WE, WR, WA>
        extends SingleDelete.WithSingleDelete<I, Q, PostgreCteBuilder, WE, WR, WA, Object, Object>
        implements PostgreDelete, _PostgreDelete {


    static <I extends Item> PostgreDelete._DynamicSubMaterializedSpec<I> dynamicCteDelete(CriteriaContext outerContext
            , Function<SubStatement, I> function) {
        throw new UnsupportedOperationException();
    }


    PostgreDeletes(CriteriaContext context) {
        super(context);
    }


}
