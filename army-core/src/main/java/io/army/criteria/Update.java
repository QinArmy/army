package io.army.criteria;


import io.army.criteria.impl.SQLS;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Update extends SQLAble, SQLDebug, QueryAble {

    interface UpdateSQLAble extends SQLAble {

    }

    interface UpdateAble extends UpdateSQLAble {

        Update asUpdate();
    }


    interface SingleUpdateAble<C> extends UpdateSQLAble {

        SetAble<C> update(TableMeta<?> tableMeta, String tableAlias);
    }

    interface MultiUpdateAble<C> extends UpdateSQLAble {

        JoinAble<C> update(TableMeta<?> tableMeta, String tableAlias);

    }

    interface SetAble<C> extends UpdateSQLAble {

        <F> WhereAble<C> set(FieldMeta<? extends IDomain, F> target, F value);

        /**
         * @see SQLS#defaultValue()
         */
        <F> WhereAble<C> set(FieldMeta<? extends IDomain, F> target, Expression<F> valueExp);

        <F> WhereAble<C> set(FieldMeta<? extends IDomain, F> target, Function<C, Expression<?>> function);
    }

    interface JoinAble<C> extends SetAble<C> {

        OnAble<C> leftJoin(TableAble tableAble, String tableAlias);

        OnAble<C> join(TableAble tableAble, String tableAlias);

        OnAble<C> rightJoin(TableAble tableAble, String tableAlias);

    }

    interface OnAble<C> extends UpdateSQLAble {

        JoinAble<C> on(List<IPredicate> predicateList);

        JoinAble<C> on(IPredicate predicate);

        JoinAble<C> on(Function<C, List<IPredicate>> function);
    }

    interface WhereAble<C> extends SetAble<C> {

        <F> WhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target, F value);

        <F> WhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target, Expression<F> valueExp);

        <F> WhereAble<C> ifSet(Predicate<C> predicate, FieldMeta<? extends IDomain, F> target
                , Function<C, Expression<?>> valueExpFunction);

        UpdateAble where(List<IPredicate> predicateList);

        UpdateAble where(Function<C, List<IPredicate>> function);

        WhereAndAble<C> where(IPredicate predicate);
    }

    interface WhereAndAble<C> extends UpdateAble {

        WhereAndAble<C> and(IPredicate predicate);

        WhereAndAble<C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        WhereAndAble<C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


}
