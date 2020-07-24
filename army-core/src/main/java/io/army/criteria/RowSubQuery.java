package io.army.criteria;

import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface RowSubQuery extends SubQuery, SetValuePart {


    interface RowSubQueryAble extends SubQueryAble {

        RowSubQuery asSubQuery();

    }

    interface RowSubQuerySQLAble extends SubQuerySQLAble {

    }

    interface RowSubQuerySelectPartAble<C> extends RowSubQuerySQLAble {

        <S extends SelectPart> RowSubQueryFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        RowSubQueryFromAble<C> select(Distinct distinct, SelectPart selectPart);

        RowSubQueryFromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> RowSubQueryFromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> RowSubQueryFromAble<C> select(List<S> selectPartList);
    }

    interface RowSubQueryFromAble<C> extends RowSubQueryAble {

        RowSubQueryFromAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        RowSubQueryFromAble<C> from(Function<C, SubQuery> function, String subQueryAlia);
    }


    interface RowSubQueryOnAble<C> extends RowSubQuerySQLAble {

        RowSubQueryJoinAble<C> on(List<IPredicate> predicateList);

        RowSubQueryJoinAble<C> on(IPredicate predicate);

        RowSubQueryJoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface RowSubQueryJoinAble<C> extends RowSubQueryWhereAble<C> {

        RowSubQueryJoinAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        RowSubQueryJoinAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        RowSubQueryJoinAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        RowSubQueryJoinAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        RowSubQueryJoinAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        RowSubQueryJoinAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface RowSubQueryWhereAble<C> extends RowSubQueryGroupByAble<C> {

        RowSubQueryGroupByAble<C> where(List<IPredicate> predicateList);

        RowSubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function);

        RowSubQueryWhereAndAble<C> where(IPredicate predicate);
    }

    interface RowSubQueryWhereAndAble<C> extends RowSubQueryGroupByAble<C> {

        RowSubQueryWhereAndAble<C> and(IPredicate predicate);

        RowSubQueryWhereAndAble<C> and(Function<C, IPredicate> function);

        RowSubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


    interface RowSubQueryGroupByAble<C> extends RowSubQueryOrderByAble<C> {

        RowSubQueryHavingAble<C> groupBy(SortPart sortPart);

        RowSubQueryHavingAble<C> groupBy(List<SortPart> sortPartList);

        RowSubQueryHavingAble<C> groupBy(Function<C, List<SortPart>> function);

        RowSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, SortPart sortPart);

        RowSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<SortPart>> expFunction);

    }

    interface RowSubQueryHavingAble<C> extends RowSubQueryOrderByAble<C> {

        RowSubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function);

        RowSubQueryOrderByAble<C> having(IPredicate predicate);

        RowSubQueryOrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        RowSubQueryOrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface RowSubQueryOrderByAble<C> extends RowSubQueryLimitAble<C> {

        RowSubQueryLimitAble<C> orderBy(SortPart sortPart);

        RowSubQueryLimitAble<C> orderBy(List<SortPart> sortPartList);

        RowSubQueryLimitAble<C> orderBy(Function<C, List<SortPart>> function);

        RowSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, SortPart sortPart);

        RowSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<SortPart>> expFunction);
    }


    interface RowSubQueryLimitAble<C> extends RowSubQueryAble {

        RowSubQueryAble limitOne();

    }


}
