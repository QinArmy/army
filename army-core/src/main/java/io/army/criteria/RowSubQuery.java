package io.army.criteria;

import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;

public interface RowSubQuery extends SubQuery, SetValuePart {


    interface RowSubQueryAble extends SubQueryAble {

        RowSubQuery asSubQuery();

    }

    interface RowSubQuerySQLAble extends SubQuerySQLAble {

    }

    interface RowSubQuerySelectPartAble<C> extends RowSubQuerySQLAble {

        <S extends SelectPart> RowSubQueryFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        RowSubQueryFromAble<C> select(Distinct distinct, SelectionGroup selectionGroup);

        RowSubQueryFromAble<C> select(SelectionGroup selectionGroup);

        <S extends SelectPart> RowSubQueryFromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> RowSubQueryFromAble<C> select(List<S> selectPartList);
    }

    interface RowSubQueryFromAble<C> extends RowSubQueryAble {

        TableRouteJoinAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        RowSubQueryFromAble<C> from(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface TableRouteJoinAble<C> extends RowSubQueryJoinAble<C> {

        RowSubQueryJoinAble<C> fromRoute(int databaseIndex, int tableIndex);

        RowSubQueryJoinAble<C> fromRoute(int tableIndex);
    }


    interface RowSubQueryOnAble<C> extends RowSubQuerySQLAble {

        RowSubQueryJoinAble<C> on(List<IPredicate> predicateList);

        RowSubQueryJoinAble<C> on(IPredicate predicate);

        RowSubQueryJoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface RowSubQueryJoinAble<C> extends RowSubQueryWhereAble<C> {

        TableRouteOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        RowSubQueryJoinAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        RowSubQueryJoinAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        RowSubQueryJoinAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface TableRouteOnAble<C> extends RowSubQueryOnAble<C> {

        RowSubQueryOnAble<C> route(int databaseIndex, int tableIndex);

        RowSubQueryOnAble<C> route(int tableIndex);
    }

    interface RowSubQueryWhereAble<C> extends RowSubQueryGroupByAble<C> {

        RowSubQueryGroupByAble<C> where(List<IPredicate> predicateList);

        RowSubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function);

        RowSubQueryWhereAndAble<C> where(IPredicate predicate);
    }

    interface RowSubQueryWhereAndAble<C> extends RowSubQueryGroupByAble<C> {

        RowSubQueryWhereAndAble<C> and(@Nullable IPredicate predicate);

        RowSubQueryWhereAndAble<C> ifAnd(Function<C, IPredicate> function);

    }


    interface RowSubQueryGroupByAble<C> extends RowSubQueryOrderByAble<C> {

        RowSubQueryHavingAble<C> groupBy(SortPart sortPart);

        RowSubQueryHavingAble<C> groupBy(List<SortPart> sortPartList);

        RowSubQueryHavingAble<C> groupBy(Function<C, List<SortPart>> function);

    }

    interface RowSubQueryHavingAble<C> extends RowSubQueryOrderByAble<C> {

        RowSubQueryOrderByAble<C> having(IPredicate predicate);

        RowSubQueryOrderByAble<C> having(List<IPredicate> predicateList);

        RowSubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function);
    }


    interface RowSubQueryOrderByAble<C> extends RowSubQueryLimitAble<C> {

        RowSubQueryLimitAble<C> orderBy(SortPart sortPart);

        RowSubQueryLimitAble<C> orderBy(List<SortPart> sortPartList);

        RowSubQueryLimitAble<C> orderBy(Function<C, List<SortPart>> function);

    }


    interface RowSubQueryLimitAble<C> extends RowSubQueryAble {

        RowSubQueryAble limitOne();

    }


}
