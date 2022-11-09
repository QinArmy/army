package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.function.TeNamedOperator;
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
abstract class OperationExpression<I extends Item> implements ArmyExpression, ItemExpression<I> {

    final Function<Selection, I> function;

    OperationExpression(Function<Selection, I> function) {
        this.function = function;
    }


    @Override
    public final boolean isNullValue() {
        return this instanceof SqlValueParam.SingleNonNamedValue
                && ((SqlValueParam.SingleNonNamedValue) this).value() == null;
    }

    @Override
    public final ItemPredicate<I> equal(Expression operand) {
        return DualPredicate.create(this, DualOperator.EQUAL, operand);
    }

    @Override
    public final <T> ItemPredicate<I> equal(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.EQUAL, operator.apply(this, operand));
    }

    @Override
    public final ItemPredicate<I> equal(Supplier<Expression> supplier) {
        return DualPredicate.create(this, DualOperator.EQUAL, supplier.get());
    }

    @Override
    public final <R> R equal(Function<Function<ItemExpression<I>, ItemPredicate<I>>, R> function) {
        return function.apply(this::equal);
    }

    @Override
    public final <R> R equal(BiFunction<Expression, Function<ItemExpression<I>, ItemPredicate<I>>, R> operator
            , Expression operand) {
        return operator.apply(operand, this::equal);
    }

    @Override
    public final ItemPredicate<I> equalAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.EQUAL, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final ItemPredicate<I> equalSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.EQUAL, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final ItemPredicate<I> equalAny(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.EQUAL, SubQueryOperator.ANY, supplier.get());
    }

    @Override
    public final ItemPredicate<I> equalSome(Supplier<SubQuery> supplier) {
        return SubQueryPredicate.create(this, DualOperator.EQUAL, SubQueryOperator.SOME, supplier.get());
    }

    @Override
    public final ItemPredicate<I> less(Expression operand) {
        return DualPredicate.create(this, DualOperator.LESS, operand);
    }

    @Override
    public final <T> ItemPredicate<I> less(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.LESS, operator.apply(this, operand));
    }


    @Override
    public final ItemPredicate<I> lessAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.ANY, subQuery);
    }


    @Override
    public final ItemPredicate<I> lessSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final ItemPredicate<I> lessAll(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS, SubQueryOperator.ALL, subQuery);
    }


    @Override
    public final ItemPredicate<I> lessEqual(Expression operand) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, operand);
    }

    @Override
    public final <T> ItemPredicate<I> lessEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, operator.apply(this, operand));
    }


    @Override
    public final ItemPredicate<I> lessEqualAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.ANY, subQuery);
    }


    @Override
    public final ItemPredicate<I> lessEqualSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.SOME, subQuery);
    }


    @Override
    public final ItemPredicate<I> lessEqualAll(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.LESS_EQUAL, SubQueryOperator.ALL, subQuery);
    }


    @Override
    public final ItemPredicate<I> great(Expression operand) {
        return DualPredicate.create(this, DualOperator.GREAT, operand);
    }

    @Override
    public final <T> ItemPredicate<I> great(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.GREAT, operator.apply(this, operand));
    }


    @Override
    public final ItemPredicate<I> greatAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.ANY, subQuery);
    }


    @Override
    public final ItemPredicate<I> greatSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.SOME, subQuery);
    }


    @Override
    public final ItemPredicate<I> greatAll(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT, SubQueryOperator.ALL, subQuery);
    }


    @Override
    public final ItemPredicate<I> greatEqual(Expression operand) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, operand);
    }


    @Override
    public final <T> ItemPredicate<I> greatEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, operator.apply(this, operand));
    }


    @Override
    public final ItemPredicate<I> greatEqualAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.ANY, subQuery);
    }

    @Override
    public final ItemPredicate<I> greatEqualSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.SOME, subQuery);
    }


    @Override
    public final ItemPredicate<I> greatEqualAll(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.GREAT_EQUAL, SubQueryOperator.ALL, subQuery);
    }


    @Override
    public final ItemPredicate<I> notEqual(Expression operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, operand);
    }


    @Override
    public final <T> ItemPredicate<I> notEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, operator.apply(this, operand));
    }

    @Override
    public final ItemPredicate<I> notEqualAny(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.ANY, subQuery);
    }


    @Override
    public final ItemPredicate<I> notEqualSome(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.SOME, subQuery);
    }

    @Override
    public final ItemPredicate<I> notEqualAll(SubQuery subQuery) {
        return SubQueryPredicate.create(this, DualOperator.NOT_EQUAL, SubQueryOperator.ALL, subQuery);
    }

    @Override
    public final ItemPredicate<I> between(Expression first, SQLs.WordAnd and, Expression second) {
        assert and == SQLs.AND;
        return BetweenPredicate.create(this, first, second);
    }

    @Override
    public final <T> ItemPredicate<I> between(BiFunction<Expression, T, Expression> operator, T first
            , SQLs.WordAnd and, T second) {
        assert and == SQLs.AND;
        return BetweenPredicate.create(this, operator.apply(this, first), operator.apply(this, second));
    }

    @Override
    public final ItemPredicate<I> isNull() {
        return UnaryPredicate.create(UnaryOperator.IS_NULL, this);
    }

    @Override
    public final ItemPredicate<I> isNotNull() {
        return UnaryPredicate.create(UnaryOperator.IS_NOT_NULL, this);
    }


    @Override
    public final ItemPredicate<I> in(Expression operand) {
        return DualPredicate.create(this, DualOperator.IN, operand);
    }

    @Override
    public final ItemPredicate<I> in(SubQuery subQuery) {
        return SubQueryPredicate.inOperator(this, DualOperator.IN, subQuery);
    }

    @Override
    public final <T, O extends Collection<T>> ItemPredicate<I> in(BiFunction<Expression, O, Expression> operator, O operand) {
        return DualPredicate.create(this, DualOperator.IN, operator.apply(this, operand));
    }


    @Override
    public final ItemPredicate<I> in(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        return DualPredicate.create(this, DualOperator.IN, namedOperator.apply(this, paramName, size));
    }

    @Override
    public final ItemPredicate<I> notIn(Expression operand) {
        return DualPredicate.create(this, DualOperator.NOT_IN, operand);
    }

    @Override
    public final ItemPredicate<I> notIn(SubQuery subQuery) {
        return SubQueryPredicate.inOperator(this, DualOperator.NOT_IN, subQuery);
    }

    @Override
    public final <T, O extends Collection<T>> ItemPredicate<I> notIn(BiFunction<Expression, O, Expression> operator, O operand) {
        return DualPredicate.create(this, DualOperator.NOT_IN, operator.apply(this, operand));
    }

    @Override
    public final ItemPredicate<I> notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        return DualPredicate.create(this, DualOperator.NOT_IN, namedOperator.apply(this, paramName, size));
    }

    @Override
    public final ItemPredicate<I> like(Expression operand) {
        return DualPredicate.create(this, DualOperator.LIKE, operand);
    }

    @Override
    public final <T> ItemPredicate<I> like(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.LIKE, operator.apply(this, operand));
    }

    @Override
    public final ItemPredicate<I> notLike(Expression operand) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, operand);
    }

    @Override
    public final <T> ItemPredicate<I> notLike(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, operator.apply(this, operand));
    }

    @Override
    public final ItemExpression<I> mod(Expression operand) {
        return DualExpression.create(this, DualOperator.MOD, operand);
    }

    @Override
    public final <T> ItemExpression<I> mod(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.MOD, operator.apply(this, operand));
    }

    @Override
    public final ItemExpression<I> times(Expression operand) {
        return DualExpression.create(this, DualOperator.TIMES, operand);
    }

    @Override
    public final <T> ItemExpression<I> times(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.TIMES, operator.apply(this, operand));
    }


    @Override
    public final ItemExpression<I> plus(Expression operand) {
        return DualExpression.create(this, DualOperator.PLUS, operand);
    }

    @Override
    public final <T> ItemExpression<I> plus(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.PLUS, operator.apply(this, operand));
    }

    @Override
    public final ItemExpression<I> minus(Expression operand) {
        return DualExpression.create(this, DualOperator.MINUS, operand);
    }

    @Override
    public final <T> ItemExpression<I> minus(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.MINUS, operator.apply(this, operand));
    }

    @Override
    public final ItemExpression<I> divide(Expression operand) {
        return DualExpression.create(this, DualOperator.DIVIDE, operand);
    }

    @Override
    public final <T> ItemExpression<I> divide(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.DIVIDE, operator.apply(this, operand));
    }


    @Override
    public final ItemExpression<I> negate() {
        return UnaryExpression.create(this, UnaryOperator.NEGATE);
    }

    @Override
    public final ItemExpression<I> bitwiseAnd(Expression operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, operand);
    }

    @Override
    public final <T> ItemExpression<I> bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, operator.apply(this, operand));
    }


    @Override
    public final ItemExpression<I> bitwiseOr(Expression operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operand);
    }


    @Override
    public final <T> ItemExpression<I> bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, operator.apply(this, operand));
    }


    @Override
    public final ItemExpression<I> xor(Expression operand) {
        return DualExpression.create(this, DualOperator.XOR, operand);
    }


    @Override
    public final <T> ItemExpression<I> xor(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.XOR, operator.apply(this, operand));
    }


    @Override
    public final ItemExpression<I> invert() {
        return UnaryExpression.create(this, UnaryOperator.INVERT);
    }

    @Override
    public final ItemExpression<I> rightShift(Expression operand) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, operand);
    }

    @Override
    public final <T> ItemExpression<I> rightShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, operator.apply(this, operand));
    }


    @Override
    public final ItemExpression<I> leftShift(Expression operand) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, operand);
    }


    @Override
    public final <T> ItemExpression<I> leftShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, operator.apply(this, operand));
    }


    @Override
    public final ItemExpression<I> asType(final @Nullable TypeMeta paramMeta) {
        if (paramMeta == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        final ItemExpression<I> expression;
        if (this instanceof MutableParamMetaSpec) {
            ((MutableParamMetaSpec) this).updateParamMeta(paramMeta);
            expression = this;
        } else {
            expression = CastExpression.cast(this, paramMeta);
        }
        return expression;
    }

    @Override
    public final ItemExpression<I> bracket() {
        return BracketsExpression.bracket(this);
    }


    @Override
    public final I as(final String alias) {
        return this.function.apply(ArmySelections.forExp(this, alias));
    }

    interface MutableParamMetaSpec {

        void updateParamMeta(TypeMeta typeMeta);
    }


    /*################################## blow protected template method ##################################*/


}
