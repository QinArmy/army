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
            extends MySQLQuery.MySQLJoinClause<C, IndexHintOn80Spec<C, Q>, On80Spec<C, Q>, PartitionOn80Spec<C, Q>>
            , Where80Spec<C, Q> {

    }


    interface PartitionOn80Spec<C, Q extends Query> extends MySQLQuery.PartitionClause<C, AsOn80Spec<C, Q>> {

    }

    interface AsOn80Spec<C, Q extends Query> extends Statement.AsClause<IndexHintOn80Spec<C, Q>> {

    }


    interface IndexHintOn80Spec<C, Q extends Query>
            extends MySQLQuery.IndexHintClause<C, IndexPurposeOn80Spec<C, Q>, IndexHintOn80Spec<C, Q>>
            , On80Spec<C, Q> {

    }

    interface IndexPurposeOn80Spec<C, Q extends Query> extends MySQLQuery.IndexPurposeClause<C, IndexHintOn80Spec<C, Q>> {

    }


    interface On80Spec<C, Q extends Query> extends Statement.OnClause<C, Join80Spec<C, Q>> {

    }

    interface Where80Spec<C, Q extends Query>
            extends Statement.WhereClause<C, GroupBy80Spec<C, Q>, WhereAnd80Spec<C, Q>>
            , GroupBy80Spec<C, Q> {

    }


    interface WhereAnd80Spec<C, Q extends Query> extends Statement.WhereAndClause<C, WhereAnd80Spec<C, Q>>
            , GroupBy80Spec<C, Q> {

    }


    interface GroupBy80Spec<C, Q extends Query> extends Query.GroupClause<C, GroupByWithRollup80Spec<C, Q>>
            , Window80Spec<C, Q> {

    }

    interface GroupByWithRollup80Spec<C, Q extends Query> extends WithRollup80Clause<C, Q>, Having80Spec<C, Q> {

        @Override
        Having80Spec<C, Q> withRollup();

        @Override
        Having80Spec<C, Q> ifWithRollup(Predicate<C> predicate);
    }


    interface Having80Spec<C, Q extends Query> extends Query.HavingClause<C, Window80Spec<C, Q>>, Window80Spec<C, Q> {

    }


    interface Window80Spec<C, Q extends Query> extends MySQLQuery.WindowClause<C, OrderBy80Spec<C, Q>>
            , OrderBy80Spec<C, Q> {

    }

    interface OrderBy80Spec<C, Q extends Query>
            extends Query.OrderByClause<C, OrderByWithRollup80Spec<C, Q>>, Limit80Spec<C, Q> {

    }

    interface WithRollup80Clause<C, Q extends Query> extends MySQLQuery.WithRollupClause<C, Limit80Spec<C, Q>> {

    }


    interface OrderByWithRollup80Spec<C, Q extends Query> extends WithRollup80Clause<C, Q>, Limit80Spec<C, Q> {

    }


    interface Limit80Spec<C, Q extends Query> extends Query.LimitClause<C, Lock80Spec<C, Q>>, Lock80Spec<C, Q> {

    }


    interface Lock80Spec<C, Q extends Query>
            extends MySQLQuery.Lock80Clause<C, Lock80LockOfOptionSpec<C, Q>, Union80Spec<C, Q>> {

    }


    interface Lock80LockOfOptionSpec<C, Q extends Query>
            extends MySQLQuery.Lock80LockOfOptionClause<C, Lock80LockOptionSpec<C, Q>>, Lock80LockOptionSpec<C, Q> {

    }


    interface Lock80LockOptionSpec<C, Q extends Query>
            extends MySQLQuery.Lock80LockOptionClause<C, Union80Spec<C, Q>>, Union80Spec<C, Q> {


    }

    interface UnionOrderBy80Spec<C, Q extends Query> extends Query.OrderByClause<C, UnionLimit80Spec<C, Q>>
            , UnionLimit80Spec<C, Q> {

    }

    interface UnionLimit80Spec<C, Q extends Query> extends Query.LimitClause<C, Union80Spec<C, Q>>, Union80Spec<C, Q> {

    }


    interface Union80Spec<C, Q extends Query>
            extends Query.UnionClause<C, UnionOrderBy80Spec<C, Q>, With80Spec<C, Q>, Q>, Query.QuerySpec<Q> {


    }


}
