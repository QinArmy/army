package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.meta.TableMeta;

import java.util.function.*;

/**
 * @since 1.0
 */
@SuppressWarnings("unused")
public interface Update extends NarrowDmlStatement, DmlStatement.DmlUpdate {


    interface _UpdateSpec {

        Update asUpdate();
    }

    interface StandardUpdateClause<UR> {

        UR update(TableMeta<?> table, String tableAlias);
    }


    /**
     * @param <C>  java type of criteria object
     * @param <SR> java type of next clause.
     */
    interface _SetClause<C, F extends DataField, SR> {

        /**
         * @see SQLs#itemPair(DataField, Object)
         */
        SR setPairs(Consumer<Consumer<ItemPair>> consumer);

        /**
         * @see SQLs#itemPair(DataField, Object)
         */
        SR setPairs(BiConsumer<C, Consumer<ItemPair>> consumer);

        SR setExp(F field, Expression value);

        SR setExp(F field, Supplier<? extends Expression> supplier);

        SR setExp(F field, Function<C, ? extends Expression> function);

        SR ifSetExp(F field, Supplier<? extends Expression> supplier);

        SR ifSetExp(F field, Function<C, ? extends Expression> function);


        SR setDefault(F field);

        SR setNull(F field);

    }


    /**
     * @param <C>  java type of criteria object
     * @param <SR> java type of next clause.
     */
    interface _SimpleSetClause<C, F extends DataField, SR> extends _SetClause<C, F, SR> {

        SR set(F field, @Nullable Object value);

        SR setLiteral(F field, @Nullable Object value);

        SR setPlus(F field, Object value);

        SR setMinus(F field, Object value);

        SR setPlusLiteral(F field, Object value);

        SR setMinusLiteral(F field, Object value);

        SR set(F field, BiFunction<DataField, Object, ItemPair> operator, Object value);

        SR setExp(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<? extends Expression> supplier);

        SR setExp(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ? extends Expression> function);

        SR setLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Object value);

        SR ifSet(F field, Supplier<?> supplier);

        SR ifSet(F field, Function<String, ?> function, String keyName);

        SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<?> supplier);

        SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ?> function);

        SR ifSet(F field, BiFunction<DataField, Object, ItemPair> operator, Function<String, ?> function, String keyName);

        SR ifSetLiteral(F field, Supplier<?> supplier);

        SR ifSetLiteral(F field, Function<String, ?> function, String keyName);

        SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<?> supplier);

        SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Function<C, ?> function);

        SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Function<String, ?> function, String keyName);

    }


    /**
     * @param <C>  java type of criteria object
     * @param <SR> java type of next clause.
     */
    interface _BatchSetClause<C, F extends DataField, SR> extends _SetClause<C, F, SR> {

        SR set(F field);

        SR setNullable(F field);

        SR setNamed(F field, String parameterName);

        SR setNullableNamed(F field, String parameterName);

        SR setPlus(F field);

        SR setMinus(F field);

        SR set(F field, BiFunction<DataField, Object, ItemPair> operator);

        SR setNamed(F field, BiFunction<DataField, Object, ItemPair> operator, String parameterName);

        SR setFields(Consumer<Consumer<F>> consumer);

        SR setFields(BiConsumer<C, Consumer<F>> consumer);

        SR setNullableFields(Consumer<Consumer<F>> consumer);

        SR setNullableFields(BiConsumer<C, Consumer<F>> consumer);
    }


    interface StandardUpdateSpec<C> extends Update.StandardUpdateClause<Update.StandardSetSpec<C>> {


    }

    interface StandardSetSpec<C> extends _SimpleSetClause<C, TableField, StandardWhereSpec<C>> {

    }


    interface StandardWhereSpec<C> extends StandardSetSpec<C>
            , _WhereClause<C, _UpdateSpec, StandardWhereAndSpec<C>> {


    }

    interface StandardWhereAndSpec<C> extends _WhereAndClause<C, StandardWhereAndSpec<C>>, _UpdateSpec {

    }



    /*################################## blow batch update interface ##################################*/

    interface StandardBatchUpdateSpec<C> extends Update.StandardUpdateClause<Update.StandardBatchSetSpec<C>> {

    }


    interface StandardBatchWhereAndSpec<C> extends _WhereAndClause<C, StandardBatchWhereAndSpec<C>>
            , _BatchParamClause<C, _UpdateSpec> {

    }

    interface StandardBatchSetSpec<C> extends _BatchSetClause<C, TableField, StandardBatchWhereSpec<C>> {


    }

    interface StandardBatchWhereSpec<C> extends StandardBatchSetSpec<C>
            , _WhereClause<C, _BatchParamClause<C, _UpdateSpec>, StandardBatchWhereAndSpec<C>>
            , _BatchParamClause<C, _UpdateSpec> {

    }





    /*################################## blow batch multi-table update api  ##################################*/


}
