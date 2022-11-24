package io.army.criteria.standard;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.function.BetweenOperator;
import io.army.function.BetweenValueOperator;
import io.army.function.ExpressionOperator;
import io.army.lang.Nullable;

import java.util.function.*;

public interface SQLFunction {

    interface AggregateFunction {

    }

    interface _OuterClauseBeforeOver {

    }


    interface _CaseEndClause<E extends Expression> {

        E end();

        E end(TypeInfer type);

    }

    interface _CaseElseClause<E extends Expression> extends _CaseEndClause<E> {

        _CaseEndClause<E> elseValue(Expression expression);

        _CaseEndClause<E> elseValue(Supplier<Expression> supplier);

        _CaseEndClause<E> elseValue(Function<Expression, Expression> valueOperator, Expression expression);

        _CaseEndClause<E> elseValue(Function<Object, Expression> valueOperator, @Nullable Object value);

        <T> _CaseEndClause<E> elseValue(Function<T, Expression> valueOperator, Supplier<T> getter);

        _CaseEndClause<E> elseValue(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                    String keyName);

        _CaseEndClause<E> elseValue(ExpressionOperator<Expression, Expression, Expression> expOperator,
                                    BiFunction<Expression, Expression, Expression> valueOperator, Expression expression);

        _CaseEndClause<E> elseValue(ExpressionOperator<Expression, Object, Expression> expOperator,
                                    BiFunction<Expression, Object, Expression> valueOperator, Object value);

        <T> _CaseEndClause<E> elseValue(ExpressionOperator<Expression, T, Expression> expOperator,
                                        BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        _CaseEndClause<E> elseValue(ExpressionOperator<Expression, Object, Expression> expOperator,
                                    BiFunction<Expression, Object, Expression> valueOperator,
                                    Function<String, ?> function, String keyName);

        _CaseEndClause<E> ifElse(Supplier<Expression> supplier);

        <T> _CaseEndClause<E> ifElse(Function<T, Expression> valueOperator, Supplier<T> getter);

        _CaseEndClause<E> ifElse(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                 String keyName);

        <T> _CaseEndClause<E> ifElse(ExpressionOperator<Expression, T, Expression> expOperator,
                                     BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        _CaseEndClause<E> ifElse(ExpressionOperator<Expression, Object, Expression> expOperator,
                                 BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function,
                                 String keyName);


    }

    interface _SqlCaseThenClause extends Item {

        Item then(Expression expression);

        Item then(Supplier<Expression> supplier);

        <T> Item then(Function<T, Expression> valueOperator, Supplier<T> getter);

        Item then(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item then(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        Item then(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

    }

    interface _SqlCaseWhenClause extends Item {

        Item when(Expression expression);

        Item when(Supplier<Expression> supplier);

        <T> Item when(Function<T, Expression> valueOperator, Supplier<T> getter);

        Item when(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item when(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        Item when(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item when(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        Item when(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        Item when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);


        <T> Item when(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);


        Item when(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        Item when(UnaryOperator<IPredicate> predicateOperator, BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        Item ifWhen(Supplier<Expression> supplier);

        <T> Item ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter);

        Item ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item ifWhen(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        Item ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item ifWhen(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        Item ifWhen(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);


        <T> Item ifWhen(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);


        Item ifWhen(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);


    }

    interface _StaticCaseThenClause<E extends Expression> extends _SqlCaseThenClause {

        @Override
        _CaseWhenSpec<E> then(Expression expression);

        @Override
        _CaseWhenSpec<E> then(Supplier<Expression> supplier);

        @Override
        <T> _CaseWhenSpec<E> then(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _CaseWhenSpec<E> then(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _CaseWhenSpec<E> then(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _CaseWhenSpec<E> then(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

    }


    interface _StaticCaseWhenClause<E extends Expression> extends _SqlCaseWhenClause {

        @Override
        _StaticCaseThenClause<E> when(Expression expression);

        @Override
        _StaticCaseThenClause<E> when(Supplier<Expression> supplier);

        @Override
        <T> _StaticCaseThenClause<E> when(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _StaticCaseThenClause<E> when(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _StaticCaseThenClause<E> when(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _StaticCaseThenClause<E> when(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _StaticCaseThenClause<E> when(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _StaticCaseThenClause<E> when(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        _StaticCaseThenClause<E> when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        @Override
        <T> _StaticCaseThenClause<E> when(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _StaticCaseThenClause<E> when(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        _StaticCaseThenClause<E> when(UnaryOperator<IPredicate> predicateOperator, BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        @Override
        _StaticCaseThenClause<E> ifWhen(Supplier<Expression> supplier);

        @Override
        <T> _StaticCaseThenClause<E> ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _StaticCaseThenClause<E> ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _StaticCaseThenClause<E> ifWhen(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _StaticCaseThenClause<E> ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _StaticCaseThenClause<E> ifWhen(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _StaticCaseThenClause<E> ifWhen(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        <T> _StaticCaseThenClause<E> ifWhen(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _StaticCaseThenClause<E> ifWhen(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);
    }


    interface _DynamicCaseThenClause extends _SqlCaseThenClause {

        @Override
        CaseWhens then(Expression expression);

        @Override
        CaseWhens then(Supplier<Expression> supplier);

        @Override
        <T> CaseWhens then(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        CaseWhens then(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> CaseWhens then(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        @Override
        CaseWhens then(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);


    }


    interface _DynamicCaseWhenClause extends _SqlCaseWhenClause {

        @Override
        _DynamicCaseThenClause when(Expression expression);

        @Override
        _DynamicCaseThenClause when(Supplier<Expression> supplier);

        @Override
        <T> _DynamicCaseThenClause when(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _DynamicCaseThenClause when(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _DynamicCaseThenClause when(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _DynamicCaseThenClause when(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _DynamicCaseThenClause when(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _DynamicCaseThenClause when(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        _DynamicCaseThenClause when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        @Override
        <T> _DynamicCaseThenClause when(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _DynamicCaseThenClause when(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        _DynamicCaseThenClause when(UnaryOperator<IPredicate> predicateOperator, BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        @Override
        _DynamicCaseThenClause ifWhen(Supplier<Expression> supplier);

        @Override
        <T> _DynamicCaseThenClause ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _DynamicCaseThenClause ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _DynamicCaseThenClause ifWhen(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _DynamicCaseThenClause ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _DynamicCaseThenClause ifWhen(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _DynamicCaseThenClause ifWhen(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        <T> _DynamicCaseThenClause ifWhen(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _DynamicCaseThenClause ifWhen(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);
    }


    interface _CaseWhenSpec<E extends Expression> extends _StaticCaseWhenClause<E>, _CaseElseClause<E> {

    }


    interface _CaseFuncWhenClause<E extends Expression> extends _StaticCaseWhenClause<E> {

        _CaseElseClause<E> whens(Consumer<CaseWhens> consumer);

    }


}
