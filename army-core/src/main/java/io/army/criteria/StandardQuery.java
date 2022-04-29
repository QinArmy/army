package io.army.criteria;

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
     * This interface representing SELECT clause for standard syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface StandardSelectClause<C, Q extends Query>
            extends StandardStatement.SelectClauseForStandard<C, StandardFromSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>FROM clause for standard syntax</li>
     *          <li>the composite {@link UnionSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface StandardFromSpec<C, Q extends Query>
            extends Statement.FromClause<C, JoinSpec<C, Q>, JoinSpec<C, Q>, StandardLestBracketClause<C, Q>>
            , UnionSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>JOIN clause for standard syntax</li>
     *          <li>LEFT BRACKET clause for standard syntax</li>
     *          <li>RIGHT BRACKET clause for standard syntax</li>
     *          <li>the composite {@link WhereSpec}</li>
     *     </ul>
     * </p>
     * <p>
     *     If and only if use below methods to create dynamic nested join,then you is allowed to declare this interface type variable
     *     <ul>
     *         <li>{@link #leftJoin()}</li>
     *         <li>{@link #join()}</li>
     *         <li>{@link #rightJoin()}</li>
     *         <li>{@link #fullJoin()}</li>
     *         <li>{@link #crossJoin()}</li>
     *     </ul>
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface JoinSpec<C, Q extends Query>
            extends StandardJoinClause<C, Q>, WhereSpec<C, Q>, StandardLestBracketClause<C, Q>
            , RightBracketClause<JoinSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing join clause for standard syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface StandardJoinClause<C, Q extends Query>
            extends JoinClause<C, Statement.OnClause<C, JoinSpec<C, Q>>, Statement.OnClause<C, JoinSpec<C, Q>>, JoinSpec<C, Q>, JoinSpec<C, Q>, StandardLestBracketClause<C, Q>> {

    }

    /**
     * <p>
     * This interface representing left bracket clause for standard syntax.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface StandardLestBracketClause<C, Q extends Query>
            extends Statement.LeftBracketClause<C, JoinSpec<C, Q>, JoinSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>WHERE clause for standard syntax</li>
     *          <li>the composite {@link GroupBySpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface WhereSpec<C, Q extends Query>
            extends WhereClause<C, GroupBySpec<C, Q>, WhereAndSpec<C, Q>>, GroupBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>AND clause for standard syntax</li>
     *          <li>the composite {@link GroupBySpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface WhereAndSpec<C, Q extends Query>
            extends WhereAndClause<C, WhereAndSpec<C, Q>>, GroupBySpec<C, Q> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>GROUP BY clause for standard syntax</li>
     *          <li>the composite {@link OrderBySpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface GroupBySpec<C, Q extends Query> extends GroupClause<C, HavingSpec<C, Q>>
            , OrderBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>HAVING clause for standard syntax</li>
     *          <li>the composite {@link OrderBySpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface HavingSpec<C, Q extends Query> extends HavingClause<C, OrderBySpec<C, Q>>
            , OrderBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>ORDER BY clause for standard syntax</li>
     *          <li>the composite {@link LimitSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface OrderBySpec<C, Q extends Query> extends LimitSpec<C, Q>
            , Query.OrderByClause<C, LimitSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>LIMIT clause for standard syntax</li>
     *          <li>the composite {@link LockSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface LimitSpec<C, Q extends Query> extends LockSpec<C, Q>
            , Query.LimitClause<C, LockSpec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>lock clause for standard syntax</li>
     *          <li>the composite {@link UnionSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface LockSpec<C, Q extends Query> extends UnionSpec<C, Q> {

        UnionSpec<C, Q> lock(LockMode lockMode);

        UnionSpec<C, Q> lock(Function<C, LockMode> function);

        UnionSpec<C, Q> ifLock(@Nullable LockMode lockMode);

        UnionSpec<C, Q> ifLock(Supplier<LockMode> supplier);

        UnionSpec<C, Q> ifLock(Function<C, LockMode> function);


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>ORDER BY clause for standard syntax</li>
     *          <li>the composite {@link UnionLimitSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface UnionOrderBySpec<C, Q extends Query> extends Query.OrderByClause<C, UnionLimitSpec<C, Q>>
            , UnionLimitSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>LIMIT clause for standard syntax</li>
     *          <li>the composite {@link UnionSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface UnionLimitSpec<C, Q extends Query>
            extends Query.LimitClause<C, UnionSpec<C, Q>>, UnionSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>UNION clause for standard syntax</li>
     *          <li>method {@link QuerySpec#asQuery()}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface UnionSpec<C, Q extends Query> extends Query.QuerySpec<Q>
            , QueryUnionClause<C, UnionOrderBySpec<C, Q>, StandardSelectClause<C, Q>> {

    }


}
