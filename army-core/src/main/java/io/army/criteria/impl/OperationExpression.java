package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.mapping._MappingFactory;
import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * this class is base class of most implementation of {@link Expression}
 */
abstract class OperationExpression<E> implements ArmyExpression<E>, _SortItem {

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
    public final IPredicate equal(Expression<?> operand) {
        return DualPredicate.create(this, DualOperator.EQ, operand);
    }

    @Override
    public final IPredicate equal(Object parameter) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final IPredicate equalParam(Object parameter) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final IPredicate equalNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.EQ, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate ifEqualParam(@Nullable Object parameter) {
        return parameter == null ? null : this.equalParam(parameter);
    }

    @Nullable
    @Override
    public final IPredicate ifEqual(final @Nullable Object parameter) {
        return parameter == null ? null : this.equal(parameter);
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
    public final IPredicate lessThan(Expression<?> expression) {
        return DualPredicate.create(this, DualOperator.LT, expression);
    }

    @Override
    public final IPredicate lessThan(Object parameter) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final IPredicate lessThanParam(Object parameter) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final IPredicate lessThanNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate ifLessThan(@Nullable Object parameter) {
        return parameter == null ? null : this.lessThan(parameter);
    }

    @Override
    public final IPredicate ifLessThanParam(@Nullable Object parameter) {
        return parameter == null ? null : this.lessThanParam(parameter);
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
    public final IPredicate lessEqual(Expression<?> operand) {
        return DualPredicate.create(this, DualOperator.LE, operand);
    }

    @Override
    public final IPredicate lessEqual(Object parameter) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final IPredicate lessEqualParam(Object parameter) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final IPredicate lessEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate ifLessEqual(@Nullable Object parameter) {
        return parameter == null ? null : this.lessEqual(parameter);
    }

    @Override
    public final IPredicate ifLessEqualParam(@Nullable Object parameter) {
        return parameter == null ? null : this.lessEqualParam(parameter);
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
    public final IPredicate greatThan(Expression<?> operand) {
        return DualPredicate.create(this, DualOperator.GT, operand);
    }

    @Override
    public final IPredicate greatThan(Object parameter) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final IPredicate greatThanParam(Object parameter) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final IPredicate greatThanNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.GT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate ifGreatThan(@Nullable Object parameter) {
        return parameter == null ? null : this.greatThan(parameter);
    }

    @Override
    public final IPredicate ifGreatThanParam(@Nullable Object parameter) {
        return parameter == null ? null : this.greatThanParam(parameter);
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
    public final IPredicate greatEqual(Expression<?> operand) {
        return DualPredicate.create(this, DualOperator.GE, operand);
    }

    @Override
    public final IPredicate greatEqual(Object parameter) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final IPredicate greatEqualParam(Object parameter) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final IPredicate greatEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.GE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate IfGreatEqual(@Nullable Object parameter) {
        return parameter == null ? null : this.greatEqual(parameter);
    }

    @Override
    public final IPredicate ifGreatEqualParam(@Nullable Object parameter) {
        return parameter == null ? null : this.greatEqualParam(parameter);
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
    public final IPredicate notEqual(Expression<?> expression) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, expression);
    }

    @Override
    public final IPredicate notEqual(Object parameter) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final IPredicate notEqualParam(Object parameter) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.strictParamWithExp(this, parameter));

    }

    @Override
    public final IPredicate notEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_EQ, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final IPredicate ifNotEqual(@Nullable Object parameter) {
        return parameter == null ? null : this.notEqual(parameter);
    }

    @Override
    public final IPredicate ifNotEqualParam(@Nullable Object parameter) {
        return parameter == null ? null : this.notEqualParam(parameter);
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
    public final IPredicate between(Expression<?> first, Expression<?> parameter) {
        return BetweenPredicate.between(this, first, parameter);
    }

    @Override
    public final IPredicate between(Object firstParameter, Object secondParameter) {
        Objects.requireNonNull(firstParameter);
        Objects.requireNonNull(secondParameter);
        return BetweenPredicate.between(this, SQLs.paramWithExp(this, firstParameter), SQLs.paramWithExp(this, secondParameter));
    }

    @Override
    public final IPredicate ifBetween(@Nullable Object firstParameter, @Nullable Object secondParameter) {
        final IPredicate predicate;
        if (firstParameter != null && secondParameter != null) {
            predicate = this.between(firstParameter, secondParameter);
        } else {
            predicate = null;
        }
        return predicate;
    }

    @Override
    public final IPredicate between(Expression<?> first, Object parameter) {
        return BetweenPredicate.between(this, first, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final IPredicate ifBetween(Expression<?> first, @Nullable Object parameter) {
        return parameter == null ? null : this.between(first, parameter);
    }

    @Override
    public final IPredicate between(Object parameter, Expression<?> second) {
        return BetweenPredicate.between(this, SQLs.paramWithExp(this, parameter), second);
    }

    @Override
    public final IPredicate ifBetween(@Nullable Object firstParameter, Expression<?> second) {
        final IPredicate predicate;
        if (firstParameter != null) {
            predicate = this.between(firstParameter, second);
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
    public final IPredicate like(String patternParameter) {
        return DualPredicate.create(this, DualOperator.LIKE, SQLs.strictParamWithExp(this, patternParameter));
    }

    @Override
    public final IPredicate likeNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LIKE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Nullable
    @Override
    public final IPredicate ifLike(@Nullable String patternParameter) {
        return patternParameter == null ? null : this.like(patternParameter);
    }

    @Override
    public final IPredicate like(Expression<String> pattern) {
        return DualPredicate.create(this, DualOperator.LIKE, pattern);
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
    public final IPredicate notLike(String patternParameter) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, SQLs.strictParamWithExp(this, patternParameter));
    }

    @Override
    public final IPredicate notLikeNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Nullable
    @Override
    public final IPredicate ifNotLike(@Nullable String patternParameter) {
        return patternParameter == null ? null : this.notLike(patternParameter);
    }

    @Override
    public final IPredicate notLike(Expression<String> pattern) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, pattern);
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
    public final Expression<E> mod(Expression<?> operator) {
        return DualExpression.create(this, DualOperator.MOD, operator);
    }

    @Override
    public final Expression<E> mod(Object parameter) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final Expression<E> modParam(Object parameter) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final Expression<E> modNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final Expression<E> mod(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> mod(String tableAlias, FieldMeta<?, ?> field) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.field(tableAlias, field));
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
    public final Expression<E> multiply(Expression<?> multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, multiplicand);
    }

    @Override
    public final Expression<E> multiply(Object parameter) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final Expression<E> multiplyParam(Object parameter) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final Expression<E> multiplyNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final Expression<E> multiply(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> multiply(String tableAlias, FieldMeta<?, ?> field) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.field(tableAlias, field));
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
    public final Expression<E> plus(Expression<?> augend) {
        return DualExpression.create(this, DualOperator.PLUS, augend);
    }

    @Override
    public final Expression<E> plus(Object parameter) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final Expression<E> plusParam(Object parameter) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final Expression<E> plusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final Expression<E> plus(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> plus(String tableAlias, FieldMeta<?, ?> field) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.field(tableAlias, field));
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
    public final Expression<E> minus(Expression<?> subtrahend) {
        return DualExpression.create(this, DualOperator.MINUS, subtrahend);
    }

    @Override
    public final Expression<E> minus(Object parameter) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final Expression<E> minusParam(Object parameter) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final Expression<E> minusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final Expression<E> minus(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> minus(String tableAlias, FieldMeta<?, ?> field) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.field(tableAlias, field));
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
    public final Expression<E> divide(Expression<?> divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, divisor);
    }

    @Override
    public final Expression<E> divide(Object parameter) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final Expression<E> divideParam(Object parameter) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final Expression<E> divideNamed(String paramName) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final Expression<E> divide(String subQueryAlias, String derivedFieldName) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.ref(subQueryAlias, derivedFieldName));
    }

