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

package io.army.criteria;

import io.army.criteria.impl.SQLs;
import io.army.function.*;
import io.army.mapping.BooleanType;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;


/**
 * <p>
 * This interface representing sql predicate in WHERE clause or HAVING clause.
 * This interface name is 'IPredicate' not 'Predicate', because of {@link java.util.function.Predicate}.
 * * @since 0.6.0
 */
public interface IPredicate extends Expression, Statement._WhereAndClause<IPredicate> {

    /**
     * @return always return {@link BooleanType#INSTANCE}
     */
    @Override
    BooleanType typeMeta();

    /**
     * Logical OR
     * <p>
     * This method representing expression (this OR predicate)
     *     */
    SimplePredicate or(IPredicate predicate);

    SimplePredicate or(Supplier<IPredicate> supplier);

    SimplePredicate or(Function<Expression, IPredicate> expOperator, Expression operand);

    <E extends RightOperand> SimplePredicate or(Function<E, IPredicate> expOperator, Supplier<E> supplier);

    <T> SimplePredicate or(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                           BiFunction<SimpleExpression, T, Expression> operator, @Nullable T value);

    <T> SimplePredicate or(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator, SQLs.SymbolSpace space,
                           BiFunction<SimpleExpression, T, Expression> valueOperator, Supplier<T> supplier);

    SimplePredicate or(InOperator inOperator, SQLs.SymbolSpace space,
                       BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef, Collection<?> value);

    SimplePredicate or(BiFunction<TeNamedOperator<SqlField>, Integer, IPredicate> expOperator,
                       TeNamedOperator<SqlField> namedOperator, int size);

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

    IPredicate ifOr(InOperator inOperator, SQLs.SymbolSpace space,
                    BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef, Supplier<Collection<?>> suppler);

    <K, V> IPredicate ifOr(InOperator inOperator, SQLs.SymbolSpace space,
                           BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef, Function<K, V> function, K key);

    IPredicate ifOr(BiFunction<TeNamedOperator<SqlField>, Integer, IPredicate> expOperator,
                    TeNamedOperator<SqlField> namedOperator, Supplier<Integer> supplier);


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


    /**
     * <p>
     * This method is designed for dialect logical operator.
     * This method name is 'blank' not 'whiteSpace' , because of distinguishing logical operator and other operator.
     *     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *     * <p>
     *
     *     *
     * @param funcRef the reference of the method of dialect logical operator,<strong>NOTE</strong>: not lambda.
     *                The first argument of funcRef always is <strong>this</strong>.
     *                For example: {@code MySQL.xor(IPredicate,IPredicate)}
     * @param right   the right operand of dialect logical operator.  It will be passed to funcRef as the second argument of funcRef
     */
    LogicalPredicate blank(BiFunction<IPredicate, IPredicate, LogicalPredicate> funcRef, IPredicate right);


    /**
     * <p>
     * This method is designed for dialect logical operator.
     * This method name is 'blank' not 'whiteSpace' , because of distinguishing logical operator and other operator.
     *     * <p>
     * <strong>Note</strong>: The first argument of funcRef always is <strong>this</strong>.
     *     * <p>
     *
     *     *
     * @param funcRef  the reference of the method of dialect logical operator,<strong>NOTE</strong>: not lambda.
     *                 The first argument of funcRef always is <strong>this</strong>.
     *                 For example: {@code MySQL.xor(IPredicate,IPredicate)}
     * @param consumer the right operand of dialect logical operator.  It will be passed to funcRef as the second argument of funcRef
     */
    LogicalPredicate blank(BiFunction<IPredicate, Consumer<Consumer<IPredicate>>, LogicalPredicate> funcRef, Consumer<Consumer<IPredicate>> consumer);


}
