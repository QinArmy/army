package io.army.criteria;

import io.army.function.TePredicate;
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

    <C> IPredicate or(Function<C, IPredicate> function);

    <E extends Expression> IPredicate or(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    <C, E extends Expression> IPredicate or(Function<E, IPredicate> expOperator, Function<C, E> function);

    <T> IPredicate or(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, T operand);

    <T> IPredicate or(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    IPredicate or(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    <T> IPredicate or(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, T first, T second);

    <T> IPredicate or(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier);

    IPredicate or(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

    IPredicate or(Consumer<Consumer<IPredicate>> consumer);

    IPredicate ifOr(Supplier<IPredicate> supplier);

    <C> IPredicate ifOr(Function<C, IPredicate> function);

    <E extends Expression> IPredicate ifOr(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    <C, E extends Expression> IPredicate ifOr(Function<E, IPredicate> expOperator, Function<C, E> function);

    <T> IPredicate ifOr(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T operand);

    <T> IPredicate ifOr(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    IPredicate ifOr(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    <T> IPredicate ifOr(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T first, @Nullable T second);

    <T> IPredicate ifOr(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier);

    IPredicate ifOr(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

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
    <E extends Expression> IPredicate and(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    /**
     * @see Statement._WhereAndClause#and(Function, Function)
     */
    <C, E extends Expression> IPredicate and(Function<E, IPredicate> expOperator, Function<C, E> function);

    /**
     * @see Statement._WhereAndClause#and(BiFunction, BiFunction, Object)
     */
    <T> IPredicate and(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, T operand);

    /**
     * @see Statement._WhereAndClause#and(BiFunction, BiFunction, Supplier)
     */
    <T> IPredicate and(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    /**
     * @see Statement._WhereAndClause#and(BiFunction, BiFunction, Function, String)
     */
    IPredicate and(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    /**
     * @see Statement._WhereAndClause#and(TePredicate, BiFunction, Object, Object)
     */
    <T> IPredicate and(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, T first, T second);

    /**
     * @see Statement._WhereAndClause#and(TePredicate, BiFunction, Supplier, Supplier)
     */
    <T> IPredicate and(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier);

    /**
     * @see Statement._WhereAndClause#and(TePredicate, BiFunction, Function, String, String)
     */
    IPredicate and(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

    IPredicate and(Consumer<Consumer<IPredicate>> consumer);


    /**
     * Logical NOT
     * <p>
     * This method representing expression ( NOT this))
     * </p>
     */
    IPredicate not();

    IPredicate ifNot(BooleanSupplier supplier);


}
