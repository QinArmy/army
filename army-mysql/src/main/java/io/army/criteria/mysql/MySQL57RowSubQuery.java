package io.army.criteria.mysql;

import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;

public interface MySQL57RowSubQuery extends RowSubQuery {


    interface MySQLSubQuerySelectPartAble<C> extends RowSubQuerySQLAble {

        <S extends SelectPart> MySQLSubQueryFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        MySQLSubQueryFromAble<C> select(Distinct distinct, SelectionGroup selectionGroup);

        MySQLSubQueryFromAble<C> select(SelectionGroup selectionGroup);

        <S extends SelectPart> MySQLSubQueryFromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> MySQLSubQueryFromAble<C> select(List<S> selectPartList);
    }

    interface MySQLSubQueryFromAble<C> extends RowSubQueryAble {

        TableRouteJoinAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryFromAble<C> from(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface TableRouteJoinAble<C> extends MySQLSubQueryJoinAble<C> {

        MySQLSubQueryJoinAble<C> fromRoute(int databaseIndex, int tableIndex);

        MySQLSubQueryJoinAble<C> fromRoute(int tableIndex);
    }


    interface MySQLSubQueryOnAble<C> extends RowSubQuerySQLAble {

        MySQLSubQueryJoinAble<C> on(List<IPredicate> predicateList);

        MySQLSubQueryJoinAble<C> on(IPredicate predicate);

        MySQLSubQueryJoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface MySQLSubQueryJoinAble<C> extends MySQLSubQueryWhereAble<C> {

        TableRouteOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryJoinAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryJoinAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        MySQLSubQueryJoinAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface TableRouteOnAble<C> extends MySQLSubQueryOnAble<C> {

        MySQLSubQueryOnAble<C> route(int databaseIndex, int tableIndex);

        MySQLSubQueryOnAble<C> route(int tableIndex);
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

        MySQLSubQueryHavingAble<C> groupBy(SortPart sortPart);

        MySQLSubQueryHavingAble<C> groupBy(List<SortPart> sortPartList);

        MySQLSubQueryHavingAble<C> groupBy(Function<C, List<SortPart>> function);

    }

    interface MySQLSubQueryHavingAble<C> extends MySQLSubQueryOrderByAble<C> {

        MySQLSubQueryOrderByAble<C> having(IPredicate predicate);

        MySQLSubQueryOrderByAble<C> having(List<IPredicate> predicateList);

        MySQLSubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function);
    }


    interface MySQLSubQueryOrderByAble<C> extends MySQLSubQueryLimitAble<C> {

        MySQLSubQueryLimitAble<C> orderBy(SortPart sortPart);

        MySQLSubQueryLimitAble<C> orderBy(List<SortPart> sortPartList);

        MySQLSubQueryLimitAble<C> orderBy(Function<C, List<SortPart>> function);

    }


    interface MySQLSubQueryLimitAble<C> extends RowSubQueryAble {

        RowSubQueryAble limitOne();

    }

}
