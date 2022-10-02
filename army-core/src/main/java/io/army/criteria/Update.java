package io.army.criteria;


import io.army.criteria.impl.SQLs;
import io.army.lang.Nullable;
import io.army.meta.ComplexTableMeta;
import io.army.meta.FieldMeta;
import io.army.meta.SingleTableMeta;
import io.army.meta.TableMeta;

import java.util.List;
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

        SR set(F field, Expression value);

        SR set(F field, Supplier<Expression> supplier);

        SR set(F field, Function<C, Expression> function);

        <T> SR set(F field, BiFunction<F, T, Expression> valueOperator, @Nullable T value);

        <T> SR set(F field, BiFunction<F, T, Expression> valueOperator, Supplier<T> supplier);

        SR set(F field, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, T, Expression> valueOperator, @Nullable T value);

        <T> SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, T, Expression> valueOperator, Supplier<T> supplier);

        SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> SR ifSet(F field, BiFunction<F, T, Expression> valueOperator, @Nullable T value);

        <T> SR ifSet(F field, BiFunction<F, T, Expression> valueOperator, Supplier<T> supplier);

        SR ifSet(F field, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, T, Expression> valueOperator, @Nullable T value);

        <T> SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, T, Expression> valueOperator, Supplier<T> supplier);

        SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

    }


    /**
     * @param <C>  java type of criteria object
     * @param <SR> java type of next clause.
     */
    interface _SimpleSetClause<C, F extends DataField, SR> extends _SetClause<C, F, SR> {

    }


    /**
     * @param <C>  java type of criteria object
     * @param <SR> java type of next clause.
     */
    interface _BatchSetClause<C, F extends DataField, SR> extends _SetClause<C, F, SR> {

        /**
         * @see #set(DataField, BiFunction, Object)
         */
        SR set(F field, BiFunction<F, String, Expression> valueOperator);

        SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, String, Expression> valueOperator);

        SR setList(List<F> fieldList, BiFunction<F, String, Expression> valueOperator);

        SR setList(List<F> fieldList, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, String, Expression> valueOperator);

        SR ifSetList(List<F> fieldList, BiFunction<F, String, Expression> valueOperator);

        SR ifSetList(List<F> fieldList, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, String, Expression> valueOperator);

    }

    interface _UpdateWhereAndClause<C, WA> extends Statement._WhereAndClause<C, WA> {

        <T> WA and(BiFunction<BiFunction<Expression, T, Expression>, T, Expression> expOperator1, BiFunction<Expression, T, Expression> operator, T operand1, BiFunction<Expression, Expression, IPredicate> expOperator2, Number numberOperand);

        <T> WA ifAnd(BiFunction<BiFunction<Expression, T, Expression>, T, Expression> expOperator1, BiFunction<Expression, T, Expression> operator, @Nullable T operand1, BiFunction<Expression, Expression, IPredicate> expOperator2, @Nullable Number numberOperand);

    }


    interface _StandardWhereAndSpec<C> extends _UpdateWhereAndClause<C, _StandardWhereAndSpec<C>>, _UpdateSpec {

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
