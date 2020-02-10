package io.army.criteria;


import io.army.domain.IDomain;
import io.army.meta.FieldMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface UpdateAble extends SQLAble, SQLBuilder {

    interface SingleUpdateAble extends SQLAble {

    }

    interface AliasAble<T extends IDomain, C> extends SingleUpdateAble {

        SetAble<T, C> as(String tableAlias);

    }

    interface SetAble<T extends IDomain , C> extends SingleUpdateAble {

        <F> WhereAble<T, C> set(FieldMeta<? super T, F> target, F value);

        <F> WhereAble<T, C> set(FieldMeta<? super T, F> target, Expression<?> valueExp);

        <F> WhereAble<T, C> set(FieldMeta<? super T, F> target, Function<C, Expression<?>> valueExpFunction);
    }

    interface WhereAble<T extends IDomain, C> extends SetAble<T, C> {

        <F> WhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value);

        <F> WhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, Expression<?> valueExp);

        <F> WhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, Function<C, Expression<?>> valueExpFunction);

        UpdateAble where(List<IPredicate> predicateList);

        UpdateAble where(Function<C, List<IPredicate>> function);

        WhereAndAble<T, C> where(IPredicate predicate);

    }

    interface WhereAndAble<T extends IDomain, C> extends UpdateAble, SingleUpdateAble {

        WhereAndAble<T, C> and(IPredicate predicate);

        WhereAndAble<T, C> ifAnd(Predicate<C> test, IPredicate predicate);

        WhereAndAble<T, C> ifAnd(Predicate<C> test, Function<C, IPredicate> function);

    }


}
