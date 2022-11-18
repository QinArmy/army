package io.army.criteria.impl;


import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.SqlValueParam;
import io.army.criteria.TypeInfer;
import io.army.criteria.dialect.SubQuery;
import io.army.function.TeNamedOperator;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * this class is base class of most implementation of {@link Expression}
 *
 * @since 1.0
 */
abstract class OperationExpression<I extends Item> implements ArmyExpression, _AliasExpression<I> {

    final Function<TypeInfer, I> function;


    OperationExpression(Function<TypeInfer, I> function) {
        this.function = function;
    }


    @Override
    public final boolean isNullValue() {
        return this instanceof SqlValueParam.SingleNonNamedValue
                && ((SqlValueParam.SingleNonNamedValue) this).value() == null;
    }

    @Override
    public final OperationPredicate<I> equal(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.EQUAL, operand);
    }

    @Override
    public final <T> OperationPredicate<I> equal(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.EQUAL, operator.apply(this, operand));
    }

    @Override
    public final OperationPredicate<I> equalAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final OperationPredicate<I> equalSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate<I> less(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS, operand);
    }

    @Override
    public final <T> OperationPredicate<I> less(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS, operator.apply(this, operand));
    }


    @Override
    public final OperationPredicate<I> lessAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate<I> lessSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS, QueryOperator.SOME, subQuery);
    }

    @Override
    public final OperationPredicate<I> lessAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate<I> lessEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS_EQUAL, operand);
    }

    @Override
    public final <T> OperationPredicate<I> lessEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS_EQUAL, operator.apply(this, operand));
    }


    @Override
    public final OperationPredicate<I> lessEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate<I> lessEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate<I> lessEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate<I> great(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT, operand);
    }

    @Override
    public final <T> OperationPredicate<I> great(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT, operator.apply(this, operand));
    }


    @Override
    public final OperationPredicate<I> greatAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate<I> greatSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate<I> greatAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate<I> greatEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT_EQUAL, operand);
    }


    @Override
    public final <T> OperationPredicate<I> greatEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT_EQUAL, operator.apply(this, operand));
    }


    @Override
    public final OperationPredicate<I> greatEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT_EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final OperationPredicate<I> greatEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate<I> greatEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate<I> notEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_EQUAL, operand);
    }


    @Override
    public final <T> OperationPredicate<I> notEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_EQUAL, operator.apply(this, operand));
    }

    @Override
    public final OperationPredicate<I> notEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.NOT_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate<I> notEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.NOT_EQUAL, QueryOperator.SOME, subQuery);
    }

    @Override
    public final OperationPredicate<I> notEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.NOT_EQUAL, QueryOperator.ALL, subQuery);
    }

    @Override
    public final OperationPredicate<I> between(Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(false, this, first, second);
    }

    @Override
    public final <T> OperationPredicate<I> between(BiFunction<Expression, T, Expression> operator, T first,
                                                   SQLs.WordAnd and, T second) {
        return Expressions.betweenPredicate(false, this, operator.apply(this, first), operator.apply(this, second));
    }

    @Override
    public final OperationPredicate<I> notBetween(Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(true, this, first, second);
    }

    @Override
    public final <T> OperationPredicate<I> notBetween(BiFunction<Expression, T, Expression> operator, T first,
                                                      SQLs.WordAnd and, T second) {
        return Expressions.betweenPredicate(true, this, operator.apply(this, first), operator.apply(this, second));
    }

    @Override
    public final OperationPredicate<I> is(SQLs.BooleanTestOperand operand) {
        return Expressions.booleanTestPredicate(this, false, operand);
    }

    @Override
    public final OperationPredicate<I> isNot(SQLs.BooleanTestOperand operand) {
        return Expressions.booleanTestPredicate(this, true, operand);
    }

    @Override
    public final OperationPredicate<I> isNull() {
        return Expressions.booleanTestPredicate(this, false, SQLs.NULL);
    }

    @Override
    public final OperationPredicate<I> isNotNull() {
        return Expressions.booleanTestPredicate(this, true, SQLs.NULL);
    }


    @Override
    public final OperationPredicate<I> in(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.IN, operand);
    }

    @Override
    public final OperationPredicate<I> in(SubQuery subQuery) {
        return Expressions.inOperator(this, DualOperator.IN, subQuery);
    }

    @Override
    public final <T extends Collection<?>> OperationPredicate<I> in(BiFunction<Expression, T, Expression> operator,
                                                                    T operand) {
        return Expressions.dualPredicate(this, DualOperator.IN, operator.apply(this, operand));
    }

    @Override
    public final OperationPredicate<I> in(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        return Expressions.dualPredicate(this, DualOperator.IN, namedOperator.apply(this, paramName, size));
    }

    @Override
    public final OperationPredicate<I> notIn(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_IN, operand);
    }

    @Override
    public final OperationPredicate<I> notIn(SubQuery subQuery) {
        return Expressions.inOperator(this, DualOperator.NOT_IN, subQuery);
    }

    @Override
    public final <T extends Collection<?>> OperationPredicate<I> notIn(BiFunction<Expression, T, Expression> operator,
                                                                       T operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_IN, operator.apply(this, operand));
    }

    @Override
    public final OperationPredicate<I> notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        return Expressions.dualPredicate(this, DualOperator.NOT_IN, namedOperator.apply(this, paramName, size));
    }

    @Override
    public final OperationPredicate<I> like(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.LIKE, operand);
    }

    @Override
    public final <T> OperationPredicate<I> like(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.LIKE, operator.apply(this, operand));
    }

    @Override
    public final OperationPredicate<I> notLike(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_LIKE, operand);
    }

    @Override
    public final <T> OperationPredicate<I> notLike(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_LIKE, operator.apply(this, operand));
    }

    @Override
    public final OperationExpression<I> mod(Expression operand) {
        return Expressions.dualExp(this, DualOperator.MOD, operand);
    }

    @Override
    public final <T> OperationExpression<I> mod(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.MOD, operator.apply(this, operand));
    }

    @Override
    public final OperationExpression<I> times(Expression operand) {
        return Expressions.dualExp(this, DualOperator.TIMES, operand);
    }

    @Override
    public final <T> OperationExpression<I> times(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.TIMES, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression<I> plus(Expression operand) {
        return Expressions.dualExp(this, DualOperator.PLUS, operand);
    }

    @Override
    public final <T> OperationExpression<I> plus(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.PLUS, operator.apply(this, operand));
    }

    @Override
    public final OperationExpression<I> minus(Expression operand) {
        return Expressions.dualExp(this, DualOperator.MINUS, operand);
    }

    @Override
    public final <T> OperationExpression<I> minus(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.MINUS, operator.apply(this, operand));
    }

    @Override
    public final OperationExpression<I> divide(Expression operand) {
        return Expressions.dualExp(this, DualOperator.DIVIDE, operand);
    }

    @Override
    public final <T> OperationExpression<I> divide(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.DIVIDE, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression<I> negate() {
        return Expressions.unaryExp(this, UnaryOperator.NEGATE);
    }

    @Override
    public final OperationExpression<I> bitwiseAnd(Expression operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_AND, operand);
    }

    @Override
    public final <T> OperationExpression<I> bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_AND, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression<I> bitwiseOr(Expression operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_OR, operand);
    }


    @Override
    public final <T> OperationExpression<I> bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_OR, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression<I> xor(Expression operand) {
        return Expressions.dualExp(this, DualOperator.XOR, operand);
    }


    @Override
    public final <T> OperationExpression<I> xor(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.XOR, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression<I> invert() {
        return Expressions.unaryExp(this, UnaryOperator.INVERT);
    }

    @Override
    public final OperationExpression<I> rightShift(Expression operand) {
        return Expressions.dualExp(this, DualOperator.RIGHT_SHIFT, operand);
    }

    @Override
    public final <T> OperationExpression<I> rightShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.RIGHT_SHIFT, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression<I> leftShift(Expression operand) {
        return Expressions.dualExp(this, DualOperator.LEFT_SHIFT, operand);
    }


    @Override
    public final <T> OperationExpression<I> leftShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.LEFT_SHIFT, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression<I> mapTo(final @Nullable TypeMeta typeMeta) {
        if (typeMeta == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return Expressions.castExp(this, typeMeta);
    }

    @Override
    public OperationExpression<I> bracket() {
        return Expressions.bracketExp(this);
    }

    @Override
    public final I as(String alias) {
        return this.function.apply(ArmySelections.forExp(this, alias));
    }


}
