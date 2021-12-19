package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/select.html">MySQL 5.7 Select statement</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/union.html">MySQL 5.7 UNION Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/join.html">MySQL 5.7 JOIN Clause</a>
 * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
 */
public interface MySQL57Query extends Query {



    /*################################## blow select clause  interfaces ##################################*/


    interface MySQLSelectPartSpec<Q extends MySQL57Query, C> {

        <S extends SelectPart> MySQLFromSpec<Q, C> select(Distinct distinct, Function<C, List<S>> function);

        <S extends SelectPart> MySQLFromSpec<Q, C> select(Function<C, List<S>> function);

        MySQLFromSpec<Q, C> select(Distinct distinct, SelectPart selectPart);

        MySQLFromSpec<Q, C> select(SelectPart selectPart);

        <S extends SelectPart> MySQLFromSpec<Q, C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> MySQLFromSpec<Q, C> select(List<S> selectPartList);
    }


    interface MySQLFromSpec<Q extends MySQL57Query, C> extends UnionClause<Q, C> {

        MySQLTableRouteJoinSpec<Q, C> from(TableMeta<?> tableMeta, String tableAlias);

        MySQLJoinSpec<Q, C> from(Function<C, SubQuery> function, String subQueryAlia);

    }


    interface MySQLIndexHintJoinSpec<Q extends MySQL57Query, C> extends MySQLJoinSpec<Q, C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLJoinSpec<Q, C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function);
    }


    interface MySQLTableRouteJoinSpec<Q extends MySQL57Query, C> extends MySQLIndexHintJoinSpec<Q, C> {

        MySQLJoinSpec<Q, C> route(int databaseIndex, int tableIndex);

        MySQLJoinSpec<Q, C> route(int tableIndex);
    }


    interface MySQLOnSpec<Q extends MySQL57Query, C> {

        MySQLJoinSpec<Q, C> on(List<IPredicate> predicateList);

        MySQLJoinSpec<Q, C> on(IPredicate predicate);

        MySQLJoinSpec<Q, C> on(Function<C, List<IPredicate>> function);

    }

    interface MySQLIndexHintOnSpec<Q extends MySQL57Query, C> extends MySQLOnSpec<Q, C> {

        /**
         * @see <a href="https://dev.mysql.com/doc/refman/5.7/en/index-hints.html">MySQL 5.7  Index Hints</a>
         */
        MySQLOnSpec<Q, C> ifIndexHintList(Function<C, List<MySQL57IndexHint>> function);

    }

    interface MySQLTableRouteOnSpec<Q extends MySQL57Query, C> extends MySQLIndexHintOnSpec<Q, C> {

        MySQLIndexHintOnSpec<Q, C> route(int databaseIndex, int tableIndex);

        MySQLIndexHintOnSpec<Q, C> route(int tableIndex);
    }

    interface MySQLJoinSpec<Q extends MySQL57Query, C> extends MySQLWhereSpec<Q, C> {

        MySQLTableRouteOnSpec<Q, C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLOnSpec<Q, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnSpec<Q, C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLOnSpec<Q, C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnSpec<Q, C> join(TableMeta<?> tableMeta, String tableAlias);

        MySQLOnSpec<Q, C> join(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnSpec<Q, C> ifJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLOnSpec<Q, C> ifJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnSpec<Q, C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLOnSpec<Q, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnSpec<Q, C> ifRightJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLOnSpec<Q, C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnSpec<Q, C> straightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLOnSpec<Q, C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLTableRouteOnSpec<Q, C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLOnSpec<Q, C> ifStraightJoin(Function<C, SubQuery> function, String subQueryAlia);

    }

    interface MySQLWhereSpec<Q extends MySQL57Query, C> extends MySQLGroupBySpec<Q, C> {

        MySQLWhereAndSpec<Q, C> where(IPredicate predicate);

        MySQLGroupBySpec<Q, C> where(List<IPredicate> predicateList);

        MySQLGroupBySpec<Q, C> ifWhere(Function<C, List<IPredicate>> function);
    }

    interface MySQLWhereAndSpec<Q extends MySQL57Query, C> extends MySQLGroupBySpec<Q, C> {

        MySQLWhereAndSpec<Q, C> and(IPredicate predicate);

        /**
         * @see Expression#ifEqual(Object)
         */
        MySQLWhereAndSpec<Q, C> ifAnd(@Nullable IPredicate predicate);

        MySQLWhereAndSpec<Q, C> ifAnd(Function<C, IPredicate> function);

    }

    interface MySQLGroupBySpec<Q extends MySQL57Query, C> extends MySQLOrderBySpec<Q, C> {

        MySQLWithRollUpSpec<Q, C> groupBy(SortPart sortPart);

        MySQLWithRollUpSpec<Q, C> groupBy(SortPart sortPart1, SortPart sortPart2);

        MySQLWithRollUpSpec<Q, C> groupBy(List<SortPart> sortPartList);

        MySQLWithRollUpSpec<Q, C> groupBy(Function<C, List<SortPart>> function);

        MySQLWithRollUpSpec<Q, C> ifGroupBy(Function<C, List<SortPart>> function);
    }

    interface MySQLWithRollUpSpec<Q extends MySQL57Query, C> extends MySQLHavingSpec<Q, C> {

        MySQLHavingSpec<Q, C> withRollUp();

        MySQLHavingSpec<Q, C> withRollUp(Predicate<C> predicate);
    }

    interface MySQLHavingSpec<Q extends MySQL57Query, C> extends MySQLOrderBySpec<Q, C> {

        MySQLOrderBySpec<Q, C> having(IPredicate predicate);

        MySQLOrderBySpec<Q, C> having(List<IPredicate> predicateList);

        MySQLOrderBySpec<Q, C> having(Function<C, List<IPredicate>> function);

        MySQLOrderBySpec<Q, C> ifHaving(Function<C, List<IPredicate>> function);
    }


    interface MySQLOrderBySpec<Q extends MySQL57Query, C> extends OrderByClause<Q, C>, MySQLLimitSpec<Q, C> {

        @Override
        MySQLLimitSpec<Q, C> orderBy(SortPart sortPart);

        @Override
        MySQLLimitSpec<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2);

        @Override
        MySQLLimitSpec<Q, C> orderBy(List<SortPart> sortPartList);

        @Override
        MySQLLimitSpec<Q, C> orderBy(Function<C, List<SortPart>> function);

        @Override
        MySQLLimitSpec<Q, C> ifOrderBy(Function<C, List<SortPart>> function);
    }


    interface MySQLLimitSpec<Q extends MySQL57Query, C> extends LimitClause<Q, C>, MySQLLockSpec<Q, C> {
        @Override
        MySQLLockSpec<Q, C> limit(int rowCount);

        @Override
        MySQLLockSpec<Q, C> limit(int offset, int rowCount);

        @Override
        MySQLLockSpec<Q, C> ifLimit(Function<C, LimitOption> function);

        @Override
        MySQLLockSpec<Q, C> ifLimit(Predicate<C> predicate, int rowCount);

        @Override
        MySQLLockSpec<Q, C> ifLimit(Predicate<C> predicate, int offset, int rowCount);
    }

    interface MySQLLockSpec<Q extends MySQL57Query, C> extends QuerySpec<Q>, UnionClause<Q, C> {

        QuerySpec<Q> forUpdate();

        MySQLLockSpec<Q, C> ifForUpdate(Predicate<C> predicate);

        QuerySpec<Q> lockInShareMode();

        MySQLLockSpec<Q, C> ifLockInShareMode(Predicate<C> predicate);
    }


}
