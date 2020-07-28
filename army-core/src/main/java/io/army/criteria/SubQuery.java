package io.army.criteria;


import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;


public interface SubQuery extends SQLStatement, SelfDescribed, DerivedTable, QueryAble {



    /*################################## blow interfaces ##################################*/

    interface SubQuerySQLAble extends SQLAble {

    }

    interface SubQueryAble extends SubQuerySQLAble {

        SubQuery asSubQuery();

    }

    interface SubQuerySelectPartAble<C> extends SubQuerySQLAble {

        <S extends SelectPart> SubQueryFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        SubQueryFromAble<C> select(Distinct distinct, SelectPart selectPart);

        SubQueryFromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> SubQueryFromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> SubQueryFromAble<C> select(List<S> selectPartList);
    }


    interface SubQueryFromAble<C> extends SubQueryUnionClause<C> {

        TableRouteJoinAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        SubQueryJoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface TableRouteJoinAble<C> extends SubQueryJoinAble<C> {

        SubQueryJoinAble<C> fromRoute(int databaseIndex, int tableIndex);

        SubQueryJoinAble<C> fromRoute(int tableIndex);
    }

    interface SubQueryOnAble<C> extends SubQuerySQLAble {

        SubQueryJoinAble<C> on(List<IPredicate> predicateList);

        SubQueryJoinAble<C> on(IPredicate predicate);

        SubQueryJoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface SubQueryJoinAble<C> extends SubQueryWhereAble<C> {

        TableRouteOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        SubQueryOnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        SubQueryOnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        SubQueryOnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface TableRouteOnAble<C> extends SubQueryOnAble<C> {

        SubQueryOnAble<C> route(int databaseIndex, int tableIndex);

        SubQueryOnAble<C> route(int tableIndex);
    }


    interface SubQueryWhereAble<C> extends SubQueryGroupByAble<C> {

        SubQueryGroupByAble<C> where(List<IPredicate> predicateList);

        SubQueryGroupByAble<C> where(Function<C, List<IPredicate>> function);

        SubQueryWhereAndAble<C> where(IPredicate predicate);
    }

    interface SubQueryWhereAndAble<C> extends SubQueryGroupByAble<C> {

        SubQueryWhereAndAble<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        SubQueryWhereAndAble<C> ifAnd(@Nullable IPredicate predicate);

        SubQueryWhereAndAble<C> ifAnd(Function<C, IPredicate> function);

    }


    interface SubQueryGroupByAble<C> extends SubQueryOrderByAble<C> {

        SubQueryHavingAble<C> groupBy(SortPart sortPart);

        SubQueryHavingAble<C> groupBy(List<SortPart> sortPartList);

        SubQueryHavingAble<C> groupBy(Function<C, List<SortPart>> function);

    }

    interface SubQueryHavingAble<C> extends SubQueryOrderByAble<C> {

        SubQueryOrderByAble<C> having(IPredicate predicate);

        SubQueryOrderByAble<C> having(List<IPredicate> predicateList);

        SubQueryOrderByAble<C> having(Function<C, List<IPredicate>> function);

    }


    interface SubQueryOrderByAble<C> extends SubQueryOrderByClause<C>, SubQueryLimitAble<C> {

        SubQueryLimitAble<C> orderBy(SortPart sortPart);

        SubQueryLimitAble<C> orderBy(List<SortPart> sortPartList);

        SubQueryLimitAble<C> orderBy(Function<C, List<SortPart>> function);

    }


    interface SubQueryLimitAble<C> extends SubQueryLimitClause<C>, SubQueryUnionClause<C> {

        SubQueryUnionClause<C> limit(int rowCount);

        SubQueryUnionClause<C> limit(int offset, int rowCount);

        SubQueryUnionClause<C> ifLimit(Function<C, Pair<Integer, Integer>> function);

        SubQueryUnionClause<C> ifLimit(Predicate<C> predicate, int rowCount);

        SubQueryUnionClause<C> ifLimit(Predicate<C> predicate, int offset, int rowCount);

    }

    interface SubQueryUnionAble<C> extends SubQueryUnionClause<C>, SubQueryOrderByClause<C> {

    }

    interface SubQueryUnionClause<C> extends SubQueryAble {

        SubQueryUnionAble<C> brackets();

        <S extends SubQuery> SubQueryUnionAble<C> union(Function<C, S> function);

        <S extends SubQuery> SubQueryUnionAble<C> unionAll(Function<C, S> function);

        <S extends SubQuery> SubQueryUnionAble<C> unionDistinct(Function<C, S> function);

    }

    interface SubQueryOrderByClause<C> extends SubQueryLimitClause<C> {

        SubQueryLimitClause<C> orderBy(SortPart sortPart);

        SubQueryLimitClause<C> orderBy(List<SortPart> sortPartList);

        SubQueryLimitClause<C> orderBy(Function<C, List<SortPart>> function);

    }

    interface SubQueryLimitClause<C> extends SubQueryAble {

        SubQueryAble limit(int rowCount);

        SubQueryAble limit(int offset, int rowCount);

        SubQueryAble ifLimit(Function<C, Pair<Integer, Integer>> function);

        SubQueryAble ifLimit(Predicate<C> predicate, int rowCount);

        SubQueryAble ifLimit(Predicate<C> predicate, int offset, int rowCount);
    }


}
