package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.criteria.impl.MySQLs;
import io.army.criteria.impl.SQLs;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing MySQL delete statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 *
 * @since 1.0
 */
public interface MySQLDelete extends DialectStatement {

    /**
     * <p>
     * This interface representing single-table DELETE clause for MySQL syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <DT> next clause java type
     * @since 1.0
     */
    interface _SingleDeleteClause<DT> extends Delete._SingleDeleteClause<DT> {


        Delete._SingleDeleteFromClause<DT> delete(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

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
    interface _SimpleSingleDeleteClause<I extends Item>
            extends _SingleDeleteClause<_SinglePartitionSpec<I>> {


    }

    interface _SingleComma<I extends Item>
            extends Query._StaticWithCommaClause<MySQLQuery._StaticCteLeftParenSpec<_SingleComma<I>>>
            , _SimpleSingleDeleteClause<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Query._DynamicWithClause}</li>
     *          <li>{@link Query._StaticWithClause}</li>
     *          <li>{@link _SimpleSingleDeleteClause}</li>
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
            extends Query._DynamicWithClause<MySQLCteBuilder, _SimpleSingleDeleteClause<I>>
            , Query._StaticWithClause<MySQLQuery._StaticCteLeftParenSpec<_SingleComma<I>>>
            , _SimpleSingleDeleteClause<I> {


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
            extends _SingleDeleteClause<_BatchSinglePartitionSpec<I>> {


    }

    interface _BatchSingleComma<I extends Item>
            extends Query._StaticWithCommaClause<MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma<I>>>
            , _BatchSingleDeleteClause<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Query._DynamicWithClause}</li>
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
            extends Query._DynamicWithClause<MySQLCteBuilder, _BatchSingleDeleteClause<I>>
            , Query._StaticWithClause<MySQLQuery._StaticCteLeftParenSpec<_BatchSingleComma<I>>>
            , _BatchSingleDeleteClause<I> {

    }



    /*################################## blow multi-table delete api interface ##################################*/

    interface _MultiDeleteFromAliasClause<FR> {

        FR from(String alias1, String alias2);

        FR from(String alias1, String alias2, String alias3);

        FR from(String alias1, String alias2, String alias3, String alias4);

        FR from(List<String> aliasList);

        FR from(Consumer<Consumer<String>> consumer);

    }


    interface _MultiDeleteHintClause<DR> {

        DR delete(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);
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
    interface _MultiDeleteAliasClause<DR> {

        DR delete(String alias1, String alias2);

        DR delete(String alias1, String alias2, String alias3);

        DR delete(String alias1, String alias2, String alias3, String alias4);

        DR delete(List<String> aliasList);

        DR delete(Consumer<Consumer<String>> consumer);
    }


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


    interface _MultiDeleteUsingTableClause<I extends Item> {

        _MultiIndexHintJoinSpec<I> using(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        <T extends TabularItem> Statement._AsClause<_MultiJoinSpec<I>> using(Supplier<T> supplier);

        <T extends TabularItem> Statement._AsClause<_MultiJoinSpec<I>> using(Query.TabularModifier modifier, Supplier<T> supplier);

        _MultiJoinSpec<I> using(String cteName);

        _MultiJoinSpec<I> using(String cteName, SQLs.WordAs wordAs, String alias);

        _MultiPartitionJoinClause<I> using(TableMeta<?> table);

        MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>> using();

    }


    interface _SimpleMultiDeleteFromAliasClause<I extends Item>
            extends _MultiDeleteFromAliasClause<_MultiDeleteUsingTableClause<I>> {

    }

    interface _MultiDeleteFromTableClause<I extends Item>
            extends MySQLQuery._MySQLFromClause<_MultiIndexHintJoinSpec<I>, _MultiJoinSpec<I>>
            , Query._DialectFromClause<_MultiPartitionJoinClause<I>>
            , Query._FromNestedClause<MySQLQuery._NestedLeftParenSpec<_MultiJoinSpec<I>>> {

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
    interface _SimpleMultiDeleteClause<I extends Item> extends _MultiDeleteAliasClause<_MultiDeleteFromTableClause<I>>
            , _MultiDeleteHintClause<_SimpleMultiDeleteFromAliasClause<I>> {


    }

    interface _MultiComma<I extends Item>
            extends Query._StaticWithCommaClause<MySQLQuery._StaticCteLeftParenSpec<_MultiComma<I>>>
            , _SimpleMultiDeleteClause<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._MySQLDynamicWithCteClause}</li>
     *          <li>{@link Query._StaticWithClause}</li>
     *          <li>{@link _SimpleMultiDeleteClause}</li>
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
    interface _MultiWithSpec<I extends Item> extends MySQLQuery._MySQLDynamicWithCteClause<_SimpleMultiDeleteClause<I>>
            , Query._StaticWithClause<MySQLQuery._StaticCteLeftParenSpec<_MultiComma<I>>>
            , _SimpleMultiDeleteClause<I> {

    }



