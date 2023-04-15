package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.standard.SQLFunction;
import io.army.function.OptionalClauseOperator;
import io.army.function.TeNamedOperator;
import io.army.lang.Nullable;
import io.army.mapping.BooleanType;
import io.army.mapping.MappingType;
import io.army.mapping.StringType;
import io.army.mapping.TextType;
import io.army.meta.TypeMeta;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * this class is base class of most implementation of {@link Expression}
 *
 * @since 1.0
 */
abstract class OperationExpression implements ArmyExpression {


    /**
     * <p>
     * Private constructor
     * </p>
     *
     * @see SimpleExpression#SimpleExpression()
     * @see CompoundExpression#CompoundExpression()
     * @see PredicateExpression#PredicateExpression()
     * @see FunctionExpression#FunctionExpression()
     */
    private OperationExpression() {
    }

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
    public final <T> OperationPredicate less(BiFunction<Expression, T, Expression> valueFunc, T operand) {
        return Expressions.dualPredicate(this, DualOperator.LESS, valueFunc.apply(this, operand));
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
        return Expressions.betweenPredicate(this, false, null, first, second);
    }

    @Override
    public final <T> OperationPredicate between(BiFunction<Expression, T, Expression> operator, T first,
                                                SQLs.WordAnd and, T second) {
        return Expressions.betweenPredicate(this, false, null, operator.apply(this, first), operator.apply(this, second));
    }

    @Override
    public final OperationPredicate notBetween(Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(this, true, null, first, second);
    }

    @Override
    public final <T> OperationPredicate notBetween(BiFunction<Expression, T, Expression> operator, T first,
                                                   SQLs.WordAnd and, T second) {
        return Expressions.betweenPredicate(this, true, null, operator.apply(this, first), operator.apply(this, second));
    }


    @Override
    public final IPredicate between(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(this, false, modifier, first, second);
    }

    @Override
    public final <T> IPredicate between(@Nullable SQLsSyntax.BetweenModifier modifier,
                                        BiFunction<Expression, T, Expression> operator, T first,
                                        SQLsSyntax.WordAnd and, T second) {
        return Expressions.betweenPredicate(this, false, modifier, operator.apply(this, first), operator.apply(this, second));
    }

