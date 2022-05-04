package io.army.criteria.mysql;

import io.army.criteria.Query;

/**
 * <p>
 * This interface representing MySQL 5.7 SELECT syntax.
 * </p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/select.html">MySQL 5.7 Select statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/union.html">MySQL 5.7 UNION Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/join.html">MySQL 5.7 JOIN Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/select-into.html">MySQL 5.7 SELECT ... INTO Statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/nested-join-optimization.html">MySQL 5.7 Nested Join Optimization</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/partitioning-selection.html">MySQL 5.7 Partition Selection</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7 Index Hints</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/optimizer-hints.html">MySQL 5.7 Optimizer Hints</a>
 * @since 1.0
 */
public interface MySQL57Query extends MySQLQuery {



    /*################################## blow select clause  interfaces ##################################*/

    /**
     * <p>
     * This interface representing SELECT clause for MySQL 57 syntax.
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
    interface _Select57Clause<C, Q extends Query> extends _MySQLSelectClause<C, _FromSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     * <ul>
     *      <li>FROM clause in MySQL 57 syntax</li>
     *      <li>the composite {@link _UnionSpec} </li>
     * </ul>
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
            , _DialectFromClause<_PartitionJoinClause<C, Q>>, _UnionSpec<C, Q>, _IntoSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing PARTITION clause after key word 'FROM' in table references for MySQL 57 syntax.
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
     * This interface representing AS clause after key word 'FROM' in table references for MySQL 57 syntax.
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
     * <ul>
     *     <li>index hint clause</li>
     *     <li>JOIN clause for MySQL 57</li>
     * </ul>
     *  after key word 'FROM' in table references for MySQL 57 syntax.
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
            extends _IndexHintClause<C, _IndexPurposeJoinClause<C, Q>, _IndexHintJoinSpec<C, Q>>
            , _JoinSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing index hint clause after below methods:
     * <ul>
     *     <li>{@link _IndexHintJoinSpec#useIndex()}</li>
     *     <li>{@link _IndexHintJoinSpec#ignoreIndex()}</li>
     *     <li>{@link _IndexHintJoinSpec#forceIndex()}</li>
     * </ul>
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
    interface _IndexPurposeJoinClause<C, Q extends Query>
            extends _IndexPurposeClause<C, _IndexHintJoinSpec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing PARTITION clause after key word 'JOIN'(non-cross join) in table references for MySQL 57 syntax.
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
     * This interface representing AS clause after key word 'JOIN'(non-cross join) in table references for MySQL 57 syntax.
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
     * <ul>
     *     <li>index hint clause</li>
     *     <li>ON clause for MySQL 57</li>
     * </ul>
     *  after key word 'JOIN'(non-cross join) in table references for MySQL 57 syntax.
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
            extends _IndexHintClause<C, _IndexPurposeOn57Clause<C, Q>, _IndexHintOnSpec<C, Q>>, _OnClause<C, _JoinSpec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing index hint clause after below methods:
     * <ul>
     *     <li>{@link _IndexHintOnSpec#useIndex()}</li>
     *     <li>{@link _IndexHintOnSpec#ignoreIndex()}</li>
     *     <li>{@link _IndexHintOnSpec#forceIndex()}</li>
     * </ul>
     * this interface is returned by {@link _IndexHintOnSpec}.
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
    interface _IndexPurposeOn57Clause<C, Q extends Query>
            extends _IndexPurposeClause<C, _IndexHintOnSpec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *    <ul>
     *        <li>join clause in MySQL 57 syntax</li>
     *        <li>the composite {@link _WhereSpec}</li>
     *    </ul>
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
            extends _MySQLJoinClause<C, _IndexHintOnSpec<C, Q>, _OnClause<C, _JoinSpec<C, Q>>>
            , _CrossJoinClause<C, _IndexHintJoinSpec<C, Q>, _JoinSpec<C, Q>>
            , _MySQLDialectJoinClause<C, _PartitionOnClause<C, Q>>, _DialectCrossJoinClause<C, _PartitionJoinClause<C, Q>>
            , _WhereSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>WHERE clause for MySQL 57</li>
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
    interface _WhereSpec<C, Q extends Query> extends _QueryWhereClause<C, _GroupBySpec<C, Q>, _WhereAndSpec<C, Q>>
            , _GroupBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>AND clause for MySQL 57</li>
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
     *          <li>GROUP BY clause for MySQL 57</li>
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
    interface _GroupBySpec<C, Q extends Query> extends _GroupClause<C, _WithRollupSpec<C, Q>>
            , _OrderBySpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>WITH ROLLUP clause for MySQL 57</li>
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
    interface _WithRollupSpec<C, Q extends Query> extends _WithRollupClause<C, _HavingSpec<C, Q>>
            , _HavingSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>HAVING clause for MySQL 57</li>
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
     *          <li>ORDER BY clause for MySQL 57</li>
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
    interface _OrderBySpec<C, Q extends Query> extends _OrderByClause<C, _LimitSpec<C, Q>>
            , _LimitSpec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>LIMIT clause for MySQL 57</li>
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
    interface _LimitSpec<C, Q extends Query> extends _LimitClause<C, _LockSpec<C, Q>>
            , _LockSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>LOCK clause for MySQL 57</li>
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
            extends _LockClause<C, _UnionSpec<C, Q>>, _UnionSpec<C, Q>, _IntoSpec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>ORDER BY clause for MySQL 57</li>
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
     *          <li>LIMIT clause for MySQL 57</li>
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
     *          <li>UNION clause for MySQL 57</li>
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
            extends _QueryUnionClause<C, _UnionOrderBySpec<C, Q>, _Select57Clause<C, Q>>
            , _QuerySpec<Q> {

    }


}
