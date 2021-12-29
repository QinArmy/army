package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.domain.IDomain;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Update extends Dml, SQLDebug {


    interface UpdateSpec {

        Update asUpdate();
    }


    interface StandardUpdateSpec<C> {

        <T extends IDomain> StandardSetSpec<T, C> update(TableMeta<T> table, String tableAlias);

    }

    interface StandardSetSpec<T extends IDomain, C> extends Update.SingleSetClause<T, C, StandardWhereSpec<T, C>> {

    }


    interface StandardWhereSpec<T extends IDomain, C> extends StandardSetSpec<T, C>
            , Statement.WhereClause<C, Update.UpdateSpec, StandardWhereAndSpec<C>> {


    }

    interface StandardWhereAndSpec<C> extends Statement.WhereAndClause<C, StandardWhereAndSpec<C>>, Update.UpdateSpec {

    }


    interface SingleSetClause<T extends IDomain, C, SR> {

        SR set(List<FieldMeta<? super T, ?>> fieldList, List<Expression<?>> valueList);

        SR set(FieldMeta<? super T, ?> field, @Nullable Object value);

        SR set(FieldMeta<? super T, ?> field, Expression<?> value);

        <F> SR set(FieldMeta<? super T, F> field, Function<C, Expression<F>> function);

        <F> SR set(FieldMeta<? super T, F> field, Supplier<Expression<F>> supplier);

        SR setNull(FieldMeta<? super T, ?> field);

        SR setDefault(FieldMeta<? super T, ?> field);

        <F extends Number> SR setPlus(FieldMeta<? super T, F> field, F value);

        <F extends Number> SR setPlus(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> SR setMinus(FieldMeta<? super T, F> field, F value);

        <F extends Number> SR setMinus(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> SR setMultiply(FieldMeta<? super T, F> field, F value);

        <F extends Number> SR setMultiply(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> SR setDivide(FieldMeta<? super T, F> field, F value);

        <F extends Number> SR setDivide(FieldMeta<? super T, F> field, Expression<F> value);

        <F extends Number> SR setMod(FieldMeta<? super T, F> field, F value);

        <F extends Number> SR setMod(FieldMeta<? super T, F> field, Expression<F> value);

        SR ifSet(List<FieldMeta<? super T, ?>> fieldList, List<Expression<?>> valueList);

        SR ifSetNull(Predicate<C> predicate, FieldMeta<? super T, ?> field);

        SR ifSetDefault(Predicate<C> predicate, FieldMeta<? super T, ?> field);

        <F> SR ifSet(FieldMeta<? super T, F> field, @Nullable F value);

        <F> SR ifSet(FieldMeta<? super T, F> field, Function<C, Expression<F>> function);

        <F> SR ifSet(FieldMeta<? super T, F> field, Supplier<Expression<F>> supplier);

        <F extends Number> SR ifSetPlus(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> SR ifSetMinus(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> SR ifSetMultiply(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> SR ifSetDivide(FieldMeta<? super T, F> field, @Nullable F value);

        <F extends Number> SR ifSetMod(FieldMeta<? super T, F> field, @Nullable F value);


    }





    /*################################## blow batch update interface ##################################*/

    interface StandardBatchUpdateSpec<C> {

        <T extends IDomain> StandardBatchSetSpec<T, C> update(TableMeta<T> table, String tableAlias);
    }

    interface StandardBatchParamSpec<C> extends Statement.BatchParamClause<C, Update.UpdateSpec> {

    }

    interface StandardBatchWhereAndSpec<C> extends Update.StandardBatchParamSpec<C>
            , Statement.WhereAndClause<C, Update.StandardBatchWhereAndSpec<C>> {

    }

    interface StandardBatchSetSpec<T extends IDomain, C>
            extends Update.BatchSingleSetClause<T, C, Update.StandardBatchWhereSpec<T, C>> {


    }

    interface StandardBatchWhereSpec<T extends IDomain, C> extends StandardBatchSetSpec<T, C>
            , Statement.WhereClause<C, Update.StandardBatchParamSpec<C>, Update.StandardBatchWhereAndSpec<C>> {

    }


    interface BatchSingleSetClause<T extends IDomain, C, SR> {

        SR set(List<FieldMeta<? super T, ?>> fieldList);

        <F> SR set(FieldMeta<? super T, F> field, Expression<F> value);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        <F> SR set(FieldMeta<? super T, F> field);

        <F> SR setNull(FieldMeta<? super T, F> field);

        <F> SR setDefault(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> SR setPlus(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> SR setMinus(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> SR setMultiply(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> SR setDivide(FieldMeta<? super T, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> SR setMod(FieldMeta<? super T, F> field);

        SR ifSet(Function<C, List<FieldMeta<? super T, ?>>> function);

        <F> SR ifSet(Predicate<C> test, FieldMeta<? super T, F> field);

        <F> SR ifSet(FieldMeta<? super T, F> filed, Function<C, Expression<F>> function);

    }


    interface MultiSetSpec<C, W> {

        W set(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList);

        W set(FieldMeta<?, ?> field, @Nullable Object value);

        W set(FieldMeta<?, ?> field, Expression<?> value);

        W set(FieldMeta<?, ?> field, Function<C, Expression<?>> function);

        W set(FieldMeta<?, ?> field, Supplier<Expression<?>> supplier);

        W setNull(FieldMeta<?, ?> field);

        W setDefault(FieldMeta<?, ?> field);

        W ifSetNull(Predicate<C> predicate, FieldMeta<?, ?> field);

        W ifSetDefault(Predicate<C> predicate, FieldMeta<?, ?> field);

        <F extends Number> W setPlus(FieldMeta<?, F> field, F value);

        <F extends Number> W setPlus(FieldMeta<?, F> field, Expression<?> value);

        <F extends Number> W setMinus(FieldMeta<?, F> field, F value);

        <F extends Number> W setMinus(FieldMeta<?, F> field, Expression<?> value);

        <F extends Number> W setMultiply(FieldMeta<?, F> field, F value);

        <F extends Number> W setMultiply(FieldMeta<?, F> field, Expression<?> value);

        <F extends Number> W setDivide(FieldMeta<?, F> field, F value);

        <F extends Number> W setDivide(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> W setMod(FieldMeta<?, F> field, F value);

        <F extends Number> W setMod(FieldMeta<?, F> field, Expression<F> value);

        W ifSet(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList);

        W ifSet(FieldMeta<?, ?> field, @Nullable Object value);

        W ifSet(FieldMeta<?, ?> field, Function<C, Expression<?>> function);

        W ifSet(FieldMeta<?, ?> field, Supplier<Expression<?>> supplier);

        <F extends Number> W ifSetPlus(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> W ifSetMinus(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> W ifSetMultiply(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> W ifSetDivide(FieldMeta<?, ?> field, @Nullable F value);

        <F extends Number> W ifSetMod(FieldMeta<?, ?> field, @Nullable F value);

    }

    interface MultiWhereSpec<C> extends Update.MultiSetSpec<C, Update.MultiWhereSpec<C>> {

        DmlSpec<Update> where(List<IPredicate> predicates);

        DmlSpec<Update> where(Function<C, List<IPredicate>> function);

        DmlSpec<Update> where(Supplier<List<IPredicate>> supplier);

        WhereAndSpec<C, Update> where(IPredicate predicate);

    }


    /*################################## blow batch multi-table update api  ##################################*/

    /**
     * @param <C> criteria used to create dynamic update statement
     * @param <W> multi-table update statement where spec type,for example {@link BatchMultiWhereSpec}
     */
    interface BatchMultiSetClause<C, W> {

        W set(List<FieldMeta<?, ?>> fieldList);

        W set(FieldMeta<?, ?> field);

        W set(FieldMeta<?, ?> field, Expression<?> value);

        W set(FieldMeta<?, ?> field, Function<C, Expression<?>> function);

        W set(FieldMeta<?, ?> field, Supplier<Expression<?>> supplier);

        W setNull(FieldMeta<?, ?> field);

        W setDefault(FieldMeta<?, ?> field);

        <F extends Number> W setPlus(FieldMeta<?, F> field);

        <F extends Number> W setPlus(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> W setMinus(FieldMeta<?, F> field);

        <F extends Number> W setMinus(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> W setMultiply(FieldMeta<?, F> field);

        <F extends Number> W setMultiply(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> W setDivide(FieldMeta<?, F> field);

        <F extends Number> W setDivide(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> W setMod(FieldMeta<?, F> field);

        <F extends Number> W setMod(FieldMeta<?, F> field, Expression<F> value);

        W ifSetDefault(Predicate<C> predicate, FieldMeta<?, ?> field);

        W ifSetNull(Predicate<C> predicate, FieldMeta<?, ?> field);

        W ifSet(Function<C, List<FieldMeta<?, ?>>> function);

        W ifSet(Supplier<List<FieldMeta<?, ?>>> supplier);

        W ifSet(Predicate<C> predicate, FieldMeta<?, ?> field);

        <F> W ifSet(FieldMeta<?, ?> field, Function<C, Expression<F>> function);

        <F> W ifSet(FieldMeta<?, ?> field, Supplier<Expression<F>> supplier);

        <F extends Number> W ifSetPlus(Predicate<C> predicate, FieldMeta<?, F> field);

        <F extends Number> W ifSetMinus(Predicate<C> predicate, FieldMeta<?, F> field);

        <F extends Number> W ifSetMultiply(Predicate<C> predicate, FieldMeta<?, F> field);

        <F extends Number> W ifSetDivide(Predicate<C> predicate, FieldMeta<?, F> field);

        <F extends Number> W ifSetMod(Predicate<C> predicate, FieldMeta<?, F> field);

    }


    interface BatchMultiWhereSpec<C> extends BatchMultiSetClause<C, BatchMultiWhereSpec<C>> {

        BatchParamSpec<C, Update> where(List<IPredicate> predicates);

        BatchParamSpec<C, Update> where(Function<C, List<IPredicate>> function);

        BatchParamSpec<C, Update> where(Supplier<List<IPredicate>> supplier);

        BatchWhereAndSpec<C, Update> where(IPredicate predicate);

    }


}
