package io.army.criteria;

import io.army.lang.Nullable;
import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public interface Select extends SQLStatement, SQLDebug, SQLStatement.SQLAble, QueryAble {

    /*################################## blow select method ##################################*/


    boolean requiredBrackets();



    /*################################## blow select clause  interfaces ##################################*/


    interface SelectSQLAble extends SQLAble {

    }

    interface SelectAble extends SelectSQLAble {
        /*
         *not doc
         * @see io.army.criteria.impl.CriteriaContextHolder.clearContext
         */
        Select asSelect();

    }


    interface SelectPartAble<C> {

        <S extends SelectPart> FromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        <S extends SelectPart> FromAble<C> select(Function<C, List<S>> function);

        FromAble<C> select(Distinct distinct, SelectPart selectPart);

        FromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> FromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> FromAble<C> select(List<S> selectPartList);
    }


    interface FromAble<C> extends UnionClause<C> {

        TableRouteJoinAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        JoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia);

    }

    interface TableRouteJoinAble<C> extends JoinAble<C> {

        JoinAble<C> fromRoute(int databaseIndex, int tableIndex);

        JoinAble<C> fromRoute(int tableIndex);
    }


    interface OnAble<C> extends SelectSQLAble {

        JoinAble<C> on(List<IPredicate> predicateList);

        JoinAble<C> on(IPredicate predicate);

        JoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface TableRouteOnAble<C> extends OnAble<C> {

        OnAble<C> route(int databaseIndex, int tableIndex);

        OnAble<C> route(int tableIndex);
    }

    interface JoinAble<C> extends WhereAble<C> {

        TableRouteOnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        OnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        OnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        TableRouteOnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        OnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface WhereAble<C> extends GroupByAble<C> {

        GroupByAble<C> where(List<IPredicate> predicateList);

        GroupByAble<C> where(Function<C, List<IPredicate>> function);

        WhereAndAble<C> where(IPredicate predicate);
    }

    interface WhereAndAble<C> extends GroupByAble<C> {

        WhereAndAble<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        WhereAndAble<C> ifAnd(@Nullable IPredicate predicate);

        WhereAndAble<C> ifAnd(Function<C, IPredicate> function);

    }


    interface GroupByAble<C> extends OrderByAble<C> {

        HavingAble<C> groupBy(SortPart sortPart);

        HavingAble<C> groupBy(List<SortPart> sortPartList);

        HavingAble<C> groupBy(Function<C, List<SortPart>> function);
    }

    interface HavingAble<C> extends OrderByAble<C> {

        OrderByAble<C> having(IPredicate predicate);

        OrderByAble<C> having(List<IPredicate> predicateList);

        OrderByAble<C> having(Function<C, List<IPredicate>> function);
    }


    interface OrderByAble<C> extends OrderByClause<C>, LimitAble<C> {

        @Override
        LimitAble<C> orderBy(SortPart sortPart);

        @Override
        LimitClause<C> orderBy(List<SortPart> sortPartList);

        @Override
        LimitAble<C> orderBy(Function<C, List<SortPart>> function);
    }


    interface LimitAble<C> extends LimitClause<C>, LockAble<C> {

        LockAble<C> limit(int rowCount);

        LockAble<C> limit(int offset, int rowCount);

        LockAble<C> ifLimit(Function<C, Pair<Integer, Integer>> function);

        LockAble<C> ifLimit(Predicate<C> test, int rowCount);

        LockAble<C> ifLimit(Predicate<C> test, int offset, int rowCount);

    }

    interface LockAble<C> extends SelectAble, UnionClause<C> {

        SelectAble lock(LockMode lockMode);

        SelectAble ifLock(Function<C, LockMode> function);

    }


    interface UnionAble<C> extends UnionClause<C>, OrderByClause<C> {

    }

    interface UnionClause<C> extends SelectAble {

        UnionAble<C> brackets();

        <S extends Select> UnionAble<C> union(Function<C, S> function);

        <S extends Select> UnionAble<C> unionAll(Function<C, S> function);

        <S extends Select> UnionAble<C> unionDistinct(Function<C, S> function);

    }

    interface OrderByClause<C> extends LimitClause<C> {

        LimitClause<C> orderBy(SortPart sortPart);

        LimitClause<C> orderBy(List<SortPart> sortPartList);

        LimitClause<C> orderBy(Function<C, List<SortPart>> function);
    }

    interface LimitClause<C> extends SelectAble {

        SelectAble limit(int rowCount);

        SelectAble limit(int offset, int rowCount);

        SelectAble ifLimit(Function<C, Pair<Integer, Integer>> function);

        SelectAble ifLimit(Predicate<C> predicate, int rowCount);

        SelectAble ifLimit(Predicate<C> predicate, int offset, int rowCount);
    }


}
