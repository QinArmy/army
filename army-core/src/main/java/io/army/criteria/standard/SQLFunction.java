/*
 * Copyright 2023-2043 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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

        Expression end(TypeInfer type);

    }

    interface _CaseElseClause extends _CaseEndClause {

        _CaseEndClause elseValue(Object expression);

        _CaseEndClause elseValue(UnaryOperator<IPredicate> valueOperator, IPredicate value);

        _CaseEndClause elseValue(Function<Expression, Expression> valueOperator, Expression value);

        <T> _CaseEndClause elseValue(Function<T, Expression> valueOperator, Supplier<T> getter);

        <T> _CaseEndClause elseValue(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

        _CaseEndClause ifElse(Supplier<?> supplier);

        <T> _CaseEndClause ifElse(Function<T, Expression> valueOperator, Supplier<T> getter);

        <K, V> _CaseEndClause ifElse(Function<V, Expression> valueOperator, Function<K, V> function, K key);

        <T> _CaseEndClause ifElse(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                  BiFunction<SimpleExpression, T, Expression> valueOperator, Supplier<T> getter);

        <K, V> _CaseEndClause ifElse(ExpressionOperator<SimpleExpression, V, Expression> expOperator,
                                     BiFunction<SimpleExpression, V, Expression> valueOperator, Function<K, V> function,
                                     K key);


    }

    interface _SqlCaseThenClause extends Item {

        Item then(Object expression);

        Item then(UnaryOperator<IPredicate> valueOperator, IPredicate value);

        Item then(Function<Expression, Expression> valueOperator, Expression value);

        <T> Item then(Function<T, Expression> valueOperator, Supplier<T> getter);

        <T> Item then(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                      BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

    }


    interface _DynamicWhenSpaceClause {

        _SqlCaseThenClause space(Object expression);

        _SqlCaseThenClause space(UnaryOperator<IPredicate> valueOperator, IPredicate predicate);

        _SqlCaseThenClause space(Function<Expression, Expression> valueOperator, Expression expression);

        <T> _SqlCaseThenClause space(Function<T, Expression> valueOperator, Supplier<T> getter);

        <T> _SqlCaseThenClause space(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

        <T> _SqlCaseThenClause space(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                                     T firstValue, SQLs.WordAnd and, T secondValue);

        _SqlCaseThenClause space(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);
    }

    interface _SqlCaseWhenClause extends Item {

        /**
         * <p>create WHEN clause of CASE function.
         *
         * @param expression {@link Expression} instance or literal
         */
        Item when(Object expression);

        Item when(UnaryOperator<IPredicate> valueOperator, IPredicate predicate);

        Item when(Function<Expression, Expression> valueOperator, Expression expression);

        <T> Item when(Function<T, Expression> valueOperator, Supplier<T> getter);

        <T> Item when(ExpressionOperator<SimpleExpression, T, Expression> expOperator,
                      BiFunction<SimpleExpression, T, Expression> valueOperator, T value);

        <T> Item when(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                      T firstValue, SQLs.WordAnd and, T secondValue);

        Item when(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second);

    }

    interface _StaticCaseThenClause extends _SqlCaseThenClause {

        /**
         * {@inheritDoc}
         */
        @Override
        _CaseWhenSpec then(Object expression);

        @Override
        _CaseWhenSpec then(UnaryOperator<IPredicate> valueOperator, IPredicate value);

        @Override
        _CaseWhenSpec then(Function<Expression, Expression> valueOperator, Expression value);

        @Override
        <T> _CaseWhenSpec then(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        <T> _CaseWhenSpec then(ExpressionOperator<SimpleExpression, T, Expression> expOperator, BiFunction<SimpleExpression, T, Expression> valueOperator, T value);
    }


    interface _StaticCaseWhenClause extends _SqlCaseWhenClause {

        /**
         * {@inheritDoc}
         */
        @Override
        _StaticCaseThenClause when(Object expression);

        @Override
        _StaticCaseThenClause when(UnaryOperator<IPredicate> valueOperator, IPredicate predicate);

        @Override
        _StaticCaseThenClause when(Function<Expression, Expression> valueOperator, Expression expression);

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
        CaseWhens then(Object expression);

        CaseWhens then(UnaryOperator<IPredicate> valueOperator, IPredicate value);

        CaseWhens then(Function<Expression, Expression> valueOperator, Expression value);


        @Override
        <T> CaseWhens then(Function<T, Expression> valueOperator, Supplier<T> getter);

        @Override
        <T> CaseWhens then(ExpressionOperator<SimpleExpression, T, Expression> expOperator, BiFunction<SimpleExpression, T, Expression> valueOperator, T value);
    }


    interface _DynamicCaseWhenClause extends _SqlCaseWhenClause {

        @Override
        _DynamicCaseThenClause when(Object expression);

        @Override
        _DynamicCaseThenClause when(UnaryOperator<IPredicate> valueOperator, IPredicate predicate);

        @Override
        _DynamicCaseThenClause when(Function<Expression, Expression> valueOperator, Expression expression);

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
