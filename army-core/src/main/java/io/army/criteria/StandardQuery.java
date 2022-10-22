package io.army.criteria;

import io.army.criteria.impl.SQLs;

import java.util.function.Supplier;

/**
 * This interface representing standard SELECT syntax.
 *
 * @since 1.0
 */
public interface StandardQuery extends Query {


    interface _UnionAndQuerySpec<I extends Item> extends _SelectSpec<I>
            , _LeftParenClause<_UnionAndQuerySpec<Statement._RightParenClause<_UnionOrderBySpec<I>>>> {

    }

    interface _ParenQueryClause<I extends Item>
            extends _LeftParenClause<_UnionAndQuerySpec<Statement._RightParenClause<_UnionOrderBySpec<I>>>> {

    }

    interface _ParenQuerySpec<I extends Item> extends _ParenQueryClause<I>, _SelectSpec<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>LIMIT clause for standard syntax</li>
     *          <li>the composite {@link _UnionSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _UnionLimitSpec<I extends Item> extends _LimitClause<_QuerySpec<I>>, _QuerySpec<I> {

    }

    interface _UnionLimitQuerySpec<I extends Item> extends _UnionLimitSpec<I>, _UnionSpec<I> {

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
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _UnionOrderBySpec<I extends Item> extends Statement._OrderByClause<_UnionLimitSpec<I>>
            , _UnionLimitQuerySpec<I> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>UNION clause for standard syntax</li>
     *          <li>method {@link _QuerySpec#asQuery()}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _UnionSpec<I extends Item> extends _QuerySpec<I>
            , _QueryUnionClause<_UnionAndQuerySpec<I>> {

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
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _LockSpec<I extends Item> extends _QuerySpec<I> {

        _QuerySpec<I> lock(LockMode lockMode);

        _QuerySpec<I> ifLock(Supplier<LockMode> supplier);

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
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _LimitSpec<I extends Item> extends _LockSpec<I>, _LimitClause<_LockSpec<I>> {

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
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _OrderBySpec<I extends Item> extends _LimitSpec<I>
            , Statement._OrderByClause<_LimitSpec<I>> {

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
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _HavingSpec<I extends Item> extends _HavingClause<_OrderBySpec<I>>
            , _OrderBySpec<I>, _UnionSpec<I> {

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
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _GroupBySpec<I extends Item> extends _GroupByClause<_HavingSpec<I>>
            , _OrderBySpec<I> {

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
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
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
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _WhereSpec<I extends Item>
            extends Statement._QueryWhereClause<_GroupBySpec<I>, _WhereAndSpec<I>>, _GroupBySpec<I> {

    }


    interface _StandardJoinClause<FS, JS>
            extends Statement._JoinClause<JS, JS>, Statement._CrossJoinClause<FS, FS> {

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
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _JoinSpec<I extends Item>
            extends _StandardJoinClause<_JoinSpec<I>, Statement._OnClause<_JoinSpec<I>>>, _WhereSpec<I> {

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
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _FromSpec<I extends Item> extends Statement._FromClause<_JoinSpec<I>, _JoinSpec<I>>
            , _UnionSpec<I> {

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
     * @param <I> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _SelectSpec<I extends Item>
            extends _DynamicModifierSelectClause<SQLs.Modifier, _FromSpec<I>>
            , Item {

    }


}
