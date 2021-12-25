package io.army.criteria;


import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * @see Select
 * @see SubQuery
 * @see Update
 */
public interface Query extends Statement {

    boolean requiredBrackets();


    interface QuerySpec<Q extends Query> {

        Q asQuery();
    }


    /*################################## blow select clause  interfaces ##################################*/


    interface SelectPartSpec<Q extends Query, C> {

        <S extends SelectPart> FromSpec<Q, C> select(Distinct distinct, Function<C, List<S>> function);

        <S extends SelectPart> FromSpec<Q, C> select(Distinct distinct, Supplier<List<S>> supplier);

        <S extends SelectPart> FromSpec<Q, C> select(Function<C, List<S>> function);

        <S extends SelectPart> FromSpec<Q, C> select(Supplier<List<S>> supplier);

        FromSpec<Q, C> select(Distinct distinct, SelectPart selectPart);

        FromSpec<Q, C> select(SelectPart selectPart);

        FromSpec<Q, C> select(SelectPart selectPart1, SelectPart selectPart2);

        FromSpec<Q, C> select(SelectPart selectPart1, SelectPart selectPart2, SelectPart selectPart3);

        <S extends SelectPart> FromSpec<Q, C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> FromSpec<Q, C> select(List<S> selectPartList);
    }


    interface FromSpec<Q extends Query, C> extends UnionClause<Q, C> {

        TableJoinSpec<Q, C> from(TableMeta<?> table, String tableAlias);

        JoinSpec<Q, C> from(Function<C, SubQuery> function, String subQueryAlia);

        JoinSpec<Q, C> from(Supplier<SubQuery> supplier, String subQueryAlia);

    }

    interface OnSpec<Q extends Query, C> {

        JoinSpec<Q, C> on(List<IPredicate> predicateList);

        JoinSpec<Q, C> on(IPredicate predicate);

        JoinSpec<Q, C> on(IPredicate predicate1, IPredicate predicate2);

        JoinSpec<Q, C> on(Function<C, List<IPredicate>> function);

        JoinSpec<Q, C> on(Supplier<List<IPredicate>> supplier);

    }

    interface TableOnSpec<Q extends Query, C> extends OnSpec<Q, C> {

        @Override
        TableJoinSpec<Q, C> on(List<IPredicate> predicateList);

        @Override
        TableJoinSpec<Q, C> on(IPredicate predicate);

        @Override
        TableJoinSpec<Q, C> on(IPredicate predicate1, IPredicate predicate2);

        @Override
        TableJoinSpec<Q, C> on(Function<C, List<IPredicate>> function);

        TableJoinSpec<Q, C> on(Supplier<List<IPredicate>> supplier);

        TableJoinSpec<Q, C> onPrimary();

    }

    interface JoinSpec<Q extends Query, C> extends WhereSpec<Q, C> {

        OnSpec<Q, C> leftJoin(TableMeta<?> table, String tableAlias);

        OnSpec<Q, C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<Q, C> leftJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        OnSpec<Q, C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        OnSpec<Q, C> ifLeftJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<Q, C> ifLeftJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        OnSpec<Q, C> join(TableMeta<?> table, String tableAlias);

        OnSpec<Q, C> join(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<Q, C> join(Supplier<SubQuery> supplier, String subQueryAlia);

        OnSpec<Q, C> ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        OnSpec<Q, C> ifJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<Q, C> ifJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        OnSpec<Q, C> rightJoin(TableMeta<?> table, String tableAlias);

        OnSpec<Q, C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<Q, C> rightJoin(Supplier<SubQuery> supplier, String subQueryAlia);

        OnSpec<Q, C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        OnSpec<Q, C> ifRightJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnSpec<Q, C> ifRightJoin(Supplier<SubQuery> supplier, String subQueryAlia);

    }

    interface TableJoinSpec<Q extends Query, C> extends JoinSpec<Q, C> {

        @Override
        TableOnSpec<Q, C> leftJoin(TableMeta<?> table, String tableAlias);

        @Override
        TableOnSpec<Q, C> ifLeftJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        @Override
        TableOnSpec<Q, C> join(TableMeta<?> table, String tableAlias);

        @Override
        TableOnSpec<Q, C> ifJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);

        @Override
        TableOnSpec<Q, C> rightJoin(TableMeta<?> table, String tableAlias);

        @Override
        TableOnSpec<Q, C> ifRightJoin(Predicate<C> predicate, TableMeta<?> table, String tableAlias);


    }

    interface WhereSpec<Q extends Query, C> extends GroupBySpec<Q, C> {

        GroupBySpec<Q, C> where(List<IPredicate> predicateList);

        GroupBySpec<Q, C> where(Function<C, List<IPredicate>> function);

        GroupBySpec<Q, C> where(Supplier<List<IPredicate>> supplier);

        WhereAndSpec<Q, C> where(IPredicate predicate);
    }

    interface WhereAndSpec<Q extends Query, C> extends GroupBySpec<Q, C> {

        WhereAndSpec<Q, C> and(IPredicate predicate);

        WhereAndSpec<Q, C> and(Supplier<IPredicate> supplier);

        WhereAndSpec<Q, C> and(Function<C, IPredicate> function);

        /**
         * @see Expression#ifEqual(Object)
         */
        WhereAndSpec<Q, C> ifAnd(@Nullable IPredicate predicate);

        WhereAndSpec<Q, C> ifAnd(Supplier<IPredicate> supplier);

        WhereAndSpec<Q, C> ifAnd(Function<C, IPredicate> function);

    }

    interface GroupBySpec<Q extends Query, C> extends OrderBySpec<Q, C> {

