package io.army.criteria.standard;

import io.army.criteria.*;
import io.army.criteria.impl.SQLs;
import io.army.function.BetweenOperator;
import io.army.function.BetweenValueOperator;
import io.army.function.ExpressionOperator;

import java.util.function.*;

public interface SQLFunction extends Item {

    String name();


    interface AggregateFunction {

    }


    interface _OuterClauseBeforeOver {

    }


    interface _CaseEndClause {

        Expression end();

        Expression end(TypeInfer type);

    }

    interface _CaseElseClause extends _CaseEndClause {

        _CaseEndClause elseValue(Expression expression);

        _CaseEndClause elseValue(Supplier<Expression> supplier);

        _CaseEndClause elseValue(UnaryOperator<IPredicate> valueOperator, IPredicate value);

        _CaseEndClause elseValue(Function<Expression, Expression> valueOperator, Expression value);

        _CaseEndClause elseValue(Function<Object, Expression> valueOperator, Object value);

        <T> _CaseEndClause elseValue(Function<T, Expression> valueOperator, Supplier<T> supplier);

        <T> _CaseEndClause elseValue(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

        _CaseEndClause ifElse(Supplier<Expression> supplier);

        <T> _CaseEndClause ifElse(Function<T, Expression> valueOperator, Supplier<T> getter);

        <K, V> _CaseEndClause ifElse(Function<V, Expression> valueOperator, Function<K, V> function,
                                     K key);

        <T> _CaseEndClause ifElse(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                  BiFunction<SimpleExpression, T, Expression> valueOperator, Supplier<T> getter);

        <K, V> _CaseEndClause ifElse(ExpressionOperator<SimpleExpression, V, Expression> expOperator,
                                     BiFunction<SimpleExpression, V, Expression> valueOperator, Function<K, V> function,
                                     K key);


    }

    interface _SqlCaseThenClause extends Item {

        Item then(Expression expression);

        Item then(Supplier<Expression> supplier);

        Item then(UnaryOperator<IPredicate> valueOperator, IPredicate value);

        Item then(Function<Expression, Expression> valueOperator, Expression value);

        Item then(Function<Object, Expression> valueOperator, Object value);

        <T> Item then(Function<T, Expression> valueOperator, Supplier<T> supplier);

        <T> Item then(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                      BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

    }


    interface _DynamicWhenSpaceClause {

        _SqlCaseThenClause space(Expression expression);

        _SqlCaseThenClause space(Supplier<Expression> supplier);

        _SqlCaseThenClause space(UnaryOperator<IPredicate> valueOperator, IPredicate predicate);

        _SqlCaseThenClause space(Function<Expression, Expression> valueOperator, Expression expression);

        _SqlCaseThenClause space(Function<Object, Expression> valueOperator, Object value);

        <T> _SqlCaseThenClause space(Function<T, Expression> valueOperator, Supplier<T> getter);

        <T> _SqlCaseThenClause space(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

        <T> _SqlCaseThenClause space(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                                     T firstValue, SQLs.WordAnd and, T secondValue);

        _SqlCaseThenClause space(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);
    }

    interface _SqlCaseWhenClause extends Item {

        Item when(Expression expression);

        Item when(Supplier<Expression> supplier);

        Item when(UnaryOperator<IPredicate> valueOperator, IPredicate predicate);

        Item when(Function<Expression, Expression> valueOperator, Expression expression);

        Item when(Function<Object, Expression> valueOperator, Object value);

        <T> Item when(Function<T, Expression> valueOperator, Supplier<T> getter);

        <T> Item when(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                      BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

        <T> Item when(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                      T firstValue, SQLs.WordAnd and, T secondValue);

        Item when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    }

    interface _StaticCaseThenClause extends _SqlCaseThenClause {

        @Override
        _CaseWhenSpec then(Expression expression);

        @Override
        _CaseWhenSpec then(Supplier<Expression> supplier);

        @Override
        _CaseWhenSpec then(UnaryOperator<IPredicate> valueOperator, IPredicate value);

        @Override
        _CaseWhenSpec then(Function<Expression, Expression> valueOperator, Expression value);

        @Override
        _CaseWhenSpec then(Function<Object, Expression> valueOperator, Object value);

        @Override
        <T> _CaseWhenSpec then(Function<T, Expression> valueOperator, Supplier<T> supplier);

        @Override
        <T> _CaseWhenSpec then(ExpressionOperator<SimpleExpression, T, Expression> expOperator, BiFunction<SimpleExpression, T, Expression> valueOperator, T value);
    }


    interface _StaticCaseWhenClause extends _SqlCaseWhenClause {

        @Override
        _StaticCaseThenClause when(Expression expression);

        @Override
        _StaticCaseThenClause when(Supplier<Expression> supplier);

        @Override
        _StaticCaseThenClause when(UnaryOperator<IPredicate> valueOperator, IPredicate predicate);

        @Override
        _StaticCaseThenClause when(Function<Expression, Expression> valueOperator, Expression expression);

        @Override
        _StaticCaseThenClause when(Function<Object, Expression> valueOperator, Object value);

        @Override
        <T> _StaticCaseThenClause when(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        <T> _StaticCaseThenClause when(ExpressionOperator<SimpleExpression, T, Expression> expOperator, BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

        @Override
        <T> _StaticCaseThenClause when(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator, T firstValue, SQLs.WordAnd and, T secondValue);

        @Override
        _StaticCaseThenClause when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);


        _CaseWhenSpec ifWhen(Consumer<_DynamicWhenSpaceClause> consumer);

    }


    interface _DynamicCaseThenClause extends _SqlCaseThenClause {

        @Override
        CaseWhens then(Expression expression);

        @Override
        CaseWhens then(Supplier<Expression> supplier);

        @Override
        CaseWhens then(UnaryOperator<IPredicate> valueOperator, IPredicate value);

        @Override
        CaseWhens then(Function<Expression, Expression> valueOperator, Expression value);

        @Override
        CaseWhens then(Function<Object, Expression> valueOperator, Object value);

        @Override
        <T> CaseWhens then(Function<T, Expression> valueOperator, Supplier<T> supplier);

        @Override
        <T> CaseWhens then(ExpressionOperator<SimpleExpression, T, Expression> expOperator, BiFunction<SimpleExpression, T, Expression> valueOperator, T value);
    }


    interface _DynamicCaseWhenClause extends _SqlCaseWhenClause {

        @Override
        _DynamicCaseThenClause when(Expression expression);

        @Override
        _DynamicCaseThenClause when(Supplier<Expression> supplier);

        @Override
        _DynamicCaseThenClause when(UnaryOperator<IPredicate> valueOperator, IPredicate predicate);

        @Override
        _DynamicCaseThenClause when(Function<Expression, Expression> valueOperator, Expression expression);

        @Override
        _DynamicCaseThenClause when(Function<Object, Expression> valueOperator, Object value);

        @Override
        <T> _DynamicCaseThenClause when(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        <T> _DynamicCaseThenClause when(ExpressionOperator<SimpleExpression, T, Expression> expOperator, BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

        @Override
        <T> _DynamicCaseThenClause when(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator, T firstValue, SQLs.WordAnd and, T secondValue);

        @Override
        _DynamicCaseThenClause when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);


    }


    interface _CaseWhenSpec extends _StaticCaseWhenClause, _CaseElseClause {

    }


    interface _CaseFuncWhenClause extends _StaticCaseWhenClause {

        _CaseElseClause whens(Consumer<CaseWhens> consumer);

    }


}
