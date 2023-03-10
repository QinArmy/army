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


    interface _CaseEndClause {

        Expression end();

        Expression end(TypeInfer type);

    }

    interface _CaseElseClause extends _CaseEndClause {

        _CaseEndClause elseValue(Expression expression);

        _CaseEndClause elseValue(Supplier<Expression> supplier);

        _CaseEndClause elseValue(Function<Expression, Expression> valueOperator, Expression expression);

        _CaseEndClause elseValue(Function<Object, Expression> valueOperator, @Nullable Object value);

        <T> _CaseEndClause elseValue(Function<T, Expression> valueOperator, Supplier<T> getter);

        _CaseEndClause elseValue(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                 String keyName);

        _CaseEndClause elseValue(ExpressionOperator<Expression, Expression, Expression> expOperator,
                                 BiFunction<Expression, Expression, Expression> valueOperator, Expression expression);

        _CaseEndClause elseValue(ExpressionOperator<Expression, Object, Expression> expOperator,
                                 BiFunction<Expression, Object, Expression> valueOperator, Object value);

        <T> _CaseEndClause elseValue(ExpressionOperator<Expression, T, Expression> expOperator,
                                     BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        _CaseEndClause elseValue(ExpressionOperator<Expression, Object, Expression> expOperator,
                                 BiFunction<Expression, Object, Expression> valueOperator,
                                 Function<String, ?> function, String keyName);

        _CaseEndClause ifElse(Supplier<Expression> supplier);

        <T> _CaseEndClause ifElse(Function<T, Expression> valueOperator, Supplier<T> getter);

        _CaseEndClause ifElse(Function<Object, Expression> valueOperator, Function<String, ?> function,
                              String keyName);

        <T> _CaseEndClause ifElse(ExpressionOperator<Expression, T, Expression> expOperator,
                                  BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        _CaseEndClause ifElse(ExpressionOperator<Expression, Object, Expression> expOperator,
                              BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function,
                              String keyName);


    }

    interface _SqlCaseThenClause extends Item {

        Item then(Expression expression);

        Item then(Supplier<Expression> supplier);

        Item then(Function<Expression, Expression> valueOperator, Expression expression);

        Item then(Function<Object, Expression> valueOperator, @Nullable Object value);

        <T> Item then(Function<T, Expression> valueOperator, Supplier<T> getter);

        Item then(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        Item then(ExpressionOperator<Expression, Expression, Expression> expOperator,
                  BiFunction<Expression, Expression, Expression> valueOperator, Expression expression);

        Item then(ExpressionOperator<Expression, Object, Expression> expOperator,
                  BiFunction<Expression, Object, Expression> valueOperator, Object value);

        <T> Item then(ExpressionOperator<Expression, T, Expression> expOperator,
                      BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        Item then(ExpressionOperator<Expression, Object, Expression> expOperator,
                  BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function,
                  String keyName);

    }

    interface _SqlCaseWhenClause extends Item {

        Item when(Expression expression);

        Item when(Supplier<Expression> supplier);

        Item when(UnaryOperator<IPredicate> valueOperator, IPredicate predicate);

        Item when(Function<Expression, Expression> valueOperator, Expression expression);

        Item when(Function<Object, Expression> valueOperator, Object value);


        <T> Item when(Function<T, Expression> valueOperator, Supplier<T> getter);

        Item when(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        Item when(ExpressionOperator<Expression, Expression, Expression> expOperator,
                  BiFunction<Expression, Expression, Expression> valueOperator, Expression expression);

        Item when(ExpressionOperator<Expression, Object, Expression> expOperator,
                  BiFunction<Expression, Object, Expression> valueOperator, Object value);

        <T> Item when(ExpressionOperator<Expression, T, Expression> expOperator,
                      BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        Item when(ExpressionOperator<Expression, Object, Expression> expOperator,
                  BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function,
                  String keyName);

        Item when(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                  Object firstValue, SQLs.WordAnd and, Object secondValue);

        <T> Item when(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                      Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        Item when(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                  Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        Item when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);


        Item ifWhen(Supplier<Expression> supplier);

        <T> Item ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter);

        Item ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item ifWhen(ExpressionOperator<Expression, T, Expression> expOperator,
                        BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        Item ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator,
                    BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function, String keyName);

        <T> Item ifWhen(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                        Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter);

        Item ifWhen(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                    Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);


        <T> Item ifWhen(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<T> expOperator,
                        BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter, SQLs.WordAnd and,
                        Supplier<T> secondGetter);


        Item ifWhen(UnaryOperator<IPredicate> predicateOperator, BetweenValueOperator<Object> expOperator,
                    BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey,
                    SQLs.WordAnd and, String secondKey);


    }

    interface _StaticCaseThenClause extends _SqlCaseThenClause {

        @Override
        _CaseWhenSpec then(Expression expression);

        @Override
        _CaseWhenSpec then(Supplier<Expression> supplier);

        @Override
        _CaseWhenSpec then(Function<Expression, Expression> valueOperator, Expression expression);

        @Override
        _CaseWhenSpec then(Function<Object, Expression> valueOperator, @Nullable Object value);

        @Override
        <T> _CaseWhenSpec then(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _CaseWhenSpec then(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);


        @Override
        _CaseWhenSpec then(ExpressionOperator<Expression, Expression, Expression> expOperator,
                           BiFunction<Expression, Expression, Expression> valueOperator, Expression expression);

        @Override
        _CaseWhenSpec then(ExpressionOperator<Expression, Object, Expression> expOperator,
                           BiFunction<Expression, Object, Expression> valueOperator, Object value);

        @Override
        <T> _CaseWhenSpec then(ExpressionOperator<Expression, T, Expression> expOperator,
                               BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _CaseWhenSpec then(ExpressionOperator<Expression, Object, Expression> expOperator,
                           BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function,
                           String keyName);

    }


    interface _StaticCaseWhenClause extends _SqlCaseWhenClause {

        @Override
        _StaticCaseThenClause when(Expression expression);

        @Override
        _StaticCaseThenClause when(Supplier<Expression> supplier);

        @Override
        _StaticCaseThenClause when(Function<Expression, Expression> valueOperator, Expression expression);

        @Override
        _StaticCaseThenClause when(Function<Object, Expression> valueOperator, Object value);

        @Override
        <T> _StaticCaseThenClause when(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _StaticCaseThenClause when(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                   String keyName);


        @Override
        _StaticCaseThenClause when(ExpressionOperator<Expression, Expression, Expression> expOperator,
                                   BiFunction<Expression, Expression, Expression> valueOperator,
                                   Expression expression);

        @Override
        _StaticCaseThenClause when(ExpressionOperator<Expression, Object, Expression> expOperator,
                                   BiFunction<Expression, Object, Expression> valueOperator, Object value);

        @Override
        <T> _StaticCaseThenClause when(ExpressionOperator<Expression, T, Expression> expOperator,
                                       BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _StaticCaseThenClause when(ExpressionOperator<Expression, Object, Expression> expOperator,
                                   BiFunction<Expression, Object, Expression> valueOperator,
                                   Function<String, ?> function, String keyName);

        @Override
        _StaticCaseThenClause when(BetweenValueOperator<Object> expOperator,
                                   BiFunction<Expression, Object, Expression> operator,
                                   Object firstValue, SQLs.WordAnd and, Object secondValue);

        @Override
        <T> _StaticCaseThenClause when(BetweenValueOperator<T> expOperator,
                                       BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter,
                                       SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _StaticCaseThenClause when(BetweenValueOperator<Object> expOperator,
                                   BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                                   String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        _StaticCaseThenClause when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and,
                                   Expression second);

        @Override
        _StaticCaseThenClause ifWhen(Supplier<Expression> supplier);

        @Override
        <T> _StaticCaseThenClause ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _StaticCaseThenClause ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                     String keyName);

        @Override
        <T> _StaticCaseThenClause ifWhen(ExpressionOperator<Expression, T, Expression> expOperator,
                                         BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _StaticCaseThenClause ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator,

                                     BiFunction<Expression, Object, Expression> valueOperator,
                                     Function<String, ?> function, String keyName);

        @Override
        <T> _StaticCaseThenClause ifWhen(BetweenValueOperator<T> expOperator,
                                         BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter,
                                         SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _StaticCaseThenClause ifWhen(BetweenValueOperator<Object> expOperator,
                                     BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                                     String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        <T> _StaticCaseThenClause ifWhen(UnaryOperator<IPredicate> predicateOperator,
                                         BetweenValueOperator<T> expOperator,
                                         BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter,
                                         SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _StaticCaseThenClause ifWhen(UnaryOperator<IPredicate> predicateOperator,
                                     BetweenValueOperator<Object> expOperator,
                                     BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                                     String firstKey, SQLs.WordAnd and, String secondKey);
    }


    interface _DynamicCaseThenClause extends _SqlCaseThenClause {

        @Override
        CaseWhens then(Expression expression);

        @Override
        CaseWhens then(Supplier<Expression> supplier);

        @Override
        CaseWhens then(Function<Expression, Expression> valueOperator, Expression expression);

        @Override
        CaseWhens then(Function<Object, Expression> valueOperator, @Nullable Object value);

        @Override
        <T> CaseWhens then(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        CaseWhens then(Function<Object, Expression> valueOperator, Function<String, ?> function, String keyName);


        @Override
        CaseWhens then(ExpressionOperator<Expression, Expression, Expression> expOperator,
                       BiFunction<Expression, Expression, Expression> valueOperator, Expression expression);

        @Override
        CaseWhens then(ExpressionOperator<Expression, Object, Expression> expOperator,
                       BiFunction<Expression, Object, Expression> valueOperator, Object value);

        @Override
        <T> CaseWhens then(ExpressionOperator<Expression, T, Expression> expOperator,
                           BiFunction<Expression, T, Expression> valueOperator, Supplier<T> operand);

        @Override
        CaseWhens then(ExpressionOperator<Expression, Object, Expression> expOperator,
                       BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function,
                       String keyName);


    }


    interface _DynamicCaseWhenClause extends _SqlCaseWhenClause {

        @Override
        _DynamicCaseThenClause when(Expression expression);

        @Override
        _DynamicCaseThenClause when(Supplier<Expression> supplier);


        @Override
        _DynamicCaseThenClause when(Function<Expression, Expression> valueOperator, Expression expression);

        @Override
        _DynamicCaseThenClause when(Function<Object, Expression> valueOperator, Object value);

        @Override
        <T> _DynamicCaseThenClause when(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _DynamicCaseThenClause when(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                    String keyName);

        @Override
        _DynamicCaseThenClause when(ExpressionOperator<Expression, Expression, Expression> expOperator,
                                    BiFunction<Expression, Expression, Expression> valueOperator, Expression expression);

        @Override
        _DynamicCaseThenClause when(ExpressionOperator<Expression, Object, Expression> expOperator,
                                    BiFunction<Expression, Object, Expression> valueOperator, Object value);

        @Override
        <T> _DynamicCaseThenClause when(ExpressionOperator<Expression, T, Expression> expOperator,
                                        BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _DynamicCaseThenClause when(ExpressionOperator<Expression, Object, Expression> expOperator,
                                    BiFunction<Expression, Object, Expression> valueOperator,
                                    Function<String, ?> function, String keyName);

        @Override
        _DynamicCaseThenClause when(BetweenValueOperator<Object> expOperator,
                                    BiFunction<Expression, Object, Expression> operator, Object firstValue,
                                    SQLs.WordAnd and, Object secondValue);

        @Override
        <T> _DynamicCaseThenClause when(BetweenValueOperator<T> expOperator,
                                        BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter,
                                        SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _DynamicCaseThenClause when(BetweenValueOperator<Object> expOperator,
                                    BiFunction<Expression, Object, Expression> operator,
                                    Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        _DynamicCaseThenClause when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

        @Override
        _DynamicCaseThenClause ifWhen(Supplier<Expression> supplier);

        @Override
        <T> _DynamicCaseThenClause ifWhen(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _DynamicCaseThenClause ifWhen(Function<Object, Expression> valueOperator, Function<String, ?> function,
                                      String keyName);

        @Override
        <T> _DynamicCaseThenClause ifWhen(ExpressionOperator<Expression, T, Expression> expOperator,
                                          BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter);

        @Override
        _DynamicCaseThenClause ifWhen(ExpressionOperator<Expression, Object, Expression> expOperator,
                                      BiFunction<Expression, Object, Expression> valueOperator,
                                      Function<String, ?> function, String keyName);

        @Override
        <T> _DynamicCaseThenClause ifWhen(BetweenValueOperator<T> expOperator,
                                          BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter,
                                          SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _DynamicCaseThenClause ifWhen(BetweenValueOperator<Object> expOperator,
                                      BiFunction<Expression, Object, Expression> operator,
                                      Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey);

        @Override
        <T> _DynamicCaseThenClause ifWhen(UnaryOperator<IPredicate> predicateOperator,
                                          BetweenValueOperator<T> expOperator,
                                          BiFunction<Expression, T, Expression> operator, Supplier<T> firstGetter,
                                          SQLs.WordAnd and, Supplier<T> secondGetter);

        @Override
        _DynamicCaseThenClause ifWhen(UnaryOperator<IPredicate> predicateOperator,
                                      BetweenValueOperator<Object> expOperator,
                                      BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                                      String firstKey, SQLs.WordAnd and, String secondKey);
    }


    interface _CaseWhenSpec extends _StaticCaseWhenClause, _CaseElseClause {

    }


    interface _CaseFuncWhenClause extends _StaticCaseWhenClause {

        _CaseElseClause whens(Consumer<CaseWhens> consumer);

    }


}
