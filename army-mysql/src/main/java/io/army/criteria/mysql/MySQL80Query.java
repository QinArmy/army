package io.army.criteria.mysql;


import io.army.criteria.NestedItems;
import io.army.criteria.Query;
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
     *          <li>{@link _WithCteClause} for MySQL 8.0</li>
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
    interface _WithSpec<C, Q extends Query> extends _WithCteClause<C, _Select80Clause<C, Q>>
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
    interface _Select80Clause<C, Q extends Query> extends _MySQLSelectClause<C, _FromSpec<C, Q>> {

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
            extends _FromClause<C, _IndexHintJoinSpec<C, Q>, _JoinSpec<C, Q>>
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
    interface _PartitionJoinClause<C, Q extends Query> extends _PartitionClause<C, _AsJoinClause<C, Q>> {

    }

    /**
     * <p>
     * This interface representing AS clause after key word 'FROM' for MySQL 8.0
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
    interface _AsJoinClause<C, Q extends Query> extends _AsClause<_IndexHintJoinSpec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _IndexHintClause} for MySQL 8.0</li>
     *          <li>the composite {@link _JoinSpec}</li>
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
    interface _IndexHintJoinSpec<C, Q extends Query>
            extends _IndexHintClause<C, _IndexPurposeJoin80Clause<C, Q>, _IndexHintJoinSpec<C, Q>>
            , _JoinSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing index hint clause after key word 'FOR' for MySQL 8.0
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
    interface _IndexPurposeJoin80Clause<C, Q extends Query>
            extends _IndexPurposeClause<C, _IndexHintJoinSpec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLJoinClause}</li>
     *          <li>{@link _MySQLJoinCteClause}</li>
     *          <li>{@link _CrossJoinCteClause}</li>
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
    interface _JoinSpec<C, Q extends Query> extends _MySQLJoinClause<C, _IndexHintOnSpec<C, Q>, _OnClause<C, _JoinSpec<C, Q>>>
            , _MySQLJoinCteClause<_OnClause<C, _JoinSpec<C, Q>>>, _CrossJoinCteClause<_JoinSpec<C, Q>>
            , _CrossJoinClause<C, _IndexHintJoinSpec<C, Q>, _JoinSpec<C, Q>>
            , _MySQLDialectJoinClause<C, _PartitionOnClause<C, Q>>
            , _DialectCrossJoinClause<C, _PartitionJoinClause<C, Q>>
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
    interface _PartitionOnClause<C, Q extends Query> extends _PartitionClause<C, _AsOnClause<C, Q>> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _AsOnClause<C, Q extends Query> extends _AsClause<_IndexHintOnSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _IndexHintClause} </li>
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
    interface _IndexHintOnSpec<C, Q extends Query>
            extends _IndexHintClause<C, _IndexPurposeOnClause<C, Q>, _IndexHintOnSpec<C, Q>>
            , _OnClause<C, _JoinSpec<C, Q>> {

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
     * @param <C> criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _IndexPurposeOnClause<C, Q extends Query> extends _IndexPurposeClause<C, _IndexHintOnSpec<C, Q>> {

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
     *          <li>COMMA clause in WINDOW clause</li>
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
    interface _WindowCommaSpec<C, Q extends Query> extends _OrderBySpec<C, Q> {

        Window._SimpleAsClause<C, _WindowCommaSpec<C, Q>> comma(String windowName);

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


    /**
     * <p>
     * This interface representing nested LEFT BRACKET clause.
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
    interface _NestedLeftBracketClause<C>
            extends _LeftBracketClause<C, _NestedIndexHintJoinSpec<C>, _NestedJoinSpec<C>>
            , _DialectLeftBracketClause<_NestedPartitionJoinClause<C>>
            , _LeftBracketCteClause<_NestedJoinSpec<C>> {

    }

    /**
     * <p>
     * This interface representing nested partition clause.
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
    interface _NestedPartitionJoinClause<C>
            extends _PartitionClause<C, _AsClause<_NestedIndexHintJoinSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _IndexHintClause}</li>
     *          <li>the composite {@link _NestedJoinSpec}</li>
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
    interface _NestedIndexHintJoinSpec<C>
            extends _IndexHintClause<C, _NestedIndexPurposeJoinClause<C>, _NestedIndexHintJoinSpec<C>>
            , _NestedJoinSpec<C> {

    }

    /**
     * <p>
     * This interface representing nested index hint clause after key word 'FOR'.
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
    interface _NestedIndexPurposeJoinClause<C> extends _IndexPurposeClause<C, _NestedIndexHintJoinSpec<C>> {

    }

    /**
     * <p>
     * This interface representing nested partition clause.
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
    interface _NestedPartitionOnClause<C> extends _PartitionClause<C, _AsClause<_NestedIndexHintOnSpec<C>>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _IndexHintClause}</li>
     *          <li>the composite {@link _NestedOnSpec}</li>
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
    interface _NestedIndexHintOnSpec<C>
            extends _IndexHintClause<C, _NestedIndexPurposeOnClause<C>, _NestedIndexHintOnSpec<C>>
            , _NestedOnSpec<C> {

    }

    /**
     * <p>
     * This interface representing nested index hint clause after key word 'FOR'.
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
    interface _NestedIndexPurposeOnClause<C> extends _IndexPurposeClause<C, _NestedIndexHintOnSpec<C>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _MySQLJoinClause}</li>
     *          <li>{@link _MySQLJoinCteClause}</li>
     *          <li>{@link _CrossJoinCteClause}</li>
     *          <li>{@link _CrossJoinClause}</li>
     *          <li>{@link _MySQLDialectJoinClause}</li>
     *          <li>{@link _DialectCrossJoinClause}</li>
     *          <li> {@link _RightBracketClause}</li>
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
    interface _NestedJoinSpec<C> extends _MySQLJoinClause<C, _NestedIndexHintOnSpec<C>, _NestedOnSpec<C>>
            , _MySQLJoinCteClause<_NestedOnSpec<C>>, _CrossJoinCteClause<_NestedJoinSpec<C>>
            , _CrossJoinClause<C, _NestedIndexHintJoinSpec<C>, _NestedJoinSpec<C>>
            , _MySQLDialectJoinClause<C, _NestedPartitionOnClause<C>>
            , _DialectCrossJoinClause<C, _NestedPartitionJoinClause<C>>
            , _RightBracketClause<NestedItems> {


    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link _OnClause}</li>
     *          <li>the composite {@link _NestedJoinSpec}</li>
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
    interface _NestedOnSpec<C> extends _OnClause<C, _NestedJoinSpec<C>>, _NestedJoinSpec<C> {

    }


}
