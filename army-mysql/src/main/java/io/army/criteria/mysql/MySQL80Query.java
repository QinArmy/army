package io.army.criteria.mysql;


import io.army.criteria.Query;
import io.army.criteria.Statement;

import java.util.function.Predicate;

public interface MySQL80Query extends MySQLQuery {


    interface With80Spec<C, Q extends Query> extends WithCteClause<C, Select80Spec<C, Q>>
            , Select80Spec<C, Q> {


    }

    interface Select80Spec<C, Q extends Query> extends Query.SelectClause<C, From80Spec<C, Q>>
            , From80Spec<C, Q> {

    }


    interface From80Spec<C, Q extends Query>
            extends MySQLQuery.MySQLFromClause<C, IndexHintJoin80Spec<C, Q>, Join80Spec<C, Q>, PartitionJoin80Spec<C, Q>>
            , Union80Spec<C, Q> {

    }


    interface PartitionJoin80Spec<C, Q extends Query> extends MySQLQuery.PartitionClause<C, AsJoin80Spec<C, Q>> {

    }

    interface AsJoin80Spec<C, Q extends Query> extends Statement.AsClause<IndexHintJoin80Spec<C, Q>> {

    }


    interface IndexHintJoin80Spec<C, Q extends Query>
            extends MySQLQuery.IndexHintClause<C, IndexPurposeJoin80Spec<C, Q>, IndexHintJoin80Spec<C, Q>>
            , Join80Spec<C, Q> {

    }

    interface IndexPurposeJoin80Spec<C, Q extends Query> extends MySQLQuery.IndexPurposeClause<C, IndexHintJoin80Spec<C, Q>> {

    }


    interface Join80Spec<C, Q extends Query>
            extends MySQLQuery.MySQLJoinClause<C, IndexHintOn80Spec<C, Q>, Statement.OnClause<C, Join80Spec<C, Q>>, PartitionOn80Clause<C, Q>>
            , Where80Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause after key word 'JOIN'(non-cross join) for MySQL 8.0
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
    interface PartitionOn80Clause<C, Q extends Query> extends MySQLQuery.PartitionClause<C, AsOn80Spec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing AS clause after key word 'JOIN'(non-cross join) for MySQL 8.0
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
    interface AsOn80Spec<C, Q extends Query> extends Statement.AsClause<IndexHintOn80Spec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link IndexHintClause} </li>
     *          <li>the composite {@link OnClause}</li>
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
    interface IndexHintOn80Spec<C, Q extends Query>
            extends MySQLQuery.IndexHintClause<C, IndexPurposeOn80Clause<C, Q>, IndexHintOn80Spec<C, Q>>
            , Statement.OnClause<C, Join80Spec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing index hint clause for MySQL 8.0.
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
    interface IndexPurposeOn80Clause<C, Q extends Query> extends MySQLQuery.IndexPurposeClause<C, IndexHintOn80Spec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link WhereClause} </li>
     *          <li>the composite {@link MySQL80Query.GroupBy80Spec}</li>
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
    interface Where80Spec<C, Q extends Query>
            extends Statement.WhereClause<C, GroupBy80Spec<C, Q>, WhereAnd80Spec<C, Q>>
            , GroupBy80Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link WhereAndClause} </li>
     *          <li>the composite {@link MySQL80Query.GroupBy80Spec}</li>
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
    interface WhereAnd80Spec<C, Q extends Query> extends Statement.WhereAndClause<C, WhereAnd80Spec<C, Q>>
            , GroupBy80Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Query.GroupClause} </li>
     *          <li>the composite {@link MySQL80Query.Window80Spec}</li>
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
    interface GroupBy80Spec<C, Q extends Query> extends Query.GroupClause<C, GroupByWithRollup80Spec<C, Q>>
            , Window80Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery.WithRollupClause} after GROUP BY clause</li>
     *          <li>the composite {@link MySQL80Query.Having80Spec}</li>
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
    interface GroupByWithRollup80Spec<C, Q extends Query> extends MySQLQuery.WithRollupClause<C, Limit80Spec<C, Q>>
            , Having80Spec<C, Q> {

        @Override
        Having80Spec<C, Q> withRollup();

        @Override
        Having80Spec<C, Q> ifWithRollup(Predicate<C> predicate);

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Query.HavingClause}</li>
     *          <li>the composite {@link MySQL80Query.Window80Spec}</li>
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
    interface Having80Spec<C, Q extends Query> extends Query.HavingClause<C, Window80Spec<C, Q>>, Window80Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery.WindowClause}</li>
     *          <li>the composite {@link MySQL80Query.OrderBy80Spec}</li>
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
    interface Window80Spec<C, Q extends Query> extends MySQLQuery.WindowClause<C, OrderBy80Spec<C, Q>>
            , OrderBy80Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Query.OrderByClause}</li>
     *          <li>the composite {@link MySQL80Query.Limit80Spec}</li>
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
    interface OrderBy80Spec<C, Q extends Query>
            extends Query.OrderByClause<C, OrderByWithRollup80Spec<C, Q>>, Limit80Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery.WithRollupClause} after ORDER BY clause</li>
     *          <li>the composite {@link MySQL80Query.Limit80Spec}</li>
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
    interface OrderByWithRollup80Spec<C, Q extends Query> extends
            MySQLQuery.WithRollupClause<C, Limit80Spec<C, Q>>, Limit80Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link  Query.LimitClause}</li>
     *          <li>the composite {@link MySQL80Query.Lock80Spec}</li>
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
    interface Limit80Spec<C, Q extends Query> extends Query.LimitClause<C, Lock80Spec<C, Q>>, Lock80Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Lock80Clause}</li>
     *          <li>the composite {@link MySQL80Query.Union80Spec}</li>
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
    interface Lock80Spec<C, Q extends Query>
            extends MySQLQuery.Lock80Clause<C, Lock80OfSpec<C, Q>, Union80Spec<C, Q>>, MySQLQuery.IntoSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Lock80OfClause}</li>
     *          <li>the composite {@link MySQL80Query.Lock80LockOptionSpec}</li>
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
    interface Lock80OfSpec<C, Q extends Query>
            extends Lock80OfClause<C, Lock80LockOptionSpec<C, Q>>, Lock80LockOptionSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Lock80OptionClause}</li>
     *          <li>the composite {@link MySQL80Query.UnionLimit80Spec}</li>
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
    interface Lock80LockOptionSpec<C, Q extends Query>
            extends Lock80OptionClause<C, Union80Spec<C, Q>>, Union80Spec<C, Q>, MySQLQuery.IntoSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>ORDER BY clause for MySQL 8.0</li>
     *          <li>the composite {@link MySQL80Query.UnionLimit80Spec}</li>
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
    interface UnionOrderBy80Spec<C, Q extends Query> extends Query.OrderByClause<C, UnionLimit80Spec<C, Q>>
            , UnionLimit80Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>LIMIT clause for MySQL 8.0</li>
     *          <li>the composite {@link MySQL80Query.Union80Spec}</li>
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
    interface UnionLimit80Spec<C, Q extends Query> extends Query.LimitClause<C, Union80Spec<C, Q>>, Union80Spec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>UNION clause for MySQL 8.0</li>
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
    interface Union80Spec<C, Q extends Query>
            extends QueryUnionClause<C, UnionOrderBy80Spec<C, Q>, With80Spec<C, Q>>, Query.QuerySpec<Q> {


    }


}
