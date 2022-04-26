package io.army.criteria.mysql;

import io.army.criteria.Query;
import io.army.criteria.Statement;

/**
 * <p>
 * This interface representing MySQL 57 SELECT syntax.
 * </p>
 *
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/select.html">MySQL 5.7 Select statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/union.html">MySQL 5.7 UNION Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/join.html">MySQL 5.7 JOIN Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface Select57Clause<C, Q extends Query> extends Query.SelectClause<C, From57Spec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     * <ul>
     *      <li>FROM clause in MySQL 57 syntax</li>
     *      <li>the composite {@link Union57Spec} </li>
     * </ul>
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
    interface From57Spec<C, Q extends Query>
            extends MySQLQuery.MySQLFromClause<C, IndexHintJoin57Spec<C, Q>, Join57Spec<C, Q>, PartitionJoin57Clause<C, Q>, LestBracket57Clause<C, Q>>
            , Union57Spec<C, Q> {

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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface PartitionJoin57Clause<C, Q extends Query> extends MySQLQuery.PartitionClause<C, AsJoin57Clause<C, Q>> {

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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface AsJoin57Clause<C, Q extends Query> extends Statement.AsClause<IndexHintJoin57Spec<C, Q>> {

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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface IndexHintJoin57Spec<C, Q extends Query>
            extends MySQLQuery.IndexHintClause<C, IndexPurposeJoin57Clause<C, Q>, IndexHintJoin57Spec<C, Q>>
            , Join57Clause<C, Q> {

    }

    /**
     * <p>
     * This interface representing index hint clause after below methods:
     * <ul>
     *     <li>{@link IndexHintJoin57Spec#useIndex()}</li>
     *     <li>{@link IndexHintJoin57Spec#ignoreIndex()}</li>
     *     <li>{@link IndexHintJoin57Spec#forceIndex()}</li>
     * </ul>
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
    interface IndexPurposeJoin57Clause<C, Q extends Query>
            extends MySQLQuery.IndexPurposeClause<C, IndexHintJoin57Spec<C, Q>> {

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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface PartitionOn57Clause<C, Q extends Query> extends MySQLQuery.PartitionClause<C, AsOn57Clause<C, Q>> {

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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface AsOn57Clause<C, Q extends Query> extends Statement.AsClause<IndexHintOn57Spec<C, Q>> {

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
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface IndexHintOn57Spec<C, Q extends Query>
            extends MySQLQuery.IndexHintClause<C, IndexPurposeOn57Spec<C, Q>, IndexHintOn57Spec<C, Q>>, Statement.OnClause<C, Join57Spec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing index hint clause after below methods:
     * <ul>
     *     <li>{@link IndexHintOn57Spec#useIndex()}</li>
     *     <li>{@link IndexHintOn57Spec#ignoreIndex()}</li>
     *     <li>{@link IndexHintOn57Spec#forceIndex()}</li>
     * </ul>
     * this interface is returned by {@link IndexHintOn57Spec}.
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
    interface IndexPurposeOn57Spec<C, Q extends Query>
            extends MySQLQuery.IndexPurposeClause<C, IndexHintOn57Spec<C, Q>> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *    <ul>
     *        <li>join clause in MySQL 57 syntax</li>
     *        <li>the composite {@link Where57Spec}</li>
     *        <li>right bracket clause</li>
     *    </ul>
     * </p>
     * <p>
     *     If and only if use below methods to create dynamic nested join,then you is allowed to declare this interface type variable
     *     <ul>
     *         <li>{@link #leftJoin()}</li>
     *         <li>{@link #join()}</li>
     *         <li>{@link #rightJoin()}</li>
     *         <li>{@link #fullJoin()}</li>
     *         <li>{@link #crossJoin()}</li>
     *         <li>{@link #straightJoin()}</li>
     *     </ul>
     * </p>
     *
     * @param <C> java criteria object java type
     * @param <Q> {@link io.army.criteria.Select} or {@link io.army.criteria.SubQuery} or {@link io.army.criteria.ScalarExpression}
     * @since 1.0
     */
    interface Join57Spec<C, Q extends Query> extends Join57Clause<C, Q>, Where57Spec<C, Q>
            , RightBracketClause<Join57Spec<C, Q>> {

    }

    /**
     * <p>
     * This interface representing join clause for MySQL 57 syntax.
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
    interface Join57Clause<C, Q extends Query>
            extends MySQLJoinClause<C, IndexHintOn57Spec<C, Q>, Statement.OnClause<C, Join57Spec<C, Q>>, PartitionOn57Clause<C, Q>
            , IndexHintJoin57Spec<C, Q>, Join57Spec<C, Q>, LestBracket57Clause<C, Q>, PartitionJoin57Clause<C, Q>> {

    }


    /**
     * <p>
     * This interface representing a left bracket clause after key word 'FROM' or key word 'JOIN' for MySQL 57 syntax.
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
    interface LestBracket57Clause<C, Q extends Query>
            extends MySQLJoinBracketClause<C, IndexHintJoin57Spec<C, Q>, Join57Spec<C, Q>, PartitionJoin57Clause<C, Q>> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>WHERE clause for MySQL 57</li>
     *          <li>the composite {@link GroupBy57Spec}</li>
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
    interface Where57Spec<C, Q extends Query> extends Statement.WhereClause<C, GroupBy57Spec<C, Q>, WhereAnd57Spec<C, Q>>
            , GroupBy57Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>AND clause for MySQL 57</li>
     *          <li>the composite {@link GroupBy57Spec}</li>
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
    interface WhereAnd57Spec<C, Q extends Query> extends Statement.WhereAndClause<C, WhereAnd57Spec<C, Q>>
            , GroupBy57Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>GROUP BY clause for MySQL 57</li>
     *          <li>the composite {@link OrderBy57Spec}</li>
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
    interface GroupBy57Spec<C, Q extends Query> extends Query.GroupClause<C, WithRollup57Spec<C, Q>>
            , OrderBy57Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>WITH ROLLUP clause for MySQL 57</li>
     *          <li>the composite {@link Having57Spec}</li>
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
    interface WithRollup57Spec<C, Q extends Query> extends MySQLQuery.WithRollupClause<C, Having57Spec<C, Q>>
            , Having57Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>HAVING clause for MySQL 57</li>
     *          <li>the composite {@link OrderBy57Spec}</li>
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
    interface Having57Spec<C, Q extends Query> extends Query.HavingClause<C, OrderBy57Spec<C, Q>>
            , OrderBy57Spec<C, Q> {


    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>ORDER BY clause for MySQL 57</li>
     *          <li>the composite {@link Limit57Spec}</li>
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
    interface OrderBy57Spec<C, Q extends Query> extends Query.OrderByClause<C, Limit57Spec<C, Q>>
            , Limit57Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>LIMIT clause for MySQL 57</li>
     *          <li>the composite {@link Lock57Spec}</li>
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
    interface Limit57Spec<C, Q extends Query> extends Query.LimitClause<C, Lock57Spec<C, Q>>
            , Lock57Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>LOCK clause for MySQL 57</li>
     *          <li>the composite {@link Union57Spec}</li>
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
    interface Lock57Spec<C, Q extends Query>
            extends MySQLQuery.LockClause<C, Union57Spec<C, Q>>, Union57Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>ORDER BY clause for MySQL 57</li>
     *          <li>the composite {@link UnionLimit57Spec}</li>
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
    interface UnionOrderBy57Spec<C, Q extends Query> extends Query.OrderByClause<C, UnionLimit57Spec<C, Q>>
            , UnionLimit57Spec<C, Q> {

    }

    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>LIMIT clause for MySQL 57</li>
     *          <li>the composite {@link Union57Spec}</li>
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
    interface UnionLimit57Spec<C, Q extends Query> extends Query.LimitClause<C, Union57Spec<C, Q>>, Union57Spec<C, Q> {

    }


    /**
     * <p>
     * This interface representing the composite of below:
     *     <ul>
     *          <li>UNION clause for MySQL 57</li>
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
    interface Union57Spec<C, Q extends Query>
            extends QueryUnionClause<C, UnionOrderBy57Spec<C, Q>, Select57Clause<C, Q>>
            , Query.QuerySpec<Q> {

    }


}
