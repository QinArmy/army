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

package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.mysql.inner._Statement;
import io.army.criteria.standard.SQLs;
import io.army.function.*;
import io.army.util._Collections;
import io.army.util._Exceptions;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.*;

/**
 * <p>
 * package class
 *
 * @since 0.6.0
 */
@SuppressWarnings("unchecked")
public abstract class WhereClause<WR, WA, OR, OD, LR, LO, LF> extends LimitRowOrderByClause<OR, OD, LR, LO, LF>
        implements Statement._WhereClause<WR, WA>
        , Statement._WhereAndClause<WA>
        , UpdateStatement._UpdateWhereAndClause<WA>
        , _Statement._WherePredicateListSpec {

    public final CriteriaContext context;


    private List<_Predicate> predicateList;

    protected WhereClause(CriteriaContext context) {
        super(context);
        this.context = context;
    }


    @Override
    public final WR where(Consumer<Consumer<IPredicate>> consumer) {
        consumer.accept(this::and);
        if (this.predicateList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::predicateListIsEmpty);
        }
        this.endWhereClauseIfNeed();
        return (WR) this;
    }


    @Override
    public final WA where(IPredicate predicate) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(predicate);
    }


    @Override
    public final WA where(Supplier<IPredicate> supplier) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(supplier.get());
    }


    @Override
    public final WA where(Function<Expression, IPredicate> expOperator, Expression operand) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final WA where(UnaryOperator<IPredicate> expOperator, IPredicate operand) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final <T> WA where(Function<T, IPredicate> expOperator, Supplier<T> supplier) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(expOperator.apply(supplier.get()));
    }

    @Override
    public final WA where(Function<BiFunction<SqlField, String, Expression>, IPredicate> fieldOperator,
                          BiFunction<SqlField, String, Expression> namedOperator) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(fieldOperator.apply(namedOperator));
    }

    @Override
    public final <T> WA where(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                              BiFunction<SimpleExpression, T, Expression> valueOperator, @Nullable T value) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(expOperator.apply(valueOperator, value));
    }

    @Override
    public final <T> WA where(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator, SQLs.SymbolSpace space,
                              BiFunction<SimpleExpression, T, Expression> valueOperator, Supplier<T> supplier) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(expOperator.apply(valueOperator, supplier.get()));
    }

    @Override
    public final WA where(InOperator inOperator, SQLs.SymbolSpace space,
                          BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef, Collection<?> value) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(inOperator.apply(funcRef, value));
    }

    @Override
    public final <K, V> WA where(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                                 BiFunction<SimpleExpression, V, Expression> operator, Function<K, V> function, K key) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(expOperator.apply(operator, function.apply(key)));
    }

    @Override
    public final <T> WA where(DialectBooleanOperator<T> fieldOperator,
                              BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                              BiFunction<SimpleExpression, T, Expression> func, @Nullable T value) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(fieldOperator.apply(operator, func, value));
    }

    @Override
    public final <K, V> WA where(DialectBooleanOperator<V> fieldOperator,
                                 BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                 BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(fieldOperator.apply(operator, func, function.apply(key)));
    }

    @Override
    public final WA where(BiFunction<TeNamedOperator<SqlField>, Integer, IPredicate> expOperator,
                          TeNamedOperator<SqlField> namedOperator, int size) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(expOperator.apply(namedOperator, size));
    }

    @Override
    public final WA where(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(expOperator.apply(first, and, second));
    }

    @Override
    public final <T> WA where(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                              T firstValue, SQLs.WordAnd and, T secondValue) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(expOperator.apply(operator, firstValue, and, secondValue));
    }

    @Override
    public final <T, U> WA where(BetweenDualOperator<T, U> expOperator,
                                 BiFunction<SimpleExpression, T, Expression> firstFuncRef, T first,
                                 SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondRef,
                                 U second) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(expOperator.apply(firstFuncRef, first, and, secondRef, second));
    }


    @Override
    public final WA where(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator,
                          String paramName, int size) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.and(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final WA whereIf(Supplier<IPredicate> supplier) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(supplier);
    }


    @Override
    public final <T> WA whereIf(Function<T, IPredicate> expOperator, Supplier<T> supplier) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(expOperator, supplier);
    }


    @Override
    public final <T> WA whereIf(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                                BiFunction<SimpleExpression, T, Expression> operator, Supplier<T> suppler) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(expOperator, operator, suppler);
    }

    @Override
    public final WA whereIf(InOperator inOperator, SQLs.SymbolSpace space,
                            BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef,
                            Supplier<Collection<?>> suppler) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(inOperator, space, funcRef, suppler);
    }

    @Override
    public final <K, V> WA whereIf(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                                   BiFunction<SimpleExpression, V, Expression> operator,
                                   Function<K, V> function, K key) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(expOperator, operator, function, key);
    }

    @Override
    public final <K, V> WA whereIf(InOperator inOperator, SQLs.SymbolSpace space,
                                   BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef,
                                   Function<K, V> function, K key) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(inOperator, space, funcRef, function, key);
    }


    @Override
    public final <T> WA whereIf(DialectBooleanOperator<T> fieldOperator,
                                BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                BiFunction<SimpleExpression, T, Expression> func, Supplier<T> getter) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(fieldOperator, operator, func, getter);
    }

    @Override
    public final WA whereIf(BiFunction<TeNamedOperator<SqlField>, Integer, IPredicate> expOperator,
                            TeNamedOperator<SqlField> namedOperator, Supplier<Integer> supplier) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(expOperator, namedOperator, supplier);
    }


    @Override
    public final <T> WA whereIf(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                                Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(expOperator, operator, firstGetter, and, secondGetter);
    }


    @Override
    public final WA whereIf(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator,
                            String paramName, Supplier<Integer> supplier) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(expOperator, namedOperator, paramName, supplier);
    }


    @Override
    public final <K, V> WA whereIf(DialectBooleanOperator<V> fieldOperator,
                                   BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                   BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(fieldOperator, operator, func, function, key);
    }

    @Override
    public final <T, U> WA whereIf(BetweenDualOperator<T, U> expOperator,
                                   BiFunction<SimpleExpression, T, Expression> firstFuncRef,
                                   Supplier<T> firstGetter, SQLs.WordAnd and,
                                   BiFunction<SimpleExpression, U, Expression> secondFuncRef, Supplier<U> secondGetter) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(expOperator, firstFuncRef, firstGetter, and, secondFuncRef, secondGetter);
    }

    @Override
    public final <K, V> WA whereIf(BetweenValueOperator<V> expOperator, BiFunction<SimpleExpression, V, Expression> operator,
                                   Function<K, V> function, K firstKey, SQLs.WordAnd and, K secondKey) {
        if (this.predicateList != null) {
            throw duplicationWhere();
        }
        return this.ifAnd(expOperator, operator, function, firstKey, and, secondKey);
    }

    @Override
    public final WA and(@Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(this.context);
        }
        List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            predicateList = _Collections.arrayList();
            this.predicateList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        predicateList.add((OperationPredicate) predicate);
        return (WA) this;
    }

    @Override
    public final WA and(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }


    @Override
    public final WA and(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final WA and(UnaryOperator<IPredicate> expOperator, IPredicate operand) {
        return this.and(expOperator.apply(operand));
    }


    @Override
    public final <T> WA and(Function<T, IPredicate> expOperator, Supplier<T> supplier) {
        return this.and(expOperator.apply(supplier.get()));
    }


    @Override
    public final WA and(Function<BiFunction<SqlField, String, Expression>, IPredicate> fieldOperator,
                        BiFunction<SqlField, String, Expression> namedOperator) {
        return this.and(fieldOperator.apply(namedOperator));
    }

    @Override
    public final WA and(BiFunction<TeNamedOperator<SqlField>, Integer, IPredicate> expOperator,
                        TeNamedOperator<SqlField> namedOperator, int size) {
        return this.and(expOperator.apply(namedOperator, size));
    }


    @Override
    public final WA and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
        return this.and(expOperator.apply(first, and, second));
    }

    @Override
    public final WA and(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator, String paramName,
                        int size) {
        return this.and(expOperator.apply(namedOperator, paramName, size));
    }


    @Override
    public final <T> WA and(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                            BiFunction<SimpleExpression, T, Expression> valueOperator, @Nullable T value) {
        return this.and(expOperator.apply(valueOperator, value));
    }

    @Override
    public final <T> WA and(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator, SQLs.SymbolSpace space,
                            BiFunction<SimpleExpression, T, Expression> valueOperator, Supplier<T> supplier) {
        return this.and(expOperator.apply(valueOperator, supplier.get()));
    }

    @Override
    public final WA and(InOperator inOperator, SQLs.SymbolSpace space,
                        BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef, Collection<?> value) {
        return this.and(inOperator.apply(funcRef, value));
    }

    @Override
    public final <K, V> WA and(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                               BiFunction<SimpleExpression, V, Expression> operator,
                               Function<K, V> function, K key) {
        return this.and(expOperator.apply(operator, function.apply(key)));
    }

    @Override
    public final <T> WA and(DialectBooleanOperator<T> fieldOperator,
                            BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                            BiFunction<SimpleExpression, T, Expression> func, @Nullable T value) {
        return this.and(fieldOperator.apply(operator, func, value));
    }

    @Override
    public final <K, V> WA and(DialectBooleanOperator<V> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                               BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key) {
        return this.and(fieldOperator.apply(operator, func, function.apply(key)));
    }

    @Override
    public final <T> WA and(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                            T firstValue, SQLs.WordAnd and, T secondValue) {
        return this.and(expOperator.apply(operator, firstValue, and, secondValue));
    }

    @Override
    public final <T, U> WA and(BetweenDualOperator<T, U> expOperator,
                               BiFunction<SimpleExpression, T, Expression> firstFuncRef, T first,
                               SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondRef, U second) {
        return this.and(expOperator.apply(firstFuncRef, first, and, secondRef, second));
    }

    @Override
    public final <T> WA and(ExpressionOperator<SimpleExpression, T, Expression> expOperator1,
                            BiFunction<SimpleExpression, T, Expression> operator, @Nullable T operand1,
                            BiFunction<Expression, Expression, IPredicate> expOperator2, ValueExpression numberOperand) {
        if (operand1 == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final Expression expression;
        expression = expOperator1.apply(operator, operand1);
        if (expression == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator2.apply(expression, numberOperand));
    }

    @Override
    public final WA and(Function<BiFunction<SqlField, String, Expression>, Expression> fieldOperator,
                        BiFunction<SqlField, String, Expression> operator,
                        BiFunction<Expression, Expression, IPredicate> expOperator2, ValueExpression numberOperand) {
        return this.and(expOperator2.apply(fieldOperator.apply(operator), numberOperand));
    }

    @Override
    public final WA ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.and(predicate);
        }
        return (WA) this;
    }


    @Override
    public final <T> WA ifAnd(Function<T, IPredicate> expOperator, Supplier<T> supplier) {
        final T expression;
        expression = supplier.get();
        if (expression != null) {
            this.and(expOperator.apply(expression));
        }
        return (WA) this;
    }


    @Override
    public final <T> WA ifAnd(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                              BiFunction<SimpleExpression, T, Expression> operator, Supplier<T> getter) {
        final T value;
        if ((value = getter.get()) != null) {
            this.and(expOperator.apply(operator, value));
        }
        return (WA) this;
    }

    @Override
    public final <K, V> WA ifAnd(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                                 BiFunction<SimpleExpression, V, Expression> operator, Function<K, V> function, K key) {
        final V value;
        if ((value = function.apply(key)) != null) {
            this.and(expOperator.apply(operator, value));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(InOperator inOperator, SQLs.SymbolSpace space,
                          BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef,
                          Supplier<Collection<?>> suppler) {
        final Collection<?> collection;
        collection = suppler.get();
        if (collection != null && collection.size() > 0) {
            this.and(inOperator.apply(funcRef, collection));
        }
        return (WA) this;
    }

    @Override
    public final <K, V> WA ifAnd(InOperator inOperator, SQLs.SymbolSpace space,
                                 BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef,
                                 Function<K, V> function, K key) {
        final Object value;
        value = function.apply(key);
        if (value instanceof Collection && ((Collection<?>) value).size() > 0) {
            this.and(inOperator.apply(funcRef, (Collection<?>) value));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(DialectBooleanOperator<T> fieldOperator,
                              BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                              BiFunction<SimpleExpression, T, Expression> func, Supplier<T> getter) {
        final T operand;
        if ((operand = getter.get()) != null) {
            this.and(fieldOperator.apply(operator, func, operand));
        }
        return (WA) this;
    }

    @Override
    public final <K, V> WA ifAnd(DialectBooleanOperator<V> fieldOperator,
                                 BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                 BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key) {
        final V value;
        if ((value = function.apply(key)) != null) {
            this.and(fieldOperator.apply(operator, func, value));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(BiFunction<TeNamedOperator<SqlField>, Integer, IPredicate> expOperator,
                          TeNamedOperator<SqlField> namedOperator, Supplier<Integer> supplier) {
        final Integer size;
        if ((size = supplier.get()) != null) {
            this.and(expOperator.apply(namedOperator, size));
        }
        return (WA) this;
    }


    @Override
    public final <T> WA ifAnd(BetweenValueOperator<T> expOperator, BiFunction<SimpleExpression, T, Expression> operator,
                              Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        final T first, second;
        if ((first = firstGetter.get()) != null && (second = secondGetter.get()) != null) {
            this.and(expOperator.apply(operator, first, and, second));
        }
        return (WA) this;
    }


    @Override
    public final <T, U> WA ifAnd(BetweenDualOperator<T, U> expOperator,
                                 BiFunction<SimpleExpression, T, Expression> firstFuncRef, Supplier<T> firstGetter,
                                 SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFuncRef,
                                 Supplier<U> secondGetter) {
        final T first;
        final U second;
        if ((first = firstGetter.get()) != null && (second = secondGetter.get()) != null) {
            this.and(expOperator.apply(firstFuncRef, first, and, secondFuncRef, second));
        }
        return (WA) this;
    }

    @Override
    public final <K, V> WA ifAnd(BetweenValueOperator<V> expOperator, BiFunction<SimpleExpression, V, Expression> operator,
                                 Function<K, V> function, K firstKey, SQLs.WordAnd and, K secondKey) {
        final V first, second;
        if ((first = function.apply(firstKey)) != null && (second = function.apply(secondKey)) != null) {
            this.and(expOperator.apply(operator, first, and, second));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator, String paramName,
                          Supplier<Integer> supplier) {
        final Integer size;
        if ((size = supplier.get()) != null) {
            this.and(expOperator.apply(namedOperator, paramName, size));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(ExpressionOperator<SimpleExpression, T, Expression> expOperator1,
                              BiFunction<SimpleExpression, T, Expression> operator, @Nullable T operand1,
                              BiFunction<Expression, Expression, IPredicate> expOperator2, ValueExpression numberOperand) {
        if (operand1 != null) {
            final Expression expression;
            expression = expOperator1.apply(operator, operand1);
            if (expression == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.and(expOperator2.apply(expression, numberOperand));
        }
        return (WA) this;
    }


    @Override
    public final List<_Predicate> wherePredicateList() {
        final List<_Predicate> list = this.predicateList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.clearStackAndCastCriteriaApi();
        }
        return list;
    }


    final void clearWhereClause() {
        this.predicateList = null;
    }

    public final List<_Predicate> endWhereClauseIfNeed() {
        List<_Predicate> list = this.predicateList;
        if (list instanceof ArrayList) {
            list = _Collections.unmodifiableList(list);
            this.predicateList = list;
        } else if (list == null) {
            if (this instanceof Statement.DmlStatementSpec) {
                //dml statement must have where clause
                throw ContextStack.criteriaError(this.context, _Exceptions::dmlNoWhereClause);
            }
            list = _Collections.emptyList();
            this.predicateList = list;
        }
        return list;
    }

    private CriteriaException duplicationWhere() {
        return ContextStack.criteriaError(this.context, "duplication where clause");
    }


    public static abstract class WhereClauseClause<WR, WA> extends WhereClause<WR, WA, Object, Object, Object, Object, Object> {

        protected WhereClauseClause(CriteriaContext context) {
            super(context);
        }


    }//WhereClauseClause


    public static final class SimpleWhereClause extends WhereClauseClause<Item, Statement._SimpleWhereAndClause>
            implements Statement._SimpleWhereAndClause,
            Statement._SimpleWhereClause {

        public SimpleWhereClause(CriteriaContext context) {
            super(context);
        }

    }//SimpleWhereClause


}
