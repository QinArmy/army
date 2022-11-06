package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.*;
import io.army.lang.Nullable;

import java.util.function.*;


public interface IPredicate extends Expression {

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR predicate)
     * </p>
     */
    IPredicate or(IPredicate predicate);

    IPredicate or(Supplier<IPredicate> supplier);

    <E> IPredicate or(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    <T> IPredicate or(ExpressionDualOperator<T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, T operand);

    <T> IPredicate or(ExpressionDualOperator<T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    IPredicate or(ExpressionDualOperator<Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    <T> IPredicate or(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);

    <T> IPredicate or(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, SQLs.WordAnd and, Supplier<T> secondSupplier);

    IPredicate or(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    IPredicate or(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    IPredicate or(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, int size);

    IPredicate or(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator, TeNamedOperator<DataField> namedOperator, int size);


    IPredicate or(Consumer<Consumer<IPredicate>> consumer);

    IPredicate ifOr(Supplier<IPredicate> supplier);

    <E> IPredicate ifOr(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    <T> IPredicate ifOr(ExpressionDualOperator<T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T operand);

    <T> IPredicate ifOr(ExpressionDualOperator<T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    IPredicate ifOr(ExpressionDualOperator<Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    <T> IPredicate ifOr(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T first, SQLs.WordAnd and, @Nullable T second);

    <T> IPredicate ifOr(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, SQLs.WordAnd and, Supplier<T> secondSupplier);

    IPredicate ifOr(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    IPredicate ifOr(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, @Nullable Integer size);

    IPredicate ifOr(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator, TeNamedOperator<DataField> namedOperator, @Nullable Integer size);


    IPredicate ifOr(Consumer<Consumer<IPredicate>> consumer);

    /**
     * Logical AND
     * <p>
     * This method representing expression (this OR predicate)
     * </p>
     *
     * @see Statement._WhereAndClause#and(IPredicate)
     */
    IPredicate and(IPredicate predicate);

    /**
     * @see Statement._WhereAndClause#and(Function, Expression)
     */
    IPredicate and(Function<Expression, IPredicate> expOperator, Expression operand);

    /**
     * @see Statement._WhereAndClause#and(Function, Supplier)
     */
    <E> IPredicate and(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    <T> IPredicate and(ExpressionDualOperator<T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, T operand);

    <T> IPredicate and(ExpressionDualOperator<T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    IPredicate and(ExpressionDualOperator<Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    <T> IPredicate and(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, T first, SQLs.WordAnd and, T second);

    <T> IPredicate and(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, SQLs.WordAnd and, Supplier<T> secondSupplier);

    IPredicate and(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    IPredicate and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    IPredicate and(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, int size);

    IPredicate and(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator, TeNamedOperator<DataField> namedOperator, int size);


    IPredicate and(Consumer<Consumer<IPredicate>> consumer);

    IPredicate ifAnd(Supplier<IPredicate> supplier);

    <E> IPredicate ifAnd(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    <T> IPredicate ifAnd(ExpressionDualOperator<T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T operand);

    <T> IPredicate ifAnd(ExpressionDualOperator<T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    IPredicate ifAnd(ExpressionDualOperator<Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    <T> IPredicate ifAnd(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T first, SQLs.WordAnd and, @Nullable T second);

    <T> IPredicate ifAnd(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, SQLs.WordAnd and, Supplier<T> secondSupplier);

    IPredicate ifAnd(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    IPredicate ifAnd(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    IPredicate ifAnd(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, @Nullable Integer size);

    IPredicate ifAnd(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator, TeNamedOperator<DataField> namedOperator, @Nullable Integer size);


    IPredicate ifAnd(Consumer<Consumer<IPredicate>> consumer);


    /**
     * Logical NOT
     * <p>
     * This method representing expression ( NOT this))
     * </p>
     */
    IPredicate not();

    IPredicate ifNot(BooleanSupplier supplier);


}
