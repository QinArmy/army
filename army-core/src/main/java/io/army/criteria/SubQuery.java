package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface SubQuery extends SelfDescribed, TableAble, Select {

    Select outerQuery();

    interface TableSubQueryAble {

        SubQuery asSubQuery();

    }

    interface SubQuerySQLAble extends SQLAble {

    }

    interface SubQuerySelectionAble<C> extends SubQuerySQLAble {

        <T extends IDomain> SubQueryFromAble<C> select(Distinct distinct, TableMeta<T> tableMeta);

        <T extends IDomain> SubQueryFromAble<C> select(TableMeta<T> tableMeta);

        SubQueryFromAble<C> select(List<Selection> selectionList);

        SubQueryFromAble<C> select(Distinct distinct, List<Selection> selectionList);

        SubQueryFromAble<C> select(Function<C, List<Selection>> function);

        SubQueryFromAble<C> select(Distinct distinct, Function<C, List<Selection>> function);
    }

    interface SubQueryFromAble<C> extends SubQuerySQLAble, TableSubQueryAble {

        <T extends IDomain> SubQueryJoinAble<C> from(TableMeta<T> tableMeta, String tableAlias);

        SubQueryJoinAble<C> from(SubQuery subQuery, String subQueryAlias);
    }


    interface SubQueryOnAble<C> extends SubQuerySQLAble {

        SubQueryJoinAble<C> on(List<IPredicate> predicateList);

        SubQueryJoinAble<C> on(IPredicate predicate);

        SubQueryJoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface SubQueryJoinAble<C> extends SubQueryWhereAble<C> {

        <T extends IDomain> SubQueryOnAble<C> leftJoin(TableMeta<T> tableMeta, String tableAlias);

        <T extends IDomain> SubQueryOnAble<C> join(TableMeta<T> tableMeta, String tableAlias);

        <T extends IDomain> SubQueryOnAble<C> rightJoin(TableMeta<T> tableMeta, String tableAlias);


        SubQueryOnAble<C> leftJoin(SubQuery subQuery, String subQueryAlias);

        SubQueryOnAble<C> join(SubQuery subQuery, String subQueryAlias);

        SubQueryOnAble<C> rightJoin(SubQuery subQuery, String subQueryAlias);
    }

    interface SubQueryWhereAble<C> extends SubQueryGroupByAble<C> {

        SubQueryGroupByAble<C> where(List<IPredicate> predicateList);

        SubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function);

        SubQueryWhereAndAble<C> where(IPredicate predicate);
    }

    interface SubQueryWhereAndAble<C> extends SubQueryGroupByAble<C> {

        SubQueryWhereAndAble<C> and(IPredicate predicate);

        SubQueryWhereAndAble<C> and(Function<C, IPredicate> function);

        SubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        SubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


    interface SubQueryGroupByAble<C> extends SubQueryOrderByAble<C> {

        SubQueryHavingAble<C> groupBy(SortExpression<?> groupExp);

        SubQueryHavingAble<C> groupBy(Function<C, List<SortExpression<?>>> function);

        SubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, SortExpression<?> groupExp);

        SubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<SortExpression<?>>> expFunction);

    }

    interface SubQueryHavingAble<C> extends SubQueryGroupByAble<C> {

        SubQueryOrderByAble<C> having(List<IPredicate> predicateList);

        SubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function);

        SubQueryOrderByAble<C> having(IPredicate predicate);

        SubQueryOrderByAble<C> ifHaving(Predicate<C> predicate, List<IPredicate> predicateList);

        SubQueryOrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        SubQueryOrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface SubQueryOrderByAble<C> extends SubQueryLimitAble<C> {

        SubQueryLimitAble<C> orderBy(SortExpression<?> groupExp);

        SubQueryLimitAble<C> orderBy(Function<C, List<SortExpression<?>>> function);

        SubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, SortExpression<?> groupExp);

        SubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<SortExpression<?>>> expFunction);
    }


    interface SubQueryLimitAble<C> extends SubQuerySQLAble, TableSubQueryAble {

        SubQuery limit(int rowCount);

        SubQuery limit(int offset, int rowCount);

        SubQuery limit(Function<C, Pair<Integer, Integer>> function);

        SubQuery ifLimit(Predicate<C> predicate, int rowCount);

        SubQuery ifLimit(Predicate<C> predicate, int offset, int rowCount);

        SubQuery ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }


}
