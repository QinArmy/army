package io.army.criteria.standard;

import io.army.criteria.Item;
import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.dialect.Window;
import io.army.criteria.impl.SQLs;

import java.util.function.Function;

/**
 * This interface representing standard SELECT syntax.
 *
 * @since 1.0
 */
public interface StandardQuery extends Query, StandardStatement {


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>UNION clause for standard syntax</li>
     *          <li>method {@link _AsQueryClause#asQuery()}</li>
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
    interface _UnionSpec<I extends Item> extends _StaticUnionClause<_SelectSpec<I>>,
            _AsQueryClause<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>LIMIT clause for standard syntax</li>
     *          <li> {@link _AsQueryClause}</li>
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
    interface _UnionLimitSpec<I extends Item> extends Statement._LimitClause<_AsQueryClause<I>>,
            _AsQueryClause<I> {

    }


    interface _UnionOrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_UnionOrderByCommaSpec<I>>, _UnionLimitSpec<I> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>ORDER BY clause for standard syntax</li>
     *          <li>the composite {@link _UnionLimitSpec}</li>
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
    interface _UnionOrderBySpec<I extends Item> extends _StaticOrderByClause<_UnionOrderByCommaSpec<I>>,
            _UnionLimitSpec<I>,
            _UnionSpec<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>lock clause for standard syntax</li>
     *          <li>the composite {@link _UnionSpec}</li>
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
    interface _LockSpec<I extends Item> extends _SimpleForUpdateClause<_AsQueryClause<I>>,
            _AsQueryClause<I> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>LIMIT clause for standard syntax</li>
     *          <li>the composite {@link _LockSpec}</li>
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
    interface _LimitSpec<I extends Item> extends _LockSpec<I>, Statement._LimitClause<_LockSpec<I>> {

    }


    interface _OrderByCommaSpec<I extends Item> extends _OrderByCommaClause<_OrderByCommaSpec<I>>, _LimitSpec<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>ORDER BY clause for standard syntax</li>
     *          <li>the composite {@link _LimitSpec}</li>
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
    interface _OrderBySpec<I extends Item> extends _LimitSpec<I>,
            _StaticOrderByClause<_OrderByCommaSpec<I>>,
            _DynamicOrderByClause<_LimitSpec<I>>,
            _UnionSpec<I> {

    }

    interface _WindowCommaSpec<I extends Item> extends _OrderBySpec<I> {

        Window._WindowAsClause<Window._StandardPartitionBySpec, _WindowCommaSpec<I>> comma(String windowName);


    }


    interface _WindowSpec<I extends Item> extends _OrderBySpec<I>,
            Window._DynamicWindowClause<Window._StandardPartitionBySpec, _OrderBySpec<I>> {

        Window._WindowAsClause<Window._StandardPartitionBySpec, _WindowCommaSpec<I>> window(String windowName);

    }

    interface _HavingAndSpec<I extends Item> extends _HavingAndClause<_HavingAndSpec<I>>, _WindowSpec<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>HAVING clause for standard syntax</li>
     *          <li>the composite {@link _OrderBySpec}</li>
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
    interface _HavingSpec<I extends Item> extends _StaticHavingClause<_HavingAndSpec<I>>,
            _DynamicHavingClause<_WindowSpec<I>>,
            _WindowSpec<I> {

    }


    interface _GroupByCommaSpec<I extends Item> extends _GroupByCommaClause<_GroupByCommaSpec<I>>, _HavingSpec<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>GROUP BY clause for standard syntax</li>
     *          <li>the composite {@link _OrderBySpec}</li>
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
    interface _GroupBySpec<I extends Item> extends _StaticGroupByClause<_GroupByCommaSpec<I>>,
            _DynamicGroupByClause<_HavingSpec<I>>,
            _WindowSpec<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>AND clause for standard syntax</li>
     *          <li>the composite {@link _GroupBySpec}</li>
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
    interface _WhereAndSpec<I extends Item>
            extends Statement._WhereAndClause<_WhereAndSpec<I>>, _GroupBySpec<I> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>WHERE clause for standard syntax</li>
     *          <li>the composite {@link _GroupBySpec}</li>
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
    interface _WhereSpec<I extends Item>
            extends Statement._QueryWhereClause<_GroupBySpec<I>, _WhereAndSpec<I>>, _GroupBySpec<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _StandardJoinClause}</li>
     *          <li>the composite {@link _WhereSpec}</li>
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
    interface _JoinSpec<I extends Item> extends _StandardJoinClause<_JoinSpec<I>, _OnClause<_JoinSpec<I>>>,
            _WhereSpec<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>FROM clause for standard syntax</li>
     *          <li>the composite {@link _UnionSpec}</li>
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
    interface _FromSpec<I extends Item> extends Statement._FromClause<_JoinSpec<I>, _AsClause<_JoinSpec<I>>>,
            _FromNestedClause<_NestedLeftParenSpec<_JoinSpec<I>>, _JoinSpec<I>>,
            _UnionSpec<I> {

    }

    interface _StandardSelectCommaClause<I extends Item>
            extends _StaticSelectCommaClause<_StandardSelectCommaClause<I>>,
            _FromSpec<I> {

    }


    /**
     * <p>
     * This interface representing SELECT clause for standard syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @since 1.0
     */
    interface _StandardSelectClause<I extends Item>
            extends _ModifierListSelectClause<SQLs.Modifier, _StandardSelectCommaClause<I>>,
            _DynamicModifierSelectClause<SQLs.Modifier, _FromSpec<I>> {

    }

    interface _SelectSpec<I extends Item> extends _StandardSelectClause<I>,
            _DynamicParensRowSetClause<_SelectSpec<_UnionOrderBySpec<I>>, _UnionOrderBySpec<I>> {

    }


    interface _CteComma<I extends Item> extends _StaticWithCommaClause<_StaticCteParensSpec<I>>,
            _StaticSpaceClause<I> {

    }


    interface _StaticCteAsClause<I extends Item> {
        _CteComma<I> as(Function<_SelectSpec<_CteComma<I>>, _CteComma<I>> function);

    }

    interface _StaticCteParensSpec<I extends Item>
            extends _OptionalParensStringClause<_StaticCteAsClause<I>>, _StaticCteAsClause<I> {

    }


    interface _WithSpec<I extends Item> extends _StandardDynamicWithClause<_SelectSpec<I>>,
            _StandardStaticWithClause<_SelectSpec<I>>,
            _SelectSpec<I> {

    }


    interface _QueryDynamicCteAsClause extends _DynamicCteAsClause<_WithSpec<_CommaClause<StandardCtes>>,
            _CommaClause<StandardCtes>> {

    }

    interface _DynamicCteParensSpec extends _OptionalParensStringClause<_QueryDynamicCteAsClause>,
            _QueryDynamicCteAsClause {

    }


}
