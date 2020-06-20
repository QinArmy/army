package io.army.criteria;

import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface ColumnSubQuery<E> extends SubQuery {

    Selection selection();

    /*################################## blow  interfaces  ##################################*/

    interface ColumnSubQuerySQLAble extends SubQuerySQLAble {

    }

    interface ColumnSubQueryAble<E> extends ColumnSubQuerySQLAble {

        ColumnSubQuery<E> asSubQuery();
    }


    interface ColumnSubQuerySelectionAble<E, C> extends ColumnSubQuerySQLAble {

        ColumnSubQueryFromAble<E, C> select(Distinct distinct, Selection selection);

        ColumnSubQueryFromAble<E, C> select(Selection selection);

    }

    interface ColumnSubQueryFromAble<E, C> extends ColumnSubQueryUnionClause<E, C> {

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

        ColumnSubQueryWhereAndAble<E, C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


    interface ColumnSubQueryGroupByAble<E, C> extends ColumnSubQueryOrderByAble<E, C> {

        ColumnSubQueryHavingAble<E, C> groupBy(SortPart sortPart);

        ColumnSubQueryHavingAble<E, C> groupBy(List<SortPart> sortPartList);

        ColumnSubQueryHavingAble<E, C> groupBy(Function<C, List<SortPart>> function);

        ColumnSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, SortPart sortPart);

        ColumnSubQueryHavingAble<E, C> ifGroupBy(Predicate<C> predicate, Function<C, List<SortPart>> function);

    }

    interface ColumnSubQueryHavingAble<E, C> extends ColumnSubQueryOrderByAble<E, C> {

        ColumnSubQueryOrderByAble<E, C> having(Function<C, List<IPredicate>> function);

        ColumnSubQueryOrderByAble<E, C> having(IPredicate predicate);

        ColumnSubQueryOrderByAble<E, C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        ColumnSubQueryOrderByAble<E, C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface ColumnSubQueryOrderByAble<E, C> extends ColumnSubQueryOrderByClause<E, C>, ColumnSubQueryLimitAble<E, C> {

        @Override
        ColumnSubQueryLimitAble<E, C> orderBy(SortPart sortPart);

        @Override
        ColumnSubQueryLimitAble<E, C> orderBy(List<SortPart> sortPartList);

        @Override
        ColumnSubQueryLimitAble<E, C> orderBy(Function<C, List<SortPart>> function);

        @Override
        ColumnSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> test, SortPart sortPart);

        @Override
        ColumnSubQueryLimitAble<E, C> ifOrderBy(Predicate<C> test, Function<C, List<SortPart>> function);
    }


    interface ColumnSubQueryLimitAble<E, C> extends ColumnSubQueryOrderByClause<E, C>, ColumnSubQueryUnionClause<E, C> {

        @Override
        ColumnSubQueryUnionClause<E, C> limit(int rowCount);

        @Override
        ColumnSubQueryUnionClause<E, C> limit(int offset, int rowCount);

        @Override
        ColumnSubQueryUnionClause<E, C> limit(Function<C, Pair<Integer, Integer>> function);

        @Override
        ColumnSubQueryUnionClause<E, C> ifLimit(Predicate<C> predicate, int rowCount);

        @Override
        ColumnSubQueryUnionClause<E, C> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        @Override
        ColumnSubQueryUnionClause<E, C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }

    interface ColumnSubQueryUnionAble<E, C> extends ColumnSubQueryUnionClause<E, C>, ColumnSubQueryOrderByClause<E, C> {

    }

    interface ColumnSubQueryUnionClause<E, C> extends ColumnSubQueryAble<E> {

        ColumnSubQueryUnionAble<E, C> brackets();

        <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> union(Function<C, S> function);

        <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> unionAll(Function<C, S> function);

        <S extends ColumnSubQuery<E>> ColumnSubQueryUnionAble<E, C> unionDistinct(Function<C, S> function);

    }

    interface ColumnSubQueryOrderByClause<E, C> extends ColumnSubQueryLimitClause<E, C> {

        ColumnSubQueryLimitClause<E, C> orderBy(SortPart sortPart);

        ColumnSubQueryLimitClause<E, C> orderBy(List<SortPart> sortPartList);

        ColumnSubQueryLimitClause<E, C> orderBy(Function<C, List<SortPart>> function);

        ColumnSubQueryLimitClause<E, C> ifOrderBy(Predicate<C> test, SortPart sortPart);

        ColumnSubQueryLimitClause<E, C> ifOrderBy(Predicate<C> test, Function<C, List<SortPart>> function);
    }

    interface ColumnSubQueryLimitClause<E, C> extends ColumnSubQueryAble<E> {

        ColumnSubQueryAble<E> limit(int rowCount);

        ColumnSubQueryAble<E> limit(int offset, int rowCount);

        ColumnSubQueryAble<E> limit(Function<C, Pair<Integer, Integer>> function);

        ColumnSubQueryAble<E> ifLimit(Predicate<C> predicate, int rowCount);

        ColumnSubQueryAble<E> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        ColumnSubQueryAble<E> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }


}
