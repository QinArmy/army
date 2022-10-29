package io.army.criteria.standard;

import io.army.criteria.Item;
import io.army.meta.TableMeta;

public interface StandardDelete extends StandardStatement {

    interface _DeleteFromClause<DR> {

        DR deleteFrom(TableMeta<?> table, String tableAlias);

    }


    interface _WhereAndSpec<I extends Item> extends _WhereAndClause<_WhereAndSpec<I>>
            , _DmlDeleteSpec<I> {

    }

    interface _WhereSpec<I extends Item>
            extends _WhereClause<_DmlDeleteSpec<I>, _WhereAndSpec<I>> {

    }


    interface _StandardDeleteClause<I extends Item> extends _DeleteFromClause<_WhereSpec<I>> {

    }

    interface _BatchWhereAndSpec<I extends Item> extends _WhereAndClause<_BatchWhereAndSpec<I>>
            , _BatchParamClause<_DmlDeleteSpec<I>> {

    }

    interface _BatchWhereSpec<I extends Item>
            extends _WhereClause<_BatchParamClause<_DmlDeleteSpec<I>>, _BatchWhereAndSpec<I>> {

    }


    interface _BatchDeleteClause<I extends Item> extends _DeleteFromClause<_BatchWhereSpec<I>> {

    }


}
