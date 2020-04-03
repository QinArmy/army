package io.army.criteria;

import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ColumnSubQuery<E> extends SubQuery {

    Selection selection();

    interface ColumnSubQueryAble<E> extends SubQueryAble {

        ColumnSubQuery<E> asSubQuery();
    }

    interface ColumnSubQuerySQLAble extends SQLAble {

    }

    interface ColumnSubQuerySelectionAble<E, C> extends ColumnSubQuerySQLAble {

        ColumnSubQueryFromAble<E, C> select(Distinct distinct, Selection selection);

        ColumnSubQueryFromAble<E, C> select(Selection selection);

    }

    interface ColumnSubQueryFromAble<E, C> extends ColumnSubQuerySQLAble, ColumnSubQueryAble<E> {

        ColumnSubQueryJoinAble<E, C> from(TableMeta<?> tableMeta, String tableAlias);

        ColumnSubQueryJoinAble<E, C> from(Function<C, SubQuery> function, String subQueryAlia);
    }


    interface ColumnSubQueryOnAble<E, C> extends ColumnSubQuerySQLAble {

        ColumnSubQueryJoinAble<E, C> on(List<IPredicate> predicateList);

        ColumnSubQueryJoinAble<E, C> on(IPredicate predicate);

        ColumnSubQueryJoinAble<E, C> on(Function<C, List<IPredicate>> function);

    }

    interface ColumnSubQueryJoinAble<E, C> extends ColumnSubQueryWhereAble<E, C> {

        ColumnSubQueryOnAble<E, C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        ColumnSubQueryOnAble<E, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        ColumnSubQueryOnAble<E, C> join(TableMeta<?> tableMeta, String tableAlias);

        ColumnSubQueryOnAble<E, C> join(Function<C, SubQuery> function, String subQueryAlia);

        ColumnSubQueryOnAble<E, C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        ColumnSubQueryOnAble<E, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
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

        ColumnSubQueryOrderByAble<E, C> having(Function<C, List<IPredicate>> function);

        ColumnSubQueryOrderByAble<E, C> having(IPredicate predicate);

        ColumnSubQueryOrderByAble<E, C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        ColumnSubQueryOrderByAble<E, C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface ColumnSubQueryOrderByAble<E, C> extends ColumnSubQueryLimitAble<E, C> {

        ColumnSubQueryLimitAble<E, C> orderBy(Expression<?> groupExp);

        ColumnSubQueryLimitAble<E, C> orderBy(Function<C, List<Expression<?>>> function);

        ColumnSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Expression<?> groupExp);

        ColumnSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface ColumnSubQueryLimitAble<E, C> extends ColumnSubQueryUnionAble<E, C> {

        ColumnSubQueryUnionAble<E, C> limit(int rowCount);

        ColumnSubQueryUnionAble<E, C> limit(int offset, int rowCount);

        ColumnSubQueryUnionAble<E, C> limit(Function<C, Pair<Integer, Integer>> function);

        ColumnSubQueryUnionAble<E, C> ifLimit(Predicate<C> predicate, int rowCount);

        ColumnSubQueryUnionAble<E, C> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        ColumnSubQueryUnionAble<E, C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }

    interface ColumnSubQueryUnionAble<E, C> extends ColumnSubQueryAble<E> {

        ColumnSubQueryUnionAble<E, C> brackets();

        <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> union(Function<C, S> function);

        <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> unionAll(Function<C, S> function);

        <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> unionDistinct(Function<C, S> function);

    }


}