    @Override
    public final Expression<E> divide(String tableAlias, FieldMeta<?, ?> field) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.field(tableAlias, field));
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
    public final Expression<E> bitwiseAnd(Expression<?> operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, operand);
    }

    @Override
    public final Expression<E> bitwiseAnd(Object parameter) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final Expression<E> bitwiseAndParam(Object parameter) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.strictParamWithExp(this, parameter));
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
    public final Expression<E> bitwiseOr(Expression<?> operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operand);
    }

    @Override
    public final Expression<E> bitwiseOr(Object parameter) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final Expression<E> bitwiseOrParam(Object parameter) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.strictParamWithExp(this, parameter));
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
    public final Expression<E> xor(Expression<?> operand) {
        return DualExpression.create(this, DualOperator.XOR, operand);
    }

    @Override
    public final Expression<E> xor(Object parameter) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.paramWithExp(this, parameter));
    }

    @Override
    public final Expression<E> xorParam(Object parameter) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.strictParamWithExp(this, parameter));
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
    public final Expression<E> rightShift(Number bitNumberParameter) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.paramWithExp(this, bitNumberParameter));
    }

    @Override
    public final Expression<E> rightShiftParam(Number parameter) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final Expression<E> rightShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <N extends Number> Expression<E> rightShift(Expression<N> bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, bitNumber);
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
    public final Expression<E> leftShift(Number bitNumberParameter) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.paramWithExp(this, bitNumberParameter));
    }

    @Override
    public final Expression<E> leftShiftParam(Number parameter) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.strictParamWithExp(this, parameter));
    }

    @Override
    public final Expression<E> leftShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.namedParam(paramName, this.paramMeta()));
    }

    @Override
    public final <N extends Number> Expression<E> leftShift(Expression<N> bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, bitNumber);
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
    public final <O> Expression<O> asType(Class<O> convertType, MappingType longMapping) {
        return CastExpression.cast(this, longMapping);
    }

    @Override
    public final <O> Expression<O> asType(Class<O> convertType, FieldMeta<?, O> longMapping) {
        return CastExpression.cast(this, longMapping);
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

    @Override
    public boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas) {
        return false;
    }

    @Override
    public boolean containsFieldOf(TableMeta<?> tableMeta) {
        return false;
    }

    @Override
    public int containsFieldCount(TableMeta<?> tableMeta) {
        return 0;
    }

    /*################################## blow protected template method ##################################*/


}
