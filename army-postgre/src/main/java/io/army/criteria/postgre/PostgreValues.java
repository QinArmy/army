package io.army.criteria.postgre;

import io.army.criteria.Item;
import io.army.criteria.RowSet;

public interface PostgreValues extends PostgreStatement, RowSet.DqlValues {


    interface _MinWithSpec<I extends Item> extends Item {

    }

    interface _DynamicSubMaterializedSpec<I extends Item>
            extends _CteMaterializedClause<_MinWithSpec<I>>
            , _MinWithSpec<I> {

    }

    interface _DynamicCteValuesSpec
            extends _SimpleCteLeftParenSpec<_DynamicSubMaterializedSpec<_AsCteClause<PostgreCteBuilder>>> {

    }

}
