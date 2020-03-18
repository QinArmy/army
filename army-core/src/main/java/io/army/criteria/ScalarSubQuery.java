package io.army.criteria;

import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ScalarSubQuery<E> extends ColumnSubQuery<E>, RowSubQuery, Expression<E> {


    interface ScalarSubQueryAble<E> extends ScalarSubQuerySQLAble {

        ScalarSubQuery<E> asScalarSubQuery();
    }

    interface ScalarSubQuerySQLAble extends SQLAble {

    }

    interface ScalarSubQuerySelectionAble<E, C> extends ScalarSubQuerySQLAble {

        ScalarSubQueryFromAble<E, C> select(Distinct distinct, String tableAlias, TableMeta<?> tableMeta);

        ScalarSubQueryFromAble<E, C> select(String tableAlias, TableMeta<?> tableMeta);

        ScalarSubQueryFromAble<E, C> select(String subQueryAlias);

        ScalarSubQueryFromAble<E, C> select(Distinct distinct, String subQueryAlias);

        ScalarSubQueryFromAble<E, C> select(List<Selection> selectionList);

        ScalarSubQueryFromAble<E, C> select(Distinct distinct, List<Selection> selectionList);

        ScalarSubQueryFromAble<E, C> select(Function<C, List<Selection>> function);

        ScalarSubQueryFromAble<E, C> select(Distinct distinct, Function<C, List<Selection>> function);

        ScalarSubQueryFromAble<E, C> select(Function<C, List<SelectionGroup>> function, boolean group);

        ScalarSubQueryFromAble<E, C> select(Distinct distinct, Function<C, List<SelectionGroup>> function, boolean group);

    }

    interface ScalarSubQueryFromAble<E, C> extends ScalarSubQuerySQLAble, ScalarSubQueryAble<E> {

        ScalarSubQueryOnAble<E, C> from(TableAble tableAble, String tableAlias);
    }


    interface ScalarSubQueryOnAble<E, C> extends ScalarSubQuerySQLAble {

        ScalarSubQueryJoinAble<E, C> on(List<IPredicate> predicateList);

        ScalarSubQueryJoinAble<E, C> on(IPredicate predicate);

        ScalarSubQueryJoinAble<E, C> on(Function<C, List<IPredicate>> function);

    }

    interface ScalarSubQueryJoinAble<E, C> extends ScalarSubQueryWhereAble<E, C> {

        ScalarSubQueryOnAble<E, C> leftJoin(TableAble tableAble, String tableAlias);

        ScalarSubQueryOnAble<E, C> join(TableAble tableAble, String tableAlias);

        ScalarSubQueryOnAble<E, C> rightJoin(TableAble tableAble, String tableAlias);
    }

    interface ScalarSubQueryWhereAble<E, C> extends ScalarSubQueryGroupByAble<E, C> {

        ScalarSubQueryGroupByAble<E, C> where(List<IPredicate> predicateList);

        ScalarSubQueryGroupByAble<E, C> where(Function<C, List<IPredicate>> function);

        ScalarSubQueryWhereAndAble<E, C> where(IPredicate predicate);
    }

    interface ScalarSubQueryWhereAndAble<E, C> extends ScalarSubQueryGroupByAble<E, C> {

        ScalarSubQueryWhereAndAble<E, C> and(IPredicate predicate);

        ScalarSubQueryWhereAndAble<E, C> and(Function<C, IPredicate> function);

        ScalarSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        ScalarSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


    interface ScalarSubQueryGroupByAble<E, C> extends ScalarSubQueryOrderByAble<E, C> {

        ScalarSubQueryHavingAble<E, C> groupBy(Expression<?> groupExp);

        ScalarSubQueryHavingAble<E, C> groupBy(Function<C, List<Expression<?>>> function);

        ScalarSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);

        ScalarSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);

    }

    interface ScalarSubQueryHavingAble<E, C> extends ScalarSubQueryOrderByAble<E, C> {

        ScalarSubQueryOrderByAble<E, C> having(List<IPredicate> predicateList);

        ScalarSubQueryOrderByAble<E, C> having(Function<C, List<IPredicate>> function);

        ScalarSubQueryOrderByAble<E, C> having(IPredicate predicate);

        ScalarSubQueryOrderByAble<E, C> ifHaving(Predicate<C> predicate, List<IPredicate> predicateList);

        ScalarSubQueryOrderByAble<E, C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        ScalarSubQueryOrderByAble<E, C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface ScalarSubQueryOrderByAble<E, C> extends ScalarSubQueryLimitAble<E, C> {

        ScalarSubQueryLimitAble<E, C> orderBy(Expression<?> groupExp);

        ScalarSubQueryLimitAble<E, C> orderBy(Function<C, List<Expression<?>>> function);

        ScalarSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp);

        ScalarSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface ScalarSubQueryLimitAble<E, C> extends ScalarSubQuerySQLAble, ScalarSubQueryAble<E> {

        ScalarSubQuery<E> limit(int rowCount);

        ScalarSubQuery<E> limit(int offset, int rowCount);

        ScalarSubQuery<E> limit(Function<C, Pair<Integer, Integer>> function);

        ScalarSubQuery<E> ifLimit(Predicate<C> predicate, int rowCount);

        ScalarSubQuery<E> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        ScalarSubQuery<E> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }

}
