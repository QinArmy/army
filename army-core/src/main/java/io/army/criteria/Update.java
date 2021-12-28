package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Update extends Dml, SQLDebug {


    interface DomainUpdateSpec<C> {

        <T extends IDomain> SetSpec<T, C, WhereSpec<T, C>> update(TableMeta<T> table, String tableAlias);
    }


    interface SetSpec<T extends IDomain, C, S> {

        S set(FieldMeta<? super T, ?> field, @Nullable Object value);

        S set(FieldMeta<? super T, ?> field, Expression<?> value);

        <F> S set(FieldMeta<? super T, F> field, Function<C, Expression<F>> function);

        <F> S set(FieldMeta<? super T, F> field, Supplier<Expression<F>> supplier);

        S setNull(FieldMeta<? super T, ?> field);


        S setDefault(FieldMeta<? super T, ?> field);

        <F extends Number> S setPlus(FieldMeta<? super T, F> field, F value);

        <F extends Number> S setPlus(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> S setMinus(FieldMeta<? super T, F> field, F value);

        <F extends Number> S setMinus(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> S setMultiply(FieldMeta<? super T, F> field, F value);

        <F extends Number> S setMultiply(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> S setDivide(FieldMeta<? super T, F> field, F value);

        <F extends Number> S setDivide(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> S setMod(FieldMeta<? super T, F> field, F value);

        <F extends Number> S setMod(FieldMeta<? super T, F> field, Expression<F> value);

        S ifSet(List<FieldMeta<? super T, ?>> fieldList, List<Expression<?>> valueList);

        <F> S ifSet(FieldMeta<? super T, F> field, @Nullable F value);

        <F> S ifSet(FieldMeta<? super T, F> field, Function<C, Expression<F>> function);

        <F> S ifSet(FieldMeta<? super T, F> field, Supplier<Expression<F>> supplier);

        <F extends Number> S ifSetPlus(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> S ifSetMinus(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> S ifSetMultiply(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> S ifSetDivide(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> S ifSetMod(FieldMeta<? super T, F> field, @Nullable F value);


    }


    interface WhereSpec<T extends IDomain, C> extends SetSpec<T, C, WhereSpec<T, C>> {

        DmlSpec<Update> where(List<IPredicate> predicates);

        DmlSpec<Update> where(Function<C, List<IPredicate>> function);

        DmlSpec<Update> where(Supplier<List<IPredicate>> supplier);

        WhereAndSpec<C, Update> where(IPredicate predicate);

    }



    /*################################## blow batch update interface ##################################*/

    interface BatchUpdateSpec<C> {

        <T extends IDomain> BatchSetSpec<T, C, BatchWhereSpec<T, C>> update(TableMeta<T> table, String tableAlias);
    }


    interface BatchSetSpec<T extends IDomain, C, S> {

        S set(List<FieldMeta<? super T, ?>> fieldList);

        <F> S set(FieldMeta<? super T, F> field, Expression<F> valueExp);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        <F> S set(FieldMeta<? super T, F> field);

        <F> S setNull(FieldMeta<? super T, F> field);

        <F> S setDefault(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> S setPlus(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> S setMinus(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> S setMultiply(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> S setDivide(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> S setMod(FieldMeta<? super T, F> field);

        S ifSet(Function<C, List<FieldMeta<? super T, ?>>> function);

        <F> S ifSet(Predicate<C> test, FieldMeta<? super T, F> field);

        <F> S ifSet(FieldMeta<? super T, F> filed, Function<C, Expression<F>> function);

    }

    interface BatchWhereSpec<T extends IDomain, C> extends Update.BatchSetSpec<T, C, BatchWhereSpec<T, C>> {

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         * @see SQLs#nonNullNamedParam(String, ParamMeta)
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         */
        BatchParamSpec<C, Update> where(List<IPredicate> predicates);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         * @see SQLs#nonNullNamedParam(String, ParamMeta)
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         */
        BatchParamSpec<C, Update> where(Function<C, List<IPredicate>> function);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         * @see SQLs#nonNullNamedParam(String, ParamMeta)
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         */
        BatchParamSpec<C, Update> where(Supplier<List<IPredicate>> supplier);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         * @see SQLs#nonNullNamedParam(String, ParamMeta)
         * @see SQLs#namedParam(GenericField)
         * @see SQLs#namedParam(String, ParamMeta)
         */
        BatchWhereAndSpec<C, Update> where(IPredicate predicate);

    }


}
