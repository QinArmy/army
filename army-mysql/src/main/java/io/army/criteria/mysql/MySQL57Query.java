package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/select.html">MySQL 5.7 Select statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/union.html">MySQL 5.7 UNION Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/join.html">MySQL 5.7 JOIN Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
 */
public interface MySQL57Query extends MySQLQuery {



    /*################################## blow select clause  interfaces ##################################*/



    interface Into57Spec<Q extends MySQLQuery, C> extends From57Spec<Q, C> {

        @Override
        From57Spec<Q, C> into(List<String> varList);

        @Override
        From57Spec<Q, C> into(Function<C, List<String>> function);

        @Override
        From57Spec<Q, C> into(Supplier<List<String>> supplier);
    }


    interface From57Spec<Q extends MySQLQuery, C> extends Union57Clause<Q, C>, Into57Clause<Q, C> {


        IndexHintJoin57Spec<Q, C> from(TableMeta<?> table, String tableAlias);

        PartitionJoin57Spec<Q, C> from(TableMeta<?> table);

        Join57Spec<Q, C> from(Function<C, SubQuery> function, String subQueryAlia);

        Join57Spec<Q, C> from(Supplier<SubQuery> supplier, String subQueryAlia);

        Join57Spec<Q, C> fromGroup(Function<C, TablePartGroup> function, String groupAlias);

        Join57Spec<Q, C> fromGroup(Supplier<TablePartGroup> supplier, String groupAlias);

    }

    interface PartitionJoin57Spec<Q extends MySQLQuery, C> extends TableAsJoin57Spec<Q, C> {

        TableAsJoin57Spec<Q, C> partition(String partitionName);

        TableAsJoin57Spec<Q, C> partition(String partitionName1, String partitionNam2);

        TableAsJoin57Spec<Q, C> partition(List<String> partitionNameList);

        TableAsJoin57Spec<Q, C> partition(Function<C, List<String>> function);

        TableAsJoin57Spec<Q, C> partition(Supplier<List<String>> function);
    }

    interface TableAsJoin57Spec<Q extends MySQLQuery, C> {

        IndexHintJoin57Spec<Q, C> as(String tableAlias);

    }

    interface IndexHintJoin57Spec<Q extends MySQLQuery, C> extends Join57Spec<Q, C>
            , MySQLQuery.IndexHintClause<C, Join57Spec<Q, C>> {

    }


    interface PartitionOn57Spec<Q extends MySQLQuery, C> extends TableAsOn57Spec<Q, C> {

        TableAsOn57Spec<Q, C> partition(String partitionName);

        TableAsOn57Spec<Q, C> partition(String partitionName1, String partitionNam2);

        TableAsOn57Spec<Q, C> partition(List<String> partitionNameList);

        TableAsOn57Spec<Q, C> partition(Function<C, List<String>> function);

        TableAsOn57Spec<Q, C> partition(Supplier<List<String>> function);
    }

    interface TableAsOn57Spec<Q extends MySQLQuery, C> {

        IndexHintOn57Spec<Q, C> as(String tableAlias);

    }

    interface IndexHintOn57Spec<Q extends MySQLQuery, C> extends On57Spec<Q, C>
            , MySQLQuery.IndexHintClause<C, On57Spec<Q, C>> {

    }


    interface On57Spec<Q extends MySQLQuery, C> {

        Join57Spec<Q, C> on(List<IPredicate> predicateList);

        Join57Spec<Q, C> on(IPredicate predicate);

        Join57Spec<Q, C> on(IPredicate predicate1, IPredicate predicate2);

        Join57Spec<Q, C> on(Function<C, List<IPredicate>> function);

        Join57Spec<Q, C> on(Supplier<List<IPredicate>> supplier);

        Join57Spec<Q, C> onId();

    }


    interface Join57Spec<Q extends MySQLQuery, C> extends Where57Spec<Q, C> {

        IndexHintOn57Spec<Q, C> leftJoin(TableMeta<?> table, String tableAlias);

        PartitionOn57Spec<Q, C> leftJoin(TableMeta<?> table);

        On57Spec<Q, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        On57Spec<Q, C> leftJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        On57Spec<Q, C> leftJoinGroup(Function<C, TablePartGroup> function, String subQueryAlia);

        IndexHintOn57Spec<Q, C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        PartitionOn57Spec<Q, C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table);

