package io.army.criteria.mysql;


import io.army.criteria.Query;
import io.army.criteria.Statement;
import io.army.criteria.Window;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

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
     *          <li>{@link WithCteClause} for MySQL 8.0</li>
     *          <li>the composite {@link Select80Clause}</li>
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
    interface With80Spec<C, Q extends Query> extends WithCteClause<C, Select80Clause<C, Q>>
            , Select80Clause<C, Q> {


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
    interface Select80Clause<C, Q extends Query> extends MySQLSelectClause<C, From80Spec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLFromClause} for MySQL 8.0</li>
     *          <li>the composite {@link Union80Spec}</li>
     *          <li>the composite {@link MySQLQuery.IntoSpec}</li>
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
    interface From80Spec<C, Q extends Query>
            extends MySQLQuery.MySQLFromClause<C, IndexHintJoin80Spec<C, Q>, JoinSpec<C, Q>
            , PartitionJoin80Clause<C, Q>, LeftBracket80Clause<C, Q>>, Union80Spec<C, Q>, MySQLQuery.IntoSpec<C, Q> {

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
    interface PartitionJoin80Clause<C, Q extends Query> extends MySQLQuery.PartitionClause<C, AsJoin80Clause<C, Q>> {

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
    interface AsJoin80Clause<C, Q extends Query> extends Statement.AsClause<IndexHintJoin80Spec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link IndexHintClause} for MySQL 8.0</li>
     *          <li>the composite {@link JoinSpec}</li>
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
    interface IndexHintJoin80Spec<C, Q extends Query>
            extends MySQLQuery.IndexHintClause<C, IndexPurposeJoin80Clause<C, Q>, IndexHintJoin80Spec<C, Q>>
            , JoinSpec<C, Q> {

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
    interface IndexPurposeJoin80Clause<C, Q extends Query>
            extends MySQLQuery.IndexPurposeClause<C, IndexHintJoin80Spec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>{@link MySQLJoinClause} for MySQL 8.0</li>
     *          <li>the composite {@link Where80Spec}</li>
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
    interface JoinSpec<C, Q extends Query>
            extends MySQLQuery.MySQLJoinClause<C, IndexHintOn80Spec<C, Q>, Statement.OnClause<C, JoinSpec<C, Q>>
            , PartitionOn80Clause<C, Q>, IndexHintJoin80Spec<C, Q>, JoinSpec<C, Q>, LeftBracket80Clause<C, Q>
            , PartitionJoin80Clause<C, Q>>, Where80Spec<C, Q> {

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
    interface LeftBracket80Clause<C, Q extends Query>
            extends MySQLJoinBracketClause<C, IndexHintJoin80Spec<C, Q>, JoinSpec<C, Q>, PartitionJoin80Clause<C, Q>> {

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
    interface PartitionOn80Clause<C, Q extends Query> extends MySQLQuery.PartitionClause<C, AsOn80Clause<C, Q>> {

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
    interface AsOn80Clause<C, Q extends Query> extends Statement.AsClause<IndexHintOn80Spec<C, Q>> {

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
            , Statement.OnClause<C, JoinSpec<C, Q>> {

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
    interface Window80Spec<C, Q extends Query> extends MySQLQuery.WindowClause<C, WindowAsClause<C, Q>>
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


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>COMMA clause in WINDOW clause</li>
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
    interface WindowCommaSpec<C, Q extends Query> extends OrderBy80Spec<C, Q> {

        WindowAsClause<C, Q> comma(String windowName);

        WindowAsClause<C, Q> ifComma(Supplier<String> supplier);

        WindowAsClause<C, Q> ifComma(Function<C, String> function);

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
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface WindowAsClause<C, Q extends Query> extends Window.AsClause<WindowLeftBracketClause<C, Q>> {


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
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface WindowLeftBracketClause<C, Q extends Query>
            extends Window.LeftBracketClause<C, WindowPartitionBySpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing RIGHT BRACKET clause in WINDOW clause for MySQL 8.0
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
    interface WindowRightBracketClause<C, Q extends Query> extends Statement.RightBracketClause<WindowCommaSpec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>PARTITION BY clause in WINDOW clause</li>
     *          <li>the composite {@link MySQL80Query.WindowOrderBySpec}</li>
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
    interface WindowPartitionBySpec<C, Q extends Query> extends Window.PartitionByExpClause<C, WindowOrderBySpec<C, Q>>
            , WindowOrderBySpec<C, Q> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>ORDER BY clause in WINDOW clause</li>
     *          <li>the composite {@link MySQL80Query.WindowFrameUnitsSpec}</li>
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
    interface WindowOrderBySpec<C, Q extends Query> extends Query.OrderByClause<C, WindowFrameUnitsSpec<C, Q>>
            , WindowFrameUnitsSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>FRAME_UNITS clause in FRAME clause</li>
     *          <li>the composite {@link MySQL80Query.WindowRightBracketClause}</li>
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
    interface WindowFrameUnitsSpec<C, Q extends Query>
            extends Window.FrameUnitsClause<C, WindowFrameBetweenClause<C, Q>, WindowFrameEndNonExpBoundClause<C, Q>>
            , WindowRightBracketClause<C, Q> {

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
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface WindowFrameBetweenClause<C, Q extends Query>
            extends Window.FrameBetweenClause<C, WindowFrameAndNonExpBoundClause<C, Q>, WindowFrameAndExpBound80Clause<C, Q>> {

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
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface WindowFrameBetweenAndClause<C, Q extends Query>
            extends Window.FrameBetweenAndClause<C, WindowFrameEndNonExpBoundClause<C, Q>, WindowFrameEndExpBoundClause<C, Q>> {

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
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface WindowFrameAndNonExpBoundClause<C, Q extends Query> extends Window.FrameNonExpBoundClause {

        /**
         * {@inheritDoc}
         */
        @Override
        WindowFrameBetweenAndClause<C, Q> currentRow();

        /**
         * {@inheritDoc}
         */
        @Override
        WindowFrameBetweenAndClause<C, Q> unboundedPreceding();

        /**
         * {@inheritDoc}
         */
        @Override
        WindowFrameBetweenAndClause<C, Q> unboundedFollowing();


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
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface WindowFrameAndExpBound80Clause<C, Q extends Query> extends Window.FrameExpBoundClause {

        /**
         * {@inheritDoc}
         */
        @Override
        WindowFrameBetweenAndClause<C, Q> preceding();

        /**
         * {@inheritDoc}
         */
        @Override
        WindowFrameBetweenAndClause<C, Q> following();

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
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface WindowFrameEndNonExpBoundClause<C, Q extends Query> extends Window.FrameNonExpBoundClause {

        /**
         * {@inheritDoc}
         */
        @Override
        WindowRightBracketClause<C, Q> currentRow();

        /**
         * {@inheritDoc}
         */
        @Override
        WindowRightBracketClause<C, Q> unboundedPreceding();

        /**
         * {@inheritDoc}
         */
        @Override
        WindowRightBracketClause<C, Q> unboundedFollowing();


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
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface WindowFrameEndExpBoundClause<C, Q extends Query> extends Window.FrameExpBoundClause {

        /**
         * {@inheritDoc}
         */
        @Override
        WindowRightBracketClause<C, Q> preceding();

        /**
         * {@inheritDoc}
         */
        @Override
        WindowRightBracketClause<C, Q> following();

    }


}
