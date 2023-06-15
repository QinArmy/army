package io.army.criteria.standard;

import io.army.criteria.Item;
import io.army.criteria.impl.SQLs;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

public interface StandardDelete extends StandardStatement {

    interface _DeleteFromClause<R> extends Item {

        R deleteFrom(SingleTableMeta<?> table, SQLs.WordAs as, String tableAlias);

    }



    interface _WhereAndSpec<I extends Item> extends _WhereAndClause<_WhereAndSpec<I>>,
            _DmlDeleteSpec<I> {

    }

    interface _WhereSpec<I extends Item>
            extends _WhereClause<_DmlDeleteSpec<I>, _WhereAndSpec<I>> {

    }


    interface _StandardDeleteClause<I extends Item> extends _DeleteFromClause<_WhereSpec<I>> {

    }


    interface _DomainDeleteClause<I extends Item> extends Item {

        _WhereSpec<I> deleteFrom(TableMeta<?> table, SQLs.WordAs as, String tableAlias);

    }




    interface _WithSpec<I extends Item> extends _StandardDynamicWithClause<_StandardDeleteClause<I>>,
            _StandardStaticWithClause<_StandardDeleteClause<I>>,
            _StandardDeleteClause<I> {

    }



}
