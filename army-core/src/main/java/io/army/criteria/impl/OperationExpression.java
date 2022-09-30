package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * this class is base class of most implementation of {@link Expression}
 *
 * @since 1.0
 */
abstract class OperationExpression implements ArmyExpression {

    OperationExpression() {
    }

    @Override
    public final Selection as(final String alias) {
        final Selection selection;
        if (this instanceof TableField) {
            selection = Selections.forField((TableField) this, alias);
        } else if (this instanceof DerivedField) {
            selection = CriteriaContexts.createDerivedSelection((DerivedField) this, alias);
        } else if (this instanceof SQLFunctions.FunctionSpec) {
            selection = Selections.forFunc((SQLFunctions.FunctionSpec) this, alias);
        } else {
            selection = Selections.forExp(this, alias);
        }
        return selection;
    }

    @Override
    public final boolean isNullValue() {
        return this instanceof SqlValueParam.SingleNonNamedValue
                && ((SqlValueParam.SingleNonNamedValue) this).value() == null;
    }

    @Override
    public final IPredicate equal(Expression operand) {
        return DualPredicate.create(this, DualOperator.EQUAL, operand);
    }

    @Override
    public final IPredicate equal(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.EQUAL, supplier.get());
    }

    @Override
    public final IPredicate equal(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.EQUAL, function.apply(this));
    }

    @Override
    public final <C> IPredicate equal(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.EQUAL, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> IPredicate equal(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualPredicate.create(this, DualOperator.EQUAL, operator.apply(this, operand));
    }

    @Override
    public final <T> IPredicate equal(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.EQUAL, operator.apply(this, supplier.get()));
    }

    @Override
    public final IPredicate equal(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.EQUAL, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final <C> IPredicate equalAny(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.EQUAL, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate equalAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.EQUAL, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate equalSome(Function<C, ? extends SubQuery> subQuery) {
        final SubQuery query;
        query = subQuery.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.EQUAL, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate equalSome(Supplier<? extends SubQuery> subQuery) {
        return SubQueryPredicate.create(this, DualOperator.EQUAL, SubQueryOperator.SOME, subQuery.get());
    }

    @Override
    public final IPredicate less(Expression operand) {
        return DualPredicate.create(this, DualOperator.LESS, operand);
    }

    @Override
    public final IPredicate less(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.LESS, supplier.get());
    }

    @Override
    public final IPredicate less(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LESS, function.apply(this));
    }

    @Override
    public final <C> IPredicate less(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LESS, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> IPredicate less(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualPredicate.create(this, DualOperator.LESS, operator.apply(this, operand));
    }

    @Override
    public final <T> IPredicate less(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.LESS, operator.apply(this, supplier.get()));
    }

    @Override
    public final IPredicate less(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.LESS, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final <C> IPredicate lessAny(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate lessAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate lessSome(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate lessSome(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate lessAll(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate lessAll(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.ALL, supplier.get());
    }


    @Override
    public final IPredicate lessEqual(Expression operand) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, operand);
    }

    @Override
    public final IPredicate lessEqual(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, supplier.get());
    }

    @Override
    public final IPredicate lessEqual(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, function.apply(this));
    }

    @Override
    public final <C> IPredicate lessEqual(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> IPredicate lessEqual(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, operator.apply(this, operand));
    }

    @Override
    public final <T> IPredicate lessEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, operator.apply(this, supplier.get()));
    }

    @Override
    public final IPredicate lessEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final <C> IPredicate lessEqualAny(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate lessEqualAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate lessEqualSome(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate lessEqualSome(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.SOME, supplier.get());
    }


    @Override
    public final <C> IPredicate lessEqualAll(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate lessEqualAll(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.ALL, supplier.get());
    }


    @Override
    public final IPredicate great(Expression operand) {
        return DualPredicate.create(this, DualOperator.GREAT, operand);
    }

    @Override
    public final IPredicate great(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.GREAT, supplier.get());
    }

    @Override
    public final IPredicate great(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.GREAT, function.apply(this));
    }

    @Override
    public final <C> IPredicate great(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.GREAT, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> IPredicate great(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualPredicate.create(this, DualOperator.GREAT, operator.apply(this, operand));
    }

    @Override
    public final <T> IPredicate great(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.GREAT, operator.apply(this, supplier.get()));
    }

    @Override
    public final IPredicate great(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.GREAT, operator.apply(this, function.apply(keyName)));
    }


    @Override
    public final <C> IPredicate greatAny(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate greatAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.ANY, supplier.get());
    }


    @Override
    public final <C> IPredicate greatSome(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate greatSome(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate greatAll(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate greatAll(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.ALL, supplier.get());
    }


    @Override
    public final IPredicate greatEqual(Expression operand) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, operand);
    }

    @Override
    public final IPredicate greatEqual(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, supplier.get());
    }

    @Override
    public final IPredicate greatEqual(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, function.apply(this));
    }

    @Override
    public final <C> IPredicate greatEqual(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> IPredicate greatEqual(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, operator.apply(this, operand));
    }

    @Override
    public final <T> IPredicate greatEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, operator.apply(this, supplier.get()));
    }

    @Override
    public final IPredicate greatEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final <C> IPredicate greatEqualAny(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate greatEqualAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate greatEqualSome(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate greatEqualSome(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate greatEqualAll(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate greatEqualAll(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.ALL, supplier.get());
    }


    @Override
    public final IPredicate notEqual(Expression operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, operand);
    }

    @Override
    public final IPredicate notEqual(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, supplier.get());
    }

    @Override
    public final IPredicate notEqual(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, function.apply(this));
    }

    @Override
    public final <C> IPredicate notEqual(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> IPredicate notEqual(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, operator.apply(this, operand));
    }

    @Override
    public final <T> IPredicate notEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, operator.apply(this, supplier.get()));
    }

    @Override
    public final IPredicate notEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final <C> IPredicate notEqualAny(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.ANY, query);
    }

    @Override
    public final IPredicate notEqualAny(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualSome(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.SOME, query);
    }

    @Override
    public final IPredicate notEqualSome(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final <C> IPredicate notEqualAll(Function<C, ? extends SubQuery> function) {
        final SubQuery query;
        query = function.apply(CriteriaContextStack.getTopCriteria());
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.ALL, query);
    }

    @Override
    public final IPredicate notEqualAll(Supplier<? extends SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.ALL, supplier.get());
    }

    @Override
    public final IPredicate between(Expression first, Expression second) {
        return BetweenPredicate.create(this, first, second);
    }

    @Override
    public final IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Object first, Object second) {
        final Expression firstExp, secondExp;
        firstExp = operator.apply(this, SQLs._safeParam(first));
        secondExp = operator.apply(this, SQLs._safeParam(second));
        return BetweenPredicate.create(this, firstExp, secondExp);
    }

    @Override
    public final IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Supplier<?> firstSupplier, Supplier<?> secondSupplier) {
        final Expression firstExp, secondExp;
        firstExp = operator.apply(this, firstSupplier.get());
        secondExp = operator.apply(this, secondSupplier.get());
        return BetweenPredicate.create(this, firstExp, secondExp);
    }

    @Override
    public final IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey) {
        final Expression firstExp, secondExp;
        firstExp = operator.apply(this, function.apply(firstKey));
        secondExp = operator.apply(this, function.apply(secondKey));
        return BetweenPredicate.create(this, firstExp, secondExp);
    }

    @Override
    public final IPredicate between(Supplier<ExpressionPair> supplier) {
        final SQLs.ExpressionPairImpl pair;
        pair = (SQLs.ExpressionPairImpl) supplier.get();
        return BetweenPredicate.create(this, pair.first, pair.second);
    }

    @Override
    public final IPredicate between(Function<Expression, ExpressionPair> function) {
        final SQLs.ExpressionPairImpl pair;
        pair = (SQLs.ExpressionPairImpl) function.apply(this);
        return BetweenPredicate.create(this, pair.first, pair.second);
    }

    @Override
    public final <C> IPredicate between(BiFunction<C, Expression, ExpressionPair> function) {
        final SQLs.ExpressionPairImpl pair;
        pair = (SQLs.ExpressionPairImpl) function.apply(CriteriaContextStack.getTopCriteria(), this);
        return BetweenPredicate.create(this, pair.first, pair.second);
    }

    @Override
    public final IPredicate betweenNamed(String firstParamName, String secondParamName) {
        return BetweenPredicate.create(this, SQLs.namedParam(this, firstParamName), SQLs.namedParam(this, secondParamName));
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
    public final IPredicate in(Expression operand) {
        return DualPredicate.create(this, DualOperator.IN, operand);
    }

    @Override
    public final IPredicate in(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.IN, supplier.get());
    }

    @Override
    public final IPredicate in(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.IN, function.apply(this));
    }

    @Override
    public final <C> IPredicate in(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.IN, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate in(BiFunction<? super Expression, O, ? extends Expression> operator, O operand) {
        return DualPredicate.create(this, DualOperator.IN, operator.apply(this, operand));
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate in(BiFunction<? super Expression, O, ? extends Expression> operator, Supplier<O> supplier) {
        return DualPredicate.create(this, DualOperator.IN, operator.apply(this, supplier.get()));
    }

    @Override
    public final IPredicate in(BiFunction<? super Expression, Collection<?>, Expression> operator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (!(value instanceof Collection)) {
            throw CriteriaUtils.nonCollectionValue(keyName);
        }
        return DualPredicate.create(this, DualOperator.IN, operator.apply(this, (Collection<?>) value));
    }


    @Override
    public final IPredicate inNamed(String paramName, int size) {
        return DualPredicate.create(this, DualOperator.IN, SQLs.namedMultiParams(this, paramName, size));
    }


    @Override
    public final IPredicate notIn(Expression operand) {
        return DualPredicate.create(this, DualOperator.NOT_IN, operand);
    }

    @Override
    public final IPredicate notIn(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_IN, supplier.get());
    }

    @Override
    public final IPredicate notIn(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_IN, function.apply(this));
    }

    @Override
    public final <C> IPredicate notIn(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_IN, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate notIn(BiFunction<? super Expression, O, ? extends Expression> operator, O operand) {
        return DualPredicate.create(this, DualOperator.NOT_IN, operator.apply(this, operand));
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate notIn(BiFunction<? super Expression, O, ? extends Expression> operator, Supplier<O> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_IN, operator.apply(this, supplier.get()));
    }

    @Override
    public final IPredicate notIn(BiFunction<? super Expression, Collection<?>, Expression> operator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (!(value instanceof Collection)) {
            throw CriteriaUtils.nonCollectionValue(keyName);
        }
        return DualPredicate.create(this, DualOperator.NOT_IN, operator.apply(this, (Collection<?>) value));
    }


    @Override
    public final IPredicate notInNamed(String paramName, int size) {
        return DualPredicate.create(this, DualOperator.NOT_IN, SQLs.namedMultiParams(this, paramName, size));
    }


    @Override
    public final IPredicate like(Expression operand) {
        return DualPredicate.create(this, DualOperator.LIKE, operand);
    }

    @Override
    public final IPredicate like(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.LIKE, supplier.get());
    }

    @Override
    public final IPredicate like(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LIKE, function.apply(this));
    }

    @Override
    public final <C> IPredicate like(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LIKE, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> IPredicate like(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualPredicate.create(this, DualOperator.LIKE, operator.apply(this, operand));
    }

    @Override
    public final <T> IPredicate like(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.LIKE, operator.apply(this, supplier.get()));
    }

    @Override
    public final IPredicate like(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.LIKE, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final IPredicate notLike(Expression operand) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, operand);
    }

    @Override
    public final IPredicate notLike(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, supplier.get());
    }

    @Override
    public final IPredicate notLike(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, function.apply(this));
    }

    @Override
    public final <C> IPredicate notLike(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> IPredicate notLike(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, operator.apply(this, operand));
    }

    @Override
    public final <T> IPredicate notLike(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, operator.apply(this, supplier.get()));
    }

    @Override
    public final IPredicate notLike(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, operator.apply(this, function.apply(keyName)));
    }


    @Override
    public final Expression mod(Expression operand) {
        return DualExpression.create(this, DualOperator.MOD, operand);
    }

    @Override
    public final Expression mod(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.MOD, supplier.get());
    }

    @Override
    public final Expression mod(Function<? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.MOD, function.apply(this));
    }

    @Override
    public final <C> Expression mod(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.MOD, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> Expression mod(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualExpression.create(this, DualOperator.MOD, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression mod(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.MOD, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression mod(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.MOD, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression times(Expression operand) {
        return DualExpression.create(this, DualOperator.TIMES, operand);
    }

    @Override
    public final Expression times(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.TIMES, supplier.get());
    }

    @Override
    public final Expression times(Function<? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.TIMES, function.apply(this));
    }

    @Override
    public final <C> Expression times(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.TIMES, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> Expression times(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualExpression.create(this, DualOperator.TIMES, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression times(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.TIMES, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression times(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.TIMES, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression plus(Expression operand) {
        return DualExpression.create(this, DualOperator.PLUS, operand);
    }

    @Override
    public final Expression plus(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.PLUS, supplier.get());
    }

    @Override
    public final Expression plus(Function<? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.PLUS, function.apply(this));
    }

    @Override
    public final <C> Expression plus(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.PLUS, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> Expression plus(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualExpression.create(this, DualOperator.PLUS, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression plus(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.PLUS, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression plus(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.PLUS, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression minus(Expression operand) {
        return DualExpression.create(this, DualOperator.MINUS, operand);
    }

    @Override
    public final Expression minus(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.MINUS, supplier.get());
    }

    @Override
    public final Expression minus(Function<? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.MINUS, function.apply(this));
    }

    @Override
    public final <C> Expression minus(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.MINUS, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> Expression minus(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualExpression.create(this, DualOperator.MINUS, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression minus(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.MINUS, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression minus(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.MINUS, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression divide(Expression operand) {
        return DualExpression.create(this, DualOperator.DIVIDE, operand);
    }

    @Override
    public final Expression divide(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.DIVIDE, supplier.get());
    }

    @Override
    public final Expression divide(Function<? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.DIVIDE, function.apply(this));
    }

    @Override
    public final <C> Expression divide(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.DIVIDE, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> Expression divide(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualExpression.create(this, DualOperator.DIVIDE, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression divide(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.DIVIDE, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression divide(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.DIVIDE, operator.apply(this, function.apply(keyName)));
    }
    @Override
    public final Expression negate() {
        return UnaryExpression.create(this, UnaryOperator.NEGATE);
    }

    @Override
    public final Expression bitwiseAnd(Expression operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, operand);
    }

    @Override
    public final Expression bitwiseAnd(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, supplier.get());
    }

    @Override
    public final Expression bitwiseAnd(Function<? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, function.apply(this));
    }

    @Override
    public final <C> Expression bitwiseAnd(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> Expression bitwiseAnd(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression bitwiseAnd(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression bitwiseAnd(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression bitwiseOr(Expression operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operand);
    }

    @Override
    public final Expression bitwiseOr(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, supplier.get());
    }

    @Override
    public final Expression bitwiseOr(Function<? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, function.apply(this));
    }

    @Override
    public final <C> Expression bitwiseOr(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> Expression bitwiseOr(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression bitwiseOr(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression bitwiseOr(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression xor(Expression operand) {
        return DualExpression.create(this, DualOperator.XOR, operand);
    }

    @Override
    public final Expression xor(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.XOR, supplier.get());
    }

    @Override
    public final Expression xor(Function<? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.XOR, function.apply(this));
    }

    @Override
    public final <C> Expression xor(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.XOR, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> Expression xor(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualExpression.create(this, DualOperator.XOR, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression xor(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.XOR, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression xor(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.XOR, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression invert() {
        return UnaryExpression.create(this, UnaryOperator.INVERT);
    }

    @Override
    public final Expression rightShift(Expression operand) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, operand);
    }

    @Override
    public final Expression rightShift(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, supplier.get());
    }

    @Override
    public final Expression rightShift(Function<? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, function.apply(this));
    }

    @Override
    public final <C> Expression rightShift(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> Expression rightShift(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression rightShift(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression rightShift(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression leftShift(Expression operand) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, operand);
    }

    @Override
    public final Expression leftShift(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, supplier.get());
    }

    @Override
    public final Expression leftShift(Function<? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, function.apply(this));
    }

    @Override
    public final <C> Expression leftShift(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public final <T> Expression leftShift(BiFunction<? super Expression, T, ? extends Expression> operator, @Nullable T operand) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression leftShift(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression leftShift(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression asType(final @Nullable TypeMeta paramMeta) {
        if (paramMeta == null) {
            throw CriteriaContextStack.nullPointer(CriteriaContextStack.peek());
        }
        final Expression expression;
        if (this instanceof MutableParamMetaSpec) {
            ((MutableParamMetaSpec) this).updateParamMeta(paramMeta);
            expression = this;
        } else {
            expression = CastExpression.cast(this, paramMeta);
        }
        return expression;
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


    interface MutableParamMetaSpec {

        void updateParamMeta(TypeMeta typeMeta);
    }


    /*################################## blow protected template method ##################################*/


}
