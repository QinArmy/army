package io.army.criteria;

import io.army.lang.Nullable;
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

        TableRouteJoinAble<E, C> from(TableMeta<?> tableMeta, String tableAlias);

        ColumnSubQueryJoinAble<E, C> from(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface TableRouteJoinAble<E, C> extends ColumnSubQueryJoinAble<E, C> {

        ColumnSubQueryJoinAble<E, C> fromRoute(int databaseIndex, int tableIndex);

        ColumnSubQueryJoinAble<E, C> fromRoute(int tableIndex);
    }


    interface ColumnSubQueryOnAble<E, C> extends ColumnSubQuerySQLAble {

        ColumnSubQueryJoinAble<E, C> on(List<IPredicate> predicateList);

        ColumnSubQueryJoinAble<E, C> on(IPredicate predicate);

        ColumnSubQueryJoinAble<E, C> on(Function<C, List<IPredicate>> function);

    }

    interface ColumnSubQueryJoinAble<E, C> extends ColumnSubQueryWhereAble<E, C> {

        TableRouteOnAble<E, C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        ColumnSubQueryOnAble<E, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<E, C> join(TableMeta<?> tableMeta, String tableAlias);

        ColumnSubQueryOnAble<E, C> join(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<E, C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        ColumnSubQueryOnAble<E, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface TableRouteOnAble<E, C> extends ColumnSubQueryOnAble<E, C> {

        ColumnSubQueryOnAble<E, C> route(int databaseIndex, int tableIndex);

        ColumnSubQueryOnAble<E, C> route(int tableIndex);
    }


    interface ColumnSubQueryWhereAble<E, C> extends ColumnSubQueryGroupByAble<E, C> {

        ColumnSubQueryGroupByAble<E, C> where(List<IPredicate> predicateList);

        ColumnSubQueryGroupByAble<E, C> where(Function<C, List<IPredicate>> function);

        ColumnSubQueryWhereAndAble<E, C> where(IPredicate predicate);
    }

    interface ColumnSubQueryWhereAndAble<E, C> extends ColumnSubQueryGroupByAble<E, C> {

        ColumnSubQueryWhereAndAble<E, C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        ColumnSubQueryWhereAndAble<E, C> ifAnd(@Nullable IPredicate predicate);

        ColumnSubQueryWhereAndAble<E, C> ifAnd(Function<C, IPredicate> function);

    }


    interface ColumnSubQueryGroupByAble<E, C> extends ColumnSubQueryOrderByAble<E, C> {

        ColumnSubQueryHavingAble<E, C> groupBy(SortPart sortPart);

        ColumnSubQueryHavingAble<E, C> groupBy(List<SortPart> sortPartList);

        ColumnSubQueryHavingAble<E, C> groupBy(Function<C, List<SortPart>> function);

    }

    interface ColumnSubQueryHavingAble<E, C> extends ColumnSubQueryOrderByAble<E, C> {

        ColumnSubQueryOrderByAble<E, C> having(Function<C, List<IPredicate>> function);

        ColumnSubQueryOrderByAble<E, C> having(IPredicate predicate);

        ColumnSubQueryOrderByAble<E, C> having(List<IPredicate> predicateList);
    }


    interface ColumnSubQueryOrderByAble<E, C> extends ColumnSubQueryOrderByClause<E, C>, ColumnSubQueryLimitAble<E, C> {

        @Override
        ColumnSubQueryLimitAble<E, C> orderBy(SortPart sortPart);

        @Override
        ColumnSubQueryLimitAble<E, C> orderBy(List<SortPart> sortPartList);

        @Override
        ColumnSubQueryLimitAble<E, C> orderBy(Function<C, List<SortPart>> function);
    }


    interface ColumnSubQueryLimitAble<E, C> extends ColumnSubQueryOrderByClause<E, C>, ColumnSubQueryUnionClause<E, C> {

        @Override
        ColumnSubQueryUnionClause<E, C> limit(int rowCount);

        @Override
        ColumnSubQueryUnionClause<E, C> limit(int offset, int rowCount);

        @Override
        ColumnSubQueryUnionClause<E, C> ifLimit(Function<C, Pair<Integer, Integer>> function);

        @Override
        ColumnSubQueryUnionClause<E, C> ifLimit(Predicate<C> predicate, int rowCount);

        @Override
        ColumnSubQueryUnionClause<E, C> ifLimit(Predicate<C> predicate, int offset, int rowCount);

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

    }

    interface ColumnSubQueryLimitClause<E, C> extends ColumnSubQueryAble<E> {

        ColumnSubQueryAble<E> limit(int rowCount);

        ColumnSubQueryAble<E> limit(int offset, int rowCount);

        ColumnSubQueryAble<E> ifLimit(Function<C, Pair<Integer, Integer>> function);

        ColumnSubQueryAble<E> ifLimit(Predicate<C> predicate, int rowCount);

        ColumnSubQueryAble<E> ifLimit(Predicate<C> predicate, int offset, int rowCount);
    }


}
