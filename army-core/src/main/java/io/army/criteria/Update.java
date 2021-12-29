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

public interface Update extends Statement {


    interface UpdateSpec {

        Update asUpdate();
    }


    interface StandardUpdateSpec<C> {

        StandardSetSpec<C> update(TableMeta<?> table, String tableAlias);

    }

    interface StandardSetSpec<C> extends SetClause<C, StandardWhereSpec<C>> {

    }


    interface StandardWhereSpec<C> extends StandardSetSpec<C>
            , Statement.WhereClause<C, Update.UpdateSpec, StandardWhereAndSpec<C>> {


    }

    interface StandardWhereAndSpec<C> extends Statement.WhereAndClause<C, StandardWhereAndSpec<C>>, Update.UpdateSpec {

    }


    interface SetClause<C, SR> {

        SR set(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList);

        SR set(FieldMeta<?, ?> field, @Nullable Object value);

        SR set(FieldMeta<?, ?> field, Expression<?> value);

        <F> SR set(FieldMeta<?, F> field, Function<C, Expression<F>> function);

        <F> SR set(FieldMeta<?, F> field, Supplier<Expression<F>> supplier);

        SR setNull(FieldMeta<?, ?> field);

        SR setDefault(FieldMeta<?, ?> field);

        <F extends Number> SR setPlus(FieldMeta<?, F> field, F value);

        <F extends Number> SR setPlus(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> SR setMinus(FieldMeta<?, F> field, F value);

        <F extends Number> SR setMinus(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> SR setMultiply(FieldMeta<?, F> field, F value);

        <F extends Number> SR setMultiply(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> SR setDivide(FieldMeta<?, F> field, F value);

        <F extends Number> SR setDivide(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> SR setMod(FieldMeta<?, F> field, F value);

        <F extends Number> SR setMod(FieldMeta<?, F> field, Expression<F> value);

        SR ifSet(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList);

        SR ifSetNull(Predicate<C> predicate, FieldMeta<?, ?> field);

        SR ifSetDefault(Predicate<C> predicate, FieldMeta<?, ?> field);

        <F> SR ifSet(FieldMeta<?, F> field, @Nullable F value);

        <F> SR ifSet(FieldMeta<?, F> field, Function<C, Expression<F>> function);

        <F> SR ifSet(FieldMeta<?, F> field, Supplier<Expression<F>> supplier);

        <F extends Number> SR ifSetPlus(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetMinus(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetMultiply(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetDivide(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetMod(FieldMeta<?, F> field, @Nullable F value);


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
            extends BatchSetClause<T, C, StandardBatchWhereSpec<T, C>> {


    }

    interface StandardBatchWhereSpec<T extends IDomain, C> extends StandardBatchSetSpec<T, C>
            , Statement.WhereClause<C, Update.StandardBatchParamSpec<C>, Update.StandardBatchWhereAndSpec<C>> {

    }


    interface BatchSetClause<T extends IDomain, C, SR> {

        SR set(List<FieldMeta<?, ?>> fieldList);

        <F> SR set(FieldMeta<?, F> field, Expression<F> value);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        <F> SR set(FieldMeta<?, F> field);

        <F> SR setNull(FieldMeta<?, F> field);

        <F> SR setDefault(FieldMeta<?, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> SR setPlus(FieldMeta<?, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> SR setMinus(FieldMeta<?, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> SR setMultiply(FieldMeta<?, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> SR setDivide(FieldMeta<?, F> field);

        /**
         * @see SQLs#nonNullNamedParam(GenericField)
         */
        <F extends Number> SR setMod(FieldMeta<?, F> field);

        SR ifSet(Function<C, List<FieldMeta<?, ?>>> function);

        <F> SR ifSet(Predicate<C> test, FieldMeta<?, F> field);

        <F> SR ifSet(FieldMeta<?, F> filed, Function<C, Expression<F>> function);

    }





    /*################################## blow batch multi-table update api  ##################################*/



}
