package io.army.criteria;

import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;

public interface Select {

    interface DistinctAble<C> extends SelectListAble<C> {

        SelectListAble<C> modifier(DistinctModifier distinctModifier);
    }

    interface SelectListAble<C> extends FromAble<C>{

        FromAble<C> select(List<Selection> selectionList);

        FromAble<C> select(Function<C,List<Selection>> function);
    }

    interface FromAble<C> extends Select {

        <T extends IDomain> AliasAble<C> from(TableMeta<T> tableMeta);
    }

    interface AliasAble<C> extends OnAble<C> {

        JoinAble<C> as(String tableAlias);
    }

    interface OnAble<C> extends WhereAble<C> {

        JoinAble<C> on(List<IPredicate> predicateList);

        OnAndAble<C> on(IPredicate predicate);
    }

    interface OnAndAble<C> extends JoinAble<C> {

        OnAndAble<C> onAnd(IPredicate predicate);
    }

    interface JoinAble<C> extends WhereAble<C> {

        <T2 extends IDomain> AliasAble<C> join(TableMeta<T2> tableMeta);
    }

    interface WhereAble<C> extends GroupByAble<C> {

        GroupByAble<C> where(List<IPredicate> predicateList);

        WhereAndAble<C> where(IPredicate predicate);
    }

    interface WhereAndAble<C> extends GroupByAble<C> {

        WhereAndAble<C> and(IPredicate predicate);
    }


    interface GroupByAble<C> extends HavingAble<C> {

        GroupByAThenAble<C> groupBy(Expression<?> groupExp);

        GroupByAThenAble<C> groupBy(Expression<?> groupExp, @Nullable Boolean asc);

    }

    interface GroupByAThenAble<C> extends HavingAble<C> {

        GroupByAThenAble<C> then(Expression<?> groupExp);

        GroupByAThenAble<C> then(Expression<?> groupExp, @Nullable Boolean asc);
    }

    interface HavingAble<C> extends OrderByAble<C> {

        OrderByAble<C> having(List<IPredicate> predicateList);

        HavingAndAble<C> having(IPredicate predicate);

    }

    interface HavingAndAble<C> extends OrderByAble<C> {

        HavingAndAble<C> havingAnd(IPredicate predicate);

    }

    interface OrderByAble<C> extends LimitAble<C> {

        OrderByThenAble<C> orderBy(Expression<?> orderExp);

        OrderByThenAble<C> orderBy(Expression<?> orderExp, @Nullable Boolean asc);
    }

    interface OrderByThenAble<C> extends LimitAble<C> {

        OrderByThenAble<C> then(Expression<?> orderExp);

        OrderByThenAble<C> then(Expression<?> orderExp, @Nullable Boolean asc);

    }

    interface LimitAble<C> extends SelectAble {

        SelectionAble limit(int rowCount);

        SelectionAble limit(int offset, int rowCount);

    }

}
