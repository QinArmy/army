package io.army.criteria;


import io.army.criteria.impl.SQLS;
import io.army.domain.IDomain;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Update extends SQLStatement, SQLAble, SQLDebug, QueryAble {

    interface UpdateSQLAble extends SQLAble {

    }

    interface UpdateAble extends UpdateSQLAble {

        Update asUpdate();
    }


    interface SingleUpdateAble<T extends IDomain, C> extends UpdateSQLAble {

        SingleSetAble<T, C> update(TableMeta<T> tableMeta, String tableAlias);
    }


    interface SingleSetAble<T extends IDomain, C> extends UpdateSQLAble {

        <F> SingleWhereAble<T, C> set(FieldMeta<? super T, F> target, F value);

        /**
         * @see SQLS#defaultValue()
         */
        <F> SingleWhereAble<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp);

        <F> SingleWhereAble<T, C> set(FieldMeta<? super T, F> target, Function<C, Expression<F>> function);
    }


    interface SingleWhereAble<T extends IDomain, C> extends SingleSetAble<T, C> {

        <F> SingleWhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value);

        <F> SingleWhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target
                , Expression<F> valueExp);

        <F> SingleWhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target
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
