package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Statement;
import io.army.criteria.Update;
import io.army.criteria.impl.inner._Dml;
import io.army.criteria.impl.inner._Predicate;
import io.army.function.TePredicate;
import io.army.lang.Nullable;
import io.army.util._CollectionUtils;
import io.army.util._Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.*;

/**
 * <p>
 * This class is base class of all dml statement.
 * </p>
 *
 * @since 1.0
 */
@SuppressWarnings("unchecked")
abstract class DmlWhereClause<C, FT, FS, FP, FJ, JT, JS, JP, WR, WA>
        extends JoinableClause<C, FT, FS, FP, FJ, JT, JS, JP>
        implements Statement, Statement._WhereClause<C, WR, WA>, Update._UpdateWhereAndClause<C, WA>, _Dml {


    private List<_Predicate> predicateList = new ArrayList<>();

    DmlWhereClause(CriteriaContext criteriaContext) {
        super(criteriaContext);
    }

    DmlWhereClause(CriteriaContext criteriaContext, ClauseCreator<FP, JT, JS, JP> clauseCreator) {
        super(criteriaContext, clauseCreator);
    }

    @Override
    public final WR where(Consumer<Consumer<IPredicate>> consumer) {
        consumer.accept(this::addPredicate);
        return (WR) this;
    }

    @Override
    public final WR where(BiConsumer<C, Consumer<IPredicate>> consumer) {
        consumer.accept(this.criteria, this::addPredicate);
        return (WR) this;
    }

    @Override
    public final WA where(@Nullable IPredicate predicate) {
        if (predicate == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        this.predicateList.add((OperationPredicate) predicate);
        return (WA) this;
    }

    @Override
    public final WA where(Supplier<IPredicate> supplier) {
        return this.where(supplier.get());
    }

    @Override
    public final WA where(Function<C, IPredicate> function) {
        return this.where(function.apply(this.criteria));
    }


    @Override
    public final WA where(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.and(expOperator.apply(operand));
    }

    @Override
    public final <E extends Expression> WA where(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.and(expOperator.apply(supplier.get()));
    }

    @Override
    public final <E extends Expression> WA where(Function<E, IPredicate> expOperator, Function<C, E> function) {
        return this.and(expOperator.apply(function.apply(this.criteria)));
    }

    @Override
    public final <T> WA where(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, T operand) {
        return this.and(expOperator, operator, operand);
    }

    @Override
    public final <T> WA where(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return this.and(expOperator, operator, supplier);
    }

    @Override
    public final WA where(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return this.and(expOperator, operator, function, keyName);
    }

    @Override
    public final <T> WA where(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, T first, T second) {
        return this.and(expOperator, operator, first, second);
    }

    @Override
    public final <T> WA where(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier) {
        return this.and(expOperator, operator, firstSupplier, secondSupplier);
    }

    @Override
    public final WA where(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey) {
        return this.and(expOperator, operator, function, firstKey, secondKey);
    }

    @Override
    public final WA whereIf(Supplier<IPredicate> supplier) {
        return this.ifAnd(supplier);
    }

    @Override
    public final WA whereIf(Function<C, IPredicate> function) {
        return this.ifAnd(function);
    }


    @Override
    public final <E extends Expression> WA whereIf(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.ifAnd(expOperator, supplier);
    }

    @Override
    public final <E extends Expression> WA whereIf(Function<E, IPredicate> expOperator, Function<C, E> function) {
        return this.ifAnd(expOperator, function);
    }

    @Override
    public final <T> WA whereIf(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, T operand) {
        return this.ifAnd(expOperator, operator, operand);
    }

    @Override
    public final <T> WA whereIf(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return this.ifAnd(expOperator, operator, supplier);
    }

    @Override
    public final WA whereIf(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return this.ifAnd(expOperator, operator, function, keyName);
    }

    @Override
    public final <T> WA whereIf(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, T first, T second) {
        return this.ifAnd(expOperator, operator, first, second);
    }

    @Override
    public final <T> WA whereIf(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier) {
        return this.ifAnd(expOperator, operator, firstSupplier, secondSupplier);
    }

    @Override
    public final WA whereIf(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey) {
        return this.ifAnd(expOperator, operator, function, firstKey, secondKey);
    }

    @Override
    public final WA and(@Nullable IPredicate predicate) {
        if (predicate == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        this.predicateList.add((OperationPredicate) predicate);
        return (WA) this;
    }

    @Override
    public final WA and(Supplier<IPredicate> supplier) {
        return this.and(supplier.get());
    }

    @Override
    public final WA and(Function<C, IPredicate> function) {
        return this.and(function.apply(this.criteria));
    }


    @Override
    public final WA and(Function<Expression, IPredicate> expOperator, Expression operand) {
        return this.and(expOperator.apply(operand));
    }


    @Override
    public final <E extends Expression> WA and(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        return this.and(expOperator.apply(supplier.get()));
    }

    @Override
    public final <E extends Expression> WA and(Function<E, IPredicate> expOperator, Function<C, E> function) {
        return this.and(expOperator.apply(function.apply(this.criteria)));
    }

    @Override
    public final <T> WA and(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        if (operand == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, operand));
    }

    @Override
    public final <T> WA and(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        final T operand;
        operand = supplier.get();
        if (operand == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, operand));
    }

    @Override
    public final WA and(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        final Object operand;
        operand = function.apply(keyName);
        if (operand == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, operand));
    }

    @Override
    public final <T> WA and(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T first, @Nullable T second) {
        if (first == null || second == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, first, second));
    }

    @Override
    public final <T> WA and(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier) {
        final T first, second;
        if ((first = firstSupplier.get()) == null || (second = secondSupplier.get()) == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, first, second));
    }

    @Override
    public final WA and(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey) {
        final Object first, second;
        if ((first = function.apply(firstKey)) == null || (second = function.apply(secondKey)) == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        return this.and(expOperator.apply(operator, first, second));
    }

    @Override
    public final <T> WA and(BiFunction<BiFunction<Expression, T, Expression>, T, Expression> expOperator1, BiFunction<Expression, T, Expression> operator, @Nullable T operand1, BiFunction<Expression, Expression, IPredicate> expOperator2, @Nullable T operator2) {
        if (operand1 == null || operator2 == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        final Expression expression;
        expression = expOperator1.apply(operator, operand1);
        if (expression == null) {
            throw CriteriaContextStack.nullPointer(this.context);
        }
        return this.and(expOperator2.apply(expression, operator.apply(expression, operator2)));
    }

    @Override
    public final WA ifAnd(Supplier<IPredicate> supplier) {
        final IPredicate predicate;
        predicate = supplier.get();
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Function<C, IPredicate> function) {
        final IPredicate predicate;
        predicate = function.apply(this.criteria);
        if (predicate != null) {
            this.predicateList.add((OperationPredicate) predicate);
        }
        return (WA) this;
    }


    @Override
    public final <E extends Expression> WA ifAnd(Function<E, IPredicate> expOperator, Supplier<E> supplier) {
        final E expression;
        expression = supplier.get();
        if (expression != null) {
            this.and(expOperator.apply(expression));
        }
        return (WA) this;
    }

    @Override
    public final <E extends Expression> WA ifAnd(Function<E, IPredicate> expOperator, Function<C, E> function) {
        final E expression;
        expression = function.apply(this.criteria);
        if (expression != null) {
            this.and(expOperator.apply(expression));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T operand) {
        if (operand != null) {
            this.and(expOperator.apply(operator, operand));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(BiFunction<BiFunction<Expression, T, Expression>, T, IPredicate> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        final T operand;
        operand = supplier.get();
        if (operand != null) {
            this.and(expOperator.apply(operator, operand));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(BiFunction<BiFunction<Expression, Object, Expression>, Object, IPredicate> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        final Object operand;
        operand = function.apply(keyName);
        if (operand != null) {
            this.and(expOperator.apply(operator, operand));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, @Nullable T first, @Nullable T second) {
        if (first != null && second != null) {
            this.and(expOperator.apply(operator, first, second));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(TePredicate<BiFunction<Expression, T, Expression>, T, T> expOperator, BiFunction<Expression, T, Expression> operator, Supplier<T> firstSupplier, Supplier<T> secondSupplier) {
        final T first, second;
        if ((first = firstSupplier.get()) != null && (second = secondSupplier.get()) != null) {
            this.and(expOperator.apply(operator, first, second));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(TePredicate<BiFunction<Expression, Object, Expression>, Object, Object> expOperator, BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String firstKey, String secondKey) {
        final Object first, second;
        if ((first = function.apply(firstKey)) != null && (second = function.apply(secondKey)) != null) {
            this.and(expOperator.apply(operator, first, second));
        }
        return (WA) this;
    }

    @Override
    public final <T> WA ifAnd(BiFunction<BiFunction<Expression, T, Expression>, T, Expression> expOperator1, BiFunction<Expression, T, Expression> operator, @Nullable T operand1, BiFunction<Expression, Expression, IPredicate> expOperator2, @Nullable T operator2) {
        if (operand1 != null || operator2 != null) {
            final Expression expression;
            expression = expOperator1.apply(operator, operand1);
            if (expression == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.and(expOperator2.apply(expression, operator.apply(expression, operator2)));
        }
        return (WA) this;
    }

    @Override
    public final List<_Predicate> predicateList() {
        final List<_Predicate> predicateList = this.predicateList;
        if (predicateList == null || predicateList instanceof ArrayList) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        return predicateList;
    }

    final void asDmlStatement() {
        final List<_Predicate> predicates = this.predicateList;
        if (predicates == null || predicates.size() == 0) {
            throw CriteriaContextStack.criteriaError(this.context, _Exceptions::dmlNoWhereClause);
        } else if (!(predicates instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        this.predicateList = _CollectionUtils.unmodifiableList(predicates);
    }

    final void clearWherePredicate() {
        this.predicateList = null;
    }


    private void addPredicate(final IPredicate predicate) {
        final List<_Predicate> predicateList = this.predicateList;
        if (!(predicateList instanceof ArrayList)) {
            throw CriteriaContextStack.castCriteriaApi(this.context);
        }
        predicateList.add((OperationPredicate) predicate);
    }


}
