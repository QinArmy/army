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


    interface SingleUpdateAble<T extends IDomain, C> extends UpdateSQLAble {

        SetAble<T, C> update(TableMeta<T> tableMeta, String tableAlias);
    }


    interface SetAble<T extends IDomain, C> extends UpdateSQLAble {

        <F> WhereAble<T, C> set(FieldMeta<T, F> target, F value);

        /**
         * @see SQLS#defaultValue()
         */
        <F> WhereAble<T, C> set(FieldMeta<T, F> target, Expression<F> valueExp);

        <F> WhereAble<T, C> set(FieldMeta<T, F> target, Function<C, Expression<F>> function);
    }


    interface WhereAble<T extends IDomain, C> extends SetAble<T, C> {

        <F> WhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<T, F> target, F value);

        <F> WhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<T, F> target
                , Expression<F> valueExp);

        <F> WhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<T, F> target
                , Function<C, Expression<F>> valueExpFunction);

        UpdateAble where(List<IPredicate> predicateList);

        UpdateAble where(Function<C, List<IPredicate>> function);

        WhereAndAble<T, C> where(IPredicate predicate);
    }

    interface WhereAndAble<T extends IDomain, C> extends UpdateAble {

        WhereAndAble<T, C> and(IPredicate predicate);

        WhereAndAble<T, C> ifAnd(Predicate<C> testPredicate, IPredicate predicate);

        WhereAndAble<T, C> ifAnd(Predicate<C> testPredicate, Function<C, IPredicate> function);

    }


}