    @Override
    public final IPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(this, true, modifier, first, second);
    }

    @Override
    public final <T> IPredicate notBetween(@Nullable SQLsSyntax.BetweenModifier modifier,
                                           BiFunction<Expression, T, Expression> operator, T first,
                                           SQLsSyntax.WordAnd and, T second) {
        return Expressions.betweenPredicate(this, true, modifier, operator.apply(this, first), operator.apply(this, second));
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
    public final OperationPredicate like(Expression pattern) {
        return Expressions.likePredicate(this, DualOperator.LIKE, pattern, SQLs.ESCAPE, null);
    }

    @Override
    public final <T> OperationPredicate like(BiFunction<MappingType, T, Expression> operator, T operand) {
        return Expressions.likePredicate(this, DualOperator.LIKE, operator.apply(TextType.INSTANCE, operand),
                SQLs.ESCAPE, null);
    }

    @Override
    public final IPredicate like(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        return Expressions.likePredicate(this, DualOperator.LIKE, pattern,
                escape, SQLs.literal(StringType.INSTANCE, escapeChar)
        );
    }

    @Override
    public final IPredicate like(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(this, DualOperator.LIKE, pattern, escape, escapeChar);
    }

    @Override
    public final <T> IPredicate like(BiFunction<MappingType, T, Expression> operator, T operand,
                                     SqlSyntax.WordEscape escape, char escapeChar) {
        return Expressions.likePredicate(this, DualOperator.LIKE, operator.apply(TextType.INSTANCE, operand),
                escape, SQLs.literal(StringType.INSTANCE, escapeChar)
        );
    }

    @Override
    public final OperationPredicate notLike(Expression pattern) {
        return Expressions.likePredicate(this, DualOperator.NOT_LIKE, pattern, SQLs.ESCAPE, null);
    }

    @Override
    public final <T> OperationPredicate notLike(BiFunction<MappingType, T, Expression> operator, T operand) {
        return Expressions.likePredicate(this, DualOperator.NOT_LIKE, operator.apply(TextType.INSTANCE, operand),
                SQLs.ESCAPE, null);
    }

    @Override
    public final IPredicate notLike(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        return Expressions.likePredicate(this, DualOperator.NOT_LIKE, pattern,
                escape, SQLs.literal(StringType.INSTANCE, escapeChar)
        );
    }

    @Override
    public final IPredicate notLike(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(this, DualOperator.NOT_LIKE, pattern, escape, escapeChar);
    }

    @Override
    public final <T> IPredicate notLike(BiFunction<MappingType, T, Expression> operator, T operand,
                                        SqlSyntax.WordEscape escape, char escapeChar) {
        return Expressions.likePredicate(this, DualOperator.NOT_LIKE, operator.apply(TextType.INSTANCE, operand),
                escape, SQLs.literal(StringType.INSTANCE, escapeChar)
        );
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
    public final OperationExpression bitwiseXor(Expression operand) {
        return Expressions.dualExp(this, DualOperator.XOR, operand);
    }


    @Override
    public final <T> OperationExpression bitwiseXor(BiFunction<Expression, T, Expression> operator, T operand) {
        return Expressions.dualExp(this, DualOperator.XOR, operator.apply(this, operand));
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
    public final Expression apply(BiFunction<Expression, Expression, Expression> operator, Expression operand) {
        final Expression result;
        result = operator.apply(this, operand);
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <T> Expression apply(BiFunction<Expression, Expression, Expression> operator,
                                      BiFunction<Expression, T, Expression> valueOperator, T value) {
        final Expression result;
        result = operator.apply(this, valueOperator.apply(this, value));
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <M extends SQLWords> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator,
                                                       Expression right, M modifier, Expression optionalExp) {
        final Expression result;
        result = operator.apply(this, right, modifier, optionalExp);
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <M extends SQLWords> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator,
                                                       Expression right, M modifier, char escapeChar) {
        final Expression result;
        result = operator.apply(this, right, modifier, SQLs.literal(StringType.INSTANCE, escapeChar));
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <M extends SQLWords, T> Expression apply(
            OptionalClauseOperator<M, Expression, Expression> operator, BiFunction<Expression, T, Expression> valueOperator,
            T value, M modifier, Expression optionalExp) {
        final Expression result;
        result = operator.apply(this, valueOperator.apply(this, value), modifier, optionalExp);
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <M extends SQLWords, T> Expression apply(
            OptionalClauseOperator<M, Expression, Expression> operator, BiFunction<Expression, T, Expression> valueOperator,
            T value, M modifier, char escapeChar) {
        final Expression result;
        result = operator.apply(this, valueOperator.apply(this, value), modifier, SQLs.literal(StringType.INSTANCE, escapeChar));
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final IPredicate test(BiFunction<Expression, Expression, IPredicate> operator, Expression operand) {
        final IPredicate result;
        result = operator.apply(this, operand);
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <T> IPredicate test(BiFunction<Expression, Expression, IPredicate> operator,
                                     BiFunction<Expression, T, Expression> valueOperator, T value) {
        final IPredicate result;
        result = operator.apply(this, valueOperator.apply(this, value));
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <M extends SQLWords> IPredicate test(
            OptionalClauseOperator<M, Expression, IPredicate> operator, Expression right, M modifier,
            Expression optionalExp) {
        final IPredicate result;
        result = operator.apply(this, right, modifier, optionalExp);
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <M extends SQLWords> IPredicate test(
            OptionalClauseOperator<M, Expression, IPredicate> operator, Expression right, M modifier, char escapeChar) {
        final IPredicate result;
        result = operator.apply(this, right, modifier, SQLs.literal(StringType.INSTANCE, escapeChar));
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <M extends SQLWords, T> IPredicate test(
            OptionalClauseOperator<M, Expression, IPredicate> operator,
            BiFunction<MappingType, T, Expression> valueOperator, T value, M modifier, Expression optionalExp) {
        final IPredicate result;
        result = operator.apply(this, valueOperator.apply(StringType.INSTANCE, value), modifier, optionalExp);
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <M extends SQLWords, T> IPredicate test(
            OptionalClauseOperator<M, Expression, IPredicate> operator,
            BiFunction<MappingType, T, Expression> valueOperator, T value, M modifier, char escapeChar) {
        final IPredicate result;
        result = operator.apply(this, valueOperator.apply(StringType.INSTANCE, value),
                modifier, SQLs.literal(StringType.INSTANCE, escapeChar));
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final OperationExpression mapTo(final @Nullable TypeMeta typeMeta) {
        if (typeMeta == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return Expressions.castExp(this, typeMeta);
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


    static abstract class SimpleExpression extends OperationExpression {

        /**
         * package constructor
         */
        SimpleExpression() {

        }

        @Override
        public final Expression bracket() {
            // always return this
            return this;
        }


    }//SimpleExpression

    static abstract class FunctionExpression extends SimpleExpression implements SQLFunction {

        /**
         * package constructor
         */
        FunctionExpression() {
        }

    }//FunctionExpression


    static abstract class CompoundExpression extends OperationExpression {

        /**
         * package constructor
         */
        CompoundExpression() {
            assert !(this instanceof IPredicate);
        }

        @Override
        public final Expression bracket() {
            return Expressions.bracketExp(this);
        }


    }//CompoundExpression


    /**
     * <p>
     * This class is base class only of below:
     *     <ul>
     *         <li>{@link MultiParamExpression}</li>
     *         <li>{@link MultiLiteralExpression}</li>
     *     </ul>
     * </p>
     *
     * @since 1.0
     */
    static abstract class MultiValueExpression extends CompoundExpression implements SqlValueParam.MultiValue {

        /**
         * package constructor
         */
        MultiValueExpression() {
        }


    }//MultiValueExpression


    static abstract class PredicateExpression extends OperationExpression implements _Predicate {

        /**
         * package constructor
         */
        PredicateExpression() {
        }


        @Override
        public final IPredicate bracket() {
            return Expressions.bracketPredicate(this);
        }

        @Override
        public final TypeMeta typeMeta() {
            return BooleanType.INSTANCE;
        }


    }//PredicateExpression


}
