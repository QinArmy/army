package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.function.TeExpression;
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
    public final <T> IPredicate equal(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.EQUAL, operator.apply(this, operand));
    }

    @Override
    public final IPredicate equalAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.EQUAL, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final IPredicate equalSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.EQUAL, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final IPredicate less(Expression operand) {
        return DualPredicate.create(this, DualOperator.LESS, operand);
    }

    @Override
    public final <T> IPredicate less(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.LESS, operator.apply(this, operand));
    }


    @Override
    public final IPredicate lessAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.ANY, subQuery);
    }


    @Override
    public final IPredicate lessSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final IPredicate lessAll(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.ALL, subQuery);
    }


    @Override
    public final IPredicate lessEqual(Expression operand) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, operand);
    }

    @Override
    public final <T> IPredicate lessEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, operator.apply(this, operand));
    }


    @Override
    public final IPredicate lessEqualAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.ANY, subQuery);
    }


    @Override
    public final IPredicate lessEqualSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.SOME, subQuery);
    }


    @Override
    public final IPredicate lessEqualAll(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.ALL, subQuery);
    }


    @Override
    public final IPredicate great(Expression operand) {
        return DualPredicate.create(this, DualOperator.GREAT, operand);
    }

    @Override
    public final <T> IPredicate great(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.GREAT, operator.apply(this, operand));
    }


    @Override
    public final IPredicate greatAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.ANY, subQuery);
    }


    @Override
    public final IPredicate greatSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.SOME, subQuery);
    }


    @Override
    public final IPredicate greatAll(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.ALL, subQuery);
    }


    @Override
    public final IPredicate greatEqual(Expression operand) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, operand);
    }


    @Override
    public final <T> IPredicate greatEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, operator.apply(this, operand));
    }


    @Override
    public final IPredicate greatEqualAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final IPredicate greatEqualSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.SOME, subQuery);
    }


    @Override
    public final IPredicate greatEqualAll(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.ALL, subQuery);
    }


    @Override
    public final IPredicate notEqual(Expression operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, operand);
    }


    @Override
    public final <T> IPredicate notEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, operator.apply(this, operand));
    }

    @Override
    public final IPredicate notEqualAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.ANY, subQuery);
    }


    @Override
    public final IPredicate notEqualSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final IPredicate notEqualAll(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.ALL, subQuery);
    }

    @Override
    public final IPredicate between(Expression first, Expression second) {
        return BetweenPredicate.create(this, first, second);
    }

    @Override
    public final <T> IPredicate between(BiFunction<Expression, T, Expression> operator, T first, T second) {
        final Expression firstExp, secondExp;
        firstExp = operator.apply(this, first);
        secondExp = operator.apply(this, second);
        return BetweenPredicate.create(this, firstExp, secondExp);
    }


    @Override
    public final IPredicate between(Supplier<ExpressionPair> supplier) {
        final SQLs.ExpressionPairImpl pair;
        pair = (SQLs.ExpressionPairImpl) supplier.get();
        return BetweenPredicate.create(this, pair.first, pair.second);
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
    public final <T, O extends Collection<T>> IPredicate in(BiFunction<Expression, O, Expression> operator, O operand) {
        return DualPredicate.create(this, DualOperator.IN, operator.apply(this, operand));
    }


    @Override
    public final IPredicate in(TeExpression<Expression, String, Integer> namedOperator, String paramName, int size) {
        return DualPredicate.create(this, DualOperator.IN, namedOperator.apply(this, paramName, size));
    }

    @Override
    public final IPredicate notIn(Expression operand) {
        return DualPredicate.create(this, DualOperator.NOT_IN, operand);
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate notIn(BiFunction<Expression, O, Expression> operator, O operand) {
        return DualPredicate.create(this, DualOperator.NOT_IN, operator.apply(this, operand));
    }

    @Override
    public final IPredicate notIn(TeExpression<Expression, String, Integer> namedOperator, String paramName, int size) {
        return DualPredicate.create(this, DualOperator.NOT_IN, namedOperator.apply(this, paramName, size));
    }


    @Override
    public final IPredicate like(Expression operand) {
        return DualPredicate.create(this, DualOperator.LIKE, operand);
    }

    @Override
    public final <T> IPredicate like(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.LIKE, operator.apply(this, operand));
    }

    @Override
    public final IPredicate notLike(Expression operand) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, operand);
    }

    @Override
    public final <T> IPredicate notLike(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, operator.apply(this, operand));
    }

    @Override
    public final Expression mod(Expression operand) {
        return DualExpression.create(this, DualOperator.MOD, operand);
    }

    @Override
    public final Expression mod(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.MOD, supplier.get());
    }

    @Override
    public final Expression mod(Function<Expression, Expression> function) {
        return DualExpression.create(this, DualOperator.MOD, function.apply(this));
    }

    @Override
    public final <T> Expression mod(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.MOD, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression mod(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.MOD, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression mod(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.MOD, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression times(Expression operand) {
        return DualExpression.create(this, DualOperator.TIMES, operand);
    }

    @Override
    public final Expression times(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.TIMES, supplier.get());
    }

    @Override
    public final Expression times(Function<Expression, Expression> function) {
        return DualExpression.create(this, DualOperator.TIMES, function.apply(this));
    }

    @Override
    public final <T> Expression times(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.TIMES, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression times(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.TIMES, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression times(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.TIMES, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression plus(Expression operand) {
        return DualExpression.create(this, DualOperator.PLUS, operand);
    }

    @Override
    public final Expression plus(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.PLUS, supplier.get());
    }

    @Override
    public final Expression plus(Function<Expression, Expression> function) {
        return DualExpression.create(this, DualOperator.PLUS, function.apply(this));
    }

    @Override
    public final <T> Expression plus(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.PLUS, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression plus(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.PLUS, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression plus(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.PLUS, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression minus(Expression operand) {
        return DualExpression.create(this, DualOperator.MINUS, operand);
    }

    @Override
    public final Expression minus(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.MINUS, supplier.get());
    }

    @Override
    public final Expression minus(Function<Expression, Expression> function) {
        return DualExpression.create(this, DualOperator.MINUS, function.apply(this));
    }


    @Override
    public final <T> Expression minus(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.MINUS, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression minus(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.MINUS, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression minus(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.MINUS, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression divide(Expression operand) {
        return DualExpression.create(this, DualOperator.DIVIDE, operand);
    }

    @Override
    public final Expression divide(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.DIVIDE, supplier.get());
    }

    @Override
    public final Expression divide(Function<Expression, Expression> function) {
        return DualExpression.create(this, DualOperator.DIVIDE, function.apply(this));
    }

    @Override
    public final <T> Expression divide(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.DIVIDE, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression divide(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.DIVIDE, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression divide(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
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
    public final Expression bitwiseAnd(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, supplier.get());
    }

    @Override
    public final Expression bitwiseAnd(Function<Expression, Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, function.apply(this));
    }

    @Override
    public final <T> Expression bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression bitwiseAnd(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression bitwiseAnd(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression bitwiseOr(Expression operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operand);
    }

    @Override
    public final Expression bitwiseOr(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, supplier.get());
    }

    @Override
    public final Expression bitwiseOr(Function<Expression, Expression> function) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, function.apply(this));
    }

    @Override
    public final <T> Expression bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression bitwiseOr(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression bitwiseOr(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression xor(Expression operand) {
        return DualExpression.create(this, DualOperator.XOR, operand);
    }

    @Override
    public final Expression xor(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.XOR, supplier.get());
    }

    @Override
    public final Expression xor(Function<Expression, Expression> function) {
        return DualExpression.create(this, DualOperator.XOR, function.apply(this));
    }

    @Override
    public final <T> Expression xor(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.XOR, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression xor(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.XOR, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression xor(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
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
    public final Expression rightShift(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, supplier.get());
    }

    @Override
    public final Expression rightShift(Function<Expression, Expression> function) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, function.apply(this));
    }

    @Override
    public final <T> Expression rightShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression rightShift(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression rightShift(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, operator.apply(this, function.apply(keyName)));
    }

    @Override
    public final Expression leftShift(Expression operand) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, operand);
    }

    @Override
    public final Expression leftShift(Supplier<Expression> supplier) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, supplier.get());
    }

    @Override
    public final Expression leftShift(Function<Expression, Expression> function) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, function.apply(this));
    }

    @Override
    public final <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, operator.apply(this, operand));
    }

    @Override
    public final <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, Supplier<T> supplier) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, operator.apply(this, supplier.get()));
    }

    @Override
    public final Expression leftShift(BiFunction<Expression, Object, Expression> operator, Function<String, ?> function, String keyName) {
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
