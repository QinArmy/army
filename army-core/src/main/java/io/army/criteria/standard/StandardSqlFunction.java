package io.army.criteria.standard;

import io.army.criteria.Clause;
import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.TypeInfer;
import io.army.criteria.impl.SQLs;
import io.army.function.BetweenOperator;
import io.army.function.BetweenValueOperator;
import io.army.function.ExpressionOperator;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public interface StandardSqlFunction {


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

    interface _StaticCaseThenClause<R extends Item> extends Item {

        R then(Expression expression);

        R then(Supplier<Expression> supplier);

        <T> R then(Function<T, Expression> valueOperator, Supplier<T> getter);

        R then(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> R then(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        R then(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> R then(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        R then(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        R then(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);
    }


    interface _StaticCaseWhenClause<R extends Item> extends Item {

        R when(Expression expression);

        R when(Supplier<Expression> supplier);

        <T> R when(Function<T, Expression> valueOperator, Supplier<T> getter);

        R when(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> R when(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        R when(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> R when(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        R when(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        R when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        R ifWhen(Supplier<Expression> supplier);

        <T> R ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter);

        R ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> R ifWhen(ExpressionOperator<Expression, T, Expression> expOperator, BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        R ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator, BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> R ifWhen(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        R ifWhen(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);


    }


    interface _CaseThenClause<I extends Item> extends _StaticCaseThenClause<_CaseWhenSpec<I>> {

    }


    interface _CaseWhenClause<I extends Item> extends _StaticCaseWhenClause<_CaseThenClause<I>> {

    }

    interface _CaseWhenSpec<I extends Item> extends _CaseWhenClause<I>, _CaseElseClause<I>, Clause {

    }


}
