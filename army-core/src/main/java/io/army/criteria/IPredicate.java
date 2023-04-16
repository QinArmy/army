package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.*;
import io.army.mapping.BooleanType;
import io.army.meta.TypeMeta;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


public interface IPredicate extends Expression, Statement._WhereAndClause<IPredicate> {

    /**
     * @return always return {@link BooleanType}
     */
    @Override
    TypeMeta typeMeta();

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


    SimplePredicate or(ExpressionOperator<Expression, Expression, IPredicate> expOperator,
                       BiFunction<Expression, Expression, Expression> operator, Expression expression);

    SimplePredicate or(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                       BiFunction<Expression, Object, Expression> operator, Object value);

    <T> SimplePredicate or(ExpressionOperator<Expression, T, IPredicate> expOperator,
                           BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    SimplePredicate or(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                       TeNamedOperator<DataField> namedOperator, int size);

    SimplePredicate or(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                       BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);

    SimplePredicate or(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                       Object firstValue, SQLs.WordAnd and, Object secondValue);

    <T> SimplePredicate or(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                           Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    SimplePredicate or(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                       Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    SimplePredicate or(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    SimplePredicate or(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName, int size);

    SimplePredicate or(Consumer<Consumer<IPredicate>> consumer);

    IPredicate ifOr(Supplier<IPredicate> supplier);

    <E> IPredicate ifOr(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    <T> IPredicate ifOr(ExpressionOperator<Expression, T, IPredicate> expOperator,
                        BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    IPredicate ifOr(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                    TeNamedOperator<DataField> namedOperator, Supplier<Integer> supplier);

    IPredicate ifOr(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                    BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName);


    <T> IPredicate ifOr(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                        Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    IPredicate ifOr(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                    Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    IPredicate ifOr(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName,
                    Supplier<Integer> supplier);

    IPredicate ifOr(Consumer<Consumer<IPredicate>> consumer);



}
