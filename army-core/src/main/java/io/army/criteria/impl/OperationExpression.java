package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.SqlValueParam;
import io.army.criteria.SubQuery;
import io.army.function.TeNamedOperator;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * this class is base class of most implementation of {@link Expression}
 *
 * @since 1.0
 */
abstract class OperationExpression implements ArmyExpression {

    OperationExpression() {
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
    public final IPredicate between(Expression first, SQLs.WordAnd and, Expression second) {
        assert and == SQLs.AND;
        return BetweenPredicate.create(this, first, second);
    }

    @Override
    public final <T> IPredicate between(BiFunction<Expression, T, Expression> operator, T first
            , SQLs.WordAnd and, T second) {
        assert and == SQLs.AND;
        return BetweenPredicate.create(this, operator.apply(this, first), operator.apply(this, second));
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
    public final IPredicate in(SubQuery subQuery) {
        return SubQueryPredicate.inOperator(this, DualOperator.IN, subQuery);
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate in(BiFunction<Expression, O, Expression> operator, O operand) {
        return DualPredicate.create(this, DualOperator.IN, operator.apply(this, operand));
    }


    @Override
    public final IPredicate in(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        return DualPredicate.create(this, DualOperator.IN, namedOperator.apply(this, paramName, size));
    }

    @Override
    public final IPredicate notIn(Expression operand) {
        return DualPredicate.create(this, DualOperator.NOT_IN, operand);
    }

    @Override
    public final IPredicate notIn(SubQuery subQuery) {
        return SubQueryPredicate.inOperator(this, DualOperator.NOT_IN, subQuery);
    }

    @Override
    public final <T, O extends Collection<T>> IPredicate notIn(BiFunction<Expression, O, Expression> operator, O operand) {
        return DualPredicate.create(this, DualOperator.NOT_IN, operator.apply(this, operand));
    }

    @Override
    public final IPredicate notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
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
    public final <T> Expression mod(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.MOD, operator.apply(this, operand));
    }

    @Override
    public final Expression times(Expression operand) {
        return DualExpression.create(this, DualOperator.TIMES, operand);
    }

    @Override
    public final <T> Expression times(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.TIMES, operator.apply(this, operand));
    }


    @Override
    public final Expression plus(Expression operand) {
        return DualExpression.create(this, DualOperator.PLUS, operand);
    }

    @Override
    public final <T> Expression plus(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.PLUS, operator.apply(this, operand));
    }

    @Override
    public final Expression minus(Expression operand) {
        return DualExpression.create(this, DualOperator.MINUS, operand);
    }

    @Override
    public final <T> Expression minus(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.MINUS, operator.apply(this, operand));
    }

    @Override
    public final Expression divide(Expression operand) {
        return DualExpression.create(this, DualOperator.DIVIDE, operand);
    }

    @Override
    public final <T> Expression divide(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.DIVIDE, operator.apply(this, operand));
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
    public final <T> Expression bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, operator.apply(this, operand));
    }


    @Override
    public final Expression bitwiseOr(Expression operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operand);
    }


    @Override
    public final <T> Expression bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operator.apply(this, operand));
    }


    @Override
    public final Expression xor(Expression operand) {
        return DualExpression.create(this, DualOperator.XOR, operand);
    }


    @Override
    public final <T> Expression xor(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.XOR, operator.apply(this, operand));
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
    public final <T> Expression rightShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, operator.apply(this, operand));
    }


    @Override
    public final Expression leftShift(Expression operand) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, operand);
    }


    @Override
    public final <T> Expression leftShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, operator.apply(this, operand));
    }


    @Override
    public final Expression asType(final @Nullable TypeMeta paramMeta) {
        if (paramMeta == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
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



    interface MutableParamMetaSpec {

        void updateParamMeta(TypeMeta typeMeta);
    }


    /*################################## blow protected template method ##################################*/


}
