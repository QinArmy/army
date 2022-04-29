package io.army.criteria.mysql;


import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.Window;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
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
     * This interface representing builder of {@link Window}
     * </p>
     *
     * @param <C> java criteria object java type
     * @see 1.0
     */
    @FunctionalInterface
    interface WindowBuilder<C> {

        _WindowAsClause<C, Window> window(String windowName);
    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link WithCteClause} for MySQL 8.0</li>
     *          <li>the composite {@link _Select80Clause}</li>
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
    interface _WithSpec<C, Q extends Query> extends WithCteClause<C, _Select80Clause<C, Q>>
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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _Select80Clause<C, Q extends Query> extends MySQLSelectClause<C, _FromSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLFromClause} for MySQL 8.0</li>
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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _FromSpec<C, Q extends Query>
            extends MySQLQuery.MySQLFromClause<C, _IndexHintJoinSpec<C, Q>, _JoinSpec<C, Q>
            , _PartitionJoinClause<C, Q>, _LeftBracket80Clause<C, Q>>, _UnionSpec<C, Q>, _IntoSpec<C, Q> {

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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _PartitionJoinClause<C, Q extends Query> extends MySQLQuery.PartitionClause<C, _AsJoinClause<C, Q>> {

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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _AsJoinClause<C, Q extends Query> extends Statement.AsClause<_IndexHintJoinSpec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link IndexHintClause} for MySQL 8.0</li>
     *          <li>the composite {@link _JoinSpec}</li>
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
    interface _IndexHintJoinSpec<C, Q extends Query>
            extends MySQLQuery.IndexHintClause<C, _IndexPurposeJoin80Clause<C, Q>, _IndexHintJoinSpec<C, Q>>
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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _IndexPurposeJoin80Clause<C, Q extends Query>
            extends MySQLQuery.IndexPurposeClause<C, _IndexHintJoinSpec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLJoinClause} for MySQL 8.0</li>
     *          <li>the composite {@link _WhereSpec}</li>
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
    interface _JoinSpec<C, Q extends Query>
            extends MySQLQuery.MySQLJoinClause<C, _IndexHintOnSpec<C, Q>, Statement.OnClause<C, _JoinSpec<C, Q>>
            , _PartitionOn80Clause<C, Q>, _IndexHintJoinSpec<C, Q>, _JoinSpec<C, Q>, _LeftBracket80Clause<C, Q>
            , _PartitionJoinClause<C, Q>>, _WhereSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing LEFT BRACKET clause for MySQL 8.0
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
    interface _LeftBracket80Clause<C, Q extends Query>
            extends MySQLJoinBracketClause<C, _IndexHintJoinSpec<C, Q>, _JoinSpec<C, Q>, _PartitionJoinClause<C, Q>> {

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
    interface _PartitionOn80Clause<C, Q extends Query> extends MySQLQuery.PartitionClause<C, _AsOnClause<C, Q>> {

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
    interface _AsOnClause<C, Q extends Query> extends Statement.AsClause<_IndexHintOnSpec<C, Q>> {

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
    interface _IndexHintOnSpec<C, Q extends Query>
            extends MySQLQuery.IndexHintClause<C, _IndexPurposeOnClause<C, Q>, _IndexHintOnSpec<C, Q>>
            , Statement.OnClause<C, _JoinSpec<C, Q>> {

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
    interface _IndexPurposeOnClause<C, Q extends Query> extends MySQLQuery.IndexPurposeClause<C, _IndexHintOnSpec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link WhereClause} </li>
     *          <li>the composite {@link _GroupBySpec}</li>
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
    interface _WhereSpec<C, Q extends Query>
            extends Statement.WhereClause<C, _GroupBySpec<C, Q>, _WhereAndSpec<C, Q>>
            , _GroupBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link WhereAndClause} </li>
     *          <li>the composite {@link _GroupBySpec}</li>
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
    interface _WhereAndSpec<C, Q extends Query> extends Statement.WhereAndClause<C, _WhereAndSpec<C, Q>>
            , _GroupBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Query.GroupClause} </li>
     *          <li>the composite {@link _WindowSpec}</li>
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
    interface _GroupBySpec<C, Q extends Query> extends Query.GroupClause<C, _GroupByWithRollupSpec<C, Q>>
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
     * @param <C> java criteria object java type
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
     *          <li>{@link Query.HavingClause}</li>
     *          <li>the composite {@link _WindowSpec}</li>
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
    interface _HavingSpec<C, Q extends Query> extends Query.HavingClause<C, _WindowSpec<C, Q>>, _WindowSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLQuery.WindowClause}</li>
     *          <li>the composite {@link _OrderBySpec}</li>
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
    interface _WindowSpec<C, Q extends Query>
            extends MySQLQuery.WindowClause<C, _WindowAsClause<C, _WindowCommaSpec<C, Q>>>
            , _OrderBySpec<C, Q> {

        _OrderBySpec<C, Q> ifWindow(Function<WindowBuilder<C>, List<Window>> function);

        _OrderBySpec<C, Q> ifWindow(BiFunction<C, WindowBuilder<C>, List<Window>> function);

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Query.OrderByClause}</li>
     *          <li>the composite {@link _LimitSpec}</li>
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
    interface _OrderBySpec<C, Q extends Query>
            extends Query.OrderByClause<C, _OrderByWithRollupSpec<C, Q>>, _LimitSpec<C, Q> {

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
     * @param <C> java criteria object java type
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
     *          <li>{@link  Query.LimitClause}</li>
     *          <li>the composite {@link _LockSpec}</li>
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
    interface _LimitSpec<C, Q extends Query> extends Query.LimitClause<C, _LockSpec<C, Q>>, _LockSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Lock80Clause}</li>
     *          <li>the composite {@link _UnionSpec}</li>
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
    interface _LockSpec<C, Q extends Query>
            extends MySQLQuery.Lock80Clause<C, _LockOfSpec<C, Q>, _UnionSpec<C, Q>>, _IntoSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Lock80OfClause}</li>
     *          <li>the composite {@link _LockLockOptionSpec}</li>
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
    interface _LockOfSpec<C, Q extends Query>
            extends Lock80OfClause<C, _LockLockOptionSpec<C, Q>>, _LockLockOptionSpec<C, Q> {

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
     * @param <C> java criteria object java type
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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _UnionOrderBySpec<C, Q extends Query> extends Query.OrderByClause<C, _UnionLimitSpec<C, Q>>
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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _UnionLimitSpec<C, Q extends Query> extends Query.LimitClause<C, _UnionSpec<C, Q>>, _UnionSpec<C, Q> {

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
    interface _UnionSpec<C, Q extends Query>
            extends QueryUnionClause<C, _UnionOrderBySpec<C, Q>, _WithSpec<C, Q>>, Query.QuerySpec<Q> {


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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface _WindowCommaSpec<C, Q extends Query> extends _OrderBySpec<C, Q> {

        _WindowAsClause<C, _WindowCommaSpec<C, Q>> comma(String windowName);

    }




    /**
     * <p>
     * This interface representing AS clause in WINDOW clause for MySQL 8.0
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <R> {@link Statement.RightBracketClause#rightBracket()} return java type
     * @since 1.0
     */
    interface _WindowAsClause<C, R> extends Window.AsClause<_WindowLeftBracketClause<C, R>> {


    }

    /**
     * <p>
     * This interface representing LEFT BRACKET clause in WINDOW clause for MySQL 8.0
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <R> {@link Statement.RightBracketClause#rightBracket()} return java type
     * @since 1.0
     */
    interface _WindowLeftBracketClause<C, R>
            extends Window.LeftBracketClause<C, _WindowPartitionBySpec<C, R>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Window.PartitionByExpClause} in WINDOW clause</li>
     *          <li>the composite {@link _WindowOrderBySpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <R> {@link Statement.RightBracketClause#rightBracket()} return java type
     * @since 1.0
     */
    interface _WindowPartitionBySpec<C, R> extends Window.PartitionByExpClause<C, _WindowOrderBySpec<C, R>>
            , _WindowOrderBySpec<C, R> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Statement.OrderByClause} clause in WINDOW clause</li>
     *          <li>the composite {@link _WindowFrameUnitsSpec}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <R> {@link Statement.RightBracketClause#rightBracket()} return java type
     * @since 1.0
     */
    interface _WindowOrderBySpec<C, R> extends Query.OrderByClause<C, _WindowFrameUnitsSpec<C, R>>
            , _WindowFrameUnitsSpec<C, R> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link Window.FrameUnitsClause}</li>
     *          <li>{@link Statement.RightBracketClause}</li>
     *     </ul>
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <R> {@link Statement.RightBracketClause#rightBracket()} return java type
     * @since 1.0
     */
    interface _WindowFrameUnitsSpec<C, R>
            extends Window.FrameUnitsClause<C, _WindowFrameBetweenClause<C, R>, _WindowFrameEndNonExpBoundClause<R>>
            , Statement.RightBracketClause<R> {

    }

    /**
     * <p>
     * This interface representing BETWEEN clause  in FRAME clause for MySQL 8.0.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <R> {@link Statement.RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _WindowFrameBetweenClause<C, R>
            extends Window.FrameBetweenClause<C, _WindowFrameNonExpBoundClause<C, R>, _WindowFrameExpBoundClause<C, R>> {

    }


    /**
     * <p>
     * This interface representing AND clause  in FRAME clause for MySQL 8.0.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <R> {@link Statement.RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _WindowFrameBetweenAndClause<C, R>
            extends Clause
            , Window.FrameBetweenAndClause<C, _WindowFrameEndNonExpBoundClause<R>, _WindowFrameEndExpBoundClause<R>> {

    }

    /**
     * <p>
     * This interface representing FRAME_START or FRAME_END  clause  in FRAME clause for MySQL 8.0.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <R> {@link Statement.RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _WindowFrameNonExpBoundClause<C, R> extends Window.FrameNonExpBoundClause<Statement.Clause> {

        /**
         * {@inheritDoc}
         */
        @Override
        _WindowFrameBetweenAndClause<C, R> currentRow();

        /**
         * {@inheritDoc}
         */
        @Override
        _WindowFrameBetweenAndClause<C, R> unboundedPreceding();

        /**
         * {@inheritDoc}
         */
        @Override
        _WindowFrameBetweenAndClause<C, R> unboundedFollowing();


    }

    /**
     * <p>
     * This interface representing FRAME_START or FRAME_END  clause  in FRAME clause for MySQL 8.0
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <R> {@link Statement.RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _WindowFrameExpBoundClause<C, R> extends Window.FrameExpBoundClause<Statement.Clause> {

        /**
         * {@inheritDoc}
         */
        @Override
        _WindowFrameBetweenAndClause<C, R> preceding();

        /**
         * {@inheritDoc}
         */
        @Override
        _WindowFrameBetweenAndClause<C, R> following();

    }


    /**
     * <p>
     * This interface representing FRAME_START or FRAME_END  clause  in FRAME clause for MySQL 8.0.
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <R> {@link Statement.RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _WindowFrameEndNonExpBoundClause<R> extends Window.FrameNonExpBoundClause<Statement.Clause> {

        /**
         * {@inheritDoc}
         */
        @Override
        Statement.RightBracketClause<R> currentRow();

        /**
         * {@inheritDoc}
         */
        @Override
        Statement.RightBracketClause<R> unboundedPreceding();

        /**
         * {@inheritDoc}
         */
        @Override
        Statement.RightBracketClause<R> unboundedFollowing();


    }

    /**
     * <p>
     * This interface representing FRAME_START or FRAME_END  clause  in FRAME clause for MySQL 8.0
     * </p>
     * <p>
     * <strong>Note:</strong><br/>
     * Application developer isn't allowed to directly use this interface,so you couldn't declare this interface type variable
     * ,because army don't guarantee compatibility to future distribution.
     * </p>
     *
     * @param <R> {@link Statement.RightBracketClause#rightBracket()} return java typ
     * @since 1.0
     */
    interface _WindowFrameEndExpBoundClause<R> extends Window.FrameExpBoundClause<Statement.Clause> {

        /**
         * {@inheritDoc}
         */
        @Override
        Statement.RightBracketClause<R> preceding();

        /**
         * {@inheritDoc}
         */
        @Override
        Statement.RightBracketClause<R> following();

    }


}
