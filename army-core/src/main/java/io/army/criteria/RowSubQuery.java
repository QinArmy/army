package io.army.criteria;

import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface RowSubQuery extends SubQuery {

    interface RowSubQueryAble extends RowSubQuerySQLAble {

        RowSubQuery asRowSubQuery();

    }

    interface RowSubQuerySQLAble extends SQLAble {

    }

    interface RowSubQuerySelectPartAble<C> extends RowSubQuerySQLAble {

        RowSubQueryFromAble<C> select(Distinct distinct, String tableAlias, TableMeta<?> tableMeta);

        RowSubQueryFromAble<C> select(String tableAlias, TableMeta<?> tableMeta);

        RowSubQueryFromAble<C> select(Distinct distinct, String subQueryAlias);

        RowSubQueryFromAble<C> select(String subQueryAlias);

        RowSubQueryFromAble<C> select(List<SelectPart> selectPartList);

        RowSubQueryFromAble<C> select(Distinct distinct, List<SelectPart> selectPartList);

        RowSubQueryFromAble<C> select(Function<C, List<SelectPart>> function);

        RowSubQueryFromAble<C> select(Distinct distinct, Function<C, List<SelectPart>> function);
    }

    interface RowSubQueryFromAble<C> extends RowSubQueryAble {

        RowSubQueryJoinAble<C> from(TableAble tableAble, String tableAlias);
    }


    interface RowSubQueryOnAble<C> extends RowSubQuerySQLAble {

        RowSubQueryJoinAble<C> on(List<IPredicate> predicateList);

        RowSubQueryJoinAble<C> on(IPredicate predicate);

        RowSubQueryJoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface RowSubQueryJoinAble<C> extends RowSubQueryWhereAble<C> {

        RowSubQueryOnAble<C> leftJoin(TableAble tableAble, String tableAlias);

        RowSubQueryOnAble<C> join(TableAble tableAble, String tableAlias);

        RowSubQueryOnAble<C> rightJoin(TableAble tableAble, String tableAlias);
    }

    interface RowSubQueryWhereAble<C> extends RowSubQueryGroupByAble<C> {

        RowSubQueryGroupByAble<C> where(List<IPredicate> predicateList);

        RowSubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function);

        RowSubQueryWhereAndAble<C> where(IPredicate predicate);
    }

    interface RowSubQueryWhereAndAble<C> extends RowSubQueryGroupByAble<C> {

        RowSubQueryWhereAndAble<C> and(IPredicate predicate);

        RowSubQueryWhereAndAble<C> and(Function<C, IPredicate> function);

        RowSubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        RowSubQueryWhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


    interface RowSubQueryGroupByAble<C> extends RowSubQueryOrderByAble<C> {

        RowSubQueryHavingAble<C> groupBy(Expression<?> groupExp);

        RowSubQueryHavingAble<C> groupBy(Function<C, List<Expression<?>>> function);

        RowSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);

        RowSubQueryHavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);

    }

    interface RowSubQueryHavingAble<C> extends RowSubQueryOrderByAble<C> {

        RowSubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function);

        RowSubQueryOrderByAble<C> having(IPredicate predicate);

        RowSubQueryOrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        RowSubQueryOrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface RowSubQueryOrderByAble<C> extends RowSubQueryLimitAble<C> {

        RowSubQueryLimitAble<C> orderBy(Expression<?> groupExp);

        RowSubQueryLimitAble<C> orderBy(Function<C, List<Expression<?>>> function);

        RowSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp);

        RowSubQueryLimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface RowSubQueryLimitAble<C> extends RowSubQuerySQLAble, RowSubQueryAble {

        RowSubQuery limit(int rowCount);

        RowSubQuery limit(int offset, int rowCount);

        RowSubQuery limit(Function<C, Pair<Integer, Integer>> function);

        RowSubQuery ifLimit(Predicate<C> predicate, int rowCount);

        RowSubQuery ifLimit(Predicate<C> predicate, int offset, int rowCount);

        RowSubQuery ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }

}
