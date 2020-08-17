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
public interface MySQL57SubQuery extends SubQuery {


    interface MySQLSubQuerySelectPartAble<C> extends SubQuerySQLAble {

        <S extends SelectPart> MySQLSubQueryFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        <S extends SelectPart> MySQLSubQueryFromAble<C> select(Function<C, List<S>> function);

        MySQLSubQueryFromAble<C> select(Distinct distinct, SelectPart selectPart);

        MySQLSubQueryFromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> MySQLSubQueryFromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> MySQLSubQueryFromAble<C> select(List<S> selectPartList);
    }


    interface MySQLSubQueryFromAble<C> extends SubQueryUnionClause<C> {

        MySQLSubQueryTableRouteJoinAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryJoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia);

    }

    interface MySQLSubQueryTableRouteJoinAble<C> extends MySQLSubQueryJoinAble<C> {

        MySQLSubQueryJoinAble<C> fromRoute(int databaseIndex, int tableIndex);

        MySQLSubQueryJoinAble<C> fromRoute(int tableIndex);
    }


    interface MySQLSubQueryOnAble<C> extends SubQuerySQLAble {

        MySQLSubQueryJoinAble<C> on(List<IPredicate> predicateList);

        MySQLSubQueryJoinAble<C> on(IPredicate predicate);

        MySQLSubQueryJoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface MySQLSubQueryTableRouteOnAble<C> extends MySQLSubQueryOnAble<C> {

        MySQLSubQueryOnAble<C> route(int databaseIndex, int tableIndex);

        MySQLSubQueryOnAble<C> route(int tableIndex);
    }

    interface MySQLSubQueryJoinAble<C> extends MySQLSubQueryWhereAble<C> {

        MySQLSubQueryTableRouteOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLSubQueryTableRouteOnAble<C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryOnAble<C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLSubQueryTableRouteOnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        MySQLSubQueryTableRouteOnAble<C> ifJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryOnAble<C> ifJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLSubQueryTableRouteOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLSubQueryTableRouteOnAble<C> ifRightJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryOnAble<C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLSubQueryTableRouteOnAble<C> straightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryOnAble<C> straightJoin(Function<C, SubQuery> function, String subQueryAlia);

        MySQLSubQueryTableRouteOnAble<C> ifStraightJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryOnAble<C> ifStraightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface MySQLSubQueryWhereAble<C> extends MySQLSubQueryGroupByAble<C> {

        MySQLSubQueryGroupByAble<C> where(List<IPredicate> predicateList);

        MySQLSubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function);

        MySQLSubQueryWhereAndAble<C> where(IPredicate predicate);
    }

    interface MySQLSubQueryWhereAndAble<C> extends MySQLSubQueryGroupByAble<C> {

        MySQLSubQueryWhereAndAble<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        MySQLSubQueryWhereAndAble<C> ifAnd(@Nullable IPredicate predicate);

        MySQLSubQueryWhereAndAble<C> ifAnd(Function<C, IPredicate> function);

    }


    interface MySQLSubQueryGroupByAble<C> extends MySQLSubQueryOrderByAble<C> {

        MySQLSubQueryWithRollUpAble<C> groupBy(SortPart sortPart);

        MySQLSubQueryWithRollUpAble<C> groupBy(List<SortPart> sortPartList);

        MySQLSubQueryWithRollUpAble<C> groupBy(Function<C, List<SortPart>> function);
    }

    interface MySQLSubQueryWithRollUpAble<C> extends MySQLSubQueryHavingAble<C> {

        MySQLSubQueryHavingAble<C> withRollUp();
    }

    interface MySQLSubQueryHavingAble<C> extends MySQLSubQueryOrderByAble<C> {

        MySQLSubQueryOrderByAble<C> having(IPredicate predicate);

        MySQLSubQueryOrderByAble<C> having(List<IPredicate> predicateList);

        MySQLSubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function);
    }

    interface MySQLSubQueryOrderByAble<C> extends MySQLSubQueryOrderByClause<C>, MySQLSubQueryLimitAble<C> {

        @Override
        MySQLSubQueryLimitAble<C> orderBy(SortPart sortPart);

        @Override
        MySQLSubQueryLimitAble<C> orderBy(List<SortPart> sortPartList);

        @Override
        MySQLSubQueryLimitAble<C> orderBy(Function<C, List<SortPart>> function);
    }


    interface MySQLSubQueryLimitAble<C> extends MySQLSubQueryLimitClause<C>, MySQLSubQueryLockAble<C> {

        @Override
        MySQLSubQueryLockAble<C> limit(int rowCount);

        @Override
        MySQLSubQueryLockAble<C> limit(int offset, int rowCount);

        @Override
        MySQLSubQueryLockAble<C> ifLimit(Function<C, LimitOption> function);

        @Override
        MySQLSubQueryLockAble<C> ifLimit(Predicate<C> test, int rowCount);

        @Override
        MySQLSubQueryLockAble<C> ifLimit(Predicate<C> test, int offset, int rowCount);

    }

    interface MySQLSubQueryLockAble<C> extends SubQueryAble, Select.UnionClause<C> {

        Select.UnionClause<C> forUpdate();

        Select.UnionClause<C> lockInShareMode();

    }


    interface MySQLSubQueryUnionAble<C> extends SubQuery.SubQueryUnionClause<C>, MySQLSubQueryOrderByClause<C> {

    }


    interface MySQLSubQueryOrderByClause<C> extends MySQLSubQueryLimitClause<C> {

        MySQLSubQueryLimitClause<C> orderBy(SortPart sortPart);

        MySQLSubQueryLimitClause<C> orderBy(List<SortPart> sortPartList);

        MySQLSubQueryLimitClause<C> orderBy(Function<C, List<SortPart>> function);
    }

    interface MySQLSubQueryLimitClause<C> extends SubQueryAble {

        SubQueryAble limit(int rowCount);

        SubQueryAble limit(int offset, int rowCount);

        SubQueryAble ifLimit(Function<C, LimitOption> function);

        SubQueryAble ifLimit(Predicate<C> predicate, int rowCount);

        SubQueryAble ifLimit(Predicate<C> predicate, int offset, int rowCount);
    }

}
