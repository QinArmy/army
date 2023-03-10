package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.criteria.impl._AliasExpression;
import io.army.function.*;

import java.util.function.*;
@Deprecated
public interface AliasPredicate<I extends Item> extends IPredicate, _AliasExpression<I>, Statement._AsClause<I> {

    @Override
    AliasPredicate<I> bracket();

    @Override
    AliasPredicate<I> or(IPredicate predicate);

    @Override
    AliasPredicate<I> or(Supplier<IPredicate> supplier);

    @Override
    AliasPredicate<I> or(Function<Expression, IPredicate> expOperator, Expression operand);

    @Override
    <E extends RightOperand> AliasPredicate<I> or(Function<E, IPredicate> expOperator, Supplier<E> supplier);


    @Override
    AliasPredicate<I> or(ExpressionOperator<Expression, Expression, IPredicate> expOperator,
                         BiFunction<Expression, Expression, Expression> operator, Expression expression);

    @Override
    AliasPredicate<I> or(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                         BiFunction<Expression, Object, Expression> operator, Object value);

    @Override
    <T> AliasPredicate<I> or(ExpressionOperator<Expression, T, IPredicate> expOperator,
                             BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    AliasPredicate<I> or(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                         BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                         String keyName);


    @Override
    AliasPredicate<I> or(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                         Object firstValue, SQLs.WordAnd and, Object secondValue);

    @Override
    <T> AliasPredicate<I> or(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                             Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    AliasPredicate<I> or(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                         Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    AliasPredicate<I> or(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    @Override
    AliasPredicate<I> or(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName,
                         int size);

    @Override
    AliasPredicate<I> or(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                         TeNamedOperator<DataField> namedOperator, int size);

    @Override
    AliasPredicate<I> or(Consumer<Consumer<IPredicate>> consumer);

    @Override
    AliasPredicate<I> ifOr(Supplier<IPredicate> supplier);

    @Override
    <E> IPredicate ifOr(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    @Override
    <T> AliasPredicate<I> ifOr(ExpressionOperator<Expression, T, IPredicate> expOperator,
                               BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    AliasPredicate<I> ifOr(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                           BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                           String keyName);

    @Override
    <T> AliasPredicate<I> ifOr(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                               Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    AliasPredicate<I> ifOr(BetweenValueOperator<Object> expOperator,
                           BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                           String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    AliasPredicate<I> ifOr(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName,
                           Supplier<Integer> supplier);

    @Override
    AliasPredicate<I> ifOr(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                           TeNamedOperator<DataField> namedOperator, Supplier<Integer> supplier);

    @Override
    AliasPredicate<I> ifOr(Consumer<Consumer<IPredicate>> consumer);

    @Override
    AliasPredicate<I> and(IPredicate predicate);

    @Override
    AliasPredicate<I> and(Supplier<IPredicate> supplier);

    @Override
    AliasPredicate<I> ifAnd(Supplier<IPredicate> supplier);

    @Override
    AliasPredicate<I> and(Function<Expression, IPredicate> expOperator, Expression operand);


    @Override
    AliasPredicate<I> and(ExpressionOperator<Expression, Expression, IPredicate> expOperator,
                          BiFunction<Expression, Expression, Expression> valueOperator, Expression expression);

    @Override
    AliasPredicate<I> and(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                          BiFunction<Expression, Object, Expression> valueOperator, Object value);

    @Override
    <E extends RightOperand> AliasPredicate<I> and(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    @Override
    AliasPredicate<I> and(Function<BiFunction<DataField, String, Expression>, IPredicate> fieldOperator,
                          BiFunction<DataField, String, Expression> namedOperator);

    @Override
    AliasPredicate<I> and(UnaryOperator<IPredicate> expOperator, IPredicate operand);

    @Override
    <T> AliasPredicate<I> and(ExpressionOperator<Expression, T, IPredicate> expOperator,
                              BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    AliasPredicate<I> and(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                          BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                          String keyName);

    @Override
    AliasPredicate<I> and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    @Override
    AliasPredicate<I> and(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName,
                          int size);

    @Override
    AliasPredicate<I> and(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                          TeNamedOperator<DataField> namedOperator, int size);

    @Override
    AliasPredicate<I> and(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                          Object firstValue, SQLs.WordAnd and, Object secondValue);

    @Override
    <T> AliasPredicate<I> and(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                              Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    AliasPredicate<I> and(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                          Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    <E extends RightOperand> AliasPredicate<I> ifAnd(Function<E, IPredicate> expOperator, Supplier<E> supplier);


    @Override
    <T> AliasPredicate<I> ifAnd(ExpressionOperator<Expression, T, IPredicate> expOperator,
                                BiFunction<Expression, T, Expression> operator, Supplier<T> getter);

    @Override
    AliasPredicate<I> ifAnd(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                            BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                            String keyName);

    @Override
    <T> AliasPredicate<I> ifAnd(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                                Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

    @Override
    AliasPredicate<I> ifAnd(BetweenValueOperator<Object> expOperator,
                            BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                            String firstKey, SQLs.WordAnd and, String secondKey);

    @Override
    AliasPredicate<I> ifAnd(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName,
                            Supplier<Integer> supplier);

    @Override
    AliasPredicate<I> ifAnd(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                            TeNamedOperator<DataField> namedOperator, Supplier<Integer> supplier);

    @Override
    AliasPredicate<I> not();

    @Override
    AliasPredicate<I> ifNot(BooleanSupplier predicate);


}
