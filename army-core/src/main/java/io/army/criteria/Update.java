package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Update extends Statement, SQLDebug {

    interface UpdateSqlSpec {

    }

    interface UpdateSpec extends UpdateSqlSpec {

        Update asUpdate();
    }


    interface DomainUpdateSpec<C> extends UpdateSqlSpec {

        <T extends IDomain> SetSpec<T, C> update(TableMeta<T> table, String tableAlias);
    }


    interface SetSpec<T extends IDomain, C> extends UpdateSqlSpec {

        <F> WhereSpec<T, C> set(FieldMeta<? super T, F> target, @Nullable F value);

        /**
         * @see SQLs#defaultValue()
         */
        <F> WhereSpec<T, C> set(FieldMeta<? super T, F> target, Expression<F> valueExp);

        <F> WhereSpec<T, C> setDefault(FieldMeta<? super T, F> target);

        <F> WhereSpec<T, C> ifSet(FieldMeta<? super T, F> target, @Nullable F value);

        <F> WhereSpec<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, F value);

        <F> WhereSpec<T, C> ifSet(FieldMeta<? super T, F> target, Function<C, Expression<F>> function);

    }


    interface WhereSpec<T extends IDomain, C> extends SetSpec<T, C> {

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

    interface BatchUpdateSpec<C> extends UpdateSqlSpec {

        <T extends IDomain> BatchSetSpec<T, C> update(TableMeta<T> table, String tableAlias);
    }


    interface BatchSetSpec<T extends IDomain, C> extends UpdateSqlSpec {


        <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> field);

        <F> BatchWhereSpec<T, C> setDefault(FieldMeta<? super T, F> field);

        <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> field, @Nullable F value);

        <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> field, Expression<F> valueExp);

        <F> BatchWhereSpec<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> field);

        <F> BatchWhereSpec<T, C> ifSet(FieldMeta<? super T, F> field, @Nullable F value);

        <F> BatchWhereSpec<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> field, F value);

        <F> BatchWhereSpec<T, C> ifSet(FieldMeta<? super T, F> filed, Function<C, Expression<F>> function);
    }

    interface BatchWhereSpec<T extends IDomain, C> extends BatchSetSpec<T, C> {

        BatchParamSpec<C> where(List<IPredicate> predicateList);

        BatchParamSpec<C> where(Function<C, List<IPredicate>> function);

        BatchWhereAndSpec<C> where(IPredicate predicate);
    }

    interface BatchWhereAndSpec<C> extends UpdateSqlSpec {

        BatchWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        BatchWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        BatchWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }

    interface BatchParamSpec<C> extends UpdateSqlSpec {

        UpdateSpec paramMaps(List<Map<String, Object>> mapList);

        UpdateSpec paramMaps(Function<C, List<Map<String, Object>>> function);

        UpdateSpec paramBeans(List<Object> beanList);

        UpdateSpec paramBeans(Function<C, List<Object>> function);
    }


}
