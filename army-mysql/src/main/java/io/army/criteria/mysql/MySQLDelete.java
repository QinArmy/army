package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing MySQL delete statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 *
 * @since 1.0
 */
public interface MySQLDelete extends Delete, DialectStatement {

    /**
     * <p>
     * This interface representing single-table DELETE clause for MySQL syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @param <DT> next clause java type
     * @since 1.0
     */
    interface _MySQLSingleDeleteClause<DT> {

        DT deleteFrom(SingleTableMeta<?> table, String tableAlias);

        _SingleDeleteFromClause<DT> delete(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link io.army.criteria.Statement._DmlRowCountLimitClause}</li>
     *          <li>{@link _DmlDeleteSpec}</li>
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
    interface _LimitSpec<I extends Item> extends Statement._DmlRowCountLimitClause<_DmlDeleteSpec<I>>
            , _DmlDeleteSpec<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link io.army.criteria.Statement._OrderByClause}</li>
     *          <li>{@link _LimitSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _OrderBySpec<I extends Item> extends _OrderByClause<_LimitSpec<I>>, _LimitSpec<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link io.army.criteria.Statement._WhereAndClause}</li>
     *          <li>{@link _OrderBySpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _SingleWhereAndSpec<I extends Item> extends _WhereAndClause<_SingleWhereAndSpec<I>>, _OrderBySpec<I> {

    }

    /**
     * <p>
     * This interface representing WHERE clause for single-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SingleWhereClause<I extends Item> extends _WhereClause<_OrderBySpec<I>, _SingleWhereAndSpec<I>> {

    }


    interface _SinglePartitionSpec<I extends Item> extends MySQLQuery._PartitionClause<_SingleWhereClause<I>>
            , _SingleWhereClause<I> {

    }

    /**
     * <p>
     * This interface representing single-table DELETE clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _SingleDelete57Clause<I extends Item>
            extends _MySQLSingleDeleteClause<_SinglePartitionSpec<I>> {


    }

    interface _SingleComma<I extends Item>
            extends Query._StaticWithCommaClause<MySQLQuery._StaticCteLeftParenSpec<_SingleComma<I>>>
            , _SingleDelete57Clause<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Query._DynamicWithCteClause}</li>
     *          <li>{@link Query._StaticWithCteClause}</li>
     *          <li>{@link MySQLDelete._SingleDelete57Clause}</li>
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
            extends Query._DynamicWithCteClause<MySQLCteBuilder, _SingleDelete57Clause<I>>
            , Query._StaticWithCteClause<MySQLQuery._StaticCteLeftParenSpec<_SingleComma<I>>>
            , _SingleDelete57Clause<I> {


    }


    /*################################## blow batch single delete api interface ##################################*/

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._RowCountLimitClause}</li>
     *          <li>{@link Statement._BatchParamClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _BatchLimitSpec<I extends Item>
            extends MySQLUpdate._DmlRowCountLimitClause<Statement._BatchParamClause<_DmlDeleteSpec<I>>>
            , Statement._BatchParamClause<_DmlDeleteSpec<I>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._OrderByClause}</li>
     *          <li>{@link MySQLDelete._BatchLimitSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _BatchOrderBySpec<I extends Item> extends _OrderByClause<_BatchLimitSpec<I>>
            , _BatchLimitSpec<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereAndClause}</li>
     *          <li>{@link MySQLDelete._BatchOrderBySpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     * @since 1.0
     */
    interface _BatchSingleWhereAndSpec<I extends Item> extends _WhereAndClause<_BatchSingleWhereAndSpec<I>>
            , _BatchOrderBySpec<I> {

    }

    /**
     * <p>
     * This interface representing WHERE clause for single-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchSingleWhereClause<I extends Item>
            extends _WhereClause<_BatchOrderBySpec<I>, _BatchSingleWhereAndSpec<I>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._PartitionClause}</li>
     *          <li>{@link MySQLDelete._BatchSingleWhereClause}</li>
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
    interface _BatchSinglePartitionSpec<I extends Item>
            extends MySQLQuery._PartitionClause<_BatchSingleWhereClause<I>>
            , _BatchSingleWhereClause<I> {

    }

    /**
     * <p>
     * This interface representing single-table DELETE clause.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _BatchSingleDeleteClause<I extends Item>
            extends _MySQLSingleDeleteClause<_BatchSinglePartitionSpec<I>> {


    }

    interface BatchSingleComma<I extends Item>
            extends Query._StaticWithCommaClause<MySQLQuery._StaticCteLeftParenSpec<BatchSingleComma<I>>>
            , _BatchSingleDeleteClause<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Query._DynamicWithCteClause}</li>
     *          <li>{@link MySQLDelete._BatchSingleDeleteClause}</li>
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
            extends Query._DynamicWithCteClause<MySQLCteBuilder, _BatchSingleDeleteClause<I>>
            , Query._StaticWithCteClause<MySQLQuery._StaticCteLeftParenSpec<BatchSingleComma<I>>>
            , _BatchSingleDeleteClause<I> {

    }



    /*################################## blow multi-table delete api interface ##################################*/


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereAndClause}</li>
     *          <li>{@link Statement._DmlDeleteSpec}</li>
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
    interface _MultiWhereAndSpec<I extends Item> extends _WhereAndClause<_MultiWhereAndSpec<I>>, _DmlDeleteSpec<I> {

    }

