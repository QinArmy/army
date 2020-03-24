package io.army.criteria;

import io.army.meta.TableMeta;
import io.army.util.Pair;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Select extends SQLDebug, SQLAble, QueryAble {

    interface SelectAble extends SelectSQLAble {
        /*
         *not doc
         * @see io.army.criteria.impl.CriteriaContextHolder.clearContext
         */
        Select asSelect();
    }

    interface SelectSQLAble extends SQLAble {

    }


    interface NoJoinSelectPartAble<C> extends SelectSQLAble {

        <S extends SelectPart> NoJoinFromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        NoJoinFromAble<C> select(Distinct distinct, SelectPart selectPart);

        NoJoinFromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> NoJoinFromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> NoJoinFromAble<C> select(List<S> selectPartList);

    }


    interface SelectPartAble<C> extends NoJoinSelectPartAble<C> {

        <S extends SelectPart> FromAble<C> select(Distinct distinct, Function<C, List<S>> function);

        FromAble<C> select(Distinct distinct, SelectPart selectPart);

        FromAble<C> select(SelectPart selectPart);

        <S extends SelectPart> FromAble<C> select(Distinct distinct, List<S> selectPartList);

        <S extends SelectPart> FromAble<C> select(List<S> selectPartList);
    }


    interface FromAble<C> extends NoJoinFromAble<C> {

        JoinAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        JoinAble<C> from(Function<C, SubQuery> function, String subQueryAlia);

    }

    interface NoJoinFromAble<C> extends WhereAble<C> {

        WhereAble<C> from(TableMeta<?> tableMeta, String tableAlias);

        WhereAble<C> from(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface OnAble<C> extends SelectSQLAble {

        JoinAble<C> on(List<IPredicate> predicateList);

        JoinAble<C> on(IPredicate predicate);

        JoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface JoinAble<C> extends WhereAble<C> {

        OnAble<C> leftJoin(TableMeta<?> tableMeta, String tableAlias);

        OnAble<C> leftJoin(Function<C, SubQuery> function, String subQueryAlia);

        OnAble<C> join(TableMeta<?> tableMeta, String tableAlias);

        OnAble<C> join(Function<C, SubQuery> function, String subQueryAlia);

        OnAble<C> rightJoin(TableMeta<?> tableMeta, String tableAlias);

        OnAble<C> rightJoin(Function<C, SubQuery> function, String subQueryAlia);
    }

    interface WhereAble<C> extends GroupByAble<C> {

        GroupByAble<C> where(List<IPredicate> predicateList);

        GroupByAble<C> where(Function<C, List<IPredicate>> function);

        WhereAndAble<C> where(IPredicate predicate);
    }

    interface WhereAndAble<C> extends GroupByAble<C> {

        WhereAndAble<C> and(IPredicate predicate);

        WhereAndAble<C> and(Function<C, IPredicate> function);

        WhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        WhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


    interface GroupByAble<C> extends OrderByAble<C> {

        HavingAble<C> groupBy(Expression<?> groupExp);

        HavingAble<C> groupBy(List<Expression<?>> groupExpList);

        HavingAble<C> groupBy(Function<C, List<Expression<?>>> function);

        HavingAble<C> ifGroupBy(Predicate<C> predicate, Expression<?> groupExp);

        HavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);

    }

    interface HavingAble<C> extends OrderByAble<C> {

        OrderByAble<C> having(IPredicate predicate);

        OrderByAble<C> having(Function<C, List<IPredicate>> function);

        OrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        OrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface OrderByAble<C> extends LimitAble<C> {

        LimitAble<C> orderBy(Expression<?> orderExp);

        LimitAble<C> orderBy(Function<C, List<Expression<?>>> function);

        LimitAble<C> ifOrderBy(Predicate<C> predicate, Expression<?> orderExp);

        LimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<Expression<?>>> expFunction);
    }


    interface LimitAble<C> extends LockAble<C> {

        LockAble<C> limit(int rowCount);

        LockAble<C> limit(int offset, int rowCount);

        LockAble<C> limit(Function<C, Pair<Integer, Integer>> function);

        LockAble<C> ifLimit(Predicate<C> predicate, int rowCount);

        LockAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount);

        LockAble<C> ifLimit(Predicate<C> predicate, Function<C, Pair<Integer, Integer>> function);
    }

    interface LockAble<C> extends SelectSQLAble, SelectAble {

        SelectAble lock(LockMode lockMode);

        SelectAble lock(Function<C, LockMode> function);

        SelectAble ifLock(Predicate<C> predicate, LockMode lockMode);

        SelectAble ifLock(Predicate<C> predicate, Function<C, LockMode> function);

    }


}
