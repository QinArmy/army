package io.army.criteria;


import io.army.lang.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @since 1.0
 */
public interface Update extends NarrowDmlStatement, DmlStatement.DmlUpdate {

    @Deprecated
    interface _UpdateSpec extends DmlStatement._DmlUpdateSpec<Update> {

    }


    interface _ItemPairBuilder {

    }

    interface _DynamicSetClause<B extends _ItemPairBuilder, SR> {
        SR set(Consumer<B> consumer);

    }

    /**
     * @param <SR> java type of next clause.
     */
    interface _StaticSetClause<F extends DataField, SR> {

        SR set(F field, Expression value);

        SR set(F field, Supplier<Expression> supplier);

        SR set(F field, Function<F, Expression> function);

        <E> SR set(F field, BiFunction<F, E, Expression> valueOperator, @Nullable E value);

        <E> SR set(F field, BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier);

        SR set(F field, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <E> SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, E, Expression> valueOperator, @Nullable E value);

        <E> SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier);

        SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        SR ifSet(F field, Supplier<Expression> supplier);

        SR ifSet(F field, Function<F, Expression> function);

        <E> SR ifSet(F field, BiFunction<F, E, Expression> valueOperator, @Nullable E value);

        <E> SR ifSet(F field, BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier);

        SR ifSet(F field, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <E> SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, E, Expression> valueOperator, @Nullable E value);

        <E> SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, E, Expression> valueOperator, Supplier<E> supplier);

        SR ifSet(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

    }


    /**
     * @param <SR> java type of next clause.
     */
    @Deprecated
    interface _SimpleSetClause<F extends DataField, SR> extends _StaticSetClause<F, SR> {

    }


    /**
     * @param <SR> java type of next clause.
     */
    interface _StaticBatchSetClause<F extends DataField, SR> extends _StaticSetClause<F, SR> {

        /**
         * @see #set(DataField, BiFunction, Object)
         */
        SR set(F field, BiFunction<F, String, Expression> valueOperator);

        SR set(F field, BiFunction<F, Expression, ItemPair> fieldOperator, BiFunction<F, String, Expression> valueOperator);

    }


    interface _StaticRowSetClause<F extends DataField, SR> extends _StaticSetClause<F, SR> {

        SR set(F field1, F field2, Supplier<SubQuery> supplier);

        SR set(F field1, F field2, F field3, Supplier<SubQuery> supplier);

        SR set(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier);

        SR set(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier);

    }

    interface _UpdateWhereAndClause<WA> extends Statement._WhereAndClause<WA> {

        <T> WA and(BiFunction<BiFunction<Expression, T, Expression>, T, Expression> expOperator1, BiFunction<Expression, T, Expression> operator, T operand1, BiFunction<Expression, Expression, IPredicate> expOperator2, Number numberOperand);

        <T> WA ifAnd(BiFunction<BiFunction<Expression, T, Expression>, T, Expression> expOperator1, BiFunction<Expression, T, Expression> operator, @Nullable T operand1, BiFunction<Expression, Expression, IPredicate> expOperator2, @Nullable Number numberOperand);

    }




}
