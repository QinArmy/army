package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.List;
import java.util.function.Consumer;
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

        <F> SR ifSet(FieldMeta<?, F> field, Function<C, Expression<F>> function);

        <F> SR ifSet(FieldMeta<?, F> field, Supplier<Expression<F>> supplier);

    }

    interface SimpleSetClause<C, SR> extends SetClause<C, SR> {

        SR set(FieldMeta<?, ?> field, @Nullable Object value);

        <F extends Number> SR setPlus(FieldMeta<?, F> field, F value);

        <F extends Number> SR setPlusParam(FieldMeta<?, F> field, F value);

        <F extends Number> SR setPlus(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> SR setMinus(FieldMeta<?, F> field, F value);

        <F extends Number> SR setMinusParam(FieldMeta<?, F> field, F value);

        <F extends Number> SR setMinus(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> SR setMultiply(FieldMeta<?, F> field, F value);

        <F extends Number> SR setMultiplyParam(FieldMeta<?, F> field, F value);

        <F extends Number> SR setMultiply(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> SR setDivide(FieldMeta<?, F> field, F value);

        <F extends Number> SR setDivideParam(FieldMeta<?, F> field, F value);

        <F extends Number> SR setDivide(FieldMeta<?, F> field, Expression<F> value);

        <F extends Number> SR setMod(FieldMeta<?, F> field, F value);

        <F extends Number> SR setModParam(FieldMeta<?, F> field, F value);

        <F extends Number> SR setMod(FieldMeta<?, F> field, Expression<F> value);

        SR setParam(FieldMeta<?, ?> field, @Nullable Object value);

        /**
         * @param pairList non-null and non-empty.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR setPairs(List<ItemPair> pairList);

        /**
         * @param supplier supply  non-null and non-empty list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR setPairs(Supplier<List<ItemPair>> supplier);

        /**
         * @param function supply  non-null and non-empty list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR setPairs(Function<C, List<ItemPair>> function);

        /**
         * @param consumer supply  non-null and non-empty list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR setPairs(Consumer<List<ItemPair>> consumer);

        /**
         * @param pairList non-null list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR ifSetPairs(List<ItemPair> pairList);

        /**
         * @param supplier supply non-null list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR ifSetPairs(Supplier<List<ItemPair>> supplier);

        /**
         * @param function supply non-null list.
         * @see SQLs#itemPair(FieldMeta, Object)
         */
        SR ifSetPairs(Function<C, List<ItemPair>> function);

        SR ifSet(FieldMeta<?, ?> field, @Nullable Object value);

        SR ifSetParam(FieldMeta<?, ?> field, @Nullable Object value);

        <F extends Number> SR ifSetPlus(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetMinus(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetMultiply(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetDivide(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetMod(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetPlusParam(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetMinusParam(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetMultiplyParam(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetDivideParam(FieldMeta<?, F> field, @Nullable F value);

        <F extends Number> SR ifSetModParam(FieldMeta<?, F> field, @Nullable F value);


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

        SR setNullable(List<FieldMeta<?, ?>> fieldList);

        SR set(List<FieldMeta<?, ?>> fieldList);

        SR set(Consumer<List<FieldMeta<?, ?>>> consumer);

        SR setNullable(Consumer<List<FieldMeta<?, ?>>> consumer);

        SR set(Function<C, List<FieldMeta<?, ?>>> function);

        SR setNullable(Function<C, List<FieldMeta<?, ?>>> function);

        SR set(Supplier<List<FieldMeta<?, ?>>> supplier);

        SR setNullable(Supplier<List<FieldMeta<?, ?>>> supplier);

        /**
         * @see SQLs#nullableNamedParam(GenericField)
         */
        <F> SR setNullable(FieldMeta<?, F> field);

        <F> SR set(FieldMeta<?, F> field);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        <F extends Number> SR setPlus(FieldMeta<?, F> field);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        <F extends Number> SR setMinus(FieldMeta<?, F> field);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        <F extends Number> SR setMultiply(FieldMeta<?, F> field);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        <F extends Number> SR setDivide(FieldMeta<?, F> field);

        /**
         * @see SQLs#namedParam(GenericField)
         */
        <F extends Number> SR setMod(FieldMeta<?, F> field);

        SR ifSet(Function<C, List<FieldMeta<?, ?>>> function);

        SR ifSetNullable(Function<C, List<FieldMeta<?, ?>>> function);

        SR ifSet(Predicate<C> test, FieldMeta<?, ?> field);

        SR ifSetNullable(Predicate<C> test, FieldMeta<?, ?> field);

    }





    /*################################## blow batch multi-table update api  ##################################*/


}
