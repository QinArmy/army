package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.mapping._MappingFactory;
import io.army.meta.ParamMeta;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * this class is base class of most implementation of {@link Expression}
 */
abstract class OperationExpression<E> implements ArmyExpression<E> {

    OperationExpression() {
    }

    @Override
    public final Selection as(final String alias) {
        final Selection selection;
        if (this instanceof GenericField) {
            selection = new FieldSelectionImpl((GenericField<?, ?>) this, alias);
        } else {
            selection = new ExpressionSelection(this, alias);
        }
        return selection;
    }

    @Override
    public final boolean isNullableValue() {
        final boolean nullable;
        if (this instanceof ValueExpression) {
            nullable = ((ValueExpression<?>) this).value() == null;
        } else {
            nullable = this instanceof NamedParam && !(this instanceof NonNullNamedParam);
        }
        return nullable;
    }

    @Override
    public final IPredicate equal(Object operand) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.paramWithExp(this, operand));
    }

    @Override
    public final IPredicate equalParam(Object operand) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.strictParamWithExp(this, operand));
    }

    @Override
    public final IPredicate equalNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate ifEqualParam(@Nullable Object operand) {
        return operand == null ? null : this.equalParam(operand);
    }

    @Nullable
    @Override
    public final IPredicate ifEqual(final @Nullable Object operand) {
        return operand == null ? null : this.equal(operand);
    }

    @Override
    public final <C, O> IPredicate equal(Function<C, Expression<O>> function) {
        return DualPredicate.create(this, DualOperator.EQ, function);
    }

    @Override
    public final <O> IPredicate equal(Supplier<Expression<O>> supplier) {
        return DualPredicate.create(this, DualOperator.EQ, supplier.get());
    }

    @Override
    public final <C> IPredicate equalAny(Function<C, ColumnSubQuery> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final IPredicate equalAny(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, ColumnSubQuery> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final IPredicate equalSome(Supplier<ColumnSubQuery> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.SOME, subQuery.get());
    }

    @Override
    public final IPredicate lessThan(Object operand) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.paramWithExp(this, operand));
    }

    @Override
    public final IPredicate lessThanParam(Object operand) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.strictParamWithExp(this, operand));
    }

    @Override
    public final IPredicate lessThanNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Nullable
    @Override
    public final IPredicate ifLessThan(@Nullable Object operand) {
        return operand == null ? null : this.lessThan(operand);
    }

    @Override
    public final IPredicate ifLessThanParam(@Nullable Object operand) {
        return operand == null ? null : this.lessThanParam(operand);
    }

    @Override
    public final <C, O> IPredicate lessThan(Function<C, Expression<O>> function) {
        return DualPredicate.create(this, DualOperator.LT, function);
    }

    @Override
    public final <O> IPredicate lessThan(Supplier<Expression<O>> supplier) {
        return DualPredicate.create(this, DualOperator.LT, supplier.get());
    }

    @Override
    public final <C> IPredicate lessThanAny(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ANY, function);
    }

    @Override
    public final IPredicate lessThanAny(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate lessThanSome(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.SOME, function);
    }

    @Override
    public final IPredicate lessThanSome(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate lessThanAll(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate lessThanAll(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate lessEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.paramWithExp(this, operand));
    }

    @Override
    public final IPredicate lessEqualParam(Object operand) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.strictParamWithExp(this, operand));
    }

    @Override
    public final IPredicate lessEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate ifLessEqual(@Nullable Object operand) {
        return operand == null ? null : this.lessEqual(operand);
    }

    @Override
    public final IPredicate ifLessEqualParam(@Nullable Object operand) {
        return operand == null ? null : this.lessEqualParam(operand);
    }

    @Override
    public final <C, O> IPredicate lessEqual(Function<C, Expression<O>> function) {
        return DualPredicate.create(this, DualOperator.LE, function);
    }

    @Override
    public final <O> IPredicate lessEqual(Supplier<Expression<O>> supplier) {
        return DualPredicate.create(this, DualOperator.LE, supplier.get());
    }

    @Override
    public final <C> IPredicate lessEqualAny(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ANY, function);
    }

    @Override
    public final IPredicate lessEqualAny(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate lessEqualSome(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.SOME, function);
    }

    @Override
    public final IPredicate lessEqualSome(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.SOME, supplier.get());
    }


    @Override
    public final <C> IPredicate lessEqualAll(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate lessEqualAll(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate greatThan(Object operand) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.paramWithExp(this, operand));
    }

    @Override
    public final IPredicate greatThanParam(Object operand) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.strictParamWithExp(this, operand));
    }

    @Override
    public final IPredicate greatThanNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate ifGreatThan(@Nullable Object operand) {
        return operand == null ? null : this.greatThan(operand);
    }

    @Override
    public final IPredicate ifGreatThanParam(@Nullable Object operand) {
        return operand == null ? null : this.greatThanParam(operand);
    }

    @Override
    public final <C, O> IPredicate greatThan(Function<C, Expression<O>> function) {
        return DualPredicate.create(this, DualOperator.GT, function);
    }

    @Override
    public final <O> IPredicate greatThan(Supplier<Expression<O>> supplier) {
        return DualPredicate.create(this, DualOperator.GT, supplier.get());
    }


    @Override
    public final <C> IPredicate greatThanAny(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ANY, function);
    }

    @Override
    public final IPredicate greatThanAny(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ANY, supplier.get());
    }


    @Override
    public final <C> IPredicate greatThanSome(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.SOME, function);
    }

    @Override
    public final IPredicate greatThanSome(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate greatThanAll(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate greatThanAll(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate greatEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.paramWithExp(this, operand));
    }

    @Override
    public final IPredicate greatEqualParam(Object operand) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.strictParamWithExp(this, operand));
    }

    @Override
    public final IPredicate greatEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate IfGreatEqual(@Nullable Object operand) {
        return operand == null ? null : this.greatEqual(operand);
    }

    @Override
    public final IPredicate ifGreatEqualParam(@Nullable Object operand) {
        return operand == null ? null : this.greatEqualParam(operand);
    }


    @Override
    public final <C, O> IPredicate greatEqual(Function<C, Expression<O>> function) {
        return DualPredicate.create(this, DualOperator.GE, function);
    }

    @Override
    public final <O> IPredicate greatEqual(Supplier<Expression<O>> supplier) {
        return DualPredicate.create(this, DualOperator.GE, supplier.get());
    }


    @Override
    public final <C> IPredicate greatEqualAny(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ANY, function);
    }

    @Override
    public final IPredicate greatEqualAny(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.SOME, function);
    }

    @Override
    public final IPredicate greatEqualSome(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate greatEqualAll(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ALL, supplier.get());
    }


    @Override
    public final IPredicate notEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.paramWithExp(this, operand));
    }

    @Override
    public final IPredicate notEqualParam(Object operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.strictParamWithExp(this, operand));

    }

    @Override
    public final IPredicate notEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate ifNotEqual(@Nullable Object operand) {
        return operand == null ? null : this.notEqual(operand);
    }

    @Override
    public final IPredicate ifNotEqualParam(@Nullable Object operand) {
        return operand == null ? null : this.notEqualParam(operand);
    }

    @Override
    public final <C, O> IPredicate notEqual(Function<C, Expression<O>> function) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, function);
    }

    @Override
    public final <O> IPredicate notEqual(Supplier<Expression<O>> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualAny(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ANY, function);
    }

    @Override
    public final IPredicate notEqualAny(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualSome(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.SOME, function);
    }

    @Override
    public final IPredicate notEqualSome(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualAll(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate notEqualAll(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate between(Object firstOperand, Object secondOperand) {
        Objects.requireNonNull(firstOperand);
        Objects.requireNonNull(secondOperand);

        final Expression<?> first, second;
        first = SQLs.paramWithExp(this, firstOperand);
        second = SQLs.paramWithExp(this, secondOperand);
        return BetweenPredicate.between(this, first, second);
    }

    @Override
    public final IPredicate betweenParam(Object firstOperand, Object secondOperand) {
        Objects.requireNonNull(firstOperand);
        Objects.requireNonNull(secondOperand);

        final Expression<?> first, second;
        first = SQLs.strictParamWithExp(this, firstOperand);
        second = SQLs.strictParamWithExp(this, secondOperand);
        return BetweenPredicate.between(this, first, second);
    }


    @Override
    public final IPredicate ifBetween(@Nullable Object firstOperand, @Nullable Object secondOperand) {
        final IPredicate predicate;
        if (firstOperand != null && secondOperand != null) {
            predicate = this.between(firstOperand, secondOperand);
        } else {
            predicate = null;
        }
        return predicate;
    }


    @Override
    public final <C> IPredicate between(Function<C, BetweenWrapper> function) {
        final BetweenWrapper wrapper;
        wrapper = function.apply(CriteriaContextStack.getCriteria());
        assert wrapper != null;
        return BetweenPredicate.between(this, wrapper.first(), wrapper.second());
    }

    @Override
    public final IPredicate ifBetweenParam(@Nullable Object firstOperand, @Nullable Object secondOperand) {
        final IPredicate predicate;
        if (firstOperand != null && secondOperand != null) {
            predicate = this.betweenParam(firstOperand, secondOperand);
        } else {
            predicate = null;
        }
        return predicate;
    }


    @Override
    public final IPredicate isNull() {
        return UnaryPredicate.create(UnaryOperator.IS_NULL, this);
    }

    @Override
    public final IPredicate isNotNull() {
        return UnaryPredicate.create(UnaryOperator.IS_NOT_NULL, this);
    }

    @Override
    public final <O> IPredicate in(Collection<O> parameters) {
        return DualPredicate.create(this, DualOperator.IN, SQLs.optimizingParams(this.paramMeta(), parameters));
    }

    @Override
    public final <O> IPredicate inParam(Collection<O> parameters) {
        return DualPredicate.create(this, DualOperator.IN, SQLs.params(this.paramMeta(), parameters));
    }

    @Override
    public final IPredicate inNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.IN, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <O> IPredicate ifIn(@Nullable Collection<O> parameters) {
        return (parameters == null || parameters.size() == 0) ? null : this.in(parameters);
    }

    @Override
    public final <O> IPredicate ifInParam(@Nullable Collection<O> parameters) {
        return (parameters == null || parameters.size() == 0) ? null : this.inParam(parameters);
    }

    @Override
    public final <O> IPredicate in(Expression<Collection<O>> parameters) {
        return DualPredicate.create(this, DualOperator.IN, parameters);
    }

    @Override
    public final <C> IPredicate in(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.IN, function);
    }

    @Override
    public final IPredicate in(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.IN, supplier.get());
    }

    @Override
    public final <O> IPredicate notIn(Collection<O> parameters) {
        return DualPredicate.create(this, DualOperator.NOT_IN, SQLs.optimizingParams(this.paramMeta(), parameters));
    }

    @Override
    public final <O> IPredicate notInParam(Collection<O> parameters) {
        return DualPredicate.create(this, DualOperator.NOT_IN, SQLs.params(this.paramMeta(), parameters));
    }

    @Override
    public final IPredicate notInNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_IN, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <O> IPredicate ifNotIn(@Nullable Collection<O> parameters) {
        return (parameters == null || parameters.size() == 0) ? null : this.notIn(parameters);
    }

    @Override
    public final <O> IPredicate ifNotInParam(@Nullable Collection<O> parameters) {
        return (parameters == null || parameters.size() == 0) ? null : this.notInParam(parameters);
    }

    @Override
    public final <O> IPredicate notIn(Expression<Collection<O>> values) {
        return DualPredicate.create(this, DualOperator.NOT_IN, values);
    }

    @Override
    public final <C> IPredicate notIn(Function<C, ColumnSubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_IN, function);
    }

    @Override
    public final IPredicate notIn(Supplier<ColumnSubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_IN, supplier.get());
    }

    @Override
    public final IPredicate like(Object pattern) {
        if (!(pattern instanceof String || pattern instanceof Expression)) {
            throw new CriteriaException("pattern must be String or Expression.");
        }
        return DualPredicate.create(this, DualOperator.LIKE, SQLs.strictParamWithExp(this, pattern));
    }

    @Nullable
    @Override
    public final IPredicate ifLike(@Nullable Object pattern) {
        return pattern == null ? null : this.like(pattern);
    }

    @Override
    public final IPredicate likeNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LIKE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> IPredicate like(Function<C, Expression<String>> function) {
        return DualPredicate.create(this, DualOperator.LIKE, function);
    }

    @Override
    public final IPredicate like(Supplier<Expression<String>> supplier) {
        return DualPredicate.create(this, DualOperator.LIKE, supplier.get());
    }

    @Override
    public final IPredicate notLike(Object pattern) {
        if (!(pattern instanceof String || pattern instanceof Expression)) {
            throw new CriteriaException("pattern must be String or Expression.");
        }
        return DualPredicate.create(this, DualOperator.NOT_LIKE, SQLs.strictParamWithExp(this, pattern));
    }

    @Override
    public final IPredicate ifNotLike(@Nullable Object pattern) {
        return pattern == null ? null : this.notLike(pattern);
    }

    @Override
    public final IPredicate notLikeNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, SQLs.namedParam(paramName, this.paramMeta()));
    }


    @Override
    public final <C> IPredicate notLike(Function<C, Expression<String>> function) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, function);
    }

    @Override
    public final IPredicate notLike(Supplier<Expression<String>> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, supplier.get());
    }

    @Override
    public final Expression<E> mod(Object operand) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.paramWithExp(this, operand));
    }

    @Override
    public final Expression<E> modParam(Object operand) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.strictParamWithExp(this, operand));
    }

    @Override
    public final Expression<E> modNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C, O> Expression<E> mod(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.MOD, function);
    }

    @Override
    public final <O> Expression<E> mod(Supplier<Expression<O>> supplier) {
        return DualExpression.create(this, DualOperator.MOD, supplier.get());
    }

    @Override
    public final Expression<E> multiply(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.paramWithExp(this, multiplicand));
    }

    @Override
    public final Expression<E> multiplyParam(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.strictParamWithExp(this, multiplicand));
    }

    @Override
    public final Expression<E> multiplyNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C, O> Expression<E> multiply(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.MULTIPLY, function);
    }

    @Override
    public final <O> Expression<E> multiply(Supplier<Expression<O>> supplier) {
        return DualExpression.create(this, DualOperator.MULTIPLY, supplier.get());
    }

    @Override
    public final Expression<E> plus(Object augend) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.paramWithExp(this, augend));
    }

    @Override
    public final Expression<E> plusParam(Object augend) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.strictParamWithExp(this, augend));
    }

    @Override
    public final Expression<E> plusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.namedParam(paramName, this.paramMeta()));
    }


    @Override
    public final <C, O> Expression<E> plus(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.PLUS, function);
    }

    @Override
    public final <O> Expression<E> plus(Supplier<Expression<O>> supplier) {
        return DualExpression.create(this, DualOperator.PLUS, supplier.get());
    }

    @Override
    public final Expression<E> minus(Object minuend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.paramWithExp(this, minuend));
    }

    @Override
    public final Expression<E> minusParam(Object minuend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.strictParamWithExp(this, minuend));
    }

    @Override
    public final Expression<E> minusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C, O> Expression<E> minus(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.MINUS, function);
    }

    @Override
    public final <O> Expression<E> minus(Supplier<Expression<O>> supplier) {
        return DualExpression.create(this, DualOperator.MINUS, supplier.get());
    }

    @Override
    public final Expression<E> divide(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.paramWithExp(this, divisor));
    }

    @Override
    public final Expression<E> divideParam(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.strictParamWithExp(this, divisor));
    }

    @Override
    public final Expression<E> divideNamed(String paramName) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C, O> Expression<E> divide(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.DIVIDE, function);
    }

    @Override
    public final <O> Expression<E> divide(Supplier<Expression<O>> supplier) {
        return DualExpression.create(this, DualOperator.DIVIDE, supplier.get());
    }

    @Override
    public final Expression<E> negate() {
        return UnaryExpression.create(this, UnaryOperator.NEGATED);
    }

    @Override
    public final Expression<E> bitwiseAnd(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.paramWithExp(this, operand));
    }

    @Override
    public final Expression<E> bitwiseAndParam(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.strictParamWithExp(this, operand));
    }

    @Override
    public final Expression<E> bitwiseAndNamed(String paramName) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C, O> Expression<E> bitwiseAnd(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.BITWISE_AND, function);
    }

    @Override
    public final <O> Expression<E> bitwiseAnd(Supplier<Expression<O>> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, supplier.get());
    }

    @Override
    public final Expression<E> bitwiseOr(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.paramWithExp(this, operand));
    }

    @Override
    public final Expression<E> bitwiseOrParam(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.strictParamWithExp(this, operand));
    }

    @Override
    public final Expression<E> bitwiseOrNamed(String paramName) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C, O> Expression<E> bitwiseOr(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.BITWISE_OR, function);
    }

    @Override
    public final <O> Expression<E> bitwiseOr(Supplier<Expression<O>> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, supplier.get());
    }

    @Override
    public final Expression<E> xor(Object operand) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.paramWithExp(this, operand));
    }

    @Override
    public final Expression<E> xorParam(Object operand) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.strictParamWithExp(this, operand));
    }

    @Override
    public final Expression<E> xorNamed(String paramName) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C, O> Expression<E> xor(Function<C, Expression<O>> function) {
        return DualExpression.functionCreate(this, DualOperator.XOR, function);
    }

    @Override
    public final <O> Expression<E> xor(Supplier<Expression<O>> supplier) {
        return DualExpression.create(this, DualOperator.XOR, supplier.get());
    }

    @Override
    public final Expression<E> inversion() {
        return UnaryExpression.create(this, UnaryOperator.INVERT);
    }

    @Override
    public final Expression<E> rightShift(Object bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.paramWithExp(this, bitNumber));
    }

    @Override
    public final Expression<E> rightShiftParam(Object bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.strictParamWithExp(this, bitNumber));
    }

    @Override
    public final Expression<E> rightShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C, N extends Number> Expression<E> rightShift(Function<C, Expression<N>> function) {
        return DualExpression.functionCreate(this, DualOperator.RIGHT_SHIFT, function);
    }

    @Override
    public final <N extends Number> Expression<E> rightShift(Supplier<Expression<N>> supplier) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, supplier.get());
    }

    @Override
    public final Expression<E> leftShift(Object bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.paramWithExp(this, bitNumber));
    }

    @Override
    public final Expression<E> leftShiftParam(Object bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.strictParamWithExp(this, bitNumber));
    }

    @Override
    public final Expression<E> leftShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C, N extends Number> Expression<E> leftShift(Function<C, Expression<N>> function) {
        return DualExpression.functionCreate(this, DualOperator.LEFT_SHIFT, function);
    }

    @Override
    public final <N extends Number> Expression<E> leftShift(Supplier<Expression<N>> supplier) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, supplier.get());
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType) {
        return CastExpression.cast(this, _MappingFactory.getMapping(convertType));
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, ParamMeta paramMeta) {
        if (convertType != paramMeta.mappingType().javaType()) {
            String m = String.format("convertType[%s] java type of paramMeta[%s]", convertType.getName(), paramMeta);
            throw new CriteriaException(m);
        }
        return CastExpression.cast(this, paramMeta);
    }

    public final Expression<E> bracket() {
        return BracketsExpression.bracket(this);
    }

    @Override
    public final SortItem asc() {
        return new SortItemImpl(this, true);
    }

    @Override
    public final SortItem desc() {
        return new SortItemImpl(this, false);
    }


    /*################################## blow protected template method ##################################*/


}
