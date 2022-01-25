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
abstract class OperationExpression implements ArmyExpression {

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
            nullable = ((ValueExpression) this).value() == null;
        } else {
            nullable = this instanceof NamedParam && !(this instanceof NonNullNamedParam);
        }
        return nullable;
    }

    @Override
    public final IPredicate equal(Object operand) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.paramWithNonNull(this, operand));
    }

    @Override
    public final IPredicate equalLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.literalWithNonNull(this, operand));
    }

    @Override
    public final IPredicate equalNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate ifEqualLiteral(@Nullable Object operand) {
        return operand == null ? null : this.equalLiteral(operand);
    }

    @Nullable
    @Override
    public final IPredicate ifEqual(final @Nullable Object operand) {
        return operand == null ? null : this.equal(operand);
    }

    @Override
    public final <C> IPredicate equal(Function<C, Object> function) {
        return this.equal(function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate equal(Supplier<Object> supplier) {
        return this.equal(supplier.get());
    }

    @Override
    public final <C> IPredicate equalAny(Function<C, SubQuery> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final IPredicate equalAny(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, SubQuery> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final IPredicate equalSome(Supplier<SubQuery> subQuery) {
        return ColumnSubQueryPredicate.create(this, DualOperator.EQ, SubQueryOperator.SOME, subQuery.get());
    }

    @Override
    public final IPredicate lessThan(Object operand) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.paramWithNonNull(this, operand));
    }

    @Override
    public final IPredicate lessThanLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.literalWithNonNull(this, operand));
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
    public final IPredicate ifLessThanLiteral(@Nullable Object operand) {
        return operand == null ? null : this.lessThanLiteral(operand);
    }

    @Override
    public final <C> IPredicate lessThan(Function<C, Object> function) {
        return DualPredicate.create(this, DualOperator.LT, function);
    }

    @Override
    public final IPredicate lessThan(Supplier<Object> supplier) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final <C> IPredicate lessThanAny(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ANY, function);
    }

    @Override
    public final IPredicate lessThanAny(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate lessThanSome(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.SOME, function);
    }

    @Override
    public final IPredicate lessThanSome(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate lessThanAll(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate lessThanAll(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LT, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate lessEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.paramWithNonNull(this, operand));
    }

    @Override
    public final IPredicate lessEqualLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.literalWithNonNull(this, operand));
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
    public final IPredicate ifLessEqualLiteral(@Nullable Object operand) {
        return operand == null ? null : this.lessEqualLiteral(operand);
    }

    @Override
    public final <C> IPredicate lessEqual(Function<C, Object> function) {
        return DualPredicate.create(this, DualOperator.LE, function);
    }

    @Override
    public final IPredicate lessEqual(Supplier<Object> supplier) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final <C> IPredicate lessEqualAny(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ANY, function);
    }

    @Override
    public final IPredicate lessEqualAny(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate lessEqualSome(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.SOME, function);
    }

    @Override
    public final IPredicate lessEqualSome(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.SOME, supplier.get());
    }


    @Override
    public final <C> IPredicate lessEqualAll(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate lessEqualAll(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.LE, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate greatThan(Object operand) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.paramWithNonNull(this, operand));
    }

    @Override
    public final IPredicate greatThanLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.literalWithNonNull(this, operand));
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
    public final IPredicate ifGreatThanLiteral(@Nullable Object operand) {
        return operand == null ? null : this.greatThanLiteral(operand);
    }

    @Override
    public final <C> IPredicate greatThan(Function<C, Object> function) {
        return DualPredicate.create(this, DualOperator.GT, function);
    }

    @Override
    public final IPredicate greatThan(Supplier<Object> supplier) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.paramWithNonNull(this, supplier.get()));
    }


    @Override
    public final <C> IPredicate greatThanAny(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ANY, function);
    }

    @Override
    public final IPredicate greatThanAny(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ANY, supplier.get());
    }


    @Override
    public final <C> IPredicate greatThanSome(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.SOME, function);
    }

    @Override
    public final IPredicate greatThanSome(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate greatThanAll(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate greatThanAll(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GT, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate greatEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.paramWithNonNull(this, operand));
    }

    @Override
    public final IPredicate greatEqualLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.literalWithNonNull(this, operand));
    }

    @Override
    public final IPredicate greatEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate ifGreatEqual(@Nullable Object operand) {
        return operand == null ? null : this.greatEqual(operand);
    }

    @Override
    public final IPredicate ifGreatEqualLiteral(@Nullable Object operand) {
        return operand == null ? null : this.greatEqualLiteral(operand);
    }


    @Override
    public final <C> IPredicate greatEqual(Function<C, Object> function) {
        return DualPredicate.create(this, DualOperator.GE, function);
    }

    @Override
    public final IPredicate greatEqual(Supplier<Object> supplier) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.paramWithNonNull(this, supplier.get()));
    }


    @Override
    public final <C> IPredicate greatEqualAny(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ANY, function);
    }

    @Override
    public final IPredicate greatEqualAny(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.SOME, function);
    }

    @Override
    public final IPredicate greatEqualSome(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate greatEqualAll(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.GE, SubQueryOperator.ALL, supplier.get());
    }


    @Override
    public final IPredicate notEqual(Object operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.paramWithNonNull(this, operand));
    }

    @Override
    public final IPredicate notEqualLiteral(Object operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.literalWithNonNull(this, operand));

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
    public final IPredicate ifNotEqualLiteral(@Nullable Object operand) {
        return operand == null ? null : this.notEqualLiteral(operand);
    }

    @Override
    public final <C> IPredicate notEqual(Function<C, Object> function) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, function);
    }

    @Override
    public final IPredicate notEqual(Supplier<Object> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final <C> IPredicate notEqualAny(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ANY, function);
    }

    @Override
    public final IPredicate notEqualAny(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualSome(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.SOME, function);
    }

    @Override
    public final IPredicate notEqualSome(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualAll(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ALL, function);
    }

    @Override
    public final IPredicate notEqualAll(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_EQ, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate between(Object firstOperand, Object secondOperand) {
        Objects.requireNonNull(firstOperand);
        Objects.requireNonNull(secondOperand);

        final Expression first, second;
        first = SQLs.paramWithNonNull(this, firstOperand);
        second = SQLs.paramWithNonNull(this, secondOperand);
        return BetweenPredicate.between(this, first, second);
    }

    @Override
    public final IPredicate betweenLiteral(Object firstOperand, Object secondOperand) {
        Objects.requireNonNull(firstOperand);
        Objects.requireNonNull(secondOperand);

        final Expression first, second;
        first = SQLs.literalWithNonNull(this, firstOperand);
        second = SQLs.literalWithNonNull(this, secondOperand);
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
    public final IPredicate ifBetweenLiteral(@Nullable Object firstOperand, @Nullable Object secondOperand) {
        final IPredicate predicate;
        if (firstOperand != null && secondOperand != null) {
            predicate = this.betweenLiteral(firstOperand, secondOperand);
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
    public final IPredicate in(Expression parameters) {
        return DualPredicate.create(this, DualOperator.IN, parameters);
    }

    @Override
    public final <C> IPredicate in(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.IN, function);
    }

    @Override
    public final IPredicate in(Supplier<SubQuery> supplier) {
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
    public final IPredicate notIn(Expression values) {
        return DualPredicate.create(this, DualOperator.NOT_IN, values);
    }

    @Override
    public final <C> IPredicate notIn(Function<C, SubQuery> function) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_IN, function);
    }

    @Override
    public final IPredicate notIn(Supplier<SubQuery> supplier) {
        return ColumnSubQueryPredicate.create(this, DualOperator.NOT_IN, supplier.get());
    }

    @Override
    public final IPredicate like(Object pattern) {
        return patterPredicate(DualOperator.LIKE, pattern);
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
    public final <C> IPredicate like(Function<C, Object> function) {
        return patterPredicate(DualOperator.LIKE, function.apply(CriteriaContextStack.getCriteria()));
    }


    @Override
    public final IPredicate like(Supplier<Object> supplier) {
        return patterPredicate(DualOperator.LIKE, supplier.get());
    }

    @Override
    public final IPredicate notLike(Object pattern) {
        return patterPredicate(DualOperator.NOT_LIKE, pattern);
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
    public final <C> IPredicate notLike(Function<C, Object> function) {
        return patterPredicate(DualOperator.NOT_LIKE, function.apply(CriteriaContextStack.getCriteria()));
    }

    @Override
    public final IPredicate notLike(Supplier<Object> supplier) {
        return patterPredicate(DualOperator.NOT_LIKE, supplier.get());
    }

    @Override
    public final Expression mod(Object operand) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.paramWithNonNull(this, operand));
    }

    @Override
    public final Expression modLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.literalWithNonNull(this, operand));
    }

    @Override
    public final Expression modNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression mod(Function<C, Object> function) {
        return DualExpression.functionCreate(this, DualOperator.MOD, function);
    }

    @Override
    public final Expression mod(Supplier<Object> supplier) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final Expression multiply(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.paramWithNonNull(this, multiplicand));
    }

    @Override
    public final Expression multiplyLiteral(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.literalWithNonNull(this, multiplicand));
    }

    @Override
    public final Expression multiplyNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression multiply(Function<C, Object> function) {
        return DualExpression.functionCreate(this, DualOperator.MULTIPLY, function);
    }

    @Override
    public final Expression multiply(Supplier<Object> supplier) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final Expression plus(Object augend) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.paramWithNonNull(this, augend));
    }

    @Override
    public final Expression plusLiteral(Object augend) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.literalWithNonNull(this, augend));
    }

    @Override
    public final Expression plusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.namedParam(paramName, this.paramMeta()));
    }


    @Override
    public final <C> Expression plus(Function<C, Object> function) {
        return DualExpression.functionCreate(this, DualOperator.PLUS, function);
    }

    @Override
    public final Expression plus(Supplier<Object> supplier) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final Expression minus(Object minuend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.paramWithNonNull(this, minuend));
    }

    @Override
    public final Expression minusLiteral(Object minuend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.literalWithNonNull(this, minuend));
    }

    @Override
    public final Expression minusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression minus(Function<C, Object> function) {
        return DualExpression.functionCreate(this, DualOperator.MINUS, function);
    }

    @Override
    public final Expression minus(Supplier<Object> supplier) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final Expression divide(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.paramWithNonNull(this, divisor));
    }

    @Override
    public final Expression divideLiteral(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.literalWithNonNull(this, divisor));
    }

    @Override
    public final Expression divideNamed(String paramName) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression divide(Function<C, Object> function) {
        return DualExpression.functionCreate(this, DualOperator.DIVIDE, function);
    }

    @Override
    public final Expression divide(Supplier<Object> supplier) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final Expression negate() {
        return UnaryExpression.create(this, UnaryOperator.NEGATED);
    }

    @Override
    public final Expression bitwiseAnd(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.paramWithNonNull(this, operand));
    }

    @Override
    public final Expression bitwiseAndLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.literalWithNonNull(this, operand));
    }

    @Override
    public final Expression bitwiseAndNamed(String paramName) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression bitwiseAnd(Function<C, Object> function) {
        return DualExpression.functionCreate(this, DualOperator.BITWISE_AND, function);
    }

    @Override
    public final Expression bitwiseAnd(Supplier<Object> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final Expression bitwiseOr(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.paramWithNonNull(this, operand));
    }

    @Override
    public final Expression bitwiseOrLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.literalWithNonNull(this, operand));
    }

    @Override
    public final Expression bitwiseOrNamed(String paramName) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression bitwiseOr(Function<C, Object> function) {
        return DualExpression.functionCreate(this, DualOperator.BITWISE_OR, function);
    }

    @Override
    public final Expression bitwiseOr(Supplier<Object> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final Expression xor(Object operand) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.paramWithNonNull(this, operand));
    }

    @Override
    public final Expression xorLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.literalWithNonNull(this, operand));
    }

    @Override
    public final Expression xorNamed(String paramName) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression xor(Function<C, Object> function) {
        return DualExpression.functionCreate(this, DualOperator.XOR, function);
    }

    @Override
    public final Expression xor(Supplier<Object> supplier) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.literalWithNonNull(this, supplier.get()));
    }

    @Override
    public final Expression inversion() {
        return UnaryExpression.create(this, UnaryOperator.INVERT);
    }

    @Override
    public final Expression rightShift(Object bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.paramWithNonNull(this, bitNumber));
    }

    @Override
    public final Expression rightShiftLiteral(Object bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.literalWithNonNull(this, bitNumber));
    }

    @Override
    public final Expression rightShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression rightShift(Function<C, Object> function) {
        return DualExpression.functionCreate(this, DualOperator.RIGHT_SHIFT, function);
    }

    @Override
    public final Expression rightShift(Supplier<Object> supplier) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final Expression leftShift(Object bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.paramWithNonNull(this, bitNumber));
    }

    @Override
    public final Expression leftShiftLiteral(Object bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.literalWithNonNull(this, bitNumber));
    }

    @Override
    public final Expression leftShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <C> Expression leftShift(Function<C, Object> function) {
        return DualExpression.functionCreate(this, DualOperator.LEFT_SHIFT, function);
    }

    @Override
    public final Expression leftShift(Supplier<Object> supplier) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.paramWithNonNull(this, supplier.get()));
    }

    @Override
    public final Expression asType(Class<?> convertType) {
        return CastExpression.cast(this, _MappingFactory.getMapping(convertType));
    }

    @Override
    public final Expression asType(ParamMeta paramMeta) {
        return CastExpression.cast(this, paramMeta);
    }

    public final Expression bracket() {
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

    private IPredicate patterPredicate(final DualOperator operator, final Object pattern) {
        switch (operator) {
            case LIKE:
            case NOT_LIKE:
                break;
            default:
                throw new IllegalArgumentException(String.format("%s error.", operator));
        }
        final Expression valueExp;
        if (pattern instanceof Expression) {
            valueExp = (Expression) pattern;
        } else if (pattern instanceof String) {
            valueExp = SQLs.paramWithNonNull(this, pattern);
        } else {
            String m = String.format("%s support only %s and %s ."
                    , operator, Expression.class.getName(), String.class.getName());
            throw new CriteriaException(m);
        }
        return DualPredicate.create(this, operator, valueExp);
    }

}
