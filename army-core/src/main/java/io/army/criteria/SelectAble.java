package io.army.criteria;

import io.army.domain.IDomain;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface SelectAble extends SQLBuilder, SQLAble {

    interface SelectSQLAble extends SQLBuilder, SQLAble {

    }

    interface DistinctAble<C> extends SelectListAble<C> {

        SelectListAble<C> modifier(DistinctModifier distinctModifier);
    }

    interface SelectListAble<C> extends SelectSQLAble {

        <T extends IDomain> FromAble<C> select(TableMeta<T> tableMeta);

        FromAble<C> select(List<Selection> selectionList);

        FromAble<C> select(Function<C, List<Selection>> function);
    }

    interface FromAble<C> extends SelectSQLAble {

        <T extends IDomain> JoinAble<C> from(TableMeta<T> tableMeta,String tableAlias);
    }


    interface OnAble<C> extends  SelectSQLAble{

        JoinAble<C> on(List<IPredicate> predicateList);

        JoinAble<C> on(IPredicate predicate);

        JoinAble<C> on(Function<C, List<IPredicate>> function);

    }

    interface JoinAble<C> extends WhereAble<C> {

        <T extends IDomain> OnAble<C> join(TableMeta<T> tableMeta,String tableAlias);
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


    interface GroupByAble<C> extends HavingAble<C> {

        HavingAble<C> groupBy(SortExpression<?> groupExp);

        HavingAble<C> groupBy(Function<C, List<SortExpression<?>>> function);

        HavingAble<C> ifGroupBy(Predicate<C> predicate, SortExpression<?> groupExp);

        HavingAble<C> ifGroupBy(Predicate<C> predicate, Function<C, List<SortExpression<?>>> expFunction);

    }

    interface HavingAble<C> extends OrderByAble<C> {

        OrderByAble<C> having(List<IPredicate> predicateList);

        OrderByAble<C> having(Function<C, List<IPredicate>> function);

        OrderByAble<C> having(IPredicate predicate);

        OrderByAble<C> ifHaving(Predicate<C> predicate, List<IPredicate> predicateList);

        OrderByAble<C> ifHaving(Predicate<C> predicate, Function<C, List<IPredicate>> function);

        OrderByAble<C> ifHaving(Predicate<C> testPredicate, IPredicate predicate);
    }


    interface OrderByAble<C> extends LimitAble<C> {

        LimitAble<C> orderBy(SortExpression<?> groupExp);

        LimitAble<C> orderBy(Function<C, List<SortExpression<?>>> function);

        LimitAble<C> ifOrderBy(Predicate<C> predicate, SortExpression<?> groupExp);

        LimitAble<C> ifOrderBy(Predicate<C> predicate, Function<C, List<SortExpression<?>>> expFunction);
    }


    interface LimitAble<C> extends LockAble<C> {

        LockAble<C> limit(int rowCount);

        LockAble<C> limit(int offset, int rowCount);

        LockAble<C> ifLimit(Predicate<C> predicate, int rowCount);

        LockAble<C> ifLimit(Predicate<C> predicate, int offset, int rowCount);
    }

    interface LockAble<C> extends SelectSQLAble, SelectAble {

        SelectAble lock(LockMode lockMode);

        SelectAble lock(Function<C, LockMode> function);

        SelectAble ifLck(Predicate<C> predicate, LockMode lockMode);

        SelectAble ifLck(Predicate<C> predicate, Function<C, LockMode> function);

    }


}
