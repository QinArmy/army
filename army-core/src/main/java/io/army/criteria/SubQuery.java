package io.army.criteria;


import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


public interface SubQuery extends SelfDescribed, TableAble, QueryAble {

    List<SelectPart> selectPartList();

    @Nullable
    SubQuery subordinateSubQuery(String subordinateSubQueryAlias);

    Selection selection(String derivedFieldName);

    QueryAble outerQuery();


    interface SubQueryAble extends SubQuerySQLAble {

        SubQuery asSubQuery();

    }

    interface SubQuerySQLAble extends SQLAble {

    }


    interface SubQuerySelectPartAble<C> extends SubQuerySQLAble {

        SubQueryFromAble<C> select(Distinct distinct, String tableAlias, TableMeta<?> tableMeta);

        SubQueryFromAble<C> select(String tableAlias, TableMeta<?> tableMeta);

        SubQueryFromAble<C> select(Distinct distinct, String subQueryAlias);

        SubQueryFromAble<C> select(String subQueryAlias);

        SubQueryFromAble<C> select(List<SelectPart> selectPartList);

        SubQueryFromAble<C> select(Distinct distinct, List<SelectPart> selectPartList);

        SubQueryFromAble<C> select(Function<C, List<SelectPart>> function);

        SubQueryFromAble<C> select(Distinct distinct, Function<C, List<SelectPart>> function);
    }


    interface SubQueryFromAble<C> extends SubQueryAble {

        SubQueryOnAble<C> from(TableAble tableAble, String tableAlias);
    }


    interface SubQueryOnAble<C> extends SubQuerySQLAble {

        SubQueryJoinAble<C> on(List<IPredicate> predicateList);

        SubQueryJoinAble<C> on(IPredicate predicate);

        SubQueryJoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface SubQueryJoinAble<C> extends SubQueryWhereAble<C> {

        SubQueryOnAble<C> leftJoin(TableAble tableAble, String tableAlias);

        SubQueryOnAble<C> join(TableAble tableAble, String tableAlias);

        SubQueryOnAble<C> rightJoin(TableAble tableAble, String tableAlias);
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

        SubQueryHavingAble<C> groupBy(Expression<?> groupExp);

        SubQueryHavingAble<C> groupBy(Function<C, List<Expression<?>>> function);

        SubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);

        SubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);

    }

    interface SubQueryHavingAble<C> extends SubQueryOrderByAble<C> {

        SubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function);

        SubQueryOrderByAble<C> having(IPredicate predicate);

        SubQueryOrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        SubQueryOrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface SubQueryOrderByAble<C> extends SubQueryLimitAble<C> {

        SubQueryLimitAble<C> orderBy(Expression<?> groupExp);

        SubQueryLimitAble<C> orderBy(Function<C, List<Expression<?>>> function);

        SubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp);

        SubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface SubQueryLimitAble<C> extends SubQuerySQLAble, SubQueryAble {

        SubQuery limit(int rowCount);

        SubQuery limit(int offset, int rowCount);

        SubQuery limit(Function<C, Pair<Integer, Integer>> function);

        SubQuery ifLimit(Predicate<C> predicate, int rowCount);

        SubQuery ifLimit(Predicate<C> predicate, int offset, int rowCount);

        SubQuery ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }


}
