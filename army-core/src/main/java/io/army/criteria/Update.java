package io.army.criteria;


import io.army.criteria.impl.SQLS;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Update extends SQLStatement, SQLStatement.SQLAble, SQLDebug, QueryAble {

    interface UpdateSQLAble extends SQLAble {

    }

    interface UpdateAble extends UpdateSQLAble {

        Update asUpdate();
    }


    interface SingleUpdateAble<T extends IDomain, C> extends UpdateSQLAble {

        SingleUpdateTableRouteAble<T, C> update(TableMeta<T> tableMeta, String tableAlias);
    }


    interface SingleUpdateTableRouteAble<T extends IDomain, C> extends SingleSetAble<T, C> {

        SingleSetAble<T, C> route(int databaseIndex, int tableIndex);

        SingleSetAble<T, C> route(int tableIndex);
    }


    interface SingleSetAble<T extends IDomain, C> extends UpdateSQLAble {

        <F> SingleWhereAble<T, C> set(FieldMeta<? super T, F> target, F value);

        /**
         * @see SQLS#defaultValue()
         */
        <F> SingleWhereAble<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp);

        <F> SingleWhereAble<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value);

        <F> SingleWhereAble<T, C> nonNullSet(FieldMeta<? super T, F> target, Function<C, Expression<F>> function);
    }


    interface SingleWhereAble<T extends IDomain, C> extends SingleSetAble<T, C> {

        UpdateAble where(List<IPredicate> predicateList);

        UpdateAble where(Function<C, List<IPredicate>> function);

        WhereAndAble<T, C> where(IPredicate predicate);
    }


    interface WhereAndAble<T extends IDomain, C> extends UpdateAble {

        WhereAndAble<T, C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        WhereAndAble<T, C> ifAnd(@Nullable IPredicate predicate);

        WhereAndAble<T, C> ifAnd(Function<C, IPredicate> function);

    }

    /*################################## blow batch update interface ##################################*/

    interface BatchUpdateAble<T extends IDomain, C> extends UpdateSQLAble {

        BatchTableRouteAble<T, C> update(TableMeta<T> tableMeta, String tableAlias);
    }

    interface BatchTableRouteAble<T extends IDomain, C> extends BatchSetAble<T, C> {

        BatchSetAble<T, C> route(int databaseIndex, int tableIndex);

        BatchSetAble<T, C> route(int tableIndex);
    }

    interface BatchSetAble<T extends IDomain, C> extends UpdateSQLAble {

        <F> BatchWhereAble<T, C> set(FieldMeta<? super T, F> target, F value);

        /**
         * @see SQLS#defaultValue()
         */
        <F> BatchWhereAble<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp);

        <F> BatchWhereAble<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> target, F value);

        <F> BatchWhereAble<T, C> ifSet(FieldMeta<? super T, F> target, Function<C, Expression<F>> function);
    }

    interface BatchWhereAble<T extends IDomain, C> extends BatchSetAble<T, C> {


        BatchNamedParamAble<C> where(List<IPredicate> predicateList);

        BatchNamedParamAble<C> where(Function<C, List<IPredicate>> function);

        BatchWhereAndAble<T, C> where(IPredicate predicate);
    }

    interface BatchWhereAndAble<T extends IDomain, C> extends BatchNamedParamAble<C> {

        BatchWhereAndAble<T, C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        BatchWhereAndAble<T, C> ifAnd(@Nullable IPredicate predicate);

        BatchWhereAndAble<T, C> ifAnd(Function<C, IPredicate> function);

    }

    interface BatchNamedParamAble<C> extends UpdateSQLAble {

        UpdateAble namedParamMaps(List<Map<String, Object>> mapList);

        UpdateAble namedParamMaps(Function<C, List<Map<String, Object>>> function);

        UpdateAble namedParamBeans(List<Object> beanList);

        UpdateAble namedParamBeans(Function<C, List<Object>> function);
    }


}
