package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.impl.inner._Statement;
import io.army.dialect.Dialect;
import io.army.function.*;
import io.army.lang.Nullable;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.function.*;

/**
 * <p>
 * package class
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class WhereClause<WR, WA, OR, LR, LO, LF> extends LimitRowOrderByClause<OR, LR, LO, LF>
        implements Statement._WhereClause<WR, WA>
        , Statement._WhereAndClause<WA>
        , UpdateStatement._UpdateWhereAndClause<WA>
        , _Statement._WherePredicateListSpec {

    final CriteriaContext context;


    private List<_Predicate> predicateList;

    WhereClause(CriteriaContext context) {
        super(context);
        this.context = context;
    }


    @Override
    public final WR where(Consumer<Consumer<IPredicate>> consumer) {
        consumer.accept(this::and);
        if (this.predicateList == null) {
            throw ContextStack.criteriaError(this.context, _Exceptions::predicateListIsEmpty);
        }
        return (WR) this;
    }


    @Override
    public final WA where(IPredicate predicate) {
        return this.and(predicate);
    }

    @Override
    public final WA where(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }


    @Override
    public final WA where(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final WA where(UnaryOperator<IPredicate> expOperator, IPredicate operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final <E extends RightOperand> WA where(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.and(expOperator.apply(supplier.get()));
    }

    @Override
    public final WA where(Function<BiFunction<DataField, String, Expression>, IPredicate> fieldOperator
            , BiFunction<DataField, String, Expression> namedOperator) {
        return this.and(fieldOperator.apply(namedOperator));
    }

    @Override
    public final WA where(ExpressionOperator<Expression, Expression, IPredicate> expOperator,
                          BiFunction<Expression, Expression, Expression> valueOperator, Expression expression) {
        return this.and(expOperator.apply(valueOperator, expression));
    }

    @Override
    public final WA where(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                          BiFunction<Expression, Object, Expression> valueOperator, Object value) {
        return this.and(expOperator.apply(valueOperator, value));
    }

    @Override
    public final <T> WA where(ExpressionOperator<Expression, T, IPredicate> expOperator,
                              BiFunction<Expression, T, Expression> valueOperator, Supplier<T> getter) {
        return this.and(expOperator.apply(valueOperator, getter.get()));
    }

    @Override
    public final WA where(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                          TeNamedOperator<DataField> namedOperator, int size) {
        return this.and(expOperator.apply(namedOperator, size));
    }

    @Override
    public final WA where(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                          BiFunction<Expression, Object, Expression> valueOperator, Function<String, ?> function,
                          String keyName) {
        return this.and(expOperator.apply(valueOperator, function.apply(keyName)));
    }


    @Override
    public final WA where(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                          Object firstValue, SQLs.WordAnd and, Object secondValue) {
        return this.and(expOperator.apply(operator, firstValue, and, secondValue));
    }

    @Override
    public final <T> WA where(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                              Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        return this.and(expOperator.apply(operator, firstGetter.get(), and, secondGetter.get()));
    }

    @Override
    public final WA where(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                          Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey) {
        return this.and(expOperator.apply(operator, function.apply(firstKey), and, function.apply(secondKey)));
    }


    @Override
    public final WA where(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
        return this.and(expOperator.apply(first, and, second));
    }

    @Override
    public final WA where(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator,
                          String paramName, int size) {
        return this.and(expOperator.apply(namedOperator, paramName, size));
    }


    @Override
    public final WA whereIf(Supplier<IPredicate> supplier) {
        return this.ifAnd(supplier);
    }


    @Override
    public final <E extends RightOperand> WA whereIf(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.ifAnd(expOperator, supplier);
    }

    @Override
    public final <T> WA whereIf(ExpressionOperator<Expression, T, IPredicate> expOperator,
                                BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        return this.ifAnd(expOperator, operator, getter);
    }

    @Override
    public final WA whereIf(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                            TeNamedOperator<DataField> namedOperator, Supplier<Integer> supplier) {
        return this.ifAnd(expOperator, namedOperator, supplier);
    }

    @Override
    public final WA whereIf(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                            BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                            String keyName) {
        return this.ifAnd(expOperator, operator, function, keyName);
    }

    @Override
    public final <T> WA whereIf(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                                Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        return this.ifAnd(expOperator, operator, firstGetter, and, secondGetter);
    }


    @Override
    public final WA whereIf(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                            Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey) {
        return this.ifAnd(expOperator, operator, function, firstKey, and, secondKey);
    }


    @Override
    public final WA whereIf(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator,
                            String paramName, Supplier<Integer> supplier) {
        return this.ifAnd(expOperator, namedOperator, paramName, supplier);
    }

    @Override
    public final WA and(@Nullable IPredicate predicate) {
        if (predicate == null) {
            throw ContextStack.nullPointer(this.context);
        }
        List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null) {
            predicateList = new ArrayList<>();
            this.predicateList = predicateList;
        } else if (!(predicateList instanceof ArrayList)) {
            throw ContextStack.castCriteriaApi(this.context);
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
    public final <E extends RightOperand> WA and(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.and(expOperator.apply(supplier.get()));
    }


    @Override
    public final WA and(Function<BiFunction<DataField, String, Expression>, IPredicate> fieldOperator,
                        BiFunction<DataField, String, Expression> namedOperator) {
        return this.and(fieldOperator.apply(namedOperator));
    }

    @Override
    public final WA and(ExpressionOperator<Expression, Expression, IPredicate> expOperator,
                        BiFunction<Expression, Expression, Expression> valueOperator, Expression expression) {
        return this.and(expOperator.apply(valueOperator, expression));
    }

    @Override
    public final WA and(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                        BiFunction<Expression, Object, Expression> valueOperator, Object value) {
        return this.and(expOperator.apply(valueOperator, value));
    }

    @Override
    public final <T> WA and(ExpressionOperator<Expression, T, IPredicate> expOperator,
                            BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        return this.and(expOperator.apply(operator, getter.get()));
    }

    @Override
    public final WA and(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                        TeNamedOperator<DataField> namedOperator, int size) {
        return this.and(expOperator.apply(namedOperator, size));
    }

    @Override
    public final WA and(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                        BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                        String keyName) {
        return this.and(expOperator.apply(operator, function.apply(keyName)));
    }


    @Override
    public final WA and(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                        Object firstValue, SQLs.WordAnd and, Object secondValue) {
        return this.and(expOperator.apply(operator, firstValue, and, secondValue));
    }

    @Override
    public final <T> WA and(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                            Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        return this.and(expOperator.apply(operator, firstGetter.get(), and, secondGetter.get()));
    }

    @Override
    public final WA and(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                        Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey) {
        return this.and(expOperator.apply(operator, function.apply(firstKey), and, function.apply(secondKey)));
    }

    @Override
    public final WA and(BetweenOperator expOperator, Expression first, SQLs.WordAnd and, Expression second) {
        return this.and(expOperator.apply(first, and, second));
    }

    @Override
    public final WA and(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName,
                        int size) {
        return this.and(expOperator.apply(namedOperator, paramName, size));
    }

    @Override
    public final <T> WA and(ExpressionOperator<Expression, T, Expression> expOperator1,
                            BiFunction<Expression, T, Expression> operator, @Nullable T operand1,
                            BiFunction<Expression, Expression, IPredicate> expOperator2,
                            @Nullable Number numberOperand) {
        if (operand1 == null || numberOperand == null) {
            throw ContextStack.nullPointer(this.context);
        }
        final Expression expression;
        expression = expOperator1.apply(operator, operand1);
        if (expression == null) {
            throw ContextStack.nullPointer(this.context);
        }
        return this.and(expOperator2.apply(expression, SQLs.literalFrom(numberOperand)));
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
    public final <E extends RightOperand> WA ifAnd(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        final E expression;
        expression = supplier.get();
        if (expression != null) {
            this.and(expOperator.apply(expression));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(ExpressionOperator<Expression, T, IPredicate> expOperator,
                              BiFunction<Expression, T, Expression> operator, Supplier<T> getter) {
        final T operand;
        operand = getter.get();
        if (operand != null) {
            this.and(expOperator.apply(operator, operand));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(BiFunction<TeNamedOperator<DataField>, Integer, IPredicate> expOperator,
                          TeNamedOperator<DataField> namedOperator, Supplier<Integer> supplier) {
        final Integer size;
        if ((size = supplier.get()) != null) {
            this.and(expOperator.apply(namedOperator, size));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(ExpressionOperator<Expression, Object, IPredicate> expOperator,
                          BiFunction<Expression, Object, Expression> operator, Function<String, ?> function,
                          String keyName) {
        final Object operand;
        operand = function.apply(keyName);
        if (operand != null) {
            this.and(expOperator.apply(operator, operand));
        }
        return (WA) this;
    }


    @Override
    public final <T> WA ifAnd(BetweenValueOperator<T> expOperator, BiFunction<Expression, T, Expression> operator,
                              Supplier<T> firstGetter, SQLs.WordAnd and, Supplier<T> secondGetter) {
        final T first, second;
        if ((first = firstGetter.get()) != null && (second = secondGetter.get()) != null) {
            this.and(expOperator.apply(operator, first, and, second));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(BetweenValueOperator<Object> expOperator, BiFunction<Expression, Object, Expression> operator,
                          Function<String, ?> function, String firstKey, SQLs.WordAnd and, String secondKey) {
        final Object first, second;
        if ((first = function.apply(firstKey)) != null && (second = function.apply(secondKey)) != null) {
            this.and(expOperator.apply(operator, first, and, second));
        }
        return (WA) this;
    }


    @Override
    public final WA ifAnd(InNamedOperator expOperator, TeNamedOperator<Expression> namedOperator, String paramName,
                          Supplier<Integer> supplier) {
        final Integer size;
        if ((size = supplier.get()) != null) {
            this.and(expOperator.apply(namedOperator, paramName, size));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(ExpressionOperator<Expression, T, Expression> expOperator1,
                              BiFunction<Expression, T, Expression> operator, @Nullable T operand1,
                              BiFunction<Expression, Expression, IPredicate> expOperator2, @Nullable Number numberOperand) {
        if (operand1 != null && numberOperand != null) {
            final Expression expression;
            expression = expOperator1.apply(operator, operand1);
            if (expression == null) {
                throw ContextStack.nullPointer(this.context);
            }
            this.and(expOperator2.apply(expression, SQLs.literalFrom(numberOperand)));
        }
        return (WA) this;
    }

    @Override
    public final List<_Predicate> wherePredicateList() {
        final List<_Predicate> list = this.predicateList;
        if (list == null || list instanceof ArrayList) {
            throw ContextStack.castCriteriaApi(this.context);
        }
        return list;
    }


    final void clearWhereClause() {
        this.predicateList = null;
    }

    final List<_Predicate> endWhereClause() {
        List<_Predicate> list = this.predicateList;
        if (list instanceof ArrayList) {
            list = _CollectionUtils.unmodifiableList(list);
            this.predicateList = list;
        } else if (list != null) {
            throw ContextStack.castCriteriaApi(this.context);
        } else if (this instanceof Statement.DmlStatementSpec) {
            //dml statement must have where clause
            throw ContextStack.criteriaError(this.context, _Exceptions::dmlNoWhereClause);
        } else {
            list = Collections.emptyList();
            this.predicateList = list;
        }
        return list;
    }


    static abstract class WhereClauseClause<WR, WA> extends WhereClause<WR, WA, Object, Object, Object, Object> {

        WhereClauseClause(CriteriaContext context) {
            super(context);
        }


        @Override
        final Dialect statementDialect() {
            throw ContextStack.castCriteriaApi(this.context);
        }

    }//WhereClauseClause


}
