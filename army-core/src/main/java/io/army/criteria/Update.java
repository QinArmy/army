package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Update extends Statement, SQLDebug {


    interface UpdateSpec {

        Update asUpdate();
    }


    interface DomainUpdateSpec<C> {

        <T extends IDomain> SetSpec<T, C> update(TableMeta<T> table, String tableAlias);
    }


    interface SetSpec<T extends IDomain, C> {

        WhereSpec<T, C> set(FieldMeta<? super T, ?> field, @Nullable Object value);

        WhereSpec<T, C> set(FieldMeta<? super T, ?> field, Expression<?> value);

        <F> WhereSpec<T, C> set(FieldMeta<? super T, F> field, Function<C, Expression<F>> function);

        <F> WhereSpec<T, C> set(FieldMeta<? super T, F> field, Supplier<Expression<F>> supplier);

        WhereSpec<T, C> setNull(FieldMeta<? super T, ?> field);

        /**
         * @see SQLs#defaultWord()
         */
        WhereSpec<T, C> setDefault(FieldMeta<? super T, ?> field);

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

        <F> WhereSpec<T, C> ifSet(FieldMeta<? super T, F> field, Function<C, Expression<F>> function);

        <F> WhereSpec<T, C> ifSet(FieldMeta<? super T, F> field, Supplier<Expression<F>> supplier);

        <F extends Number> WhereSpec<T, C> ifSetPlus(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> WhereSpec<T, C> ifSetMinus(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> WhereSpec<T, C> ifSetMultiply(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> WhereSpec<T, C> ifSetDivide(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> WhereSpec<T, C> ifSetMod(FieldMeta<? super T, F> field, @Nullable F value);


    }


    interface WhereSpec<T extends IDomain, C> extends SetSpec<T, C> {

        UpdateSpec where(List<IPredicate> predicates);

        UpdateSpec where(Function<C, List<IPredicate>> function);

        UpdateSpec where(Supplier<List<IPredicate>> supplier);

        WhereAndSpec<T, C> where(IPredicate predicate);

    }


    interface WhereAndSpec<T extends IDomain, C> extends UpdateSpec {

        WhereAndSpec<T, C> and(IPredicate predicate);

        WhereAndSpec<T, C> and(Function<C, IPredicate> function);

        WhereAndSpec<T, C> and(Supplier<IPredicate> supplier);

        /**
         * @see Expression#ifEqual(Object)
         */
        WhereAndSpec<T, C> ifAnd(@Nullable IPredicate predicate);

        WhereAndSpec<T, C> ifAnd(Function<C, IPredicate> function);

        WhereAndSpec<T, C> ifAnd(Supplier<IPredicate> supplier);

    }

    /*################################## blow batch update interface ##################################*/

    interface BatchUpdateSpec<C> {

        <T extends IDomain> BatchSetSpec<T, C> update(TableMeta<T> table, String tableAlias);
    }


    interface BatchSetSpec<T extends IDomain, C> {

        <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> field, Expression<F> valueExp);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        <F> BatchWhereSpec<T, C> set(FieldMeta<? super T, F> field);

        <F> BatchWhereSpec<T, C> setNull(FieldMeta<? super T, F> field);

        <F> BatchWhereSpec<T, C> setDefault(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> BatchWhereSpec<T, C> setPlus(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> BatchWhereSpec<T, C> setMinus(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> BatchWhereSpec<T, C> setMultiply(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> BatchWhereSpec<T, C> setDivide(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> BatchWhereSpec<T, C> setMod(FieldMeta<? super T, F> field);

        <F> BatchWhereSpec<T, C> ifSet(Predicate<C> test, FieldMeta<? super T, F> field);

        <F> BatchWhereSpec<T, C> ifSet(FieldMeta<? super T, F> filed, Function<C, Expression<F>> function);

    }

    interface BatchWhereSpec<T extends IDomain, C> extends BatchSetSpec<T, C> {

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         * @see SQLs#nonNullNamedParam(String, ParamMeta)
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         */
        BatchParamSpec<C> where(List<IPredicate> predicates);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         * @see SQLs#nonNullNamedParam(String, ParamMeta)
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         */
        BatchParamSpec<C> where(Function<C, List<IPredicate>> function);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         * @see SQLs#nonNullNamedParam(String, ParamMeta)
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         */
        BatchParamSpec<C> where(Supplier<List<IPredicate>> supplier);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         * @see SQLs#nonNullNamedParam(String, ParamMeta)
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         */
        BatchWhereAndSpec<C> where(IPredicate predicate);
    }

    interface BatchWhereAndSpec<C> {

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         * @see SQLs#nonNullNamedParam(String, ParamMeta)
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         */
        BatchWhereAndSpec<C> and(IPredicate predicate);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         * @see SQLs#nonNullNamedParam(String, ParamMeta)
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         * @see Expression#ifEqual(Object)
         */
        BatchWhereAndSpec<C> ifAnd(@Nullable IPredicate predicate);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         * @see SQLs#nonNullNamedParam(String, ParamMeta)
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         */
        BatchWhereAndSpec<C> ifAnd(Function<C, IPredicate> function);

    }

    interface BatchParamSpec<C> {

        UpdateSpec paramMaps(List<Map<String, Object>> mapList);

        UpdateSpec paramMaps(Function<C, List<Map<String, Object>>> function);

        UpdateSpec paramBeans(List<Object> beanList);

        UpdateSpec paramBeans(Function<C, List<Object>> function);
    }


}
