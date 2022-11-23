package io.army.criteria.standard;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.meta.*;

public interface StandardUpdate extends StandardStatement {


    interface _WhereAndSpec<I extends Item> extends Update._UpdateWhereAndClause<_WhereAndSpec<I>>
            , _DmlUpdateSpec<I> {

    }


    interface _StandardWhereClause<I extends Item> extends _WhereClause<_DmlUpdateSpec<I>, _WhereAndSpec<I>> {

    }

    interface _WhereSpec<I extends Item, F extends TableField> extends _StandardSetClause<I, F>
            , _StandardWhereClause<I> {


    }

    interface _StandardSetClause<I extends Item, F extends TableField>
            extends Update._StaticSetClause<F, _WhereSpec<I, F>>
            , Update._DynamicSetClause<ItemPairs<F>, _StandardWhereClause<I>> {

    }

    interface _SingleUpdateClause<I extends Item> {

        <T> _StandardSetClause<I, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <P> _StandardSetClause<I, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, SQLs.WordAs as, String tableAlias);


    }

    interface _DomainUpdateClause {

        _StandardSetClause<Update, FieldMeta<?>> update(TableMeta<?> table, String tableAlias);

        <T> _StandardSetClause<Update, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <T> _StandardSetClause<Update, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as, String tableAlias);

    }

    interface _BatchWhereAndSpec<I extends Item>
            extends Update._UpdateWhereAndClause<_BatchWhereAndSpec<I>>
            , _BatchParamClause<_DmlUpdateSpec<I>> {

    }

    interface _BatchWhereClause<I extends Item>
            extends _WhereClause<_BatchParamClause<_DmlUpdateSpec<I>>, _BatchWhereAndSpec<I>> {

    }

    interface _BatchWhereSpec<I extends Item, F extends TableField> extends _BatchSetClause<I, F>
            , _BatchWhereClause<I> {

    }

    interface _BatchSetClause<I extends Item, F extends TableField>
            extends Update._StaticBatchSetClause<F, _BatchWhereSpec<I, F>>
            , Update._DynamicSetClause<BatchItemPairs<F>, _BatchWhereClause<I>> {


    }

    interface _BatchSingleUpdateClause {

        <T> _BatchSetClause<Update, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <P> _BatchSetClause<Update, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, SQLs.WordAs as, String tableAlias);

    }

    interface _BatchDomainUpdateClause {

        _BatchSetClause<Update, FieldMeta<?>> update(TableMeta<?> table, String tableAlias);

        <T> _BatchSetClause<Update, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <T> _BatchSetClause<Update, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as, String tableAlias);

    }


}
