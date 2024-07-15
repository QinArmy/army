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
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.function.*;
import io.army.meta.*;
import io.army.modelgen._MetaBridge;
import io.army.util._Collections;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.*;

/**
 * This class is base class of all {@link IPredicate} implementation .
 */
abstract class OperationPredicate extends OperationExpression.PredicateExpression {

    /**
     * <p>
     * Private constructor .
     *
     * @see OperationSimplePredicate#OperationSimplePredicate()
     * @see OperationCompoundPredicate#OperationPredicate()
     */
    private OperationPredicate() {

    }


    @Override
    public final SimplePredicate or(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return orPredicate(this, predicate);
    }

    @Override
    public final SimplePredicate or(Supplier<IPredicate> supplier) {
        return this.or(supplier.get());
    }

    @Override
    public final SimplePredicate or(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.or(expOperator.apply(operand));
    }

    @Override
    public final <E extends RightOperand> SimplePredicate or(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.or(expOperator.apply(supplier.get()));
    }

    @Override
    public final <T> SimplePredicate or(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                                        BiFunction<SimpleExpression, T, Expression> operator, @Nullable T value) {
        return this.or(expOperator.apply(operator, value));
    }

    @Override
    public final <T> SimplePredicate or(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                                        SQLs.SymbolSpace space, BiFunction<SimpleExpression, T, Expression> valueOperator,
                                        Supplier<T> supplier) {
        return this.or(expOperator.apply(valueOperator, supplier.get()));
    }

    @Override
    public final SimplePredicate or(InOperator inOperator, SQLs.SymbolSpace space,
                                    BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef, Collection<?> value) {
        return this.or(inOperator.apply(funcRef, value));
    }


    @Override
    public final SimplePredicate or(BiFunction<TeNamedOperator<SqlField>, Integer, IPredicate> expOperator,
                                    TeNamedOperator<SqlField> namedOperator, int size) {
        return this.or(expOperator.apply(namedOperator, size));
    }

    @Override
    public final SimplePredicate or(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
        return this.or(expOperator.apply(first, and, second));
    }

