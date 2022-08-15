package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import java.util.function.*;

/**
 * @since 1.0
 */
public interface Update extends NarrowDmlStatement, DmlStatement.DmlUpdate {


    interface _UpdateSpec extends DmlStatement._DmlUpdateSpec<Update> {

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

        SR ifNonNullSet(F field, BiFunction<DataField, Object, ItemPair> operator, @Nullable Object operand);

        SR ifSetLiteral(F field, Supplier<?> supplier);

        SR ifSetLiteral(F field, Function<String, ?> function, String keyName);

        SR ifSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, Supplier<?> supplier);

        SR ifNonNullSetLiteral(F field, BiFunction<DataField, Object, ItemPair> operator, @Nullable Object operand);

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


    interface _StandardWhereAndSpec<C> extends _WhereAndClause<C, _StandardWhereAndSpec<C>>, _UpdateSpec {

    }

    interface _StandardWhereSpec<C, F extends TableField> extends _StandardSetClause<C, F>
            , _WhereClause<C, _UpdateSpec, _StandardWhereAndSpec<C>> {


    }

    interface _StandardSetClause<C, F extends TableField> extends _SimpleSetClause<C, F, _StandardWhereSpec<C, F>> {

    }

    interface _StandardSingleUpdateClause<C> {

        <T> _StandardSetClause<C, FieldMeta<T>> update(SingleTableMeta<T> table, String tableAlias);

        <P> _StandardSetClause<C, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, String tableAlias);

    }

    interface _StandardDomainUpdateClause<C> {

      <T> _StandardSetClause<C, FieldMeta<? super T>> update(TableMeta<T> table, String tableAlias);

    }


    /*################################## blow batch update interface ##################################*/

    interface _StandardBatchWhereAndSpec<C> extends _WhereAndClause<C, _StandardBatchWhereAndSpec<C>>
            , _BatchParamClause<C, _UpdateSpec> {

    }

    interface _StandardBatchWhereSpec<C, F extends TableField> extends _StandardBatchSetClause<C, F>
            , _WhereClause<C, _BatchParamClause<C, _UpdateSpec>, _StandardBatchWhereAndSpec<C>>
            , _BatchParamClause<C, _UpdateSpec> {

    }


    interface _StandardBatchSetClause<C, F extends TableField>
            extends _BatchSetClause<C, F, _StandardBatchWhereSpec<C, F>> {


    }

    interface _StandardBatchSingleUpdateClause<C> {

        <T> _StandardBatchSetClause<C, FieldMeta<T>> update(SingleTableMeta<T> table, String tableAlias);

        <P> _StandardBatchSetClause<C, FieldMeta<P>> update(ComplexTableMeta<P, ?> table, String tableAlias);

    }

    interface _StandardBatchDomainUpdateClause<C> {

        <T> _StandardBatchSetClause<C, FieldMeta<? super T>> update(TableMeta<T> table, String tableAlias);

    }


}