    /*################################## blow batch multi-table delete interface ##################################*/

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
    interface _BatchMultiWhereAndSpec<I extends Item>
            extends _WhereAndClause<_BatchMultiWhereAndSpec<I>>, _BatchParamClause<_DmlDeleteSpec<I>> {

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
    interface _BatchMultiWhereClause<I extends Item>
            extends _WhereClause<_BatchParamClause<_DmlDeleteSpec<I>>, _BatchMultiWhereAndSpec<I>> {

    }


    interface _BatchMultiIndexHintOnSpec<I extends Item>
            extends MySQLQuery._IndexHintForJoinClause<_BatchMultiIndexHintOnSpec<I>>
            , Statement._OnClause<_BatchMultiJoinSpec<I>> {

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
    interface _BatchMultiPartitionOnClause<I extends Item>
            extends MySQLQuery._PartitionAndAsClause<_BatchMultiIndexHintOnSpec<I>> {

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
    interface _BatchMultiJoinSpec<I extends Item>
            extends MySQLQuery._MySQLJoinClause<_BatchMultiIndexHintOnSpec<I>, _OnClause<_BatchMultiJoinSpec<I>>>
            , MySQLQuery._MySQLCrossJoinClause<_BatchMultiIndexHintJoinSpec<I>, _BatchMultiJoinSpec<I>>
            , MySQLQuery._MySQLJoinNestedClause<MySQLQuery._NestedLeftParenSpec<_OnClause<_BatchMultiJoinSpec<I>>>>
            , _CrossJoinNestedClause<MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>>>
            , MySQLQuery._MySQLDynamicJoinClause<_BatchMultiJoinSpec<I>>
            , MySQLQuery._MySQLDynamicCrossJoinClause<_BatchMultiJoinSpec<I>>
            , MySQLQuery._MySQLDialectJoinClause<_BatchMultiPartitionOnClause<I>>
            , _DialectCrossJoinClause<_BatchMultiPartitionJoinClause<I>>
            , _BatchMultiWhereClause<I> {

    }

    interface _BatchMultiIndexHintJoinSpec<I extends Item>
            extends MySQLQuery._IndexHintForJoinClause<_BatchMultiIndexHintJoinSpec<I>>
            , _BatchMultiJoinSpec<I> {

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
    interface _BatchMultiPartitionJoinClause<I extends Item>
            extends MySQLQuery._PartitionAndAsClause<_BatchMultiIndexHintJoinSpec<I>> {

    }


    interface _BatchMultiDeleteUsingTableClause<I extends Item> {

        _BatchMultiIndexHintJoinSpec<I> using(TableMeta<?> table, SQLs.WordAs wordAs, String tableAlias);

        <T extends TabularItem> Statement._AsClause<_BatchMultiJoinSpec<I>> using(Supplier<T> supplier);

        <T extends TabularItem> Statement._AsClause<_BatchMultiJoinSpec<I>> using(Query.TabularModifier modifier, Supplier<T> supplier);

        _BatchMultiJoinSpec<I> using(String cteName);

        _BatchMultiJoinSpec<I> using(String cteName, SQLs.WordAs wordAs, String alias);

        _BatchMultiPartitionJoinClause<I> using(TableMeta<?> table);

        MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>> using();

    }


    interface _BatchMultiDeleteFromAliasClause<I extends Item>
            extends _MultiDeleteFromAliasClause<_BatchMultiDeleteUsingTableClause<I>> {
    }

    interface _BatchMultiDeleteFromTableClause<I extends Item>
            extends MySQLQuery._MySQLFromClause<_BatchMultiIndexHintJoinSpec<I>, _BatchMultiJoinSpec<I>>
            , Query._DialectFromClause<_BatchMultiPartitionJoinClause<I>>
            , Query._FromNestedClause<MySQLQuery._NestedLeftParenSpec<_BatchMultiJoinSpec<I>>> {

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
    interface _BatchMultiDeleteClause<I extends Item>
            extends _MultiDeleteHintClause<_BatchMultiDeleteFromAliasClause<I>>
            , _MultiDeleteAliasClause<_BatchMultiDeleteFromTableClause<I>> {


    }

    interface _BatchMultiComma<I extends Item>
            extends Query._StaticWithCommaClause<MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma<I>>>
            , _BatchMultiDeleteClause<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery._MySQLDynamicWithCteClause}</li>
     *          <li>{@link Query._StaticWithClause}</li>
     *          <li>{@link _SimpleMultiDeleteClause}</li>
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
            extends MySQLQuery._MySQLDynamicWithCteClause<_BatchMultiDeleteClause<I>>
            , Query._StaticWithClause<MySQLQuery._StaticCteLeftParenSpec<_BatchMultiComma<I>>>
            , _BatchMultiDeleteClause<I> {

    }


}
