package io.army.criteria.standard;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.meta.*;

/**
 * <p>
 * This interface representing standard update statement.
 * </p>
 *
 * @since 1.0
 */
public interface StandardUpdate extends StandardStatement {


    interface _WhereAndSpec<I extends Item> extends UpdateStatement._UpdateWhereAndClause<_WhereAndSpec<I>>,
            _DmlUpdateSpec<I> {

    }


    interface _StandardWhereClause<I extends Item> extends _WhereClause<_DmlUpdateSpec<I>, _WhereAndSpec<I>> {

    }


    interface _StandardSetClause<I extends Item, F extends TableField>
            extends UpdateStatement._StaticSetClause<F, _WhereSpec<I, F>>,
            UpdateStatement._DynamicSetClause<UpdateStatement._ItemPairs<F>, _StandardWhereClause<I>> {

    }

    interface _WhereSpec<I extends Item, F extends TableField> extends _StandardSetClause<I, F>,
            _StandardWhereClause<I> {


    }

    interface _SingleUpdateClause<I extends Item> extends Item {

        <T> _StandardSetClause<I, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <P> _StandardSetClause<I, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, SQLs.WordAs as, String tableAlias);


    }

    interface _DomainUpdateClause extends Item {

        _StandardSetClause<Update, FieldMeta<?>> update(TableMeta<?> table, String tableAlias);

        <T> _StandardSetClause<Update, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <T> _StandardSetClause<Update, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as, String tableAlias);

    }

    interface _BatchWhereAndSpec<I extends Item>
            extends UpdateStatement._UpdateWhereAndClause<_BatchWhereAndSpec<I>>,
            _BatchParamClause<_DmlUpdateSpec<I>> {

    }

    interface _BatchWhereClause<I extends Item>
            extends _WhereClause<_BatchParamClause<_DmlUpdateSpec<I>>, _BatchWhereAndSpec<I>> {

    }


    interface _BatchSetClause<I extends Item, F extends TableField>
            extends UpdateStatement._StaticBatchSetClause<F, _BatchWhereSpec<I, F>>,
            UpdateStatement._DynamicSetClause<UpdateStatement._BatchItemPairs<F>, _BatchWhereClause<I>> {

    }

    interface _BatchWhereSpec<I extends Item, F extends TableField> extends _BatchSetClause<I, F>,
            _BatchWhereClause<I> {

    }

    interface _BatchSingleUpdateClause extends Item {

        <T> _BatchSetClause<BatchUpdate, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <P> _BatchSetClause<BatchUpdate, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, SQLs.WordAs as, String tableAlias);

    }

    interface _BatchDomainUpdateClause extends Item {

        _BatchSetClause<BatchUpdate, FieldMeta<?>> update(TableMeta<?> table, String tableAlias);

        <T> _BatchSetClause<BatchUpdate, FieldMeta<T>> update(SingleTableMeta<T> table, SQLs.WordAs as, String tableAlias);

        <T> _BatchSetClause<BatchUpdate, FieldMeta<? super T>> update(ChildTableMeta<T> table, SQLs.WordAs as, String tableAlias);

    }


    interface _WithSpec<I extends Item> extends _StandardDynamicWithClause<_SingleUpdateClause<I>>,
            _StandardStaticWithClause<_SingleUpdateClause<I>>,
            _SingleUpdateClause<I> {

    }

    interface _BatchWithSpec extends _StandardDynamicWithClause<_BatchSingleUpdateClause>,
            _StandardStaticWithClause<_BatchSingleUpdateClause>,
            _BatchSingleUpdateClause {

    }


}
