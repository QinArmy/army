package io.army.criteria;


import io.army.criteria.impl.Sqls;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Update extends SQLStatement, SQLDebug {

    interface UpdateSQLSpec {

    }

    interface UpdateSpec extends UpdateSQLSpec {

        Update asUpdate();
    }


    interface SingleUpdateSpec<T extends IDomain, C> extends UpdateSQLSpec {

        SingleUpdateTableRouteSpec<T, C> update(TableMeta<T> tableMeta, String tableAlias);
    }


    interface SingleUpdateTableRouteSpec<T extends IDomain, C> extends SingleSetSpec<T, C> {

        SingleSetSpec<T, C> route(int databaseIndex, int tableIndex);

        SingleSetSpec<T, C> route(int tableIndex);
    }


    interface SingleSetSpec<T extends IDomain, C> extends UpdateSQLSpec {

        <F> SingleWhereSpec<T, C> set(FieldMeta<? super T, F> target, F value);

        /**
         * @see Sqls#defaultValue()
         */
        <F> SingleWhereSpec<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp);

        <F> SingleWhereSpec<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value);

        <F> SingleWhereSpec<T, C> ifSet(FieldMeta<? super T, F> target, Function<C, Expression<F>> function);
    }


    interface SingleWhereSpec<T extends IDomain, C> extends SingleSetSpec<T, C> {

        UpdateSpec where(List<IPredicate> predicateList);

        UpdateSpec where(Function<C, List<IPredicate>> function);

        WhereAndSpec<T, C> where(IPredicate predicate);
    }


    interface WhereAndSpec<T extends IDomain, C> extends UpdateSpec {

        WhereAndSpec<T, C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        WhereAndSpec<T, C> ifAnd(@Nullable IPredicate predicate);

        WhereAndSpec<T, C> ifAnd(Function<C, IPredicate> function);

    }

    /*################################## blow batch update interface ##################################*/

    interface BatchUpdateSpec<T extends IDomain, C> extends UpdateSQLSpec {

        BatchTableRouteSpec<T, C> update(TableMeta<T> tableMeta, String tableAlias);
    }

    interface BatchTableRouteSpec<T extends IDomain, C> extends BatchSetSpec<T, C> {

        BatchSetSpec<T, C> route(int databaseIndex, int tableIndex);

        BatchSetSpec<T, C> route(int tableIndex);
    }

    interface BatchSetSpec<T extends IDomain, C> extends UpdateSQLSpec {

        <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> target, F value);

        /**
         * @see Sqls#defaultValue()
         */
        <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp);

        <F> BatchWhereSpec<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> target, F value);

        <F> BatchWhereSpec<T, C> ifSet(FieldMeta<? super T, F> target, Function<C, Expression<F>> function);
    }

    interface BatchWhereSpec<T extends IDomain, C> extends BatchSetSpec<T, C> {


        BatchNamedParamSpec<C> where(List<IPredicate> predicateList);

        BatchNamedParamSpec<C> where(Function<C, List<IPredicate>> function);

        BatchWhereAndSpec<T, C> where(IPredicate predicate);
    }

    interface BatchWhereAndSpec<T extends IDomain, C> extends BatchNamedParamSpec<C> {

        BatchWhereAndSpec<T, C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        BatchWhereAndSpec<T, C> ifAnd(@Nullable IPredicate predicate);

        BatchWhereAndSpec<T, C> ifAnd(Function<C, IPredicate> function);

    }

    interface BatchNamedParamSpec<C> extends UpdateSQLSpec {

        UpdateSpec namedParamMaps(List<Map<String, Object>> mapList);

        UpdateSpec namedParamMaps(Function<C, List<Map<String, Object>>> function);

        UpdateSpec namedParamBeans(List<Object> beanList);

        UpdateSpec namedParamBeans(Function<C, List<Object>> function);
    }


}
