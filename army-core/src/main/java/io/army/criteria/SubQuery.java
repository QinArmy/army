package io.army.criteria;


import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


public interface SubQuery extends SelfDescribed, DerivedTable, QueryAble {


    /*################################## blow interfaces ##################################*/


    interface SubQueryAble extends SubQuerySQLAble {

        SubQuery asSubQuery();

    }

    interface SubQuerySQLAble extends SQLAble {

    }


    interface SubQuerySelectPartAble<C> extends SubQuerySQLAble {

        <S extends SelectPart> SubQueryFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        SubQueryFromAble<C> select(Distinct distinct, SelectPart selectPart);

        SubQueryFromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> SubQueryFromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> SubQueryFromAble<C> select(List<S> selectPartList);
    }


    interface SubQueryFromAble<C> extends SubQueryAble {

        SubQueryOnAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        SubQueryOnAble<C> from(Function<C, SubQuery> function, String subQueryAlia);
    }


    interface SubQueryOnAble<C> extends SubQuerySQLAble {

        SubQueryJoinAble<C> on(List<IPredicate> predicateList);

        SubQueryJoinAble<C> on(IPredicate predicate);

        SubQueryJoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface SubQueryJoinAble<C> extends SubQueryWhereAble<C> {

        SubQueryOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        SubQueryOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        SubQueryOnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        SubQueryOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        SubQueryOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        SubQueryOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
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


    interface SubQueryLimitAble<C> extends SubQueryUnionAble<C> {

        SubQueryUnionAble<C> limit(int rowCount);

        SubQueryUnionAble<C> limit(int offset, int rowCount);

        SubQueryUnionAble<C> limit(Function<C, Pair<Integer, Integer>> function);

        SubQueryUnionAble<C> ifLimit(Predicate<C> predicate, int rowCount);

        SubQueryUnionAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        SubQueryUnionAble<C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }

    interface SubQueryUnionAble<C> extends SubQueryAble {

        SubQueryUnionAble<C> brackets();

        <S extends SubQuery> SubQueryUnionAble<C> union(Function<C, S> function);

        <S extends SubQuery> SubQueryUnionAble<C> unionAll(Function<C, S> function);

        <S extends SubQuery> SubQueryUnionAble<C> unionDistinct(Function<C, S> function);

    }


}
