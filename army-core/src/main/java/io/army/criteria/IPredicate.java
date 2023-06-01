package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.*;
import io.army.mapping.BooleanType;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public interface IPredicate extends Expression, Statement._WhereAndClause<IPredicate> {

    /**
     * @return always return {@link BooleanType}
     */
    @Override
    BooleanType typeMeta();

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR predicate)
     * </p>
     */
    SimplePredicate or(IPredicate predicate);

    SimplePredicate or(Supplier<IPredicate> supplier);

    SimplePredicate or(Function<Expression, IPredicate> expOperator, Expression operand);

    <E extends RightOperand> SimplePredicate or(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    <T> SimplePredicate or(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                           BiFunction<SimpleExpression, T, Expression> operator, T value);

    SimplePredicate or(BiFunction<TeNamedOperator<SQLField>, Integer, IPredicate> expOperator,
                       TeNamedOperator<SQLField> namedOperator, int size);

    <T> SimplePredicate or(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                           T firstValue, SQLs.WordAnd and, T secondValue);

    <T, U> SimplePredicate or(BetweenDualOperator<T, U> expOperator, BiFunction<SimpleExpression, T, Expression> firstFunc,
                              T firstValue, SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFunc, U secondValue);

    SimplePredicate or(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    SimplePredicate or(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator, String paramName, int size);

    SimplePredicate or(Consumer<Consumer<IPredicate>> consumer);

    IPredicate ifOr(Supplier<IPredicate> supplier);

    <T> IPredicate ifOr(Function<T, IPredicate> expOperator, Supplier<T> supplier);

    <T> IPredicate ifOr(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                        BiFunction<SimpleExpression, T, Expression> operator, Supplier<T> getter);

    IPredicate ifOr(BiFunction<TeNamedOperator<SQLField>, Integer, IPredicate> expOperator,
                    TeNamedOperator<SQLField> namedOperator, Supplier<Integer> supplier);

    <K, V> IPredicate ifOr(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                           BiFunction<SimpleExpression, V, Expression> operator, Function<K, V> function, K keyName);


    <T> IPredicate ifOr(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                        Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    <T, U> IPredicate ifOr(BetweenDualOperator<T, U> expOperator, BiFunction<SimpleExpression, T, Expression> firstFunc,
                           Supplier<T> firstGetter, SQLs.WordAnd and,
                           BiFunction<SimpleExpression, U, Expression> secondFunc, Supplier<U> secondGetter);

    IPredicate ifOr(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator, String paramName,
                    Supplier<Integer> supplier);

    IPredicate ifOr(Consumer<Consumer<IPredicate>> consumer);


}
