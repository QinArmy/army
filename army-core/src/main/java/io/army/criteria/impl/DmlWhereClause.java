package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.Statement;
import io.army.criteria.impl.inner._Dml;
import io.army.criteria.impl.inner._Predicate;
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
        implements Statement, Statement._WhereClause<C, WR, WA>, Statement._WhereAndClause<C, WA>, _Dml {


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
    public final WA where(Function<Object, IPredicate> operator, DataField operand) {
        return this.and(operator.apply(operand));
    }

    @Override
    public final WA where(Function<Object, IPredicate> operator, Supplier<?> operand) {
        return this.and(operator.apply(operand.get()));
    }

    @Override
    public final WA where(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        return this.and(operator.apply(operand.apply(keyName)));
    }

    @Override
    public final WA where(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        return this.and(operator.apply(firstOperand.get(), secondOperand.get()));
    }

    @Override
    public final WA where(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        return this.and(operator.apply(operand.apply(firstKey), operand.apply(secondKey)));
    }

    @Override
    public final WA whereIfNonNull(@Nullable Function<Object, IPredicate> operator, @Nullable Object operand) {
        return this.ifNonNullAnd(operator, operand);
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
    public final WA whereIf(Function<Object, IPredicate> operator, Supplier<?> operand) {
        return this.ifAnd(operator, operand);
    }

    @Override
    public final WA whereIf(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        return this.ifAnd(operator, operand, keyName);
    }

    @Override
    public final WA whereIf(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        return this.ifAnd(operator, firstOperand, secondOperand);
    }

    @Override
    public final WA whereIf(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        return this.ifAnd(operator, operand, firstKey, secondKey);
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
    public final WA and(Function<Object, IPredicate> operator, DataField operand) {
        return this.and(operator.apply(operand));
    }

    @Override
    public final WA and(Function<Object, IPredicate> operator, Supplier<?> operand) {
        return this.and(operator.apply(operand.get()));
    }

    @Override
    public final WA and(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        return this.and(operator.apply(operand.apply(keyName)));
    }

    @Override
    public final WA and(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        return this.and(operator.apply(firstOperand.get(), secondOperand.get()));
    }

    @Override
    public final WA and(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        return this.and(operator.apply(operand.apply(firstKey), operand.apply(secondKey)));
    }

    @Override
    public final WA ifNonNullAnd(@Nullable Function<Object, IPredicate> operator, @Nullable Object operand) {
        if (operator != null && operand != null) {
            this.and(operator.apply(operand));
        }
        return (WA) this;
    }

    @Override
    public final WA ifNonNullAnd(Function<Object, ? extends Expression> firstOperator, @Nullable Object firstOperand
            , BiFunction<Expression, Object, IPredicate> secondOperator, Object secondOperand) {
        if (firstOperand != null) {
            this.and(secondOperator.apply(firstOperator.apply(firstOperand), secondOperand));
        }
        return (WA) this;
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
    public final WA ifAnd(Function<Object, IPredicate> operator, Supplier<?> operand) {
        final Object paramOrExp;
        paramOrExp = operand.get();
        if (paramOrExp != null) {
            this.and(operator.apply(paramOrExp));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Function<Object, IPredicate> operator, Function<String, ?> operand, String keyName) {
        final Object paramOrExp;
        paramOrExp = operand.apply(keyName);
        if (paramOrExp != null) {
            this.and(operator.apply(paramOrExp));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(BiFunction<Object, Object, IPredicate> operator, Supplier<?> firstOperand
            , Supplier<?> secondOperand) {
        final Object first, second;
        if ((first = firstOperand.get()) != null && (second = secondOperand.get()) != null) {
            this.and(operator.apply(first, second));
        }
        return (WA) this;
    }


    @Override
    public final WA ifAnd(BiFunction<Object, Object, IPredicate> operator, Function<String, ?> operand
            , String firstKey, String secondKey) {
        final Object first, second;
        if ((first = operand.apply(firstKey)) != null && (second = operand.apply(secondKey)) != null) {
            this.and(operator.apply(first, second));
        }
        return (WA) this;
    }

    @Override
    public final WA ifAnd(Function<Object, ? extends Expression> firstOperator, Supplier<?> firstOperand
            , BiFunction<Expression, Object, IPredicate> secondOperator, Object secondOperand) {
        final Object firstValue;
        firstValue = firstOperand.get();
        if (firstValue != null) {
            final Expression expression;
            expression = firstOperator.apply(firstValue);
            if (expression == null) {
                throw CriteriaContextStack.nullPointer(this.context);
            }
            this.and(secondOperator.apply(expression, secondOperand));
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