    /**
     * /**
     * <p>
     * This interface representing WHERE clause multi-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _MultiWhereClause<I extends Item> extends _WhereClause<_DmlDeleteSpec<I>, _MultiWhereAndSpec<I>> {

    }


    interface _MultiIndexHintOnSpec<I extends Item>
            extends MySQLQuery._IndexHintForJoinClause<_MultiIndexHintOnSpec<I>>
            , Statement._OnClause<_MultiJoinSpec<I>> {

    }

    /**
     * /**
     * <p>
     * This interface representing PARTITION clause multi-table DELETE syntax.
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
            extends MySQLQuery._PartitionAndAsClause<_MultiIndexHintOnSpec<I>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>JOIN clause</li>
     *          <li>{@link MySQLDelete._MultiWhereClause}</li>
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
    interface _MultiJoinSpec<I extends Item>
            extends MySQLQuery._MySQLJoinClause<_MultiIndexHintOnSpec<I>, _OnClause<_MultiJoinSpec<I>>>
            , MySQLQuery._MySQLCrossJoinClause<_MultiIndexHintJoinSpec<I>, _MultiJoinSpec<I>>
            , MySQLQuery._MySQLJoinNestedClause<MySQLQuery._NestedLeftParenSpec<_OnClause<_MultiJoinSpec<I>>>>
            , _CrossJoinNestedClause<MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>>>
            , MySQLQuery._MySQLDynamicJoinClause<_MultiJoinSpec<I>>
            , MySQLQuery._MySQLDynamicCrossJoinClause<_MultiJoinSpec<I>>
            , MySQLQuery._MySQLDialectJoinClause<_MultiPartitionOnClause<I>>
            , _DialectCrossJoinClause<_MultiPartitionJoinClause<I>>
            , _MultiWhereClause<I> {

    }

    interface _MultiIndexHintJoinSpec<I extends Item>
            extends MySQLQuery._IndexHintForJoinClause<_MultiIndexHintJoinSpec<I>>
            , _MultiJoinSpec<I> {

    }

    /**
     * /**
     * <p>
     * This interface representing PARTITION clause multi-table DELETE syntax.
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
            extends MySQLQuery._PartitionAndAsClause<_MultiIndexHintJoinSpec<I>> {

    }


    /**
     * <p>
     * This interface representing DELETE clause for multi-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _MultiDeleteClause<I extends Item> {


        _MultiIndexHintJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        _MultiIndexHintJoinSpec<Update> update(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);


        <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , Supplier<T> supplier);

        <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Supplier<T> supplier);

        <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers
                , Query.TabularModifier modifier, Supplier<T> supplier);

        <T extends TabularItem> _AsClause<_MultiJoinSpec<Update>> update(Query.TabularModifier modifier, Supplier<T> supplier);

        _MultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, String cteName);

        _MultiJoinSpec<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, String cteName, SQLs.WordAs wordAs, String alias);

        _MultiJoinSpec<Update> update(String cteName);

        _MultiJoinSpec<Update> update(String cteName, SQLs.WordAs wordAs, String alias);

        _MultiPartitionJoinClause<Update> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers, TableMeta<?> table);

        _MultiPartitionJoinClause<Update> update(TableMeta<?> table);

        MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<Update>> update(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

        MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<Update>> update();


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _WithCteClause2}</li>
     *          <li>{@link _MultiDelete57Clause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _WithAndMultiDeleteSpec<C> extends _WithCteClause2<C, SubQuery, _MultiDelete57Clause<C>>
            , _MultiDelete57Clause<C> {

    }



    /*################################## blow batch multi-table delete interface ##################################*/

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement._WhereAndClause}</li>
     *          <li>{@link Statement._BatchParamClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiWhereAndSpec<C> extends _WhereAndClause<C, _BatchMultiWhereAndSpec<C>>
            , _BatchParamClause<C, _DeleteSpec> {


    }

    /**
     * <p>
     * This interface representing AND clause for batch multi-table DELETE.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiWhereClause<C>
            extends _WhereClause<C, _BatchParamClause<C, _DeleteSpec>, _BatchMultiWhereAndSpec<C>> {


    }


    interface _BatchMultiIndexHintOnSpec<C>
            extends MySQLQuery._IndexHintForJoinClause<C, _BatchMultiIndexHintOnSpec<C>>
            , Statement._OnClause<C, _BatchMultiJoinSpec<C>> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for batch multi-table DELETE.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiPartitionOnClause<C>
            extends MySQLQuery._PartitionClause<C, _AsClause<_BatchMultiIndexHintOnSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>JOIN clause</li>
     *          <li>{@link MySQLDelete._MultiWhereClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiJoinSpec<C>
            extends MySQLQuery._MySQLJoinClause<C, _BatchMultiIndexHintOnSpec<C>, _OnClause<C, _BatchMultiJoinSpec<C>>>
            , MySQLQuery._MySQLCrossJoinClause<C, _BatchMultiIndexHintJoinSpec<C>, _BatchMultiJoinSpec<C>>
            , MySQLQuery._MySQLIfJoinClause<C, _BatchMultiJoinSpec<C>>
            , MySQLQuery._MySQLDialectJoinClause<_BatchMultiPartitionOnClause<C>>
            , DialectStatement._DialectCrossJoinClause<_BatchMultiPartitionJoinClause<C>>
            , _BatchMultiWhereClause<C> {

    }

    interface _BatchMultiIndexHintJoinSpec<C>
            extends MySQLQuery._IndexHintForJoinClause<C, _BatchMultiIndexHintJoinSpec<C>>
            , _BatchMultiJoinSpec<C> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause for batch multi-table DELETE.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiPartitionJoinClause<C>
            extends MySQLQuery._PartitionClause<C, _AsClause<_BatchMultiIndexHintJoinSpec<C>>> {

    }


    /**
     * <p>
     * This interface representing batch DELETE clause for multi-table DELETE syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchMultiDeleteClause<C> extends _MultiDeleteClause<
            C,
            _BatchMultiIndexHintJoinSpec<C>,
            _BatchMultiJoinSpec<C>
            , _BatchMultiPartitionJoinClause<C>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _WithCteClause2}</li>
     *          <li>{@link _BatchMultiDeleteClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> criteria object java type
     * @since 1.0
     */
    interface _BatchWithAndMultiDeleteSpec<C>
            extends _WithCteClause2<C, SubQuery, _BatchMultiDeleteClause<C>>
            , _BatchMultiDeleteClause<C> {

    }


}
