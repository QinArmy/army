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

    interface SelectionGroupAble<C> extends SelectSQLAble {

        FromAble<C> select(Distinct distinct, String tableAlias, TableMeta<?> tableMeta);

        FromAble<C> select(String tableAlias, TableMeta<?> tableMeta);

        FromAble<C> select(Distinct distinct, String subQueryAlias);

        FromAble<C> select(String subQueryAlias);

    }


    interface SelectionAble<C> extends SelectSQLAble {

        NoJoinFromAble<C> select(Distinct distinct, String tableAlias, TableMeta<?> tableMeta);

        NoJoinFromAble<C> select(String tableAlias, TableMeta<?> tableMeta);

        NoJoinFromAble<C> select(Distinct distinct, String subQueryAlias);

        NoJoinFromAble<C> select(String subQueryAlias);

        NoJoinFromAble<C> select(Distinct distinct, List<Selection> selectionList);

        NoJoinFromAble<C> select(List<Selection> selectionList);

        NoJoinFromAble<C> select(Distinct distinct, Selection selection);

        NoJoinFromAble<C> select(Selection selection);
    }


    interface SelectPartAble<C> extends SelectionGroupAble<C> {

        FromAble<C> select(List<SelectPart> selectPartList);

        FromAble<C> select(Distinct distinct, List<SelectPart> selectPartList);

        FromAble<C> select(Function<C, List<SelectPart>> function);

        FromAble<C> select(Distinct distinct, Function<C, List<SelectPart>> function);
    }


    interface FromAble<C> extends SelectAble {

        JoinAble<C> from(TableAble tableAble, String tableAlias);

    }

    interface NoJoinFromAble<C> extends SelectAble {

        WhereAble<C> from(TableAble tableAble, String tableAlias);
    }

    interface OnAble<C> extends SelectSQLAble {

        JoinAble<C> on(List<IPredicate> predicateList);

        JoinAble<C> on(IPredicate predicate);

        JoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface JoinAble<C> extends WhereAble<C> {

        OnAble<C> leftJoin(TableAble tableAble, String tableAlias);

        OnAble<C> join(TableAble tableAble, String tableAlias);

        OnAble<C> rightJoin(TableAble tableAble, String tableAlias);
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