        HavingSpec<Q, C> groupBy(SortPart sortPart);

        HavingSpec<Q, C> groupBy(SortPart sortPart1, SortPart sortPart2);

        HavingSpec<Q, C> groupBy(List<SortPart> sortPartList);

        HavingSpec<Q, C> groupBy(Function<C, List<SortPart>> function);

        HavingSpec<Q, C> groupBy(Supplier<List<SortPart>> supplier);

        HavingSpec<Q, C> ifGroupBy(@Nullable SortPart sortPart);

        HavingSpec<Q, C> ifGroupBy(Supplier<List<SortPart>> supplier);

        HavingSpec<Q, C> ifGroupBy(Function<C, List<SortPart>> function);
    }

    interface HavingSpec<Q extends Query, C> extends OrderBySpec<Q, C> {

        OrderBySpec<Q, C> having(IPredicate predicate);

        OrderBySpec<Q, C> having(IPredicate predicate1, IPredicate predicate2);

        OrderBySpec<Q, C> having(List<IPredicate> predicateList);

        OrderBySpec<Q, C> having(Supplier<List<IPredicate>> supplier);

        OrderBySpec<Q, C> having(Function<C, List<IPredicate>> function);

        OrderBySpec<Q, C> ifHaving(@Nullable IPredicate predicate);

        OrderBySpec<Q, C> ifHaving(Supplier<List<IPredicate>> supplier);

        OrderBySpec<Q, C> ifHaving(Function<C, List<IPredicate>> function);
    }


    interface OrderBySpec<Q extends Query, C> extends OrderByClause<Q, C>, LimitSpec<Q, C> {

        @Override
        LimitSpec<Q, C> orderBy(SortPart sortPart);

        @Override
        LimitSpec<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2);

        @Override
        LimitSpec<Q, C> orderBy(List<SortPart> sortPartList);

        @Override
        LimitSpec<Q, C> orderBy(Function<C, List<SortPart>> function);

        @Override
        LimitSpec<Q, C> orderBy(Supplier<List<SortPart>> supplier);

        @Override
        LimitSpec<Q, C> ifOrderBy(@Nullable SortPart sortPart);

        @Override
        LimitSpec<Q, C> ifOrderBy(Supplier<List<SortPart>> supplier);

        @Override
        LimitSpec<Q, C> ifOrderBy(Function<C, List<SortPart>> function);


    }


    interface LimitSpec<Q extends Query, C> extends LimitClause<Q, C>, LockSpec<Q, C> {

        @Override
        LockSpec<Q, C> limit(long rowCount);

        @Override
        LockSpec<Q, C> limit(long offset, long rowCount);

        @Override
        LockSpec<Q, C> limit(Function<C, LimitOption> function);

        @Override
        LockSpec<Q, C> limit(Supplier<LimitOption> supplier);

        @Override
        LockSpec<Q, C> ifLimit(Supplier<LimitOption> function);

        @Override
        LockSpec<Q, C> ifLimit(Function<C, LimitOption> function);


    }

    interface LockSpec<Q extends Query, C> extends QuerySpec<Q>, UnionClause<Q, C> {

        QuerySpec<Q> lock(LockMode lockMode);

        QuerySpec<Q> lock(Function<C, LockMode> function);

        QuerySpec<Q> ifLock(@Nullable LockMode lockMode);

        QuerySpec<Q> ifLock(Supplier<LockMode> supplier);

        QuerySpec<Q> ifLock(Function<C, LockMode> function);


    }


    interface UnionSpec<Q extends Query, C> extends UnionClause<Q, C>, OrderByClause<Q, C> {

    }

    interface UnionClause<Q extends Query, C> extends QuerySpec<Q> {

        UnionSpec<Q, C> bracketsQuery();

        UnionSpec<Q, C> union(Function<C, Q> function);

        UnionSpec<Q, C> union(Supplier<Q> supplier);

        SelectPartSpec<Q, C> union();

        SelectPartSpec<Q, C> unionAll();

        SelectPartSpec<Q, C> unionDistinct();

        UnionSpec<Q, C> unionAll(Function<C, Q> function);

        UnionSpec<Q, C> unionDistinct(Function<C, Q> function);

        UnionSpec<Q, C> unionAll(Supplier<Q> function);

        UnionSpec<Q, C> unionDistinct(Supplier<Q> function);

    }

    interface OrderByClause<Q extends Query, C> extends LimitClause<Q, C> {

        LimitClause<Q, C> orderBy(SortPart sortPart);

        LimitClause<Q, C> orderBy(SortPart sortPart1, SortPart sortPart2);

        LimitClause<Q, C> orderBy(List<SortPart> sortPartList);

        LimitClause<Q, C> orderBy(Function<C, List<SortPart>> function);

        LimitClause<Q, C> orderBy(Supplier<List<SortPart>> supplier);

        LimitClause<Q, C> ifOrderBy(@Nullable SortPart sortPart);

        LimitClause<Q, C> ifOrderBy(Supplier<List<SortPart>> supplier);

        LimitClause<Q, C> ifOrderBy(Function<C, List<SortPart>> function);
    }

    interface LimitClause<Q extends Query, C> extends QuerySpec<Q> {

        QuerySpec<Q> limit(long rowCount);

        QuerySpec<Q> limit(long offset, long rowCount);


        QuerySpec<Q> limit(Function<C, LimitOption> function);

        QuerySpec<Q> limit(Supplier<LimitOption> supplier);

        QuerySpec<Q> ifLimit(Function<C, LimitOption> function);

        QuerySpec<Q> ifLimit(Supplier<LimitOption> supplier);

    }

}
