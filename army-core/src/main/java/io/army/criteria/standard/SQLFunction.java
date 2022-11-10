package io.army.criteria.standard;

import io.army.criteria.CaseWhens;
import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.TypeInfer;
import io.army.criteria.impl.SQLs;
import io.army.function.BetweenOperator;
import io.army.function.BetweenValueOperator;
import io.army.function.ExpressionOperator;

import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface SQLFunction {

    interface AggregateFunction {

    }

    interface _OuterOptionalClause {

    }


    interface _CaseEndClause<I extends Item> {

        I end();

        I end(TypeInfer type);

    }

    interface _CaseElseClause<I extends Item> extends _CaseEndClause<I> {

        _CaseEndClause<I> Else(Expression expression);

        _CaseEndClause<I> Else(Supplier<Expression> supplier);

        <T> _CaseEndClause<I> Else(Function<T, Expression> valueOperator, Supplier<T> getter);

        _CaseEndClause<I> Else(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> _CaseEndClause<I> Else(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        _CaseEndClause<I> Else(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> _CaseEndClause<I> Else(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        _CaseEndClause<I> Else(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        _CaseEndClause<I> Else(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        _CaseEndClause<I> ifElse(Supplier<Expression> supplier);

        <T> _CaseEndClause<I> ifElse(Function<T, Expression> valueOperator, Supplier<T> getter);

        _CaseEndClause<I> ifElse(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> _CaseEndClause<I> ifElse(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        _CaseEndClause<I> ifElse(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> _CaseEndClause<I> ifElse(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        _CaseEndClause<I> ifElse(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);


    }

    interface _SqlCaseThenClause extends Item {

        Item then(Expression expression);

        Item then(Supplier<Expression> supplier);

        <T> Item then(Function<T, Expression> valueOperator, Supplier<T> getter);

        Item then(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item then(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        Item then(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item then(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        Item then(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        Item then(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);
    }

    interface _SqlCaseWhenClause extends Item {

        Item when(Expression expression);

        Item when(Supplier<Expression> supplier);

        <T> Item when(Function<T, Expression> valueOperator, Supplier<T> getter);

        Item when(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item when(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        Item when(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item when(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        Item when(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        Item when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        Item ifWhen(Supplier<Expression> supplier);

        <T> Item ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter);

        Item ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item ifWhen(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        Item ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item ifWhen(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        Item ifWhen(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

    }

    interface _StaticCaseThenClause<R extends Item> extends _SqlCaseThenClause {

        @Override
        _CaseWhenSpec<R> then(Expression expression);

        @Override
        _CaseWhenSpec<R> then(Supplier<Expression> supplier);

        @Override
        <T> _CaseWhenSpec<R> then(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _CaseWhenSpec<R> then(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _CaseWhenSpec<R> then(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        @Override
        _CaseWhenSpec<R> then(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _CaseWhenSpec<R> then(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _CaseWhenSpec<R> then(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        _CaseWhenSpec<R> then(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);
    }


    interface _StaticCaseWhenClause<R extends Item> extends _SqlCaseWhenClause {

        @Override
        _StaticCaseThenClause<R> when(Expression expression);

        @Override
        _StaticCaseThenClause<R> when(Supplier<Expression> supplier);

        @Override
        <T> _StaticCaseThenClause<R> when(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _StaticCaseThenClause<R> when(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _StaticCaseThenClause<R> when(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        @Override
        _StaticCaseThenClause<R> when(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _StaticCaseThenClause<R> when(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _StaticCaseThenClause<R> when(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        _StaticCaseThenClause<R> when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        @Override
        _StaticCaseThenClause<R> ifWhen(Supplier<Expression> supplier);

        @Override
        <T> _StaticCaseThenClause<R> ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _StaticCaseThenClause<R> ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _StaticCaseThenClause<R> ifWhen(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        @Override
        _StaticCaseThenClause<R> ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _StaticCaseThenClause<R> ifWhen(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _StaticCaseThenClause<R> ifWhen(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);


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

        @Override
        <T> CaseWhens then(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        CaseWhens then(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        CaseWhens then(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

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
        <T> _DynamicCaseThenClause when(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        @Override
        _DynamicCaseThenClause when(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _DynamicCaseThenClause when(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _DynamicCaseThenClause when(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        _DynamicCaseThenClause when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        @Override
        _DynamicCaseThenClause ifWhen(Supplier<Expression> supplier);

        @Override
        <T> _DynamicCaseThenClause ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _DynamicCaseThenClause ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _DynamicCaseThenClause ifWhen(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        @Override
        _DynamicCaseThenClause ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        @Override
        <T> _DynamicCaseThenClause ifWhen(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _DynamicCaseThenClause ifWhen(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);
    }


    interface _CaseThenClause<I extends Item> extends _StaticCaseThenClause<I> {

    }


    interface _CaseWhenClause<I extends Item> extends _StaticCaseWhenClause<I> {

    }

    interface _CaseWhenSpec<I extends Item> extends _CaseWhenClause<I>, _CaseElseClause<I> {

    }


    interface _CaseFuncWhenClause<I extends Item> extends _CaseWhenClause<I> {

        _CaseEndClause<I> whens(Consumer<CaseWhens> consumer);
    }


}
