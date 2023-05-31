package io.army.criteria;


import io.army.criteria.dialect.SubQuery;
import io.army.function.ExpressionOperator;
import io.army.lang.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <p>
 * This interface representing primary update statement.This interface is base interface of below:
 *     <ul>
 *         <li>{@link Update}</li>
 *         <li>{@link BatchUpdate}</li>
 *         <li>{@link io.army.criteria.dialect.ReturningUpdate}</li>
 *         <li>{@link io.army.criteria.dialect.BatchReturningUpdate}</li>
 *     </ul>
 * </p>
 *
 * @since 1.0
 */
public interface UpdateStatement extends NarrowDmlStatement {

    @Deprecated
    interface _UpdateSpec extends DmlStatement._DmlUpdateSpec<UpdateStatement> {

    }


    interface _ItemPairBuilder {

    }

    interface _DynamicSetClause<B extends _ItemPairBuilder, SR> {
        SR sets(Consumer<B> consumer);

    }

    /**
     * @param <SR> java type of next clause.
     */
    interface _StaticSetClause<F extends DataField, SR> {

        SR set(F field, Expression value);

        <R extends AssignmentItem> SR set(F field, Supplier<R> supplier);

        <R extends AssignmentItem> SR set(F field, Function<F, R> function);

        <E, R extends AssignmentItem> SR set(F field, BiFunction<F, E, R> valueOperator, @Nullable E value);

        <K, V, R extends AssignmentItem> SR set(F field, BiFunction<F, V, R> valueOperator, Function<K, V> function, K key);

        <E, V, R extends AssignmentItem> SR set(F field, BiFunction<F, V, R> fieldOperator,
                                                BiFunction<F, E, V> valueOperator, E value);

        <K, V, U, R extends AssignmentItem> SR set(F field, BiFunction<F, U, R> fieldOperator,
                                                   BiFunction<F, V, U> valueOperator, Function<K, V> function, K key);

        <R extends AssignmentItem> SR ifSet(F field, Supplier<R> supplier);

        <R extends AssignmentItem> SR ifSet(F field, Function<F, R> function);

        <E, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, E, R> valueOperator, Supplier<E> supplier);

        <K, V, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, V, R> valueOperator,
                                                  Function<K, V> function, K key);

        <E, V, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, V, R> fieldOperator,
                                                  BiFunction<F, E, V> valueOperator, Supplier<E> getter);

        <K, V, U, R extends AssignmentItem> SR ifSet(F field, BiFunction<F, U, R> fieldOperator,
                                                     BiFunction<F, V, U> valueOperator, Function<K, V> function, K key);

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


        SR setNamed(F field, BiFunction<F, String, Expression> valueOperator);

        <R extends AssignmentItem> SR setNamed(F field, BiFunction<F, Expression, R> fieldOperator, BiFunction<F, String, Expression> valueOperator);

    }


    interface _StaticRowSetClause<F extends DataField, SR> extends _StaticSetClause<F, SR> {

        SR setRow(F field1, F field2, Supplier<SubQuery> supplier);

        SR setRow(F field1, F field2, F field3, Supplier<SubQuery> supplier);

        SR setRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier);

        SR setRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier);

        SR ifSetRow(F field1, F field2, Supplier<SubQuery> supplier);

        SR ifSetRow(F field1, F field2, F field3, Supplier<SubQuery> supplier);

        SR ifSetRow(F field1, F field2, F field3, F field4, Supplier<SubQuery> supplier);

        SR ifSetRow(Consumer<Consumer<F>> consumer, Supplier<SubQuery> supplier);

    }

    interface _BatchRowPairs<F extends DataField> extends _ItemPairBuilder,
            _StaticRowSetClause<F, _BatchRowPairs<F>>,
            _StaticBatchSetClause<F, _BatchRowPairs<F>> {


    }

    interface _ItemPairs<F extends DataField> extends _ItemPairBuilder,
            _StaticSetClause<F, _ItemPairs<F>> {


    }

    interface _BatchItemPairs<F extends DataField> extends _ItemPairBuilder,
            _StaticBatchSetClause<F, _BatchItemPairs<F>> {


    }

    interface _RowPairs<F extends DataField> extends _ItemPairBuilder,
            _StaticRowSetClause<F, _RowPairs<F>> {


    }

    interface _UpdateWhereAndClause<WA> extends Statement._WhereAndClause<WA> {

        <T> WA and(ExpressionOperator<SimpleExpression, T, Expression> expOperator1,
                   BiFunction<SimpleExpression, T, Expression> operator, T operand1,
                   BiFunction<Expression, Expression, IPredicate> expOperator2, Number numberOperand);

        <T> WA ifAnd(ExpressionOperator<SimpleExpression, T, Expression> expOperator1,
                     BiFunction<SimpleExpression, T, Expression> operator, @Nullable T operand1,
                     BiFunction<Expression, Expression, IPredicate> expOperator2, @Nullable Number numberOperand);

        WA and(Function<BiFunction<DataField, String, Expression>, Expression> fieldOperator,
               BiFunction<DataField, String, Expression> operator,
               BiFunction<Expression, Expression, IPredicate> expOperator2, Number numberOperand);

    }


}
