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

/**
 * @since 1.0
 */
@SuppressWarnings("unused")
public interface Update extends NarrowDmlStatement {


    interface _UpdateSpec {

        Update asUpdate();
    }

    interface StandardUpdateClause<UR> {

        UR update(TableMeta<?> table, String tableAlias);
    }


    interface StandardUpdateSpec<C> extends Update.StandardUpdateClause<Update.StandardSetSpec<C>> {


    }

    interface StandardSetSpec<C> extends _SimpleSetClause<C, StandardWhereSpec<C>> {

    }


    interface StandardWhereSpec<C> extends StandardSetSpec<C>
            , _WhereClause<C, _UpdateSpec, StandardWhereAndSpec<C>> {


    }

    interface StandardWhereAndSpec<C> extends _WhereAndClause<C, StandardWhereAndSpec<C>>, _UpdateSpec {

    }

    /**
     * @param <C>  java type of criteria object
     * @param <SR> java type of next clause.
     */
    interface SetClause<C, SR> {

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

        SR setExp(TableField<?> field, Supplier<? extends Expression> supplier);

        SR setExp(TableField<?> field, Function<C, ? extends Expression> function);

        SR ifSetExp(TableField<?> field, Supplier<? extends Expression> supplier);

        SR ifSetExp(TableField<?> field, Function<C, ? extends Expression> function);

    }


    /**
     * @param <C>  java type of criteria object
     * @param <SR> java type of next clause.
     */
    interface _SimpleSetClause<C, SR> extends SetClause<C, SR> {

        SR set(TableField<?> field, @Nullable Object paramOrExp);

        SR setLiteral(TableField<?> field, @Nullable Object paramOrExp);

        /**
         * <p>
         * output follow below rule:
         *    <ul>
         *        <li>parameter,equivalence : this.setExp(field, field.plus(parameter)),output : column = column + ?</li>
         *        <li>{@link Expression},equivalence : this.setExp(field, field.plus(exp)),output : column = column + exp</li>
         *    </ul>
         * </p>
         *
         * @param paramOrExp non-null parameter or {@link Expression}.
         */
        SR setPlus(TableField<?> field, Object paramOrExp);

        /**
         * <p>
         * output follow below rule:
         *    <ul>
         *        <li>parameter,equivalence : this.setExp(field, field.plusLiteral(parameter)),output : column = column + literal</li>
         *        <li>{@link Expression},equivalence : this.setExp(field, field.plus(exp)),output : column = column + exp</li>
         *    </ul>
         * </p>
         *
         * @param paramOrExp non-null parameter or {@link Expression}.
         */
        SR setPlusLiteral(TableField<?> field, Object paramOrExp);

        SR setMinus(TableField<?> field, Object paramOrExp);

        SR setMinusLiteral(TableField<?> field, Object paramOrExp);

        SR setMultiply(TableField<?> field, Object paramOrExp);

        SR setMultiplyLiteral(TableField<?> field, Object paramOrExp);

        SR setDivide(TableField<?> field, Object paramOrExp);

        SR setDivideLiteral(TableField<?> field, Object paramOrExp);

        SR setMod(TableField<?> field, Object paramOrExp);

        SR setModLiteral(TableField<?> field, Object paramOrExp);

        SR ifSet(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSet(TableField<?> field, Supplier<?> paramOrExp);

        SR ifSetLiteral(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSetLiteral(TableField<?> field, Supplier<?> paramOrExp);

        SR ifSetPlus(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSetPlus(TableField<?> field, Supplier<?> paramOrExp);

        SR ifSetMinus(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSetMinus(TableField<?> field, Supplier<?> paramOrExp);

        SR ifSetMultiply(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSetMultiply(TableField<?> field, Supplier<?> paramOrExp);

        SR ifSetDivide(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSetDivide(TableField<?> field, Supplier<?> paramOrExp);

        SR ifSetMod(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSetMod(TableField<?> field, Supplier<?> paramOrExp);

        SR ifSetPlusLiteral(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSetPlusLiteral(TableField<?> field, Supplier<?> paramOrExp);

        SR ifSetMinusLiteral(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSetMinusLiteral(TableField<?> field, Supplier<?> paramOrExp);

        SR ifSetMultiplyLiteral(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSetMultiplyLiteral(TableField<?> field, Supplier<?> paramOrExp);

        SR ifSetDivideLiteral(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSetDivideLiteral(TableField<?> field, Supplier<?> paramOrExp);

        SR ifSetModLiteral(TableField<?> field, Function<String, ?> function, String keyName);

        SR ifSetModLiteral(TableField<?> field, Supplier<?> paramOrExp);

    }

    /**
     * @param <C>  java type of criteria object
     * @param <SR> java type of next clause.
     */
    interface _BatchSetClause<C, SR> extends SetClause<C, SR> {

        SR setExp(TableField<?> field, Expression value);

        SR setNullable(List<? extends TableField<?>> fieldList);

        SR set(List<TableField<?>> fieldList);

        SR set(Consumer<List<TableField<?>>> consumer);

        SR setNullable(Consumer<List<TableField<?>>> consumer);

        SR set(Function<C, List<TableField<?>>> function);

        SR setNullable(Function<C, List<TableField<?>>> function);

        SR set(Supplier<List<TableField<?>>> supplier);

        SR setNullable(Supplier<List<TableField<?>>> supplier);

        /**
         * @see SQLs#nullableNamedParam(TableField)
         */
        SR setNullable(TableField<?> field);

        SR set(TableField<?> field);

        /**
         * @see SQLs#namedParam(TableField)
         */
        SR setPlus(TableField<?> field);

        /**
         * @see SQLs#namedParam(TableField)
         */
        SR setMinus(TableField<?> field);

        /**
         * @see SQLs#namedParam(TableField)
         */
        SR setMultiply(TableField<?> field);

        /**
         * @see SQLs#namedParam(TableField)
         */
        SR setDivide(TableField<?> field);

        /**
         * @see SQLs#namedParam(TableField)
         */
        SR setMod(TableField<?> field);

        SR ifSet(Function<C, List<TableField<?>>> function);

        SR ifSetNullable(Function<C, List<TableField<?>>> function);

        SR ifSet(Predicate<C> test, TableField<?> field);

        SR ifSetNullable(Predicate<C> test, TableField<?> field);

    }



    /*################################## blow batch update interface ##################################*/

    interface StandardBatchUpdateSpec<C> extends Update.StandardUpdateClause<Update.StandardBatchSetSpec<C>> {

    }


    interface StandardBatchWhereAndSpec<C> extends _WhereAndClause<C, StandardBatchWhereAndSpec<C>>
            , _BatchParamClause<C, _UpdateSpec> {

    }

    interface StandardBatchSetSpec<C> extends _BatchSetClause<C, StandardBatchWhereSpec<C>> {


    }

    interface StandardBatchWhereSpec<C> extends StandardBatchSetSpec<C>
            , _WhereClause<C, _BatchParamClause<C, _UpdateSpec>, StandardBatchWhereAndSpec<C>>
            , _BatchParamClause<C, _UpdateSpec> {

    }





    /*################################## blow batch multi-table update api  ##################################*/


}