        On57Spec<Q, C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia);

        On57Spec<Q, C> ifLeftJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        On57Spec<Q, C> ifLeftJoinGroup(Function<C, TablePartGroup> function, String subQueryAlia);

        IndexHintOn57Spec<Q, C> join(TableMeta<?> table, String tableAlias);

        PartitionOn57Spec<Q, C> join(TableMeta<?> table);

        On57Spec<Q, C> join(Function<C, SubQuery> function, String subQueryAlia);

        On57Spec<Q, C> join(Supplier<SubQuery> supplier, String subQueryAlia);

        IndexHintOn57Spec<Q, C> ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        PartitionOn57Spec<Q, C> ifJoin(Predicate<C> predicate, TableMeta<?> table);

        On57Spec<Q, C> joinGroup(Function<C, TablePartGroup> function, String subQueryAlia);

        On57Spec<Q, C> ifJoin(Function<C, SubQuery> function, String subQueryAlia);

        On57Spec<Q, C> ifJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        On57Spec<Q, C> ifJoinGroup(Function<C, TablePartGroup> function, String subQueryAlia);

        IndexHintOn57Spec<Q, C> rightJoin(TableMeta<?> table, String tableAlias);

        PartitionOn57Spec<Q, C> rightJoin(TableMeta<?> table);

        On57Spec<Q, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        On57Spec<Q, C> rightJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        On57Spec<Q, C> rightJoinGroup(Function<C, TablePartGroup> function, String subQueryAlia);

        IndexHintOn57Spec<Q, C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        PartitionOn57Spec<Q, C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table);

        On57Spec<Q, C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia);

        On57Spec<Q, C> ifRightJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        On57Spec<Q, C> ifRightJoinGroup(Function<C, TablePartGroup> function, String subQueryAlia);

        IndexHintOn57Spec<Q, C> straightJoin(TableMeta<?> table, String tableAlias);

        PartitionOn57Spec<Q, C> straightJoin(TableMeta<?> table);

        On57Spec<Q, C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

        On57Spec<Q, C> straightJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        On57Spec<Q, C> straightJoinGroup(Function<C, TablePartGroup> function, String subQueryAlia);

        IndexHintOn57Spec<Q, C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        PartitionOn57Spec<Q, C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> table);

        On57Spec<Q, C> ifStraightJoin(Function<C, SubQuery> function, String subQueryAlia);

        On57Spec<Q, C> ifStraightJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        On57Spec<Q, C> ifStraightJoinGroup(Function<C, TablePartGroup> function, String subQueryAlia);

    }


    interface Where57Spec<Q extends MySQLQuery, C> extends GroupBy57Spec<Q, C> {

        WhereAnd57Spec<Q, C> where(@Nullable IPredicate predicate);

        GroupBy57Spec<Q, C> where(List<IPredicate> predicateList);

        GroupBy57Spec<Q, C> where(Function<C, List<IPredicate>> function);

        GroupBy57Spec<Q, C> where(Supplier<List<IPredicate>> supplier);
    }

    interface WhereAnd57Spec<Q extends MySQLQuery, C> extends GroupBy57Spec<Q, C> {

        WhereAnd57Spec<Q, C> and(IPredicate predicate);

        WhereAnd57Spec<Q, C> and(Function<C, IPredicate> function);

        WhereAnd57Spec<Q, C> and(Supplier<IPredicate> supplier);

        /**
         * @see Expression#ifEqual(Object)
         */
        WhereAnd57Spec<Q, C> ifAnd(@Nullable IPredicate predicate);

        WhereAnd57Spec<Q, C> ifAnd(Function<C, IPredicate> function);

        WhereAnd57Spec<Q, C> ifAnd(Supplier<IPredicate> function);

    }

    interface GroupBy57Spec<Q extends MySQLQuery, C> extends OrderBy57Spec<Q, C> {

        WithRollUp57Spec<Q, C> groupBy(SortPart sortPart);

        WithRollUp57Spec<Q, C> groupBy(SortPart sortPart1, SortPart sortPart2);

        WithRollUp57Spec<Q, C> groupBy(List<SortPart> sortPartList);

        WithRollUp57Spec<Q, C> groupBy(Function<C, List<SortPart>> function);

        WithRollUp57Spec<Q, C> groupBy(Supplier<List<SortPart>> supplier);

        WithRollUp57Spec<Q, C> ifGroupBy(@Nullable SortPart sortPart);

        WithRollUp57Spec<Q, C> ifGroupBy(Function<C, List<SortPart>> function);

        WithRollUp57Spec<Q, C> ifGroupBy(Supplier<List<SortPart>> supplier);
    }

    interface WithRollUp57Spec<Q extends MySQLQuery, C> extends Having57Spec<Q, C> {

        Having57Spec<Q, C> withRollUp();
    }

    interface Having57Spec<Q extends MySQLQuery, C> extends OrderBy57Spec<Q, C> {

        OrderBy57Spec<Q, C> having(IPredicate predicate);

        OrderBy57Spec<Q, C> having(List<IPredicate> predicateList);

        OrderBy57Spec<Q, C> having(Function<C, List<IPredicate>> function);

        OrderBy57Spec<Q, C> having(Supplier<List<IPredicate>> supplier);

        OrderBy57Spec<Q, C> ifHaving(@Nullable IPredicate predicate);

        OrderBy57Spec<Q, C> ifHaving(Function<C, List<IPredicate>> function);

        OrderBy57Spec<Q, C> ifHaving(Supplier<List<IPredicate>> supplier);
    }


    interface OrderBy57Spec<Q extends MySQLQuery, C> extends Query.OrderByClause<Q, C>, Limit57Spec<Q, C> {

        @Override
        Limit57Spec<Q, C> orderBy(SortPart sortPart);

        @Override
        Limit57Spec<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2);

        @Override
        Limit57Spec<Q, C> orderBy(List<SortPart> sortPartList);

        @Override
        Limit57Spec<Q, C> orderBy(Function<C, List<SortPart>> function);

        @Override
        Limit57Spec<Q, C> orderBy(Supplier<List<SortPart>> supplier);

        @Override
        Limit57Spec<Q, C> ifOrderBy(@Nullable SortPart sortPart);

        @Override
        Limit57Spec<Q, C> ifOrderBy(Function<C, List<SortPart>> function);

        Limit57Spec<Q, C> ifOrderBy(Supplier<List<SortPart>> supplier);
    }


    interface Limit57Spec<Q extends MySQLQuery, C> extends Query.LimitClause<Q, C>, Into57Clause<Q, C> {

        @Override
        Into57Clause<Q, C> limit(long rowCount);

        @Override
        Into57Clause<Q, C> limit(long offset, long rowCount);

        @Override
        Into57Clause<Q, C> limit(Function<C, LimitOption> function);

        @Override
        Into57Clause<Q, C> limit(Supplier<LimitOption> supplier);

        @Override
        Into57Clause<Q, C> ifLimit(Function<C, LimitOption> function);

        @Override
        Into57Clause<Q, C> ifLimit(Supplier<LimitOption> supplier);

    }


    interface Union57Spec<Q extends MySQLQuery, C> extends Union57Clause<Q, C>, Query.OrderByClause<Q, C> {

    }


    interface Union57Clause<Q extends MySQLQuery, C> extends QuerySpec<Q> {

        Union57Spec<Q, C> bracketsQuery();

        Union57Spec<Q, C> union(Function<C, Q> function);

        Union57Spec<Q, C> union(Supplier<Q> supplier);

        MySQLSelectPartSpec<Q, C> union();

        MySQLSelectPartSpec<Q, C> unionAll();

        MySQLSelectPartSpec<Q, C> unionDistinct();

        Union57Spec<Q, C> unionAll(Function<C, Q> function);

        Union57Spec<Q, C> unionDistinct(Function<C, Q> function);

        Union57Spec<Q, C> unionAll(Supplier<Q> supplier);

        Union57Spec<Q, C> unionDistinct(Supplier<Q> supplier);

    }


    interface Into57Clause<Q extends MySQLQuery, C> extends Lock57Spec<Q, C> {

        Lock57Spec<Q, C> into(List<String> varList);

        Lock57Spec<Q, C> into(Function<C, List<String>> function);

        Lock57Spec<Q, C> into(Supplier<List<String>> supplier);

    }

    interface Lock57Spec<Q extends MySQLQuery, C> extends Union57Clause<Q, C> {

        QuerySpec<Q> forUpdate();

        QuerySpec<Q> lockInShareMode();

        QuerySpec<Q> ifForUpdate(Predicate<C> predicate);

        QuerySpec<Q> ifLockInShareMode(Predicate<C> predicate);

    }


}
