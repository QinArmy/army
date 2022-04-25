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
public interface StandardQuery extends Query, Statement {


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
    interface StandardSelectClause<C, Q extends Query> extends Query.SelectClause<C, StandardFromSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing from clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardFromSpec<C, Q extends Query>
            extends Statement.FromClause<C, StandardJoinClause<C, Q>, StandardJoinClause<C, Q>, StandardLestBracketClause<C, Q>>
            , StandardUnionClause<C, Q> {

    }


    /**
     * <p>
     * This interface representing on clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardOnSpec<C, Q extends Query> extends OnClause<C, StandardJoinSpec<C, Q>> {


    }

    /**
     * <p>
     * This interface representing join clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardJoinSpec<C, Q extends Query>
            extends StandardJoinClause<C, Q>, StandardWhereSpec<C, Q>, LeftBracketClause<StandardJoinClause<C, Q>>
            , RightBracketClause<StandardJoinSpec<C, Q>> {

    }

    interface StandardJoinClause<C, Q extends Query>
            extends JoinClause<C, StandardOnSpec<C, Q>, StandardOnSpec<C, Q>, StandardJoinSpec<C, Q>, StandardJoinSpec<C, Q>> {

    }

    interface StandardLestBracketClause<C, Q extends Query>
            extends Statement.LeftBracketClause<C, StandardJoinSpec<C, Q>, StandardJoinSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing where clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardWhereSpec<C, Q extends Query>
            extends WhereClause<C, StandardGroupBySpec<C, Q>, StandardWhereAndSpec<C, Q>>
            , StandardGroupBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing where and clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardWhereAndSpec<C, Q extends Query>
            extends WhereAndClause<C, StandardWhereAndSpec<C, Q>>, StandardGroupBySpec<C, Q> {


    }

    /**
     * <p>
     * This interface representing group by clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardGroupBySpec<C, Q extends Query> extends GroupClause<C, StandardHavingSpec<C, Q>>
            , StandardOrderBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing having clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardHavingSpec<C, Q extends Query> extends HavingClause<C, StandardOrderBySpec<C, Q>>
            , StandardOrderBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing order by clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardOrderBySpec<C, Q extends Query> extends StandardLimitSpec<C, Q>
            , Query.OrderByClause<C, StandardLimitSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing limit clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardLimitSpec<C, Q extends Query> extends StandardLockSpec<C, Q>
            , Query.LimitClause<C, StandardLockSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing order by clause(after union) of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface UnionOrderBySpec<C, Q extends Query> extends Query.OrderByClause<C, UnionLimitSpec<C, Q>>
            , UnionLimitSpec<C, Q> {

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
            , Query.UnionClause<C, Q, UnionOrderBySpec<C, Q>, StandardSelectClause<C, Q>> {

    }

    /**
     * <p>
     * This interface representing lock clause of standard query (SELECT or Sub Query).
     * </p>
     *
     * @param <C> java type of criteria instance used to create dynamic query.
     * @see SQLs#query(Object)
     * @see SQLs#subQuery(Object)
     * @see SQLs#scalarSubQuery(Object)
     */
    interface StandardLockSpec<C, Q extends Query> extends StandardUnionClause<C, Q> {

        StandardUnionClause<C, Q> lock(LockMode lockMode);

        StandardUnionClause<C, Q> lock(Function<C, LockMode> function);

        StandardUnionClause<C, Q> ifLock(@Nullable LockMode lockMode);

        StandardUnionClause<C, Q> ifLock(Supplier<LockMode> supplier);

        StandardUnionClause<C, Q> ifLock(Function<C, LockMode> function);


    }


}
