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


    interface UpdateSpec {

        Update asUpdate();
    }


    interface DomainUpdateSpec<C> {

        <T extends IDomain> RouteSpec<T, C> update(TableMeta<T> table, String tableAlias);
    }

    interface RouteSpec<T extends IDomain, C> extends SetSpec<T, C> {

        SetSpec<T, C> route(int databaseIndex, int tableIndex);

        SetSpec<T, C> route(int tableIndex);

        SetSpec<T, C> routeAll();
    }


    interface SetSpec<T extends IDomain, C> {

        <F> WhereSpec<T, C> set(FieldMeta<? super T, F> field, @Nullable F value);

        <F> WhereSpec<T, C> set(FieldMeta<? super T, F> field, Expression<F> valueExp);

        /**
         * @see SQLs#defaultValue()
         */
        <F> WhereSpec<T, C> setDefault(FieldMeta<? super T, F> field);

        <F extends Number> WhereSpec<T, C> setPlus(FieldMeta<? super T, F> field, F value);

        <F extends Number> WhereSpec<T, C> setPlus(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> WhereSpec<T, C> setMinus(FieldMeta<? super T, F> field, F value);

        <F extends Number> WhereSpec<T, C> setMinus(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> WhereSpec<T, C> setMultiply(FieldMeta<? super T, F> field, F value);

        <F extends Number> WhereSpec<T, C> setMultiply(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> WhereSpec<T, C> setDivide(FieldMeta<? super T, F> field, F value);

        <F extends Number> WhereSpec<T, C> setDivide(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> WhereSpec<T, C> setMod(FieldMeta<? super T, F> field, F value);

        <F extends Number> WhereSpec<T, C> setMod(FieldMeta<? super T, F> field, Expression<F> value);

        <F> WhereSpec<T, C> ifSet(FieldMeta<? super T, F> field, @Nullable F value);

        <F> WhereSpec<T, C> ifSet(Predicate<C> predicate, FieldMeta<? super T, F> target, @Nullable F value);

        <F> WhereSpec<T, C> ifSet(FieldMeta<? super T, F> field, Function<C, Expression<F>> function);

        <F extends Number> WhereSpec<T, C> ifSetPlus(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> WhereSpec<T, C> ifSetMinus(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> WhereSpec<T, C> ifSetMultiply(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> WhereSpec<T, C> ifSetDivide(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> WhereSpec<T, C> ifSetMod(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> WhereSpec<T, C> ifSetPlus(Predicate<C> test, FieldMeta<? super T, F> field, F value);

        <F extends Number> WhereSpec<T, C> ifSetMinus(Predicate<C> test, FieldMeta<? super T, F> field, F value);

        <F extends Number> WhereSpec<T, C> ifSetMultiply(Predicate<C> test, FieldMeta<? super T, F> field, F value);

        <F extends Number> WhereSpec<T, C> ifSetDivide(Predicate<C> test, FieldMeta<? super T, F> field, F value);

        <F extends Number> WhereSpec<T, C> ifSetMod(Predicate<C> test, FieldMeta<? super T, F> field, F value);

    }


    interface WhereSpec<T extends IDomain, C> extends SetSpec<T, C> {

        UpdateSpec where(List<IPredicate> predicates);

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

    interface BatchUpdateSpec<C> {

        <T extends IDomain> BatchRouteSpec<T, C> update(TableMeta<T> table, String tableAlias);
    }

    interface BatchRouteSpec<T extends IDomain, C> extends BatchSetSpec<T, C> {

        BatchSetSpec<T, C> route(int databaseIndex, int tableIndex);

        BatchSetSpec<T, C> route(int tableIndex);
    }


    interface BatchSetSpec<T extends IDomain, C> {

        <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> field, Expression<F> valueExp);

        <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> field);

        <F> BatchWhereSpec<T, C> setDefault(FieldMeta<? super T, F> field);

        <F extends Number> BatchWhereSpec<T, C> setPlus(FieldMeta<? super T, F> field);

        <F extends Number> BatchWhereSpec<T, C> setMinus(FieldMeta<? super T, F> field);

        <F extends Number> BatchWhereSpec<T, C> setMultiply(FieldMeta<? super T, F> field);

        <F extends Number> BatchWhereSpec<T, C> setDivide(FieldMeta<? super T, F> field);

        <F extends Number> BatchWhereSpec<T, C> setMod(FieldMeta<? super T, F> field);

        <F> BatchWhereSpec<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> field);

        <F> BatchWhereSpec<T, C> ifSet(FieldMeta<? super T, F> filed, Function<C, Expression<F>> function);

    }

    interface BatchWhereSpec<T extends IDomain, C> extends BatchSetSpec<T, C> {

        BatchParamSpec<C> where(List<IPredicate> predicates);

        BatchParamSpec<C> where(Function<C, List<IPredicate>> function);

        BatchWhereAndSpec<C> where(IPredicate predicate);
    }

    interface BatchWhereAndSpec<C> {

        BatchWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see Expression#equalIfNonNull(Object)
         */
        BatchWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        BatchWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }

    interface BatchParamSpec<C> {

        UpdateSpec paramMaps(List<Map<String, Object>> mapList);

        UpdateSpec paramMaps(Function<C, List<Map<String, Object>>> function);

        UpdateSpec paramBeans(List<Object> beanList);

        UpdateSpec paramBeans(Function<C, List<Object>> function);
    }


}
