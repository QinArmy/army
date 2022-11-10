package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.function.BiAsExpFunction;
import io.army.function.BiAsFunction;
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
abstract class OperationExpression<I extends Item> implements ArmyExpression, _ItemExpression<I> {

    final Function<? super Selection, I> asFunction;

    OperationExpression(Function<? super Selection, I> asFunction) {
        this.asFunction = asFunction;
    }


    @Override
    public final boolean isNullValue() {
        return this instanceof SqlValueParam.SingleNonNamedValue
                && ((SqlValueParam.SingleNonNamedValue) this).value() == null;
    }

    @Override
    public final ItemPredicate<I> equal(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.EQUAL, operand);
    }

    @Override
    public final <T> ItemPredicate<I> equal(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.EQUAL, operator.apply(this, operand));
    }

    @Override
    public final ItemPredicate<I> equal(Supplier<Expression> supplier) {
        return Expressions.dualPredicate(this, DualOperator.EQUAL, supplier.get());
    }


    @Override
    public final <R> R equal(BiAsFunction<ItemPredicate<I>, I, R> function) {
        return function.apply(this::equal, this::onOuterEnd);
    }

    @Override
    public final <R> R equal(BiAsExpFunction<ItemPredicate<I>, I, R> function) {
        return function.apply(null, this::equal, this::onOuterEnd);
    }

    @Override
    public final <R> R equal(BiAsExpFunction<ItemPredicate<I>, I, R> function, @Nullable Expression operand) {
        if (operand == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return function.apply(operand, this::equal, this::onOuterEnd);
    }

    @Override
    public final ItemPredicate<I> equalAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final ItemPredicate<I> equalSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.EQUAL, QueryOperator.SOME, subQuery);
    }

    @Override
    public final ItemPredicate<I> equalAny(Supplier<SubQuery> supplier) {
        return Expressions.compareQueryPredicate(this, DualOperator.EQUAL, QueryOperator.ANY, supplier.get());
    }

    @Override
    public final ItemPredicate<I> equalSome(Supplier<SubQuery> supplier) {
        return Expressions.compareQueryPredicate(this, DualOperator.EQUAL, QueryOperator.SOME, supplier.get());
    }

    @Override
    public final ItemPredicate<I> less(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS, operand);
    }

    @Override
    public final <T> ItemPredicate<I> less(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS, operator.apply(this, operand));
    }


    @Override
    public final ItemPredicate<I> lessAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS, QueryOperator.ANY, subQuery);
    }


    @Override
    public final ItemPredicate<I> lessSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS, QueryOperator.SOME, subQuery);
    }

    @Override
    public final ItemPredicate<I> lessAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS, QueryOperator.ALL, subQuery);
    }


    @Override
    public final ItemPredicate<I> lessEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS_EQUAL, operand);
    }

    @Override
    public final <T> ItemPredicate<I> lessEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS_EQUAL, operator.apply(this, operand));
    }


    @Override
    public final ItemPredicate<I> lessEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final ItemPredicate<I> lessEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final ItemPredicate<I> lessEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final ItemPredicate<I> great(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT, operand);
    }

    @Override
    public final <T> ItemPredicate<I> great(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT, operator.apply(this, operand));
    }


    @Override
    public final ItemPredicate<I> greatAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT, QueryOperator.ANY, subQuery);
    }


    @Override
    public final ItemPredicate<I> greatSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT, QueryOperator.SOME, subQuery);
    }


    @Override
    public final ItemPredicate<I> greatAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT, QueryOperator.ALL, subQuery);
    }


    @Override
    public final ItemPredicate<I> greatEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT_EQUAL, operand);
    }


    @Override
    public final <T> ItemPredicate<I> greatEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT_EQUAL, operator.apply(this, operand));
    }


    @Override
    public final ItemPredicate<I> greatEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT_EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final ItemPredicate<I> greatEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final ItemPredicate<I> greatEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final ItemPredicate<I> notEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_EQUAL, operand);
    }


    @Override
    public final <T> ItemPredicate<I> notEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_EQUAL, operator.apply(this, operand));
    }

    @Override
    public final ItemPredicate<I> notEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.NOT_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final ItemPredicate<I> notEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.NOT_EQUAL, QueryOperator.SOME, subQuery);
    }

    @Override
    public final ItemPredicate<I> notEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.NOT_EQUAL, QueryOperator.ALL, subQuery);
    }

    @Override
    public final ItemPredicate<I> between(Expression first, SQLs.WordAnd and, Expression second) {
        assert and == SQLs.AND;
        return Expressions.betweenPredicate(this, first, second);
    }

    @Override
    public final <T> ItemPredicate<I> between(BiFunction<Expression, T, Expression> operator, T first
            , SQLs.WordAnd and, T second) {
        assert and == SQLs.AND;
        return Expressions.betweenPredicate(this, operator.apply(this, first), operator.apply(this, second));
    }

    @Override
    public final ItemPredicate<I> isNull() {
        return Expressions.unaryPredicate(UnaryOperator.IS_NULL, this);
    }

    @Override
    public final ItemPredicate<I> isNotNull() {
        return Expressions.unaryPredicate(UnaryOperator.IS_NOT_NULL, this);
    }


    @Override
    public final ItemPredicate<I> in(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.IN, operand);
    }

    @Override
    public final ItemPredicate<I> in(SubQuery subQuery) {
        return Expressions.inOperator(this, DualOperator.IN, subQuery);
    }

    @Override
    public final <T, O extends Collection<T>> ItemPredicate<I> in(BiFunction<Expression, O, Expression> operator, O operand) {
        return Expressions.dualPredicate(this, DualOperator.IN, operator.apply(this, operand));
    }


    @Override
    public final ItemPredicate<I> in(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        return Expressions.dualPredicate(this, DualOperator.IN, namedOperator.apply(this, paramName, size));
    }

    @Override
    public final ItemPredicate<I> notIn(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_IN, operand);
    }

    @Override
    public final ItemPredicate<I> notIn(SubQuery subQuery) {
        return Expressions.inOperator(this, DualOperator.NOT_IN, subQuery);
    }

    @Override
    public final <T, O extends Collection<T>> ItemPredicate<I> notIn(BiFunction<Expression, O, Expression> operator, O operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_IN, operator.apply(this, operand));
    }

    @Override
    public final ItemPredicate<I> notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        return Expressions.dualPredicate(this, DualOperator.NOT_IN, namedOperator.apply(this, paramName, size));
    }

    @Override
    public final ItemPredicate<I> like(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.LIKE, operand);
    }

    @Override
    public final <T> ItemPredicate<I> like(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.LIKE, operator.apply(this, operand));
    }

    @Override
    public final ItemPredicate<I> notLike(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_LIKE, operand);
    }

    @Override
    public final <T> ItemPredicate<I> notLike(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_LIKE, operator.apply(this, operand));
    }

    @Override
    public final _ItemExpression<I> mod(Expression operand) {
        return Expressions.dualExp(this, DualOperator.MOD, operand);
    }

    @Override
    public final <T> _ItemExpression<I> mod(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.MOD, operator.apply(this, operand));
    }

    @Override
    public final _ItemExpression<I> times(Expression operand) {
        return Expressions.dualExp(this, DualOperator.TIMES, operand);
    }

    @Override
    public final <T> _ItemExpression<I> times(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.TIMES, operator.apply(this, operand));
    }


    @Override
    public final _ItemExpression<I> plus(Expression operand) {
        return Expressions.dualExp(this, DualOperator.PLUS, operand);
    }

    @Override
    public final <T> _ItemExpression<I> plus(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.PLUS, operator.apply(this, operand));
    }

    @Override
    public final _ItemExpression<I> minus(Expression operand) {
        return Expressions.dualExp(this, DualOperator.MINUS, operand);
    }

    @Override
    public final <T> _ItemExpression<I> minus(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.MINUS, operator.apply(this, operand));
    }

    @Override
    public final _ItemExpression<I> divide(Expression operand) {
        return Expressions.dualExp(this, DualOperator.DIVIDE, operand);
    }

    @Override
    public final <T> _ItemExpression<I> divide(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.DIVIDE, operator.apply(this, operand));
    }


    @Override
    public final _ItemExpression<I> negate() {
        return Expressions.unaryExp(this, UnaryOperator.NEGATE);
    }

    @Override
    public final _ItemExpression<I> bitwiseAnd(Expression operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_AND, operand);
    }

    @Override
    public final <T> _ItemExpression<I> bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_AND, operator.apply(this, operand));
    }


    @Override
    public final _ItemExpression<I> bitwiseOr(Expression operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_OR, operand);
    }


    @Override
    public final <T> _ItemExpression<I> bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_OR, operator.apply(this, operand));
    }


    @Override
    public final _ItemExpression<I> xor(Expression operand) {
        return Expressions.dualExp(this, DualOperator.XOR, operand);
    }


    @Override
    public final <T> _ItemExpression<I> xor(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.XOR, operator.apply(this, operand));
    }


    @Override
    public final _ItemExpression<I> invert() {
        return Expressions.unaryExp(this, UnaryOperator.INVERT);
    }

    @Override
    public final _ItemExpression<I> rightShift(Expression operand) {
        return Expressions.dualExp(this, DualOperator.RIGHT_SHIFT, operand);
    }

    @Override
    public final <T> _ItemExpression<I> rightShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.RIGHT_SHIFT, operator.apply(this, operand));
    }


    @Override
    public final _ItemExpression<I> leftShift(Expression operand) {
        return Expressions.dualExp(this, DualOperator.LEFT_SHIFT, operand);
    }


    @Override
    public final <T> _ItemExpression<I> leftShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.LEFT_SHIFT, operator.apply(this, operand));
    }


    @Override
    public final _ItemExpression<I> asType(final @Nullable TypeMeta paramMeta) {
        if (paramMeta == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        final _ItemExpression<I> expression;
        if (this instanceof MutableParamMetaSpec) {
            ((MutableParamMetaSpec) this).updateParamMeta(paramMeta);
            expression = this;
        } else {
            expression = Expressions.castExp(this, paramMeta);
        }
        return expression;
    }


    @Override
    public final I as(final String alias) {
        return this.asFunction.apply(ArmySelections.forExp(this, alias));
    }


    final I onOuterEnd(Selection selection) {
        return this.asFunction.apply(selection);
    }

    interface MutableParamMetaSpec {

        void updateParamMeta(TypeMeta typeMeta);
    }







}
