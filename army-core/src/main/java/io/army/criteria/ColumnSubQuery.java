package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ColumnSubQuery<E> extends SubQuery {

    Selection selection();

    interface ColumnSubQueryAble<E> {

        ColumnSubQuery<E> asColumnSubQuery();
    }

    interface ColumnSubQuerySQLAble extends SQLAble {

    }

    interface ColumnSubQuerySelectionAble<E, C> extends ColumnSubQuerySQLAble {

        <T extends IDomain> ColumnSubQueryFromAble<E, C> select(Distinct distinct, TableMeta<T> tableMeta);

        <T extends IDomain> ColumnSubQueryFromAble<E, C> select(TableMeta<T> tableMeta);

        ColumnSubQueryFromAble<E, C> select(String subQueryAlias);

        ColumnSubQueryFromAble<E, C> select(Distinct distinct, String subQueryAlias);

        ColumnSubQueryFromAble<E, C> select(List<Selection> selectionList);

        ColumnSubQueryFromAble<E, C> select(Distinct distinct, List<Selection> selectionList);

        ColumnSubQueryFromAble<E, C> select(Function<C, List<Selection>> function);

        ColumnSubQueryFromAble<E, C> select(Distinct distinct, Function<C, List<Selection>> function);
    }

    interface ColumnSubQueryFromAble<E, C> extends ColumnSubQuerySQLAble, ColumnSubQueryAble<E> {

        ColumnSubQueryOnAble<E, C> from(TableAble tableAble, String tableAlias);
    }


    interface ColumnSubQueryOnAble<E, C> extends ColumnSubQuerySQLAble {

        ColumnSubQueryJoinAble<E, C> on(List<IPredicate> predicateList);

        ColumnSubQueryJoinAble<E, C> on(IPredicate predicate);

        ColumnSubQueryJoinAble<E, C> on(Function<C, List<IPredicate>> function);

    }

    interface ColumnSubQueryJoinAble<E, C> extends ColumnSubQueryWhereAble<E, C> {

        ColumnSubQueryOnAble<E, C> leftJoin(TableAble tableAble, String tableAlias);

        ColumnSubQueryOnAble<E, C> join(TableAble tableAble, String tableAlias);

        ColumnSubQueryOnAble<E, C> rightJoin(TableAble tableAble, String tableAlias);
    }

    interface ColumnSubQueryWhereAble<E, C> extends ColumnSubQueryGroupByAble<E, C> {

        ColumnSubQueryGroupByAble<E, C> where(List<IPredicate> predicateList);

        ColumnSubQueryGroupByAble<E, C> where(Function<C, List<IPredicate>> function);

        ColumnSubQueryWhereAndAble<E, C> where(IPredicate predicate);
    }

    interface ColumnSubQueryWhereAndAble<E, C> extends ColumnSubQueryGroupByAble<E, C> {

        ColumnSubQueryWhereAndAble<E, C> and(IPredicate predicate);

        ColumnSubQueryWhereAndAble<E, C> and(Function<C, IPredicate> function);

        ColumnSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        ColumnSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


    interface ColumnSubQueryGroupByAble<E, C> extends ColumnSubQueryOrderByAble<E, C> {

        ColumnSubQueryHavingAble<E, C> groupBy(Expression<?> groupExp);

        ColumnSubQueryHavingAble<E, C> groupBy(Function<C, List<Expression<?>>> function);

        ColumnSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);

        ColumnSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);

    }

    interface ColumnSubQueryHavingAble<E, C> extends ColumnSubQueryOrderByAble<E, C> {

        ColumnSubQueryOrderByAble<E, C> having(List<IPredicate> predicateList);

        ColumnSubQueryOrderByAble<E, C> having(Function<C, List<IPredicate>> function);

        ColumnSubQueryOrderByAble<E, C> having(IPredicate predicate);

        ColumnSubQueryOrderByAble<E, C> ifHaving(Predicate<C> predicate, List<IPredicate> predicateList);

        ColumnSubQueryOrderByAble<E, C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        ColumnSubQueryOrderByAble<E, C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface ColumnSubQueryOrderByAble<E, C> extends ColumnSubQueryLimitAble<E, C> {

        ColumnSubQueryLimitAble<E, C> orderBy(Expression<?> groupExp);

        ColumnSubQueryLimitAble<E, C> orderBy(Function<C, List<Expression<?>>> function);

        ColumnSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp);

        ColumnSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface ColumnSubQueryLimitAble<E, C> extends ColumnSubQuerySQLAble, ColumnSubQueryAble<E> {

        ColumnSubQuery<E> limit(int rowCount);

        ColumnSubQuery<E> limit(int offset, int rowCount);

        ColumnSubQuery<E> limit(Function<C, Pair<Integer, Integer>> function);

        ColumnSubQuery<E> ifLimit(Predicate<C> predicate, int rowCount);

        ColumnSubQuery<E> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        ColumnSubQuery<E> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }
}