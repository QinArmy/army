package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _UnionOrderBySpec<C, Q extends Query> extends _OrderByClause<C, _UnionLimitSpec<C, Q>>
            , _UnionLimitSpec<C, Q> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _UnionLimitSpec<C, Q extends Query>
            extends _LimitClause<C, _UnionSpec<C, Q>>, _UnionSpec<C, Q> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _UnionSpec<C, Q extends Query> extends _QuerySpec<Q>
            , _QueryUnionClause<C, _UnionOrderBySpec<C, Q>, _StandardSelectClause<C, Q>> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _LockSpec<C, Q extends Query> extends _UnionSpec<C, Q> {

        _UnionSpec<C, Q> lock(LockMode lockMode);

        _UnionSpec<C, Q> lock(Function<C, LockMode> function);

        _UnionSpec<C, Q> ifLock(@Nullable LockMode lockMode);

        _UnionSpec<C, Q> ifLock(Supplier<LockMode> supplier);

        _UnionSpec<C, Q> ifLock(Function<C, LockMode> function);

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _LimitSpec<C, Q extends Query> extends _LockSpec<C, Q>, _LimitClause<C, _LockSpec<C, Q>> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _OrderBySpec<C, Q extends Query> extends _LimitSpec<C, Q>
            , _OrderByClause<C, _LimitSpec<C, Q>> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _HavingSpec<C, Q extends Query> extends _HavingClause<C, _OrderBySpec<C, Q>>
            , _OrderBySpec<C, Q> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _GroupBySpec<C, Q extends Query> extends _GroupClause<C, _HavingSpec<C, Q>>
            , _OrderBySpec<C, Q> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _WhereAndSpec<C, Q extends Query>
            extends _WhereAndClause<C, _WhereAndSpec<C, Q>>, _GroupBySpec<C, Q> {


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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _WhereSpec<C, Q extends Query>
            extends _QueryWhereClause<C, _GroupBySpec<C, Q>, _WhereAndSpec<C, Q>>, _GroupBySpec<C, Q> {

    }


    interface _StandardJoinClause<C, FS, JS>
            extends Statement._JoinClause<C, JS, JS>, Statement._CrossJoinClause<C, FS, FS>
            , Statement._IfJoinClause<C, FS> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _JoinSpec<C, Q extends Query>
            extends _StandardJoinClause<C, _JoinSpec<C, Q>, _OnClause<C, _JoinSpec<C, Q>>>, _WhereSpec<C, Q> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _StandardFromSpec<C, Q extends Query> extends _FromClause<C, _JoinSpec<C, Q>, _JoinSpec<C, Q>>
            , _UnionSpec<C, Q> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _StandardSelectClause<C, Q extends Query>
            extends Query.SelectClause<C, SQLs.Modifier, _StandardFromSpec<C, Q>> {

    }


    /*-------------------below nested join interface -------------------*/

    interface _NestedOnSpec<C> extends Statement._OnClause<C, _NestedJoinSpec<C>>, _NestedJoinSpec<C> {

    }

    interface _NestedJoinSpec<C> extends Statement._JoinClause<C, _NestedOnSpec<C>, _NestedOnSpec<C>>
            , Statement._CrossJoinClause<C, _NestedJoinSpec<C>, _NestedJoinSpec<C>>
            , Statement._IfJoinClause<C, _NestedJoinSpec<C>>
            , Statement._RightParenClause<NestedItems> {

    }

    interface _StandardNestedLeftParenClause<C>
            extends Statement._LeftParenClause<C, _NestedJoinSpec<C>, _NestedJoinSpec<C>> {

    }


    /*-------------------below if join clause interface -------------------*/

    interface _IfOnClause<C> extends Statement._OnClause<C, JoinItemBlock<C>>, ItemBlock<C> {

    }


}
