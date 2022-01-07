package io.army.criteria.mysql;

import io.army.criteria.Query;
import io.army.criteria.Statement;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/select.html">MySQL 5.7 Select statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/union.html">MySQL 5.7 UNION Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/join.html">MySQL 5.7 JOIN Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
 */
public interface MySQL57Query extends MySQLQuery {



    /*################################## blow select clause  interfaces ##################################*/

    interface Select57Spec<C, Q extends Query> extends Query.SelectClause<C, From57Spec<C, Q>> {

    }

    interface From57Spec<C, Q extends Query>
            extends MySQLQuery.MySQLFromClause<C, IndexHintJoin57Spec<C, Q>, Join57Spec<C, Q>, PartitionJoin57Spec<C, Q>>
            , Union57Spec<C, Q> {

    }


    interface PartitionJoin57Spec<C, Q extends Query> extends MySQLQuery.PartitionClause<C, As57JoinSpec<C, Q>> {

    }

    interface As57JoinSpec<C, Q extends Query> extends Statement.AsClause<IndexHintJoin57Spec<C, Q>> {

    }

    interface IndexHintJoin57Spec<C, Q extends Query>
            extends MySQLQuery.IndexHintClause<C, IndexPurposeJoin57Spec<C, Q>, IndexHintJoin57Spec<C, Q>>
            , Join57Spec<C, Q> {

    }

    interface IndexPurposeJoin57Spec<C, Q extends Query> extends IndexPurposeClause<C, IndexHintJoin57Spec<C, Q>> {

    }

    interface PartitionOn57Spec<C, Q extends Query> extends MySQLQuery.PartitionClause<C, As57OnSpec<C, Q>> {

    }

    interface As57OnSpec<C, Q extends Query> extends Statement.AsClause<IndexHintOn57Spec<C, Q>> {

    }

    interface IndexHintOn57Spec<C, Q extends Query>
            extends MySQLQuery.IndexHintClause<C, IndexPurposeOn57Spec<C, Q>, IndexHintOn57Spec<C, Q>>, On57Spec<C, Q> {

    }

    interface IndexPurposeOn57Spec<C, Q extends Query> extends IndexPurposeClause<C, IndexHintOn57Spec<C, Q>> {

    }


    interface On57Spec<C, Q extends Query> extends Statement.OnClause<C, Join57Spec<C, Q>> {

    }

    interface Join57Spec<C, Q extends Query>
            extends MySQLQuery.MySQLJoinClause<C, IndexHintOn57Spec<C, Q>, On57Spec<C, Q>, PartitionOn57Spec<C, Q>>
            , Where57Spec<C, Q> {

    }

    interface Where57Spec<C, Q extends Query> extends Statement.WhereClause<C, GroupBy57Spec<C, Q>, WhereAnd57Spec<C, Q>>
            , GroupBy57Spec<C, Q> {

    }

    interface WhereAnd57Spec<C, Q extends Query> extends Statement.WhereAndClause<C, WhereAnd57Spec<C, Q>>
            , GroupBy57Spec<C, Q> {

    }

    interface GroupBy57Spec<C, Q extends Query> extends Query.GroupClause<C, WithRollup57Spec<C, Q>>
            , OrderBy57Spec<C, Q> {

    }

    interface WithRollup57Spec<C, Q extends Query> extends MySQLQuery.WithRollupClause<C, Having57Spec<C, Q>>
            , Having57Spec<C, Q> {

    }


    interface Having57Spec<C, Q extends Query> extends Query.HavingClause<C, OrderBy57Spec<C, Q>>
            , OrderBy57Spec<C, Q> {


    }

    interface OrderBy57Spec<C, Q extends Query> extends Query.OrderByClause<C, Limit57Spec<C, Q>>
            , Limit57Spec<C, Q> {

    }


    interface Limit57Spec<C, Q extends Query> extends Query.LimitClause<C, Lock57Spec<C, Q>>
            , Lock57Spec<C, Q> {

    }

    interface Lock57Spec<C, Q extends Query>
            extends MySQLQuery.LockClause<C, Union57Spec<C, Q>>, Union57Spec<C, Q> {

    }

    interface UnionOrderBy57Spec<C, Q extends Query> extends Query.OrderByClause<C, UnionLimit57Spec<C, Q>>
            , UnionLimit57Spec<C, Q> {

    }

    interface UnionLimit57Spec<C, Q extends Query> extends Query.LimitClause<C, Union57Spec<C, Q>>, Union57Spec<C, Q> {

    }


    interface Union57Spec<C, Q extends Query>
            extends Query.UnionClause<C, UnionOrderBy57Spec<C, Q>, Select57Spec<C, Q>, Q>
            , Query.QuerySpec<Q> {

    }


}
