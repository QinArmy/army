package io.army.criteria;


import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @see Select
 * @see SubQuery
 * @see Update
 */
public interface Query extends SQLStatement {

    boolean requiredBrackets();

    interface QuerySQLSpec {

    }

    interface QuerySpec<Q extends Query> extends QuerySQLSpec {

        Q asQuery();
    }


    /*################################## blow select clause  interfaces ##################################*/


    interface SelectPartSpec<Q extends Query, C> extends QuerySQLSpec {

        <S extends SelectPart> FromSpec<Q, C> select(Distinct distinct, Function<C, List<S>> function);

        <S extends SelectPart> FromSpec<Q, C> select(Function<C, List<S>> function);

        FromSpec<Q, C> select(Distinct distinct, SelectPart selectPart);

        FromSpec<Q, C> select(SelectPart selectPart);

        <S extends SelectPart> FromSpec<Q, C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> FromSpec<Q, C> select(List<S> selectPartList);
    }


    interface FromSpec<Q extends Query, C> extends UnionClause<Q, C> {

        TableRouteJoinSpec<Q, C> from(TableMeta<?> tableMeta, String tableAlias);

        JoinSpec<Q, C> from(Function<C, SubQuery> function, String subQueryAlia);

    }

    interface TableRouteJoinSpec<Q extends Query, C> extends JoinSpec<Q, C> {

        JoinSpec<Q, C> route(int databaseIndex, int tableIndex);

        JoinSpec<Q, C> route(int tableIndex);
    }


    interface OnSpec<Q extends Query, C> extends QuerySQLSpec {

        JoinSpec<Q, C> on(List<IPredicate> predicateList);

        JoinSpec<Q, C> on(IPredicate predicate);

        JoinSpec<Q, C> on(Function<C, List<IPredicate>> function);

    }

    interface TableRouteOnSpec<Q extends Query, C> extends OnSpec<Q, C> {

        OnSpec<Q, C> route(int databaseIndex, int tableIndex);

        OnSpec<Q, C> route(int tableIndex);
    }

    interface JoinSpec<Q extends Query, C> extends WhereSpec<Q, C> {

        TableRouteOnSpec<Q, C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        OnSpec<Q, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnSpec<Q, C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        OnSpec<Q, C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnSpec<Q, C> join(TableMeta<?> tableMeta, String tableAlias);

        OnSpec<Q, C> join(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnSpec<Q, C> ifJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        OnSpec<Q, C> ifJoin(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnSpec<Q, C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        OnSpec<Q, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnSpec<Q, C> ifRightJoin(Predicate<C> predicate, TableMeta<?> tableMeta, String tableAlias);

        OnSpec<Q, C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface WhereSpec<Q extends Query, C> extends GroupBySpec<Q, C> {

        GroupBySpec<Q, C> where(List<IPredicate> predicateList);

        GroupBySpec<Q, C> where(Function<C, List<IPredicate>> function);

        WhereAndSpec<Q, C> where(IPredicate predicate);
    }

    interface WhereAndSpec<Q extends Query, C> extends GroupBySpec<Q, C> {

        WhereAndSpec<Q, C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        WhereAndSpec<Q, C> ifAnd(@Nullable IPredicate predicate);

        WhereAndSpec<Q, C> ifAnd(Function<C, IPredicate> function);

    }

    interface GroupBySpec<Q extends Query, C> extends OrderBySpec<Q, C> {

        HavingSpec<Q, C> groupBy(SortPart sortPart);

        HavingSpec<Q, C> groupBy(SortPart sortPart1, SortPart sortPart2);

        HavingSpec<Q, C> groupBy(List<SortPart> sortPartList);

        HavingSpec<Q, C> groupBy(Function<C, List<SortPart>> function);

        HavingSpec<Q, C> ifGroupBy(Function<C, List<SortPart>> function);
    }

    interface HavingSpec<Q extends Query, C> extends OrderBySpec<Q, C> {

        OrderBySpec<Q, C> having(IPredicate predicate);

        OrderBySpec<Q, C> having(List<IPredicate> predicateList);

        OrderBySpec<Q, C> having(Function<C, List<IPredicate>> function);

        OrderBySpec<Q, C> ifHaving(Function<C, List<IPredicate>> function);
    }


    interface OrderBySpec<Q extends Query, C> extends OrderByClause<Q, C>, LimitSpec<Q, C> {

        @Override
        LimitClause<Q, C> orderBy(SortPart sortPart);

        @Override
        LimitClause<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2);

        @Override
        LimitSpec<Q, C> orderBy(List<SortPart> sortPartList);

        @Override
        LimitClause<Q, C> orderBy(Function<C, List<SortPart>> function);

        @Override
        LimitSpec<Q, C> ifOrderBy(Function<C, List<SortPart>> function);
    }


    interface LimitSpec<Q extends Query, C> extends LimitClause<Q, C>, LockSpec<Q, C> {
        @Override
        LockSpec<Q, C> limit(int rowCount);

        @Override
        LockSpec<Q, C> limit(int offset, int rowCount);

        @Override
        LockSpec<Q, C> ifLimit(Function<C, LimitOption> function);

        @Override
        LockSpec<Q, C> ifLimit(Predicate<C> predicate, int rowCount);

        @Override
        LockSpec<Q, C> ifLimit(Predicate<C> predicate, int offset, int rowCount);
    }

    interface LockSpec<Q extends Query, C> extends QuerySpec<Q>, UnionClause<Q, C> {

        QuerySpec<Q> lock(LockMode lockMode);

        QuerySpec<Q> ifLock(Function<C, LockMode> function);

    }


    interface UnionSpec<Q extends Query, C> extends UnionClause<Q, C>, OrderByClause<Q, C> {

    }

    interface UnionClause<Q extends Query, C> extends QuerySpec<Q> {

        UnionSpec<Q, C> bracketsQuery();

        UnionSpec<Q, C> union(Function<C, Q> function);

        UnionSpec<Q, C> unionAll(Function<C, Q> function);

        UnionSpec<Q, C> unionDistinct(Function<C, Q> function);

    }

    interface OrderByClause<Q extends Query, C> extends LimitClause<Q, C> {

        LimitClause<Q, C> orderBy(SortPart sortPart);

        LimitClause<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2);

        LimitClause<Q, C> orderBy(List<SortPart> sortPartList);

        LimitClause<Q, C> orderBy(Function<C, List<SortPart>> function);

        LimitClause<Q, C> ifOrderBy(Function<C, List<SortPart>> function);
    }

    interface LimitClause<Q extends Query, C> extends QuerySpec<Q> {

        QuerySpec<Q> limit(int rowCount);

        QuerySpec<Q> limit(int offset, int rowCount);

        QuerySpec<Q> ifLimit(Function<C, LimitOption> function);

        QuerySpec<Q> ifLimit(Predicate<C> predicate, int rowCount);

        QuerySpec<Q> ifLimit(Predicate<C> predicate, int offset, int rowCount);
    }

}
