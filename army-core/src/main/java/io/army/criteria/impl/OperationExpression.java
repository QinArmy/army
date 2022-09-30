package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;
import io.army.util._ClassUtils;
import io.army.util._Exceptions;

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
    public IPredicate equal(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.EQUAL, supplier.get());
    }

    @Override
    public IPredicate equal(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.EQUAL, function.apply(this));
    }

    @Override
    public <C> IPredicate equal(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.EQUAL, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public <T> IPredicate equal(BiFunction<? super Expression, T, ? extends Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.EQUAL, operator.apply(this, operand));
    }

    @Override
    public <T> IPredicate equal(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.EQUAL, operator.apply(this, supplier.get()));
    }

    @Override
    public IPredicate equal(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.EQUAL, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public IPredicate equalNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.EQUAL, SQLs.namedParam(this, paramName));
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
    public IPredicate less(Expression operand) {
        return DualPredicate.create(this, DualOperator.LESS, operand);
    }

    @Override
    public IPredicate less(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.LESS, supplier.get());
    }

    @Override
    public IPredicate less(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LESS, function.apply(this));
    }

    @Override
    public <C> IPredicate less(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LESS, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public <T> IPredicate less(BiFunction<? super Expression, T, ? extends Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.LESS, operator.apply(this, operand));
    }

    @Override
    public <T> IPredicate less(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.LESS, operator.apply(this, supplier.get()));
    }

    @Override
    public IPredicate less(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.LESS, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public IPredicate lessNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LESS, SQLs.namedParam(this, paramName));
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
    public IPredicate lessEqual(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, supplier.get());
    }

    @Override
    public IPredicate lessEqual(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, function.apply(this));
    }

    @Override
    public <C> IPredicate lessEqual(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public <T> IPredicate lessEqual(BiFunction<? super Expression, T, ? extends Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, operator.apply(this, operand));
    }

    @Override
    public <T> IPredicate lessEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, operator.apply(this, supplier.get()));
    }

    @Override
    public IPredicate lessEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public IPredicate lessEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, SQLs.namedParam(this, paramName));
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
    public IPredicate great(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.GREAT, supplier.get());
    }

    @Override
    public IPredicate great(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.GREAT, function.apply(this));
    }

    @Override
    public <C> IPredicate great(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.GREAT, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public <T> IPredicate great(BiFunction<? super Expression, T, ? extends Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.GREAT, operator.apply(this, operand));
    }

    @Override
    public <T> IPredicate great(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.GREAT, operator.apply(this, supplier.get()));
    }

    @Override
    public IPredicate great(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.GREAT, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public IPredicate greatNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.GREAT, SQLs.namedParam(this, paramName));
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
    public IPredicate greatEqual(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, supplier.get());
    }

    @Override
    public IPredicate greatEqual(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, function.apply(this));
    }

    @Override
    public <C> IPredicate greatEqual(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public <T> IPredicate greatEqual(BiFunction<? super Expression, T, ? extends Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, operator.apply(this, operand));
    }

    @Override
    public <T> IPredicate greatEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, operator.apply(this, supplier.get()));
    }

    @Override
    public IPredicate greatEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public IPredicate greatEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, SQLs.namedParam(this, paramName));
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
    public IPredicate notEqual(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, supplier.get());
    }

    @Override
    public IPredicate notEqual(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, function.apply(this));
    }

    @Override
    public <C> IPredicate notEqual(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public <T> IPredicate notEqual(BiFunction<? super Expression, T, ? extends Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, operator.apply(this, operand));
    }

    @Override
    public <T> IPredicate notEqual(BiFunction<? super Expression, T, ? extends Expression> operator, Supplier<T> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, operator.apply(this, supplier.get()));
    }

    @Override
    public IPredicate notEqual(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String keyName) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public IPredicate notEqualNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, SQLs.namedParam(this, paramName));
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
    public IPredicate between(Expression first, Expression second) {
        return BetweenPredicate.create(this, first, second);
    }

    @Override
    public IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Object first, Object second) {
        final Expression firstExp, secondExp;
        firstExp = operator.apply(this, SQLs._safeParam(first));
        secondExp = operator.apply(this, SQLs._safeParam(second));
        return BetweenPredicate.create(this, firstExp, secondExp);
    }

    @Override
    public IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Supplier<?> firstSupplier, Supplier<?> secondSupplier) {
        final Expression firstExp, secondExp;
        firstExp = operator.apply(this, firstSupplier.get());
        secondExp = operator.apply(this, secondSupplier.get());
        return BetweenPredicate.create(this, firstExp, secondExp);
    }

    @Override
    public IPredicate between(BiFunction<? super Expression, Object, ? extends Expression> operator, Function<String, ?> function, String firstKey, String secondKey) {
        final Expression firstExp, secondExp;
        firstExp = operator.apply(this, function.apply(firstKey));
        secondExp = operator.apply(this, function.apply(secondKey));
        return BetweenPredicate.create(this, firstExp, secondExp);
    }

    @Override
    public IPredicate between(Supplier<ExpressionPair> supplier) {
        final SQLs.ExpressionPairImpl pair;
        pair = (SQLs.ExpressionPairImpl) supplier.get();
        return BetweenPredicate.create(this, pair.first, pair.second);
    }

    @Override
    public IPredicate between(Function<Expression, ExpressionPair> function) {
        final SQLs.ExpressionPairImpl pair;
        pair = (SQLs.ExpressionPairImpl) function.apply(this);
        return BetweenPredicate.create(this, pair.first, pair.second);
    }

    @Override
    public <C> IPredicate between(BiFunction<C, Expression, ExpressionPair> function) {
        final SQLs.ExpressionPairImpl pair;
        pair = (SQLs.ExpressionPairImpl) function.apply(CriteriaContextStack.getTopCriteria(), this);
        return BetweenPredicate.create(this, pair.first, pair.second);
    }

    @Override
    public IPredicate betweenNamed(String firstParamName, String secondParamName) {
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
    public IPredicate in(Expression operand) {
        return DualPredicate.create(this, DualOperator.IN, operand);
    }

    @Override
    public IPredicate in(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.IN, supplier.get());
    }

    @Override
    public IPredicate in(Function<? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.IN, function.apply(this));
    }

    @Override
    public <C> IPredicate in(BiFunction<C, ? super Expression, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.IN, function.apply(CriteriaContextStack.getTopCriteria(), this));
    }

    @Override
    public <T, O extends Collection<T>> IPredicate in(BiFunction<? super Expression, O, ? extends Expression> operator, O operand) {
        return DualPredicate.create(this, DualOperator.IN, operator.apply(this, operand));
    }

    @Override
    public <T, O extends Collection<T>> IPredicate in(BiFunction<? super Expression, O, ? extends Expression> operator, Supplier<O> supplier) {
        return DualPredicate.create(this, DualOperator.IN, operator.apply(this, supplier.get()));
    }

    @Override
    public IPredicate in(BiFunction<? super Expression, Collection<?>, Expression> operator, Function<String, ?> function, String keyName) {
        final Object value;
        value = function.apply(keyName);
        if (!(value instanceof Collection)) {

        }
        return DualPredicate.create(this, DualOperator.IN, operator.apply(this, (Collection<?>) value));
    }


    @Override
    public IPredicate inNamed(String paramName, int size) {
        return null;
    }

    @Override
    public final IPredicate like(Object pattern) {
        return this.pattern(DualOperator.LIKE, pattern);
    }

    @Override
    public final IPredicate likeNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.LIKE, SQLs.namedParam(this.typeMeta(), paramName));
    }

    @Override
    public final <C> IPredicate likeExp(Function<C, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.LIKE, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate likeExp(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.LIKE, supplier.get());
    }

    @Override
    public final IPredicate notLike(Object pattern) {
        return this.pattern(DualOperator.NOT_LIKE, pattern);
    }

    @Override
    public final IPredicate notLikeNamed(String paramName) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, SQLs.namedParam(this.typeMeta(), paramName));
    }

    @Override
    public final <C> IPredicate notLikeExp(Function<C, ? extends Expression> function) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final IPredicate notLikeExp(Supplier<? extends Expression> supplier) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, supplier.get());
    }

    @Override
    public final Expression mod(Object operand) {
        return DualExpression.create(this, DualOperator.MOD, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final Expression modLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.MOD, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final Expression modNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MOD, SQLs.namedParam(this.typeMeta(), paramName));
    }

    @Override
    public final <C> Expression modExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.MOD, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression modExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.MOD, supplier.get());
    }

    @Override
    public final Expression times(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs._nonNullParam(this, multiplicand));
    }

    @Override
    public final Expression timesLiteral(Object multiplicand) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs._nonNullLiteral(this, multiplicand));
    }

    @Override
    public final Expression timesNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MULTIPLY, SQLs.namedParam(this.typeMeta(), paramName));
    }

    @Override
    public final <C> Expression timesExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.MULTIPLY, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression timesExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.MULTIPLY, supplier.get());
    }

    @Override
    public final Expression plus(Object augend) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs._nonNullParam(this, augend));
    }

    @Override
    public final Expression plusLiteral(Object augend) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs._nonNullLiteral(this, augend));
    }

    @Override
    public final Expression plusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.PLUS, SQLs.namedParam(this.typeMeta(), paramName));
    }


    @Override
    public final <C> Expression plusExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.PLUS, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression plusExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.PLUS, supplier.get());
    }

    @Override
    public final Expression minus(Object minuend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs._nonNullParam(this, minuend));
    }

    @Override
    public final Expression minusLiteral(Object minuend) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs._nonNullLiteral(this, minuend));
    }

    @Override
    public final Expression minusNamed(String paramName) {
        return DualExpression.create(this, DualOperator.MINUS, SQLs.namedParam(this.typeMeta(), paramName));
    }

    @Override
    public final <C> Expression minusExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.MINUS, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression minusExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.MINUS, supplier.get());
    }

    @Override
    public final Expression divide(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs._nonNullParam(this, divisor));
    }

    @Override
    public final Expression divideLiteral(Object divisor) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs._nonNullLiteral(this, divisor));
    }

    @Override
    public final Expression divideNamed(String paramName) {
        return DualExpression.create(this, DualOperator.DIVIDE, SQLs.namedParam(this.typeMeta(), paramName));
    }

    @Override
    public final <C> Expression divideExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.DIVIDE, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression divideExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.DIVIDE, supplier.get());
    }

    @Override
    public final Expression negate() {
        return UnaryExpression.create(this, UnaryOperator.NEGATED);
    }

    @Override
    public final Expression bitwiseAnd(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final Expression bitwiseAndLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final Expression bitwiseAndNamed(String paramName) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, SQLs.namedParam(this.typeMeta(), paramName));
    }

    @Override
    public final <C> Expression bitwiseAndExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression bitwiseAndExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, supplier.get());
    }

    @Override
    public final Expression bitwiseOr(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final Expression bitwiseOrLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final Expression bitwiseOrNamed(String paramName) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, SQLs.namedParam(this.typeMeta(), paramName));
    }

    @Override
    public final <C> Expression bitwiseOrExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression bitwiseOrExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, supplier.get());
    }

    @Override
    public final Expression xor(Object operand) {
        return DualExpression.create(this, DualOperator.XOR, SQLs._nonNullParam(this, operand));
    }

    @Override
    public final Expression xorLiteral(Object operand) {
        return DualExpression.create(this, DualOperator.XOR, SQLs._nonNullLiteral(this, operand));
    }

    @Override
    public final Expression xorNamed(String paramName) {
        return DualExpression.create(this, DualOperator.XOR, SQLs.namedParam(this.typeMeta(), paramName));
    }

    @Override
    public final <C> Expression xorExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.XOR, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression xorExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.XOR, supplier.get());
    }

    @Override
    public final Expression inversion() {
        return UnaryExpression.create(this, UnaryOperator.INVERT);
    }

    @Override
    public final Expression rightShift(Object bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs._nonNullParam(this, bitNumber));
    }

    @Override
    public final Expression rightShiftLiteral(Object bitNumber) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs._nonNullLiteral(this, bitNumber));
    }

    @Override
    public final Expression rightShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, SQLs.namedParam(this.typeMeta(), paramName));
    }

    @Override
    public final <C> Expression rightShiftExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression rightShiftExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, supplier.get());
    }

    @Override
    public final Expression leftShift(Object bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs._nonNullParam(this, bitNumber));
    }

    @Override
    public final Expression leftShiftLiteral(Object bitNumber) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs._nonNullLiteral(this, bitNumber));
    }

    @Override
    public final Expression leftShiftNamed(String paramName) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, SQLs.namedParam(this.typeMeta(), paramName));
    }

    @Override
    public final <C> Expression leftShiftExp(Function<C, ? extends Expression> function) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, function.apply(CriteriaContextStack.getTopCriteria()));
    }

    @Override
    public final Expression leftShiftExp(Supplier<? extends Expression> supplier) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, supplier.get());
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

    private IPredicate pattern(final DualOperator operator, final Object pattern) {
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
        } else if (pattern instanceof SubQuery) {
            throw _Exceptions.nonScalarSubQuery((SubQuery) pattern);
        } else if (pattern instanceof String) {
            valueExp = SQLs._nonNullParam(this, pattern);
        } else {
            String m = String.format("%s support only %s and %s ."
                    , operator, Expression.class.getName(), String.class.getName());
            throw new CriteriaException(m);
        }
        return DualPredicate.create(this, operator, valueExp);
    }

    @Nullable
    private IPredicate ifInOrNotIn(final DualOperator operator, final @Nullable Object value, final boolean optimizing) {
        switch (operator) {
            case IN:
            case NOT_IN:
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        final IPredicate predicate;
        final Collection<?> collection;
        if (value == null) {
            predicate = null;
        } else if (value instanceof Expression) {
            predicate = DualPredicate.create(this, operator, (Expression) value);
        } else if (value instanceof SubQuery) {
            throw _Exceptions.nonScalarSubQuery((SubQuery) value);
        } else if ((collection = (Collection<?>) value).size() == 0) {
            predicate = null;
        } else if (optimizing) {
            predicate = DualPredicate.create(this, operator, SQLs.preferLiteralParams(this.typeMeta(), collection));
        } else {
            predicate = DualPredicate.create(this, operator, SQLs.params(this.typeMeta(), collection));
        }
        return predicate;
    }


    private static CriteriaException nonCollectionError(DualOperator operator, Object operand) {
        switch (operator) {
            case IN:
            case NOT_IN:
                break;
            default:
                throw _Exceptions.unexpectedEnum(operator);
        }
        String m = String.format("%s operator support only %s and %s ,but operand is %s"
                , operator.signText, Expression.class.getName()
                , Collection.class.getName()
                , _ClassUtils.safeClassName(operand));
        return new CriteriaException(m);
    }

}
