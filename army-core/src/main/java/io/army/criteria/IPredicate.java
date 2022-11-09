package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.*;
import io.army.lang.Nullable;

import java.util.function.*;


public interface IPredicate extends Expression, Statement._WhereAndClause<IPredicate> {

    @Override
    IPredicate bracket();

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR predicate)
     * </p>
     */
    IPredicate or(IPredicate predicate);

    IPredicate or(Supplier<IPredicate> supplier);

     IPredicate or(Function<Expression, IPredicate> expOperator, Expression operand);

    <E extends RightOperand> IPredicate or(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    <T> IPredicate or(ExpressionOperator<Expression, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    IPredicate or(ExpressionOperator<Expression, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    <T> IPredicate or(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    IPredicate or(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    IPredicate or(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    IPredicate or(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, int size);

    IPredicate or(Consumer<Consumer<IPredicate>> consumer);

    IPredicate ifOr(Supplier<IPredicate> supplier);

    <E> IPredicate ifOr(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    <T> IPredicate ifOr(ExpressionOperator<Expression, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    IPredicate ifOr(ExpressionOperator<Expression, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);


    <T> IPredicate ifOr(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    IPredicate ifOr(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    IPredicate ifOr(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, @Nullable Integer size);

    IPredicate ifOr(Consumer<Consumer<IPredicate>> consumer);

    /**
     * Logical NOT
     * <p>
     * This method representing expression ( NOT this))
     * </p>
     */
    IPredicate not();

    IPredicate ifNot(BooleanSupplier predicate);


}
