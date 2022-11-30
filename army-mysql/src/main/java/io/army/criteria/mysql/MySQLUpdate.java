package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Supplier;


/**
 * <p>
 * This interface representing MySQL update statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 *
 * @since 1.0
 */
public interface MySQLUpdate extends MySQLStatement {


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Update._DmlUpdateSpec}</li>
     *          <li>method {@link Statement._DmlRowCountLimitClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _LimitSpec<I extends Item> extends Statement._DmlRowCountLimitClause<_DmlUpdateSpec<I>>
            , _DmlUpdateSpec<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLUpdate._LimitSpec}</li>
     *          <li>method {@link _StaticOrderByClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _OrderBySpec<I extends Item> extends _StaticOrderByClause<_LimitSpec<I>>, _LimitSpec<I>
            , _DynamicOrderByClause<SortItems, _LimitSpec<I>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLUpdate._OrderBySpec}</li>
     *          <li>method {@link Statement._WhereAndClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SingleWhereAndSpec<I extends Item> extends Update._UpdateWhereAndClause<_SingleWhereAndSpec<I>>
            , _OrderBySpec<I> {

    }

    interface _SingleWhereClause<I extends Item> extends _WhereClause<_OrderBySpec<I>, _SingleWhereAndSpec<I>> {

    }

    /**
     * <p>
     * This interface representing SET clause for single-table UPDATE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SingleSetClause<I extends Item, T>
            extends Update._StaticSetClause<FieldMeta<T>, _SingleWhereSpec<I, T>>
            , Update._DynamicSetClause<ItemPairs<FieldMeta<T>>, _SingleWhereClause<I>> {
    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _SingleSetClause}</li>
     *          <li>method {@link Statement._WhereClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SingleWhereSpec<I extends Item, T> extends _SingleWhereClause<I>, _SingleSetClause<I, T> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintForOrderByClause}</li>
     *          <li>method {@link _SingleSetClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SingleIndexHintSpec<I extends Item, T>
            extends MySQLQuery._IndexHintForOrderByClause<_SingleIndexHintSpec<I, T>>
            , _SingleSetClause<I, T> {

    }

    interface _SinglePartitionClause<I extends Item, T>
            extends _PartitionAndAsClause_0<_SingleIndexHintSpec<I, T>> {

    }

    interface _SingleUpdateClause<I extends Item> {

        <T> _SingleIndexHintSpec<I, T> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias);

        <P> _SingleIndexHintSpec<I, P> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias);

        <T> _SingleIndexHintSpec<I, T> update(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias);

        <P> _SingleIndexHintSpec<I, P> update(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias);

        <T> _SinglePartitionClause<I, T> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, SingleTableMeta<T> table);

        <P> _SinglePartitionClause<I, P> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, ComplexTableMeta<P, ?> table);

        <T> _SinglePartitionClause<I, T> update(SingleTableMeta<T> table);

        <P> _SinglePartitionClause<I, P> update(ComplexTableMeta<P, ?> table);

    }


    interface _SingleComma<I extends Item> extends _StaticWithCommaClause<_StaticCteParensSpec<_SingleComma<I>>>
            , _SingleUpdateClause<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLDynamicWithClause}</li>
     *          <li>method {@link _SingleUpdateClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SingleWithSpec<I extends Item>
            extends _MySQLDynamicWithClause<_SingleUpdateClause<I>>
            , _StaticWithClause<_StaticCteParensSpec<_SingleComma<I>>>
            , _SingleUpdateClause<I> {

    }

    /*################################## blow batch single-table update spec ##################################*/

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._DmlRowCountLimitClause}</li>
     *          <li>method {@link Statement._BatchParamClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchLimitSpec<I extends Item>
            extends Update._DmlRowCountLimitClause<_BatchParamClause<_DmlUpdateSpec<I>>>
            , Statement._BatchParamClause<_DmlUpdateSpec<I>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _StaticOrderByClause}</li>
     *          <li>method {@link _BatchLimitSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchOrderBySpec<I extends Item> extends _StaticOrderByClause<_BatchLimitSpec<I>>
            , _BatchLimitSpec<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereAndClause}</li>
     *          <li>method {@link _BatchOrderBySpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _BatchSingleWhereAndSpec<I extends Item>
            extends Update._UpdateWhereAndClause<_BatchSingleWhereAndSpec<I>>
            , _BatchOrderBySpec<I> {

    }


    interface _BatchSingleWhereClause<I extends Item>
            extends _WhereClause<_BatchOrderBySpec<I>, _BatchSingleWhereAndSpec<I>> {

    }


    /**
     * <p>
     * This interface representing SET clause for batch single-table UPDATE.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchSingleSetClause<I extends Item, T>
            extends Update._StaticBatchSetClause<FieldMeta<T>, _BatchSingleWhereSpec<I, T>>
            , Update._DynamicSetClause<BatchItemPairs<FieldMeta<T>>, _BatchSingleWhereClause<I>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _BatchSingleWhereClause}</li>
     *          <li>method {@link MySQLUpdate._BatchSingleSetClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchSingleWhereSpec<I extends Item, T> extends _BatchSingleWhereClause<I>
            , _BatchSingleSetClause<I, T> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintForOrderByClause}</li>
     *          <li>method {@link _BatchSingleSetClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _BatchSingleIndexHintSpec<I extends Item, T>
            extends MySQLQuery._IndexHintForOrderByClause<_BatchSingleIndexHintSpec<I, T>>
            , _BatchSingleSetClause<I, T> {

    }

    interface _BatchSinglePartitionClause<I extends Item, T>
            extends _PartitionAndAsClause_0<_BatchSingleIndexHintSpec<I, T>> {

    }

    interface _BatchSingleUpdateClause<I extends Item> {


        <T> _BatchSingleIndexHintSpec<I, T> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias);

        <P> _BatchSingleIndexHintSpec<I, P> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias);

        <T> _BatchSingleIndexHintSpec<I, T> update(SingleTableMeta<T> table, SQLs.WordAs wordAs, String alias);

        <P> _BatchSingleIndexHintSpec<I, P> update(ComplexTableMeta<P, ?> table, SQLs.WordAs wordAs, String alias);

        <T> _BatchSinglePartitionClause<I, T> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, SingleTableMeta<T> table);

        <P> _BatchSinglePartitionClause<I, P> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, ComplexTableMeta<P, ?> table);

        <T> _BatchSinglePartitionClause<I, T> update(SingleTableMeta<T> table);

        <P> _BatchSinglePartitionClause<I, P> update(ComplexTableMeta<P, ?> table);

    }


    interface _BatchSingleComma<I extends Item>
            extends _StaticWithCommaClause<_StaticCteParensSpec<_BatchSingleComma<I>>>
            , _BatchSingleUpdateClause<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link io.army.criteria.mysql.MySQLStatement._MySQLDynamicWithClause}</li>
     *          <li>method {@link _BatchSingleUpdateClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchSingleWithSpec<I extends Item>
            extends _MySQLDynamicWithClause<_BatchSingleUpdateClause<I>>
            , _StaticWithClause<_StaticCteParensSpec<_BatchSingleComma<I>>>
            , _BatchSingleUpdateClause<I> {

    }


    /*################################## blow multi-table update api interface ##################################*/


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Update._UpdateWhereAndClause}</li>
     *          <li>{@link Statement._DmlUpdateSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _MultiWhereAndSpec<I extends Item> extends Update._UpdateWhereAndClause<_MultiWhereAndSpec<I>>
            , _DmlUpdateSpec<I> {

    }

    /**
     * <p>
     * This interface representing SET clause for multi-table update clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _MultiSetClause<I extends Item> extends Update._StaticSetClause<TableField, _MultiWhereSpec<I>>
            , Update._DynamicSetClause<ItemPairs<TableField>, _MultiWhereSpec<I>>{

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereClause}</li>
     *          <li>{@link MySQLUpdate._MultiSetClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _MultiWhereSpec<I extends Item> extends _WhereClause<_DmlUpdateSpec<I>, _MultiWhereAndSpec<I>>
            , _MultiSetClause<I> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintClause}</li>
     *          <li>method {@link Statement._AsClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _MultiIndexHintOnSpec<I extends Item>
            extends _IndexHintForJoinClause<_MultiIndexHintOnSpec<I>>
            , _OnClause<_MultiJoinSpec<I>> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for multi-table update clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _MultiPartitionOnClause<I extends Item>
            extends _PartitionAndAsClause_0<_MultiIndexHintOnSpec<I>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>join clause</li>
     *          <li>{@link  _MultiSetClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     */
    interface _MultiJoinSpec<I extends Item>
            extends _MySQLJoinClause<_MultiIndexHintOnSpec<I>, _OnClause<_MultiJoinSpec<I>>>
            , _MySQLCrossJoinClause<_MultiIndexHintJoinSpec<I>, _MultiJoinSpec<I>>
            , _MySQLJoinNestedClause<_NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>>>
            , _CrossJoinNestedClause<_NestedLeftParenSpec<_MultiJoinSpec<I>>>
            , _MySQLDynamicJoinCrossClause<_MultiJoinSpec<I>>
            , _MySQLDynamicCrossJoinClause<_MultiJoinSpec<I>>
            , _MySQLDialectJoinClause<_MultiPartitionOnClause<I>>
            , _DialectCrossJoinClause<_MultiPartitionJoinClause<I>>
            , _MultiSetClause<I> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintForJoinClause}</li>
     *          <li>{@link _MultiJoinSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _MultiIndexHintJoinSpec<I extends Item>
            extends MySQLQuery._IndexHintForJoinClause<_MultiIndexHintJoinSpec<I>>
            , _MultiJoinSpec<I> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for multi-table update clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _MultiPartitionJoinClause<I extends Item>
            extends _PartitionAndAsClause_0<_MultiIndexHintJoinSpec<I>> {

    }


    /**
     * <p>
     * This interface representing multi-table UPDATE clause for MySQL syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface MultiUpdateClause<I extends Item> {

        _MultiIndexHintJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        _MultiIndexHintJoinSpec<I> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);


        <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , Supplier<T> supplier);

        <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Supplier<T> supplier);

        <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , Query.DerivedModifier modifier, Supplier<T> supplier);

        <T extends TabularItem> _AsClause<_MultiJoinSpec<I>> update(Query.DerivedModifier modifier, Supplier<T> supplier);

        _MultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, String cteName);

        _MultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, String cteName, SQLs.WordAs wordAs, String alias);

        _MultiJoinSpec<I> update(String cteName);

        _MultiJoinSpec<I> update(String cteName, SQLs.WordAs wordAs, String alias);

        _MultiPartitionJoinClause<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, TableMeta<?> table);

        _MultiPartitionJoinClause<I> update(TableMeta<?> table);

        MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

        MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>> update();

    }


    interface _MultiComma<I extends Item>
            extends _StaticWithCommaClause<_StaticCteParensSpec<_MultiComma<I>>>
            , MultiUpdateClause<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLDynamicWithClause}</li>
     *          <li>{@link _StaticWithClause}</li>
     *          <li>{@link MultiUpdateClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _MultiWithSpec<I extends Item> extends _MySQLDynamicWithClause<MultiUpdateClause<I>>
            , _StaticWithClause<_StaticCteParensSpec<_MultiComma<I>>>
            , MultiUpdateClause<I> {

    }

    /*################################## blow batch multi-table update spec ##################################*/


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._BatchParamClause}</li>
     *          <li>method {@link Statement._WhereAndClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _BatchMultiWhereAndSpec<I extends Item>
            extends Update._UpdateWhereAndClause<_BatchMultiWhereAndSpec<I>>
            , _BatchParamClause<_DmlUpdateSpec<I>> {


    }

    /**
     * <p>
     * This interface representing index SET clause for batch multi-table update clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchMultiSetClause<I extends Item>
            extends Update._StaticBatchSetClause<TableField, _BatchMultiWhereSpec<I>>
            , Update._DynamicSetClause<BatchItemPairs<TableField>, _BatchMultiWhereSpec<I>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLUpdate._BatchMultiSetClause}</li>
     *          <li>method {@link Statement._WhereClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchMultiWhereSpec<I extends Item>
            extends _WhereClause<_BatchParamClause<_DmlUpdateSpec<I>>, _BatchMultiWhereAndSpec<I>>
            , _BatchMultiSetClause<I> {


    }



    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintClause}</li>
     *          <li>{@link Statement._OnClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _BatchMultiIndexHintOnSpec<I extends Item>
            extends MySQLQuery._IndexHintForJoinClause<_BatchMultiIndexHintOnSpec<I>>
            , _OnClause<_BatchMultiJoinSpec<I>> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for batch multi-table UPDATE clause.
     * </p>
     * <p>/
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchMultiPartitionOnClause<I extends Item>
            extends _PartitionAndAsClause_0<_BatchMultiIndexHintOnSpec<I>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>JOIN clause</li>
     *          <li>{@link MySQLUpdate._BatchMultiSetClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     */
    interface _BatchMultiJoinSpec<I extends Item>
            extends MySQLQuery._MySQLJoinClause<_BatchMultiIndexHintOnSpec<I>, _OnClause<_BatchMultiJoinSpec<I>>>
            , MySQLQuery._MySQLCrossJoinClause<_BatchMultiIndexHintJoinSpec<I>, _BatchMultiJoinSpec<I>>
            , MySQLQuery._MySQLJoinNestedClause<MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<I>>>>
            , _CrossJoinNestedClause<MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>>>
            , _MySQLDynamicJoinCrossClause<_BatchMultiJoinSpec<I>>
            , MySQLQuery._MySQLDynamicCrossJoinClause<_BatchMultiJoinSpec<I>>
            , MySQLQuery._MySQLDialectJoinClause<_BatchMultiPartitionOnClause<I>>
            , _DialectCrossJoinClause<_BatchMultiPartitionJoinClause<I>>
            , _BatchMultiSetClause<I> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._IndexHintForJoinClause}</li>
     *          <li>{@link _BatchMultiJoinSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _BatchMultiIndexHintJoinSpec<I extends Item>
            extends MySQLQuery._IndexHintForJoinClause< _BatchMultiIndexHintJoinSpec<I>>
            , _BatchMultiJoinSpec<I> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for batch multi-table UPDATE clause.
     * </p>
     * <p>/
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchMultiPartitionJoinClause<I extends Item>
            extends _PartitionAndAsClause_0<_BatchMultiIndexHintJoinSpec<I>> {

    }

    /**
     * <p>
     * This interface representing multi-table UPDATE clause for MySQL syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchMultiUpdateClause<I extends Item> {

        _BatchMultiIndexHintJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        _BatchMultiIndexHintJoinSpec<I> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);


        <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , Supplier<T> supplier);

        <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Supplier<T> supplier);

        <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , Query.DerivedModifier modifier, Supplier<T> supplier);

        <T extends TabularItem> _AsClause<_BatchMultiJoinSpec<I>> update(Query.DerivedModifier modifier, Supplier<T> supplier);

        _BatchMultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, String cteName);

        _BatchMultiJoinSpec<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, String cteName, SQLs.WordAs wordAs, String alias);

        _BatchMultiJoinSpec<I> update(String cteName);

        _BatchMultiJoinSpec<I> update(String cteName, SQLs.WordAs wordAs, String alias);

        _BatchMultiPartitionJoinClause<I> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, TableMeta<?> table);

        _BatchMultiPartitionJoinClause<I> update(TableMeta<?> table);

        MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

        MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> update();

    }


    interface _BatchMultiComma<I extends Item>
            extends _StaticWithCommaClause<_StaticCteParensSpec<_BatchMultiComma<I>>>
            , _BatchMultiUpdateClause<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLDynamicWithClause}</li>
     *          <li>{@link _StaticWithClause}</li>
     *          <li>{@link _BatchMultiUpdateClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchMultiWithSpec<I extends Item>
            extends _MySQLDynamicWithClause<_BatchMultiUpdateClause<I>>
            , _StaticWithClause<_StaticCteParensSpec<_BatchMultiComma<I>>>
            , _BatchMultiUpdateClause<I> {

    }


}
