package io.army.criteria.mysql;


import io.army.criteria.Item;
import io.army.criteria.Query;
import io.army.criteria.SubQuery;
import io.army.criteria.Window;

import java.util.function.Predicate;

/**
 * <p>
 * This interface representing MySQL 8.0 SELECT syntax.
 * </p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/select.html">MySQL 8.0 Select statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/union.html">MySQL 8.0 UNION Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/join.html">MySQL 8.0 JOIN Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/select-into.html">MySQL 8.0 SELECT ... INTO Statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/nested-join-optimization.html">MySQL 8.0 Nested Join Optimization</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/partitioning-selection.html">MySQL 8.0 Partition Selection</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/innodb-locking-reads.html#innodb-locking-reads-nowait-skip-locked">MySQL 8.0 Locking Read Concurrency with NOWAIT and SKIP LOCKED</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/parenthesized-query-expressions.html">MySQL 8.0 Parenthesized Query Expressions</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/index-hints.html">MySQL 8.0 Index Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/optimizer-hints.html">MySQL 8.0 Optimizer Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/with.html">MySQL 8.0 WITH (Common Table Expressions)</a>
 * @see <a href="https://dev.mysql.com/doc/refman/8.0/en/window-functions-named-windows.html">MySQL 8.0 Named Windows</a>
 * @since 1.0
 */
