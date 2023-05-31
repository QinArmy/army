package io.army.criteria.mysql;

import io.army.criteria.DeleteStatement;
import io.army.criteria.Item;
import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Hint;
import io.army.criteria.impl.MySQLs;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing MySQL delete statement,the instance of this interface can only be parsed by MySQL dialect instance.
 * </p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html">MySQL 8.0 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/optimizer-hints.html">MySQL 5.7 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/delete.html">DELETE Statement</a>
 * @since 1.0
 */
public interface MySQLDelete extends MySQLStatement {

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
    interface _SingleDeleteClause<DT> extends DeleteStatement._SingleDeleteClause<DT> {


        DeleteStatement._SingleDeleteFromClause<DT> delete(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);

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
    interface _LimitSpec<I extends Item> extends Statement._DmlRowCountLimitClause<_DmlDeleteSpec<I>>,
            _DmlDeleteSpec<I> {

    }

    interface _OrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_OrderByCommaSpec<I>>, _LimitSpec<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _OrderByClause}</li>
     *          <li>{@link _LimitSpec}</li>
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
    interface _OrderBySpec<I extends Item> extends _OrderByClause<_OrderByCommaSpec<I>>, _LimitSpec<I> {

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
     *
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


    interface _SinglePartitionSpec<I extends Item> extends _PartitionClause<_SingleWhereClause<I>>,
            _SingleWhereClause<I> {

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
    interface _SimpleSingleDeleteClause<I extends Item> extends _SingleDeleteClause<_SinglePartitionSpec<I>> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _DynamicWithClause}</li>
     *          <li>{@link _StaticWithClause}</li>
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
            extends _MySQLDynamicWithClause<_SimpleSingleDeleteClause<I>>,
            _MySQLStaticWithClause<_SimpleSingleDeleteClause<I>>,
            _SimpleSingleDeleteClause<I> {

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
     *
     * @since 1.0
     */
    interface _BatchLimitSpec<I extends Item>
            extends MySQLUpdate._DmlRowCountLimitClause<Statement._BatchParamClause<_DmlDeleteSpec<I>>>,
            Statement._BatchParamClause<_DmlDeleteSpec<I>> {

    }

    interface _BatchOrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_BatchOrderByCommaSpec<I>>,
            _BatchLimitSpec<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _OrderByClause}</li>
     *          <li>{@link MySQLDelete._BatchLimitSpec}</li>
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
    interface _BatchOrderBySpec<I extends Item> extends _OrderByClause<_BatchOrderByCommaSpec<I>>, _BatchLimitSpec<I> {

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
     *
     * @since 1.0
     */
    interface _BatchSingleWhereAndSpec<I extends Item> extends _WhereAndClause<_BatchSingleWhereAndSpec<I>>,
            _BatchOrderBySpec<I> {

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
     *          <li>{@link _PartitionClause}</li>
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
            extends _PartitionClause<_BatchSingleWhereClause<I>>, _BatchSingleWhereClause<I> {

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
    interface _BatchSingleDeleteClause<I extends Item> extends _SingleDeleteClause<_BatchSinglePartitionSpec<I>> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _DynamicWithClause}</li>
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
            extends _MySQLDynamicWithClause<_BatchSingleDeleteClause<I>>,
            _MySQLStaticWithClause<_BatchSingleDeleteClause<I>>,
            _BatchSingleDeleteClause<I> {

    }



    /*################################## blow multi-table delete api interface ##################################*/

    interface _MultiDeleteFromAliasClause<R> {

        R from(String alias);

        R from(String alias1, String alias2);

        R from(String alias1, String alias2, String alias3);

        R from(String alias1, String alias2, String alias3, String alias4);

        R from(List<String> aliasList);

        R from(Consumer<Consumer<String>> consumer);

    }


    interface _MultiDeleteHintClause<I extends Item> extends Item {

        _MultiDeleteFromAliasClause<I> delete(Supplier<List<Hint>> hints, List<MySQLs.Modifier> modifiers);
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
    interface _MultiDeleteAliasClause<R> {

        R delete(String alias);

        R delete(String alias1, String alias2);

        R delete(String alias1, String alias2, String alias3);

        R delete(String alias1, String alias2, String alias3, String alias4);

        R delete(List<String> aliasList);

        R delete(Consumer<Consumer<String>> consumer);
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
            extends MySQLQuery._IndexHintForJoinClause<_MultiIndexHintOnSpec<I>>,
            _DynamicIndexHintClause<_IndexForJoinSpec<Object>, _MultiIndexHintOnSpec<I>>,
            Statement._OnClause<_MultiJoinSpec<I>> {

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
    interface _MultiPartitionOnClause<I extends Item> extends _PartitionAsClause<_MultiIndexHintOnSpec<I>> {

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
            extends _MySQLJoinClause<_MultiIndexHintOnSpec<I>, _AsParensOnClause<_MultiJoinSpec<I>>>,
            _MySQLCrossClause<_MultiIndexHintJoinSpec<I>, _ParensJoinSpec<I>>,
            _MySQLJoinCteClause<_OnClause<_MultiJoinSpec<I>>>,
            _CrossJoinCteClause<_MultiJoinSpec<I>>,
            _MySQLJoinNestedClause<_OnClause<_MultiJoinSpec<I>>>,
            _MySQLCrossNestedClause<_MultiJoinSpec<I>>,
            _MySQLDynamicJoinCrossClause<_MultiJoinSpec<I>>,
            _MySQLDialectJoinClause<_MultiPartitionOnClause<I>>,
            _DialectCrossJoinClause<_MultiPartitionJoinClause<I>>,
            _MultiWhereClause<I> {

    }

    interface _MultiIndexHintJoinSpec<I extends Item>
            extends MySQLQuery._IndexHintForJoinClause<_MultiIndexHintJoinSpec<I>>,
            _MultiJoinSpec<I> {

    }

    interface _ParensJoinSpec<I extends Item> extends _OptionalParensStringClause<_MultiJoinSpec<I>>, _MultiJoinSpec<I> {

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
    interface _MultiPartitionJoinClause<I extends Item> extends _PartitionAsClause<_MultiIndexHintJoinSpec<I>> {

    }


    interface _SimpleMultiDeleteUsingClause<I extends Item>
            extends _UsingModifierClause<_MultiIndexHintJoinSpec<I>, Statement._AsClause<_ParensJoinSpec<I>>>,
            _UsingCteClause<_MultiJoinSpec<I>>,
            _MySQLUsingTableClause<_MultiPartitionJoinClause<I>>,
            _MySQLUsingNestedClause<_MultiJoinSpec<I>> {

    }


    interface _MultiDeleteFromTableClause<I extends Item>
            extends _MySQLFromClause<_MultiIndexHintJoinSpec<I>, _ParensJoinSpec<I>>,
            Query._FromTableClause<_MultiPartitionJoinClause<I>>,
            _MySQLFromNestedClause<_MultiJoinSpec<I>> {

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
    interface _SimpleMultiDeleteClause<I extends Item> extends _MultiDeleteAliasClause<_MultiDeleteFromTableClause<I>>,
            _MultiDeleteHintClause<_SimpleMultiDeleteUsingClause<I>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLDynamicWithClause}</li>
     *          <li>{@link _StaticWithClause}</li>
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
    interface _MultiWithSpec<I extends Item> extends _MySQLDynamicWithClause<_SimpleMultiDeleteClause<I>>,
            _MySQLStaticWithClause<_SimpleMultiDeleteClause<I>>,
            _SimpleMultiDeleteClause<I> {

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
            extends MySQLQuery._IndexHintForJoinClause<_BatchMultiIndexHintOnSpec<I>>,
            _DynamicIndexHintClause<_IndexForJoinSpec<Object>, _BatchMultiIndexHintOnSpec<I>>,
            Statement._OnClause<_BatchMultiJoinSpec<I>> {

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
    interface _BatchMultiPartitionOnClause<I extends Item> extends _PartitionAsClause<_BatchMultiIndexHintOnSpec<I>> {

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
            extends _MySQLJoinClause<_BatchMultiIndexHintOnSpec<I>, _AsParensOnClause<_BatchMultiJoinSpec<I>>>,
            _MySQLCrossClause<_BatchMultiIndexHintJoinSpec<I>, _BatchParensJoinSpec<I>>,
            _MySQLJoinCteClause<_OnClause<_BatchMultiJoinSpec<I>>>,
            _CrossJoinCteClause<_BatchMultiJoinSpec<I>>,
            _MySQLJoinNestedClause<_OnClause<_BatchMultiJoinSpec<I>>>,
            _MySQLCrossNestedClause<_BatchMultiJoinSpec<I>>,
            _MySQLDynamicJoinCrossClause<_BatchMultiJoinSpec<I>>,
            _MySQLDialectJoinClause<_BatchMultiPartitionOnClause<I>>,
            _DialectCrossJoinClause<_BatchMultiPartitionJoinClause<I>>,
            _BatchMultiWhereClause<I> {

    }

    interface _BatchMultiIndexHintJoinSpec<I extends Item>
            extends MySQLQuery._IndexHintForJoinClause<_BatchMultiIndexHintJoinSpec<I>>, _BatchMultiJoinSpec<I> {

    }

    interface _BatchParensJoinSpec<I extends Item> extends _OptionalParensStringClause<_BatchMultiJoinSpec<I>>,
            _BatchMultiJoinSpec<I> {

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
            extends _PartitionAsClause<_BatchMultiIndexHintJoinSpec<I>> {

    }


    interface _BatchMultiDeleteUsingTableClause<I extends Item>
            extends _UsingModifierClause<_BatchMultiIndexHintJoinSpec<I>, Statement._AsClause<_BatchParensJoinSpec<I>>>,
            _UsingCteClause<_BatchMultiJoinSpec<I>>,
            _MySQLUsingTableClause<_BatchMultiPartitionJoinClause<I>>,
            _MySQLUsingNestedClause<_BatchMultiJoinSpec<I>> {

    }


    interface _BatchMultiDeleteFromTableClause<I extends Item>
            extends MySQLQuery._MySQLFromClause<_BatchMultiIndexHintJoinSpec<I>, _BatchParensJoinSpec<I>>,
            Query._FromTableClause<_BatchMultiPartitionJoinClause<I>>,
            _MySQLFromNestedClause<_BatchMultiJoinSpec<I>> {

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
            extends _MultiDeleteHintClause<_BatchMultiDeleteUsingTableClause<I>>,
            _MultiDeleteAliasClause<_BatchMultiDeleteFromTableClause<I>> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLDynamicWithClause}</li>
     *          <li>{@link _StaticWithClause}</li>
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
            extends _MySQLDynamicWithClause<_BatchMultiDeleteClause<I>>,
            _MySQLStaticWithClause<_BatchMultiDeleteClause<I>>,
            _BatchMultiDeleteClause<I> {

    }


}
