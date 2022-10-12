package io.army.criteria;

import io.army.meta.TableMeta;

public interface StandardDelete extends StandardStatement {

    interface StandardDeleteFromClause<DR> {

        DR deleteFrom(TableMeta<?> table, String tableAlias);

    }


    interface StandardDeleteSpec<I extends Item> extends StandardDeleteFromClause<StandardWhereSpec<I>> {

    }

    interface StandardWhereSpec<I extends Item>
            extends _WhereClause<DmlStatement._DmlDeleteSpec<I>, StandardWhereAndSpec<I>> {

    }

    interface StandardWhereAndSpec<I extends Item> extends _WhereAndClause<StandardWhereAndSpec<I>>
            , DmlStatement._DmlDeleteSpec<I> {

    }

    interface StandardBatchDeleteSpec<I extends Item> extends StandardDeleteFromClause<StandardBatchWhereSpec<I>> {

    }

    interface StandardBatchWhereSpec<I extends Item>
            extends _WhereClause<_BatchParamClause<DmlStatement._DmlDeleteSpec<I>>, StandardBatchWhereAndSpec<I>> {

    }

    interface StandardBatchWhereAndSpec<I extends Item> extends _WhereAndClause<StandardBatchWhereAndSpec<I>>
            , _BatchParamClause<DmlStatement._DmlDeleteSpec<I>> {

    }


}