public interface MySQL80Query extends MySQLQuery {


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _WithCteClause2} for MySQL 8.0</li>
     *          <li>the composite {@link _Select80Clause}</li>
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
    interface _WithSpec<C, Q extends Query> extends _WithCteClause2<C, SubQuery, _Select80Clause<C, Q>>
            , _Select80Clause<C, Q> {


    }

    /**
     * <p>
     * This interface representing SELECT clause for MySQL 8.0
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
    interface _Select80Clause<Q extends Item> extends _MySQLSelectClause<C, _FromSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _FromClause}</li>
     *          <li>{@link _DialectFromClause}</li>
     *          <li>{@link _FromCteClause}</li>
     *          <li>the composite {@link _UnionSpec}</li>
     *          <li>the composite {@link _IntoSpec}</li>
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
    interface _FromSpec<C, Q extends Query>
            extends _FromClause<C, _QueryUseIndexJoinSpec<C, Q>, _JoinSpec<C, Q>>
            , _DialectFromClause<_PartitionJoinClause<C, Q>>, _FromCteClause<_JoinSpec<C, Q>>
            , _UnionSpec<C, Q>, _IntoSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause after key word 'FROM'(or 'CROSS JOIN') for MySQL 8.0
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
    interface _PartitionJoinClause<C, Q extends Query>
            extends _PartitionClause<C, _AsClause<_QueryUseIndexJoinSpec<C, Q>>> {

    }

    interface _QueryUseIndexJoinSpec<C, Q extends Query> extends _QueryIndexHintClause<C, _QueryUseIndexJoinSpec<C, Q>>
            , _JoinSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLJoinClause}</li>
     *          <li>{@link _MySQLCrossJoinClause}</li>
     *          <li>{@link _MySQLDialectJoinClause}</li>
     *          <li>{@link _DialectCrossJoinClause}</li>
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
    interface _JoinSpec<C, Q extends Query> extends _MySQLJoinClause<C, _QueryUseIndexOnSpec<C, Q>, _OnClause<C, _JoinSpec<C, Q>>>
            , _MySQLCrossJoinClause<C, _QueryUseIndexJoinSpec<C, Q>, _JoinSpec<C, Q>>
            , _MySQLIfJoinClause<C, _JoinSpec<C, Q>>
            , _MySQLDialectJoinClause<_PartitionOnClause<C, Q>>
            , _DialectCrossJoinClause<_PartitionJoinClause<C, Q>>
            , _WhereSpec<C, Q> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _PartitionOnClause<C, Q extends Query> extends _PartitionClause<C, _AsClause<_QueryUseIndexOnSpec<C, Q>>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _QueryIndexHintClause} </li>
     *          <li>the composite {@link _OnClause}</li>
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
    interface _QueryUseIndexOnSpec<C, Q extends Query>
            extends _QueryIndexHintClause<C, _QueryUseIndexOnSpec<C, Q>>
            , _OnClause<C, _JoinSpec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _WhereClause} </li>
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
            extends _WhereClause<C, _GroupBySpec<C, Q>, _WhereAndSpec<C, Q>>
            , _GroupBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _WhereAndClause} </li>
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
    interface _WhereAndSpec<C, Q extends Query> extends _WhereAndClause<C, _WhereAndSpec<C, Q>>
            , _GroupBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _GroupClause} </li>
     *          <li>the composite {@link _WindowSpec}</li>
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
    interface _GroupBySpec<C, Q extends Query> extends _GroupClause<C, _GroupByWithRollupSpec<C, Q>>
            , _WindowSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _WithRollupClause} after GROUP BY clause</li>
     *          <li>the composite {@link _HavingSpec}</li>
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
    interface _GroupByWithRollupSpec<C, Q extends Query> extends _WithRollupClause<C, _LimitSpec<C, Q>>
            , _HavingSpec<C, Q> {

        @Override
        _HavingSpec<C, Q> withRollup();

        @Override
        _HavingSpec<C, Q> ifWithRollup(Predicate<C> predicate);

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _HavingClause}</li>
     *          <li>the composite {@link _WindowSpec}</li>
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
    interface _HavingSpec<C, Q extends Query> extends _HavingClause<C, _WindowSpec<C, Q>>, _WindowSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Window._WindowClause}</li>
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
    interface _WindowSpec<C, Q extends Query>
            extends Window._WindowClause<C, Window._SimpleAsClause<C, _WindowCommaSpec<C, Q>>, _OrderBySpec<C, Q>>
            , _OrderBySpec<C, Q> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Window._CommaClause}</li>
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
    interface
    _WindowCommaSpec<C, Q extends Query> extends _OrderBySpec<C, Q>
            , Window._CommaClause<Window._SimpleAsClause<C, _WindowCommaSpec<C, Q>>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _OrderByClause}</li>
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
    interface _OrderBySpec<C, Q extends Query>
            extends _OrderByClause<C, _OrderByWithRollupSpec<C, Q>>, _LimitSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _WithRollupClause} after ORDER BY clause</li>
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
    interface _OrderByWithRollupSpec<C, Q extends Query> extends
            _WithRollupClause<C, _LimitSpec<C, Q>>, _LimitSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link  _LimitClause}</li>
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
    interface _LimitSpec<C, Q extends Query> extends _LimitClause<C, _LockSpec<C, Q>>, _LockSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _Lock80Clause}</li>
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
    interface _LockSpec<C, Q extends Query>
            extends _Lock80Clause<C, _LockOfSpec<C, Q>, _UnionSpec<C, Q>>, _IntoSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _Lock80OfClause}</li>
     *          <li>the composite {@link _LockLockOptionSpec}</li>
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
    interface _LockOfSpec<C, Q extends Query>
            extends _Lock80OfClause<C, _LockLockOptionSpec<C, Q>>, _LockLockOptionSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _Lock80OptionClause}</li>
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
    interface _LockLockOptionSpec<C, Q extends Query>
            extends _Lock80OptionClause<C, _UnionSpec<C, Q>>, _UnionSpec<C, Q>, _IntoSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>ORDER BY clause for MySQL 8.0</li>
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
     *          <li>LIMIT clause for MySQL 8.0</li>
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
    interface _UnionLimitSpec<C, Q extends Query> extends _LimitClause<C, _UnionSpec<C, Q>>, _UnionSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>UNION clause for MySQL 8.0</li>
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
    interface _UnionSpec<C, Q extends Query>
            extends _QueryUnionClause<C, _UnionOrderBySpec<C, Q>, _WithSpec<C, Q>>, _QuerySpec<Q> {


    }





}
