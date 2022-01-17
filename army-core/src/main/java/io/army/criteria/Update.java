package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface Update extends Statement {


    interface UpdateSpec {

        Update asUpdate();
    }

    interface StandardUpdateClause<UR> {

        UR update(TableMeta<?> table, String tableAlias);
    }


    interface StandardUpdateSpec<C> extends Update.StandardUpdateClause<Update.StandardSetSpec<C>> {


    }

    interface StandardSetSpec<C> extends SimpleSetClause<C, StandardWhereSpec<C>> {

    }


    interface StandardWhereSpec<C> extends StandardSetSpec<C>
            , Statement.WhereClause<C, Update.UpdateSpec, StandardWhereAndSpec<C>> {


    }

    interface StandardWhereAndSpec<C> extends Statement.WhereAndClause<C, StandardWhereAndSpec<C>>, Update.UpdateSpec {

    }



    interface SetClause<C, SR> {

        SR set(FieldMeta<?, ?> field, Expression<?> value);

        <F> SR set(FieldMeta<?, F> field, Function<C, Expression<F>> function);

        <F> SR set(FieldMeta<?, F> field, Supplier<Expression<F>> supplier);

        SR setNull(FieldMeta<?, ?> field);

        SR setDefault(FieldMeta<?, ?> field);

        SR ifSetNull(Predicate<C> predicate, FieldMeta<?, ?> field);

        SR ifSetDefault(Predicate<C> predicate, FieldMeta<?, ?> field);

        <F> SR ifSet(FieldMeta<?, F> field, Function<C, Expression<F>> function);

        <F> SR ifSet(FieldMeta<?, F> field, Supplier<Expression<F>> supplier);

    }

    interface SimpleSetClause<C, SR> extends SetClause<C, SR> {

        SR set(FieldMeta<?, ?> field, @Nullable Object value);

        SR set(List<FieldMeta<?, ?>> fieldList, List<Expression<?>> valueList);

        SR setValues(Map<FieldMeta<?, ?>, Expression<?>> fieldValues);

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

        SR ifSetValues(Function<C, Map<FieldMeta<?, ?>, Expression<?>>> function);

        SR ifSetValues(Supplier<Map<FieldMeta<?, ?>, Expression<?>>> supplier);

        <F> SR ifSet(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetPlus(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetMinus(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetMultiply(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetDivide(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetMod(FieldMeta<?, F> field, @Nullable F value);


    }


    /*################################## blow batch update interface ##################################*/

    interface StandardBatchUpdateSpec<C> extends Update.StandardUpdateClause<Update.StandardBatchSetSpec<C>> {

    }


    interface StandardBatchWhereAndSpec<C> extends Statement.WhereAndClause<C, Update.StandardBatchWhereAndSpec<C>>
            , Statement.BatchParamClause<C, Update.UpdateSpec> {

    }

    interface StandardBatchSetSpec<C> extends Update.BatchSetClause<C, StandardBatchWhereSpec<C>> {


    }

    interface StandardBatchWhereSpec<C> extends StandardBatchSetSpec<C>
            , Statement.WhereClause<C, Statement.BatchParamClause<C, Update.UpdateSpec>, Update.StandardBatchWhereAndSpec<C>>
            , Statement.BatchParamClause<C, Update.UpdateSpec> {

    }


    interface BatchSetClause<C, SR> extends SetClause<C, SR> {

        SR set(List<FieldMeta<?, ?>> fieldList);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        <F> SR set(FieldMeta<?, F> field);

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

    }





    /*################################## blow batch multi-table update api  ##################################*/


}