    @Override
    public final SimplePredicate or(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator
            , String paramName, int size) {
        return this.or(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final <T> SimplePredicate or(BetweenValueOperator<T> expOperator,
                                        BiFunction<SimpleExpression, T, Expression> operator, T firstValue,
                                        SQLs.WordAnd and, T secondValue) {
        return this.or(expOperator.apply(operator, firstValue, and, secondValue));
    }

    @Override
    public final <T, U> SimplePredicate or(BetweenDualOperator<T, U> expOperator,
                                           BiFunction<SimpleExpression, T, Expression> firstFunc, T firstValue,
                                           SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFunc,
                                           U secondValue) {
        return this.or(expOperator.apply(firstFunc, firstValue, and, secondFunc, secondValue));
    }

    @Override
    public final SimplePredicate or(Consumer<Consumer<IPredicate>> consumer) {
        final List<IPredicate> list = _Collections.arrayList();
        consumer.accept(list::add);
        final SimplePredicate predicate;
        switch (list.size()) {
            case 0:
                throw CriteriaUtils.dontAddAnyItem();
            case 1:
                predicate = orPredicate(this, list.get(0));
                break;
            default:
                predicate = orPredicate(this, list);
        }
        return predicate;
    }

    @Override
    public final IPredicate ifOr(Supplier<IPredicate> supplier) {
        final IPredicate result;
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate == null) {
            result = this;
        } else {
            result = this.or(predicate);
        }
        return result;
    }

    @Override
    public final <T> IPredicate ifOr(Function<T, IPredicate> expOperator, Supplier<T> supplier) {
        final T operand;
        operand = supplier.get();
        final IPredicate predicate;
        if (operand == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operand));
        }
        return predicate;
    }

    @Override
    public final IPredicate ifOr(BiFunction<TeNamedOperator<SqlField>, Integer, IPredicate> expOperator,
                                 TeNamedOperator<SqlField> namedOperator, Supplier<Integer> supplier) {
        final IPredicate predicate;
        final Integer size;
        if ((size = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(namedOperator, size));
        }
        return predicate;
    }

    @Override
    public final <T> IPredicate ifOr(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> operator, Supplier<T> getter) {
        final IPredicate predicate;
        final T value;
        if ((value = getter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, value));
        }
        return predicate;
    }

    @Override
    public final IPredicate ifOr(InOperator inOperator, SQLs.SymbolSpace space,
                                 BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef,
                                 Supplier<Collection<?>> suppler) {
        final IPredicate predicate;
        final Collection<?> collection;
        if ((collection = suppler.get()) == null || collection.size() == 0) {
            predicate = this;
        } else {
            predicate = this.or(inOperator.apply(funcRef, collection));
        }
        return predicate;
    }

    @Override
    public final <K, V> IPredicate ifOr(InOperator inOperator, SQLs.SymbolSpace space,
                                        BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef,
                                        Function<K, V> function, K key) {
        final IPredicate predicate;
        final V value;
        if ((value = function.apply(key)) instanceof Collection && ((Collection<?>) value).size() > 0) {
            predicate = this.or(inOperator.apply(funcRef, (Collection<?>) value));
        } else {
            predicate = this;
        }
        return predicate;
    }

    @Override
    public final <K, V> IPredicate ifOr(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                                        BiFunction<SimpleExpression, V, Expression> operator, Function<K, V> function,
                                        K keyName) {
        final IPredicate predicate;
        final V value;
        if ((value = function.apply(keyName)) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, value));
        }
        return predicate;
    }

    @Override
    public final <T> IPredicate ifOr(BetweenValueOperator<T> expOperator,
                                     BiFunction<SimpleExpression, T, Expression> operator,
                                     Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        final IPredicate predicate;
        final T first, second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }

    @Override
    public final <T, U> IPredicate ifOr(BetweenDualOperator<T, U> expOperator,
                                        BiFunction<SimpleExpression, T, Expression> firstFunc, Supplier<T> firstGetter,
                                        SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondFunc,
                                        Supplier<U> secondGetter) {
        final IPredicate predicate;
        final T first;
        final U second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(firstFunc, first, and, secondFunc, second));
        }
        return predicate;
    }

    @Override
    public final IPredicate ifOr(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator,
                                 String paramName, Supplier<Integer> supplier) {
        final IPredicate predicate;
        final Integer value;
        if ((value = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.or(expOperator.apply(namedOperator, paramName, value));
        }
        return predicate;
    }

    @Override
    public final IPredicate ifOr(Consumer<Consumer<IPredicate>> consumer) {
        final List<IPredicate> list = _Collections.arrayList();
        consumer.accept(list::add);
        final IPredicate predicate;
        switch (list.size()) {
            case 0:
                predicate = this;
                break;
            case 1:
                predicate = orPredicate(this, list.get(0));
                break;
            default:
                predicate = orPredicate(this, list);
        }
        return predicate;
    }

    @Override
    public final IPredicate and(final @Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return andPredicate(this, predicate);
    }

    @Override
    public final IPredicate and(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final IPredicate and(UnaryOperator<IPredicate> expOperator, IPredicate operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final IPredicate and(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final IPredicate and(BiFunction<TeNamedOperator<SqlField>, Integer, IPredicate> expOperator,
                                TeNamedOperator<SqlField> namedOperator, int size) {
        return this.and(expOperator.apply(namedOperator, size));
    }


    @Override
    public final IPredicate and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and,
                                Expression second) {
        return this.and(expOperator.apply(first, and, second));
    }

    @Override
    public final IPredicate and(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator,
                                String paramName, int size) {
        return this.and(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final IPredicate and(Function<BiFunction<SqlField, String, Expression>, IPredicate> fieldOperator,
                                BiFunction<SqlField, String, Expression> namedOperator) {
        return this.and(fieldOperator.apply(namedOperator));
    }

    @Override
    public final <T> IPredicate and(Function<T, IPredicate> expOperator, Supplier<T> supplier) {
        return this.and(expOperator.apply(supplier.get()));
    }

    @Override
    public final <T> IPredicate and(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                                    BiFunction<SimpleExpression, T, Expression> valueOperator, @Nullable T value) {
        return this.and(expOperator.apply(valueOperator, value));
    }

    @Override
    public final <T> IPredicate and(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator,
                                    SQLs.SymbolSpace space, BiFunction<SimpleExpression, T, Expression> valueOperator,
                                    Supplier<T> supplier) {
        return this.and(expOperator.apply(valueOperator, supplier.get()));
    }

    @Override
    public final IPredicate and(InOperator inOperator, SQLs.SymbolSpace space,
                                BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef, Collection<?> value) {
        return this.and(inOperator.apply(funcRef, value));
    }

    @Override
    public final <K, V> IPredicate and(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator,
                                       BiFunction<SimpleExpression, V, Expression> operator,
                                       Function<K, V> function, K key) {
        return this.and(expOperator.apply(operator, function.apply(key)));
    }

    @Override
    public final <T> IPredicate and(DialectBooleanOperator<T> fieldOperator,
                                    BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                    BiFunction<SimpleExpression, T, Expression> func, @Nullable T value) {
        return this.and(fieldOperator.apply(operator, func, value));
    }

    @Override
    public final <K, V> IPredicate and(DialectBooleanOperator<V> fieldOperator, BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                       BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function, K key) {
        return this.and(fieldOperator.apply(operator, func, function.apply(key)));
    }

    @Override
    public final <T> IPredicate and(BetweenValueOperator<T> expOperator,
                                    BiFunction<SimpleExpression, T, Expression> operator, T firstValue,
                                    SQLs.WordAnd and, T secondValue) {
        return this.and(expOperator.apply(operator, firstValue, and, secondValue));
    }

    @Override
    public final <T, U> IPredicate and(BetweenDualOperator<T, U> expOperator,
                                       BiFunction<SimpleExpression, T, Expression> firstFuncRef, T first,
                                       SQLs.WordAnd and, BiFunction<SimpleExpression, U, Expression> secondRef,
                                       U second) {
        return this.and(expOperator.apply(firstFuncRef, first, and, secondRef, second));
    }

    @Override
    public final IPredicate ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        final IPredicate operand;
        if ((operand = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = andPredicate(this, operand);
        }
        return predicate;
    }

    @Override
    public final <E> IPredicate ifAnd(Function<E, IPredicate> expOperator,
                                      Supplier<E> supplier) {
        final IPredicate predicate;
        final E value;
        if ((value = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(value));
        }
        return predicate;
    }

    @Override
    public final <T> IPredicate ifAnd(ExpressionOperator<SimpleExpression, T, IPredicate> expOperator
            , BiFunction<SimpleExpression, T, Expression> operator, Supplier<T> getter) {
        final IPredicate predicate;
        final T operand;
        if ((operand = getter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, operand));
        }
        return predicate;
    }

    @Override
    public final IPredicate ifAnd(InOperator inOperator, SQLs.SymbolSpace space,
                                  BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef,
                                  Supplier<Collection<?>> suppler) {
        final IPredicate predicate;
        final Collection<?> collection;
        if ((collection = suppler.get()) == null || collection.size() == 0) {
            predicate = this;
        } else {
            predicate = this.and(inOperator.apply(funcRef, collection));
        }
        return predicate;
    }

    @Override
    public final <K, V> IPredicate ifAnd(ExpressionOperator<SimpleExpression, V, IPredicate> expOperator
            , BiFunction<SimpleExpression, V, Expression> operator, Function<K, V> function, K keyName) {
        final IPredicate predicate;
        final V operand;
        if ((operand = function.apply(keyName)) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, operand));
        }
        return predicate;
    }

    @Override
    public final <K, V> IPredicate ifAnd(InOperator inOperator, SQLs.SymbolSpace space,
                                         BiFunction<SimpleExpression, Collection<?>, RowExpression> funcRef,
                                         Function<K, V> function, K key) {
        final IPredicate predicate;
        final V value;
        if ((value = function.apply(key)) instanceof Collection && ((Collection<?>) value).size() > 0) {
            predicate = this.and(inOperator.apply(funcRef, (Collection<?>) value));
        } else {
            predicate = this;
        }
        return predicate;
    }

    @Override
    public final <T> IPredicate ifAnd(DialectBooleanOperator<T> fieldOperator,
                                      BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                      BiFunction<SimpleExpression, T, Expression> func, Supplier<T> getter) {
        final IPredicate predicate;
        final T operand;
        if ((operand = getter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(fieldOperator.apply(operator, func, operand));
        }
        return predicate;
    }

    @Override
    public final <K, V> IPredicate ifAnd(DialectBooleanOperator<V> fieldOperator,
                                         BiFunction<SimpleExpression, Expression, CompoundPredicate> operator,
                                         BiFunction<SimpleExpression, V, Expression> func, Function<K, V> function,
                                         K key) {
        final IPredicate predicate;
        final V operand;
        if ((operand = function.apply(key)) == null) {
            predicate = this;
        } else {
            predicate = this.and(fieldOperator.apply(operator, func, operand));
        }
        return predicate;
    }


    @Override
    public final IPredicate ifAnd(BiFunction<TeNamedOperator<SqlField>, Integer, IPredicate> expOperator,
                                  TeNamedOperator<SqlField> namedOperator, Supplier<Integer> supplier) {
        final IPredicate predicate;
        final Integer size;
        if ((size = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(namedOperator, size));
        }
        return predicate;
    }

    @Override
    public final <T> IPredicate ifAnd(BetweenValueOperator<T> expOperator,
                                      BiFunction<SimpleExpression, T, Expression> operator,
                                      Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        final IPredicate predicate;
        final T first, second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }

    @Override
    public final <K, V> IPredicate ifAnd(BetweenValueOperator<V> expOperator,
                                         BiFunction<SimpleExpression, V, Expression> operator,
                                         Function<K, V> function, K firstKey, SQLs.WordAnd and, K secondKey) {
        final IPredicate predicate;
        final V first, second;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(operator, first, and, second));
        }
        return predicate;
    }


    @Override
    public final IPredicate ifAnd(InNamedOperator expOperator, TeNamedOperator<SimpleExpression> namedOperator
            , String paramName, Supplier<Integer> supplier) {
        final IPredicate predicate;
        final Integer size;
        if ((size = supplier.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(namedOperator, paramName, size));
        }
        return predicate;
    }

    @Override
    public final <T, U> IPredicate ifAnd(BetweenDualOperator<T, U> expOperator,
                                         BiFunction<SimpleExpression, T, Expression> firstFuncRef,
                                         Supplier<T> firstGetter, SQLs.WordAnd and,
                                         BiFunction<SimpleExpression, U, Expression> secondFuncRef,
                                         Supplier<U> secondGetter) {
        final IPredicate predicate;
        final T first;
        final U second;
        if ((first = firstGetter.get()) == null || (second = secondGetter.get()) == null) {
            predicate = this;
        } else {
            predicate = this.and(expOperator.apply(firstFuncRef, first, and, secondFuncRef, second));
        }
        return predicate;
    }

    @Override
    public final LogicalPredicate blank(BiFunction<IPredicate, IPredicate, LogicalPredicate> funcRef, IPredicate right) {
        final LogicalPredicate predicate;
        predicate = funcRef.apply(this, right);
        if (predicate == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return predicate;
    }

    @Override
    public final LogicalPredicate blank(BiFunction<IPredicate, Consumer<Consumer<IPredicate>>, LogicalPredicate> funcRef,
                                        Consumer<Consumer<IPredicate>> consumer) {
        final LogicalPredicate predicate;
        predicate = funcRef.apply(this, consumer);
        if (predicate == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return predicate;
    }

    @Override
    public final boolean isOptimistic() {
        final boolean match;
        final Expressions.DualPredicate predicate;
        if (!(this instanceof Expressions.DualPredicate)
                || (predicate = (Expressions.DualPredicate) this).operator != DualBooleanOperator.EQUAL) {
            match = false;
        } else if (predicate.left instanceof TableField
                && _MetaBridge.VERSION.equals(((TableField) predicate.left).fieldName())) {
            match = predicate.right instanceof SqlValueParam.SingleValue
                    || predicate.right instanceof NamedParam;
        } else if (predicate.right instanceof TableField
                && _MetaBridge.VERSION.equals(((TableField) predicate.right).fieldName())) {
            match = predicate.left instanceof SqlValueParam.SingleValue
                    || predicate.left instanceof NamedParam;

        } else {
            match = false;
        }
        return match;
    }

    @Override
    public final _Predicate getIdPredicate() {
        final _Predicate predicate;
        if (this instanceof Expressions.DualPredicate) {
            predicate = getIdPredicateFromDual((Expressions.DualPredicate) this);
        } else if (this instanceof Expressions.InOperationPredicate) {
            predicate = getIdPredicateFromInPredicate((Expressions.InOperationPredicate) this);
        } else if (this instanceof AndPredicate) {
            predicate = getIdPredicateFromAndPredicate((AndPredicate) this);
        } else {
            predicate = null;
        }
        return predicate;
    }


    @Override
    public final TableField findParentId(final ChildTableMeta<?> child, final String alias) {
        final TableField parentId;
        final Expressions.DualPredicate predicate;
        final TableMeta<?> leftTable, rightTable;
        final TableField leftField, rightField;


        if (!(this instanceof Expressions.DualPredicate) || (predicate = (Expressions.DualPredicate) this).operator != DualBooleanOperator.EQUAL) {
            parentId = null;
        } else if (!(predicate.left instanceof TableField && predicate.right instanceof TableField)) {
            parentId = null;
        } else if (!((leftField = (TableField) predicate.left).fieldName().equals(_MetaBridge.ID)
                && (rightField = (TableField) predicate.right).fieldName().equals(_MetaBridge.ID))) {
            parentId = null;
        } else if ((leftTable = leftField.tableMeta()) != child & (rightTable = rightField.tableMeta()) != child) {
            parentId = null;
        } else if ((leftTable == child && rightTable == child.parentMeta())) {
            if (leftField instanceof FieldMeta || ((QualifiedField<?>) leftField).tableAlias().equals(alias)) {
                parentId = rightField;
            } else {
                parentId = null;
            }
        } else if (rightTable == child && leftTable == child.parentMeta()) {
            if (rightField instanceof FieldMeta || ((QualifiedField<?>) rightField).tableAlias().equals(alias)) {
                parentId = leftField;
            } else {
                parentId = null;
            }
        } else {
            parentId = null;
        }
        return parentId;
    }

    static OperationSimplePredicate bracketPredicate(final IPredicate predicate) {
        final OperationSimplePredicate result;
        if (predicate instanceof BracketPredicate) {
            result = (BracketPredicate) predicate;
        } else {
            result = new BracketPredicate((OperationPredicate) predicate);
        }
        return result;
    }

    static OperationSimplePredicate orPredicate(OperationPredicate left, IPredicate right) {
        return new OrPredicate(left, Collections.singletonList((OperationPredicate) right));
    }

    static OperationSimplePredicate orPredicate(OperationPredicate left, List<IPredicate> rightList) {
        final int size = rightList.size();
        assert size > 0;
        final List<OperationPredicate> list = new ArrayList<>(size);
        for (IPredicate right : rightList) {
            list.add((OperationPredicate) right);
        }
        return new OrPredicate(left, Collections.unmodifiableList(list));
    }


    static AndPredicate andPredicate(OperationPredicate left, @Nullable IPredicate right) {
        assert right != null;
        return new AndPredicate(left, (OperationPredicate) right);
    }

    static OperationPredicate notPredicate(final IPredicate predicate) {
        return new NotPredicate((OperationPredicate) predicate);
    }


    /**
     * @see SQLs#TRUE
     * @see SQLs#FALSE
     */
    static SQLs.WordBoolean booleanWord(final boolean value) {
        return value ? BooleanWord.TRUE : BooleanWord.FALSE;
    }


    @Nullable
    private static _Predicate getIdPredicateFromAndPredicate(final AndPredicate andPredicate) {
        _Predicate predicate = null;
        if (andPredicate.left instanceof Expressions.DualPredicate) {
            predicate = getIdPredicateFromDual((Expressions.DualPredicate) andPredicate.left);
        } else if (andPredicate.left instanceof Expressions.InOperationPredicate) {
            predicate = getIdPredicateFromInPredicate((Expressions.InOperationPredicate) andPredicate.left);
        } else if (andPredicate.left instanceof AndPredicate) {
            predicate = getIdPredicateFromAndPredicate((AndPredicate) andPredicate.left);
        }
        if (predicate != null) {
            return predicate;
        }

        if (andPredicate.right instanceof Expressions.DualPredicate) {
            predicate = getIdPredicateFromDual((Expressions.DualPredicate) andPredicate.right);
        } else if (andPredicate.right instanceof Expressions.InOperationPredicate) {
            predicate = getIdPredicateFromInPredicate((Expressions.InOperationPredicate) andPredicate.right);
        } else if (andPredicate.right instanceof AndPredicate) {
            predicate = getIdPredicateFromAndPredicate((AndPredicate) andPredicate.right);
        }
        return predicate;
    }

    @Nullable
    private static _Predicate getIdPredicateFromInPredicate(final Expressions.InOperationPredicate predicate) {
        final _Predicate result;
        if (!(predicate.left instanceof PrimaryFieldMeta)) {
            result = null;
        } else if (predicate.not) {
            result = null;
        } else if (predicate.right instanceof RowValueExpression) {
            result = predicate;
        } else {
            result = null;
        }
        return result;
    }

    @Nullable
    private static _Predicate getIdPredicateFromDual(final Expressions.DualPredicate predicate) {
        final _Predicate result;
        if (!(predicate.left instanceof PrimaryFieldMeta)) {
            result = null;
        } else if (predicate.operator == DualBooleanOperator.EQUAL && predicate.right instanceof ValueExpression) {
            result = predicate;
        } else {
            result = null;
        }
        return result;
    }

    /**
     * <p>
     * Private class.This class is base class of below:
     * <li>{@link BooleanWord}</li>
     * <li>{@link BracketPredicate}</li>
     * <li>{@link SqlFunctionPredicate}</li>
     * <li>{@link OrPredicate},because OR/XOR operator always have outer parentheses。</li>
     */
    static abstract class OperationSimplePredicate extends OperationPredicate
            implements SimplePredicate, ArmySimpleExpression {

        /**
         * <p>
         * <strong>Private constructor</strong>
         */
        private OperationSimplePredicate() {
        }


    }//OperationSimplePredicate


    static abstract class SqlFunctionPredicate extends OperationSimplePredicate implements ArmySQLFunction {

        final String name;

        private final boolean buildIn;

        SqlFunctionPredicate(String name, boolean buildIn) {
            this.name = name;
            this.buildIn = buildIn;
        }

        @Override
        public final String name() {
            return this.name;
        }


        @Override
        public final void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {

            context.appendFuncName(this.buildIn, this.name);

            if (this instanceof FunctionUtils.NoParensFunction) {
                return;
            }

            if (this instanceof FunctionUtils.NoArgFunction) {
                sqlBuilder.append(_Constant.PARENS);
            } else {
                sqlBuilder.append(_Constant.LEFT_PAREN);
                this.appendArg(sqlBuilder, context);
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

            if (this instanceof FunctionUtils.FunctionOuterClause) {
                ((FunctionUtils.FunctionOuterClause) this).appendFuncRest(sqlBuilder, context);
            }

        }

        @Override
        public final boolean currentLevelContainFieldOf(ParentTableMeta<?> table) {
            // function always false
            return false;
        }

        @Override
        public final String toString() {
            final StringBuilder builder = new StringBuilder();

            builder.append(_Constant.SPACE)
                    .append(this.name); // function name
            if (!(this instanceof FunctionUtils.NoParensFunction)) {
                if (this instanceof FunctionUtils.NoArgFunction) {
                    builder.append(_Constant.RIGHT_PAREN);
                } else {
                    builder.append(_Constant.LEFT_PAREN);
                    argToString(builder);
                    builder.append(_Constant.SPACE_RIGHT_PAREN);
                }
            }
            if (this instanceof FunctionUtils.FunctionOuterClause) {
                ((FunctionUtils.FunctionOuterClause) this).funcRestToString(builder);
            }
            return builder.toString();
        }

        abstract void appendArg(StringBuilder sqlBuilder, _SqlContext context);


        abstract void argToString(StringBuilder builder);


    }//SqlFunctionPredicate


    static abstract class OperationCompoundPredicate extends OperationPredicate implements CompoundPredicate {

        OperationCompoundPredicate() {
        }


    }// CompoundPredicate


    private static final class BracketPredicate extends OperationSimplePredicate {

        private final OperationPredicate predicate;

        private BracketPredicate(OperationPredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);

            this.predicate.appendSql(sqlBuilder, context);

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);

        }

        @Override
        public boolean currentLevelContainFieldOf(ParentTableMeta<?> table) {
            return this.predicate.currentLevelContainFieldOf(table);
        }

    } //BracketPredicate

    private static final class OrPredicate extends OperationSimplePredicate {

        private final OperationPredicate left;

        private final List<OperationPredicate> rightList;

        private OrPredicate(OperationPredicate left, List<OperationPredicate> rightList) {
            this.left = left;
            this.rightList = rightList;
        }


        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            this.appendOrPredicate(sqlBuilder, context);
        }

        @Override
        public boolean currentLevelContainFieldOf(final ParentTableMeta<?> table) {
            if (this.left.currentLevelContainFieldOf(table)) {
                return true;
            }

            boolean contain = false;
            for (OperationPredicate predicate : rightList) {
                if (predicate.currentLevelContainFieldOf(table)) {
                    contain = true;
                    break;
                }
            }
            return contain;
        }

        @Override
        public String toString() {
            final StringBuilder builder;
            builder = new StringBuilder();
            this.appendOrPredicate(builder, null);
            return builder.toString();
        }

        private void appendOrPredicate(final StringBuilder sqlBuilder, final @Nullable _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);// outer left paren

            final OperationPredicate left = this.left;

            if (context == null) {
                sqlBuilder.append(left);
            } else {
                left.appendSql(sqlBuilder, context);
            }

            boolean rightInnerParen;
            for (OperationPredicate right : this.rightList) {

                sqlBuilder.append(_Constant.SPACE_OR);
                rightInnerParen = right instanceof AndPredicate;
                if (rightInnerParen) {
                    sqlBuilder.append(_Constant.SPACE_LEFT_PAREN); // inner left bracket
                }

                if (context == null) {
                    sqlBuilder.append(right);
                } else {
                    right.appendSql(sqlBuilder, context);
                }

                if (rightInnerParen) {
                    sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);// inner right bracket
                }
            }

            sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN); // outer right paren
        }


    }//OrPredicate

    private static final class AndPredicate extends OperationCompoundPredicate {

        final OperationPredicate left;

        final OperationPredicate right;

        private AndPredicate(OperationPredicate left, OperationPredicate right) {
            this.left = left;
            this.right = right;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            // 1. append left operand
            this.left.appendSql(sqlBuilder, context);

            // 2. append AND operator
            sqlBuilder.append(_Constant.SPACE_AND);

            final OperationPredicate right = this.right;
            final boolean rightOuterParens = right instanceof AndPredicate;

            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            // 3. append right operand
            right.appendSql(sqlBuilder, context);
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

        }

        @Override
        public boolean currentLevelContainFieldOf(final ParentTableMeta<?> table) {
            return this.left.currentLevelContainFieldOf(table) || this.right.currentLevelContainFieldOf(table);
        }

        @Override
        public String toString() {
            final StringBuilder sqlBuilder = new StringBuilder();

            // 1. append left operand
            sqlBuilder.append(this.left);
            // 2. append AND operator
            sqlBuilder.append(_Constant.SPACE_AND);

            final OperationPredicate right = this.right;
            final boolean rightOuterParens = right instanceof AndPredicate;

            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            // 3. append right operand
            sqlBuilder.append(right);
            if (rightOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            return sqlBuilder.toString();
        }


    }//AndPredicate

    private static final class NotPredicate extends OperationCompoundPredicate {

        private final OperationPredicate predicate;

        /**
         * @see #notPredicate(IPredicate)
         */
        private NotPredicate(OperationPredicate predicate) {
            this.predicate = predicate;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(_Constant.SPACE_NOT);

            final OperationPredicate predicate = this.predicate;
            final boolean operandOuterParens = predicate instanceof AndPredicate;

            if (operandOuterParens) {
                sqlBuilder.append(_Constant.SPACE_LEFT_PAREN);
            }
            predicate.appendSql(sqlBuilder, context);

            if (operandOuterParens) {
                sqlBuilder.append(_Constant.SPACE_RIGHT_PAREN);
            }

        }

        @Override
        public boolean currentLevelContainFieldOf(ParentTableMeta<?> table) {
            return this.predicate.currentLevelContainFieldOf(table);
        }

        @Override
        public String toString() {
            final StringBuilder builder;
            builder = new StringBuilder()
                    .append(" NOT");

            final OperationPredicate predicate = this.predicate;
            final boolean operandOuterParens = predicate instanceof AndPredicate;

            if (operandOuterParens) {
                builder.append(_Constant.SPACE_LEFT_PAREN);
            }
            builder.append(this.predicate);

            if (operandOuterParens) {
                builder.append(_Constant.SPACE_RIGHT_PAREN);
            }
            return builder.toString();
        }


    }//NotPredicate


    /**
     * @see SQLs#TRUE
     * @see SQLs#FALSE
     */
    private static final class BooleanWord extends OperationSimplePredicate
            implements SQLs.WordBoolean, SQLs.ArmyKeyWord {

        private static final BooleanWord TRUE = new BooleanWord(true);

        private static final BooleanWord FALSE = new BooleanWord(false);

        private final String spaceWord;

        private BooleanWord(boolean value) {
            this.spaceWord = value ? " TRUE" : " FALSE";
        }

        @Override
        public String spaceRender() {
            return this.spaceWord;
        }

        @Override
        public void appendSql(final StringBuilder sqlBuilder, final _SqlContext context) {
            sqlBuilder.append(this.spaceWord);
        }

        @Override
        public boolean currentLevelContainFieldOf(ParentTableMeta<?> table) {
            // key word , always false
            return false;
        }

        @Override
        public String toString() {
            return this.spaceWord;
        }


    }//BooleanWord


}
