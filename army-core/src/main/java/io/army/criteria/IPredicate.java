package io.army.criteria;

import io.army.function.TePredicate;
import io.army.lang.Nullable;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


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

    IPredicate or(Function<Expression, IPredicate> expOperator, Supplier<Expression> supplier);

    <C> IPredicate or(Function<Expression, IPredicate> expOperator, Function<C, Expression> function);

    <T> IPredicate or(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, T operand);

    <T> IPredicate or(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    IPredicate or(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    <T> IPredicate or(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, T first, T second);

    <T> IPredicate or(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier);

    IPredicate or(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

    IPredicate or(Consumer<Consumer<IPredicate>> consumer);

    IPredicate ifOr(Supplier<IPredicate> supplier);

    <C> IPredicate ifOr(Function<C, IPredicate> function);

    IPredicate ifOr(Function<Expression, IPredicate> expOperator, Supplier<Expression> supplier);

    <C> IPredicate ifOr(Function<Expression, IPredicate> expOperator, Function<C, Expression> function);

    <T> IPredicate ifOr(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T operand);

    <T> IPredicate ifOr(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier);

    IPredicate ifOr(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    <T> IPredicate ifOr(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T first, @Nullable T second);

    <T> IPredicate ifOr(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier);

    IPredicate ifOr(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey);

    IPredicate ifOr(Consumer<Consumer<IPredicate>> consumer);


    /**
     * Logical NOT
     * <p>
     * This method representing expression ( NOT this))
     * </p>
     */
    IPredicate not();


}
