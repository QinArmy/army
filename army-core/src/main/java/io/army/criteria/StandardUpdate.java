package io.army.criteria;

import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

public interface StandardUpdate extends StandardStatement {


    interface _StandardWhereAndSpec<I extends Item> extends Update._UpdateWhereAndClause<_StandardWhereAndSpec<I>>
            , _DmlUpdateSpec<I> {

    }


    interface _StandardWhereClause<I extends Item> extends _WhereClause<_DmlUpdateSpec<I>, _StandardWhereAndSpec<I>> {

    }

    interface _StandardWhereSpec<I extends Item, F extends TableField> extends _StandardSetClause<I, F>
            , _StandardWhereClause<I> {


    }

    interface _StandardSetClause<I extends Item, F extends TableField>
            extends Update._StaticSetClause<F, _StandardWhereSpec<I, F>>
            , Update._DynamicSetClause<F, ItemPairs<F>, _StandardWhereClause<I>> {

    }

    interface _StandardSingleUpdateClause {

        <T> _StandardSetClause<Update, FieldMeta<T>> update(SingleTableMeta<T> table, String tableAlias);

        <P> _StandardSetClause<Update, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, String tableAlias);

    }

    interface _StandardDomainUpdateClause {

        <T> _StandardSetClause<Update, FieldMeta<? super T>> update(TableMeta<T> table, String tableAlias);

    }

    interface _StandardBatchWhereAndSpec<I extends Item>
            extends Update._UpdateWhereAndClause<_StandardBatchWhereAndSpec<I>>
            , _BatchParamClause<_DmlUpdateSpec<I>> {

    }

    interface _StandardBatchWhereClause<I extends Item>
            extends _WhereClause<_BatchParamClause<_DmlUpdateSpec<I>>, _StandardBatchWhereAndSpec<I>> {

    }

    interface _StandardBatchWhereSpec<I extends Item, F extends TableField> extends _StandardBatchSetClause<I, F>
            , _StandardBatchWhereClause<I> {

    }

    interface _StandardBatchSetClause<I extends Item, F extends TableField>
            extends Update._StaticBatchSetClause<F, _StandardBatchWhereSpec<I, F>>
            , Update._DynamicSetClause<F, BatchItemPairs<F>, _StandardBatchWhereClause<I>> {


    }

    interface _StandardBatchSingleUpdateClause {

        <T> _StandardBatchSetClause<Update, FieldMeta<T>> update(SingleTableMeta<T> table, String tableAlias);

        <P> _StandardBatchSetClause<Update, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, String tableAlias);

    }

    interface _StandardBatchDomainUpdateClause {

        <T> _StandardBatchSetClause<Update, FieldMeta<? super T>> update(TableMeta<T> table, String tableAlias);

    }


}
