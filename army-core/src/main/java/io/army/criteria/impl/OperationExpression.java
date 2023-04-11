package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
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


    @Override
    public final boolean isNullValue() {
        return this instanceof SqlValueParam.SingleNonNamedValue
                && ((SqlValueParam.SingleNonNamedValue) this).value() == null;
    }

    @Override
    public final OperationPredicate equal(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.EQUAL, operand);
    }

    @Override
    public final <T> OperationPredicate equal(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.EQUAL, operator.apply(this, operand));
    }

    @Override
    public final OperationPredicate equalAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final OperationPredicate equalSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate less(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS, operand);
    }

    @Override
    public final <T> OperationPredicate less(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS, operator.apply(this, operand));
    }


    @Override
    public final OperationPredicate lessAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate lessSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS, QueryOperator.SOME, subQuery);
    }

    @Override
    public final OperationPredicate lessAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate lessEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS_EQUAL, operand);
    }

    @Override
    public final <T> OperationPredicate lessEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS_EQUAL, operator.apply(this, operand));
    }


    @Override
    public final OperationPredicate lessEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate lessEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate lessEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.LESS_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate great(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT, operand);
    }

    @Override
    public final <T> OperationPredicate great(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT, operator.apply(this, operand));
    }


    @Override
    public final OperationPredicate greatAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate greatSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate greatAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate greatEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT_EQUAL, operand);
    }


    @Override
    public final <T> OperationPredicate greatEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.GREAT_EQUAL, operator.apply(this, operand));
    }


    @Override
    public final OperationPredicate greatEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT_EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final OperationPredicate greatEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate greatEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.GREAT_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate notEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_EQUAL, operand);
    }


    @Override
    public final <T> OperationPredicate notEqual(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_EQUAL, operator.apply(this, operand));
    }

    @Override
    public final OperationPredicate notEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.NOT_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate notEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.NOT_EQUAL, QueryOperator.SOME, subQuery);
    }

    @Override
    public final OperationPredicate notEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualOperator.NOT_EQUAL, QueryOperator.ALL, subQuery);
    }

    @Override
    public final OperationPredicate between(Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(false, null, this, first, second);
    }

    @Override
    public final <T> OperationPredicate between(BiFunction<Expression, T, Expression> operator, T first,
                                                SQLs.WordAnd and, T second) {
        return Expressions.betweenPredicate(false, null, this, operator.apply(this, first), operator.apply(this, second));
    }

    @Override
    public final OperationPredicate notBetween(Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(true, null, this, first, second);
    }

    @Override
    public final <T> OperationPredicate notBetween(BiFunction<Expression, T, Expression> operator, T first,
                                                   SQLs.WordAnd and, T second) {
        return Expressions.betweenPredicate(true, null, this, operator.apply(this, first), operator.apply(this, second));
    }


    @Override
    public final IPredicate between(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(false, modifier, this, first, second);
    }

    @Override
    public final <T> IPredicate between(@Nullable SQLsSyntax.BetweenModifier modifier,
                                        BiFunction<Expression, T, Expression> operator, T first,
                                        SQLsSyntax.WordAnd and, T second) {
        return Expressions.betweenPredicate(false, modifier, this, operator.apply(this, first), operator.apply(this, second));
    }

    @Override
    public final IPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(true, modifier, this, first, second);
    }

    @Override
    public final <T> IPredicate notBetween(@Nullable SQLsSyntax.BetweenModifier modifier,
                                           BiFunction<Expression, T, Expression> operator, T first,
                                           SQLsSyntax.WordAnd and, T second) {
        return Expressions.betweenPredicate(true, modifier, this, operator.apply(this, first), operator.apply(this, second));
    }

    @Override
    public final OperationPredicate is(SQLsSyntax.BooleanTestWord operand) {
        return Expressions.booleanTestPredicate(this, false, operand);
    }

    @Override
    public final OperationPredicate isNot(SQLsSyntax.BooleanTestWord operand) {
        return Expressions.booleanTestPredicate(this, true, operand);
    }

    @Override
    public final OperationPredicate isNull() {
        return Expressions.booleanTestPredicate(this, false, SQLs.NULL);
    }

    @Override
    public final OperationPredicate isNotNull() {
        return Expressions.booleanTestPredicate(this, true, SQLs.NULL);
    }

    @Override
    public final IPredicate is(SQLsSyntax.IsComparisonWord operator, Expression operand) {
        return Expressions.isComparisonPredicate(this, false, operator, operand);
    }

    @Override
    public final IPredicate isNot(SQLsSyntax.IsComparisonWord operator, Expression operand) {
        return Expressions.isComparisonPredicate(this, true, operator, operand);
    }

    @Override
    public final <T> IPredicate is(SQLsSyntax.IsComparisonWord operator,
                                   BiFunction<Expression, T, Expression> valueOperator, @Nullable T value) {
        return Expressions.isComparisonPredicate(this, false, operator, valueOperator.apply(this, value));
    }

    @Override
    public final <T> IPredicate isNot(SQLsSyntax.IsComparisonWord operator,
                                      BiFunction<Expression, T, Expression> valueOperator, @Nullable T value) {
        return Expressions.isComparisonPredicate(this, true, operator, valueOperator.apply(this, value));
    }

    @Override
    public final OperationPredicate in(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.IN, operand);
    }

    @Override
    public final OperationPredicate in(SubQuery subQuery) {
        return Expressions.inOperator(this, DualOperator.IN, subQuery);
    }

    @Override
    public final <T extends Collection<?>> OperationPredicate in(BiFunction<Expression, T, Expression> operator,
                                                                 T operand) {
        return Expressions.dualPredicate(this, DualOperator.IN, operator.apply(this, operand));
    }

    @Override
    public final OperationPredicate in(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        return Expressions.dualPredicate(this, DualOperator.IN, namedOperator.apply(this, paramName, size));
    }

    @Override
    public final OperationPredicate notIn(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_IN, operand);
    }

    @Override
    public final OperationPredicate notIn(SubQuery subQuery) {
        return Expressions.inOperator(this, DualOperator.NOT_IN, subQuery);
    }

    @Override
    public final <T extends Collection<?>> OperationPredicate notIn(BiFunction<Expression, T, Expression> operator,
                                                                    T operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_IN, operator.apply(this, operand));
    }

    @Override
    public final OperationPredicate notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        return Expressions.dualPredicate(this, DualOperator.NOT_IN, namedOperator.apply(this, paramName, size));
    }

    @Override
    public final OperationPredicate like(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.LIKE, operand);
    }

    @Override
    public final <T> OperationPredicate like(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.LIKE, operator.apply(this, operand));
    }

    @Override
    public final OperationPredicate notLike(Expression operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_LIKE, operand);
    }

    @Override
    public final <T> OperationPredicate notLike(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualPredicate(this, DualOperator.NOT_LIKE, operator.apply(this, operand));
    }

    @Override
    public final OperationExpression mod(Expression operand) {
        return Expressions.dualExp(this, DualOperator.MOD, operand);
    }

    @Override
    public final <T> OperationExpression mod(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.MOD, operator.apply(this, operand));
    }

    @Override
    public final OperationExpression times(Expression operand) {
        return Expressions.dualExp(this, DualOperator.TIMES, operand);
    }

    @Override
    public final <T> OperationExpression times(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.TIMES, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression plus(Expression operand) {
        return Expressions.dualExp(this, DualOperator.PLUS, operand);
    }

    @Override
    public final <T> OperationExpression plus(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.PLUS, operator.apply(this, operand));
    }

    @Override
    public final OperationExpression minus(Expression operand) {
        return Expressions.dualExp(this, DualOperator.MINUS, operand);
    }

    @Override
    public final <T> OperationExpression minus(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.MINUS, operator.apply(this, operand));
    }

    @Override
    public final OperationExpression divide(Expression operand) {
        return Expressions.dualExp(this, DualOperator.DIVIDE, operand);
    }

    @Override
    public final <T> OperationExpression divide(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.DIVIDE, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression negate() {
        return Expressions.unaryExp(this, UnaryOperator.NEGATE);
    }

    @Override
    public final OperationExpression bitwiseAnd(Expression operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_AND, operand);
    }

    @Override
    public final <T> OperationExpression bitwiseAnd(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_AND, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression bitwiseOr(Expression operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_OR, operand);
    }


    @Override
    public final <T> OperationExpression bitwiseOr(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.BITWISE_OR, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression xor(Expression operand) {
        return Expressions.dualExp(this, DualOperator.XOR, operand);
    }


    @Override
    public final <T> OperationExpression xor(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.XOR, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression invert() {
        return Expressions.unaryExp(this, UnaryOperator.INVERT);
    }

    @Override
    public final OperationExpression rightShift(Expression operand) {
        return Expressions.dualExp(this, DualOperator.RIGHT_SHIFT, operand);
    }

    @Override
    public final <T> OperationExpression rightShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.RIGHT_SHIFT, operator.apply(this, operand));
    }


    @Override
    public final OperationExpression leftShift(Expression operand) {
        return Expressions.dualExp(this, DualOperator.LEFT_SHIFT, operand);
    }


    @Override
    public final <T> OperationExpression leftShift(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.LEFT_SHIFT, operator.apply(this, operand));
    }

    @Override
    public final Expression concat(Expression rightString) {
        return Expressions.concatStringExp(this, rightString);
    }

    @Override
    public final <T> Expression concat(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.concatStringExp(this, operator.apply(this, operand));
    }

    @Override
    public final <E extends Expression> E apply(BiFunction<Expression, Expression, E> operator, Expression operand) {
        final E result;
        result = operator.apply(this, operand);
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <E extends Expression, T> E apply(BiFunction<Expression, Expression, E> operator,
                                                   BiFunction<Expression, T, Expression> valueOperator, T operand) {
        final E result;
        result = operator.apply(this, valueOperator.apply(this, operand));
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final OperationExpression mapTo(final @Nullable TypeMeta typeMeta) {
        if (typeMeta == null) {
            throw ContextStack.nullPointer(ContextStack.peek());
        }
        return Expressions.castExp(this, typeMeta);
    }

    @Override
    public Expression bracket() {
        return Expressions.bracketExp(this);
    }

    @Override
    public final Selection as(String selectionAlas) {
        return ArmySelections.forExp(this, selectionAlas);
    }


    @Override
    public final SortItem asSortItem() {
        //always return this
        return this;
    }

    @Override
    public final SortItem asc() {
        return ArmySortItems.create(this, SQLs.ASC, null);
    }

    @Override
    public final SortItem desc() {
        return ArmySortItems.create(this, SQLs.DESC, null);
    }

    @Override
    public final SortItem ascSpace(@Nullable Statement.NullsFirstLast firstLast) {
        return ArmySortItems.create(this, SQLs.ASC, firstLast);
    }

    @Override
    public final SortItem descSpace(@Nullable Statement.NullsFirstLast firstLast) {
        return ArmySortItems.create(this, SQLs.DESC, firstLast);
    }


}
