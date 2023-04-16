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
abstract class OperationExpression implements FunctionArg.SingleFunctionArg {


    /**
     * <p>
     * Private constructor
     * </p>
     *
     * @see OperationSimpleExpression#OperationSimpleExpression()
     * @see CompoundExpression#CompoundExpression()
     * @see PredicateExpression#PredicateExpression()
     * @see SqlFunctionExpression#SqlFunctionExpression()
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
        return Expressions.dualPredicate(this, BooleanDualOperator.EQUAL, operand);
    }


    @Override
    public final OperationPredicate equalAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final OperationPredicate equalSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate less(Expression operand) {
        return Expressions.dualPredicate(this, BooleanDualOperator.LESS, operand);
    }


    @Override
    public final OperationPredicate lessAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.LESS, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate lessSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.LESS, QueryOperator.SOME, subQuery);
    }

    @Override
    public final OperationPredicate lessAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.LESS, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate lessEqual(Expression operand) {
        return Expressions.dualPredicate(this, BooleanDualOperator.LESS_EQUAL, operand);
    }


    @Override
    public final OperationPredicate lessEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.LESS_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate lessEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.LESS_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate lessEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.LESS_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate great(Expression operand) {
        return Expressions.dualPredicate(this, BooleanDualOperator.GREAT, operand);
    }


    @Override
    public final OperationPredicate greatAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.GREAT, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate greatSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.GREAT, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate greatAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.GREAT, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate greatEqual(Expression operand) {
        return Expressions.dualPredicate(this, BooleanDualOperator.GREAT_EQUAL, operand);
    }


    @Override
    public final OperationPredicate greatEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.GREAT_EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final OperationPredicate greatEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.GREAT_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate greatEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.GREAT_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate notEqual(Expression operand) {
        return Expressions.dualPredicate(this, BooleanDualOperator.NOT_EQUAL, operand);
    }


    @Override
    public final OperationPredicate notEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.NOT_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate notEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.NOT_EQUAL, QueryOperator.SOME, subQuery);
    }

    @Override
    public final OperationPredicate notEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, BooleanDualOperator.NOT_EQUAL, QueryOperator.ALL, subQuery);
    }

    @Override
    public final OperationPredicate between(Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(this, false, null, first, second);
    }


    @Override
    public final OperationPredicate notBetween(Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(this, true, null, first, second);
    }

    @Override
    public final IPredicate between(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(this, false, modifier, first, second);
    }

    @Override
    public final IPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(this, true, modifier, first, second);
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
    public final OperationPredicate in(Expression operand) {
        return Expressions.dualPredicate(this, BooleanDualOperator.IN, operand);
    }

    @Override
    public final OperationPredicate in(SubQuery subQuery) {
        return Expressions.inOperator(this, BooleanDualOperator.IN, subQuery);
    }

    @Override
    public final OperationPredicate notIn(Expression operand) {
        return Expressions.dualPredicate(this, BooleanDualOperator.NOT_IN, operand);
    }

    @Override
    public final OperationPredicate notIn(SubQuery subQuery) {
        return Expressions.inOperator(this, BooleanDualOperator.NOT_IN, subQuery);
    }

    @Override
    public final OperationPredicate like(Expression pattern) {
        return Expressions.likePredicate(this, BooleanDualOperator.LIKE, pattern, SQLs.ESCAPE, null);
    }


    @Override
    public final IPredicate like(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        return Expressions.likePredicate(this, BooleanDualOperator.LIKE, pattern,
                escape, SQLs.literal(StringType.INSTANCE, escapeChar)
        );
    }

    @Override
    public final IPredicate like(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(this, BooleanDualOperator.LIKE, pattern, escape, escapeChar);
    }


    @Override
    public final OperationPredicate notLike(Expression pattern) {
        return Expressions.likePredicate(this, BooleanDualOperator.NOT_LIKE, pattern, SQLs.ESCAPE, null);
    }


    @Override
    public final IPredicate notLike(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        return Expressions.likePredicate(this, BooleanDualOperator.NOT_LIKE, pattern,
                escape, SQLs.literal(StringType.INSTANCE, escapeChar)
        );
    }

    @Override
    public final IPredicate notLike(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(this, BooleanDualOperator.NOT_LIKE, pattern, escape, escapeChar);
    }

    @Override
    public final OperationExpression mod(Expression operand) {
        return Expressions.dualExp(this, ExpDualOperator.MOD, operand);
    }


    @Override
    public final OperationExpression times(Expression operand) {
        return Expressions.dualExp(this, ExpDualOperator.TIMES, operand);
    }


    @Override
    public final OperationExpression plus(Expression operand) {
        return Expressions.dualExp(this, ExpDualOperator.PLUS, operand);
    }


    @Override
    public final OperationExpression minus(Expression operand) {
        return Expressions.dualExp(this, ExpDualOperator.MINUS, operand);
    }


    @Override
    public final OperationExpression divide(Expression operand) {
        return Expressions.dualExp(this, ExpDualOperator.DIVIDE, operand);
    }


    @Override
    public final OperationExpression bitwiseAnd(Expression operand) {
        return Expressions.dualExp(this, ExpDualOperator.BITWISE_AND, operand);
    }


    @Override
    public final OperationExpression bitwiseOr(Expression operand) {
        return Expressions.dualExp(this, ExpDualOperator.BITWISE_OR, operand);
    }

    @Override
    public final OperationExpression bitwiseXor(Expression operand) {
        return Expressions.dualExp(this, ExpDualOperator.BITWISE_XOR, operand);
    }


    @Override
    public final OperationExpression rightShift(Expression operand) {
        return Expressions.dualExp(this, ExpDualOperator.RIGHT_SHIFT, operand);
    }

    @Override
    public final OperationExpression leftShift(Expression operand) {
        return Expressions.dualExp(this, ExpDualOperator.LEFT_SHIFT, operand);
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
    public final IPredicate test(BiFunction<Expression, Expression, IPredicate> operator, Expression operand) {
        final IPredicate result;
        result = operator.apply(this, operand);
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


    static abstract class OperationSimpleExpression extends OperationExpression implements SimpleExpression {

        /**
         * package constructor
         */
        OperationSimpleExpression() {

        }

        @Override
        public final <T> OperationPredicate equal(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, BooleanDualOperator.EQUAL, funcRef.apply(this, value));
        }


        @Override
        public final <T> OperationPredicate less(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, BooleanDualOperator.LESS, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationPredicate lessEqual(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, BooleanDualOperator.LESS_EQUAL, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationPredicate great(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, BooleanDualOperator.GREAT, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationPredicate greatEqual(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, BooleanDualOperator.GREAT_EQUAL, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationPredicate notEqual(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, BooleanDualOperator.NOT_EQUAL, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationPredicate between(BiFunction<Expression, T, Expression> funcRef, T first,
                                                    SQLs.WordAnd and, T second) {
            return Expressions.betweenPredicate(this, false, null, funcRef.apply(this, first), funcRef.apply(this, second));
        }

        @Override
        public final <T> OperationPredicate notBetween(BiFunction<Expression, T, Expression> funcRef, T first,
                                                       SQLs.WordAnd and, T second) {
            return Expressions.betweenPredicate(this, true, null, funcRef.apply(this, first), funcRef.apply(this, second));
        }

        @Override
        public final <T> IPredicate between(@Nullable SQLsSyntax.BetweenModifier modifier,
                                            BiFunction<Expression, T, Expression> funcRef, T first,
                                            SQLsSyntax.WordAnd and, T second) {
            return Expressions.betweenPredicate(this, false, modifier, funcRef.apply(this, first), funcRef.apply(this, second));
        }

        @Override
        public final <T> IPredicate notBetween(@Nullable SQLsSyntax.BetweenModifier modifier,
                                               BiFunction<Expression, T, Expression> funcRef, T first,
                                               SQLsSyntax.WordAnd and, T second) {
            return Expressions.betweenPredicate(this, true, modifier, funcRef.apply(this, first), funcRef.apply(this, second));
        }

        @Override
        public final <T> IPredicate is(SQLsSyntax.IsComparisonWord operator,
                                       BiFunction<Expression, T, Expression> funcRef, @Nullable T value) {
            return Expressions.isComparisonPredicate(this, false, operator, funcRef.apply(this, value));
        }

        @Override
        public final <T> IPredicate isNot(SQLsSyntax.IsComparisonWord operator,
                                          BiFunction<Expression, T, Expression> funcRef, @Nullable T value) {
            return Expressions.isComparisonPredicate(this, true, operator, funcRef.apply(this, value));
        }

        @Override
        public final <T extends Collection<?>> OperationPredicate in(BiFunction<Expression, T, Expression> funcRef,
                                                                     T value) {
            return Expressions.dualPredicate(this, BooleanDualOperator.IN, funcRef.apply(this, value));
        }

        @Override
        public final <T extends Collection<?>> OperationPredicate notIn(BiFunction<Expression, T, Expression> funcRef,
                                                                        T value) {
            return Expressions.dualPredicate(this, BooleanDualOperator.NOT_IN, funcRef.apply(this, value));
        }

        @Override
        public final OperationPredicate in(TeNamedOperator<Expression> funcRef, String paramName, int size) {
            return Expressions.dualPredicate(this, BooleanDualOperator.IN, funcRef.apply(this, paramName, size));
        }

        @Override
        public final OperationPredicate notIn(TeNamedOperator<Expression> funcRef, String paramName, int size) {
            return Expressions.dualPredicate(this, BooleanDualOperator.NOT_IN, funcRef.apply(this, paramName, size));
        }

        @Override
        public final <T> OperationPredicate like(BiFunction<MappingType, T, Expression> funcRef, T value) {
            return Expressions.likePredicate(this, BooleanDualOperator.LIKE, funcRef.apply(TextType.INSTANCE, value),
                    SQLs.ESCAPE, null);
        }


        @Override
        public final <T> IPredicate like(BiFunction<MappingType, T, Expression> funcRef, T value,
                                         SqlSyntax.WordEscape escape, char escapeChar) {
            return Expressions.likePredicate(this, BooleanDualOperator.LIKE, funcRef.apply(TextType.INSTANCE, value),
                    escape, SQLs.literal(StringType.INSTANCE, escapeChar)
            );
        }


        @Override
        public final <T> OperationPredicate notLike(BiFunction<MappingType, T, Expression> funcRef, T value) {
            return Expressions.likePredicate(this, BooleanDualOperator.NOT_LIKE, funcRef.apply(TextType.INSTANCE, value),
                    SQLs.ESCAPE, null);
        }

        @Override
        public final <T> IPredicate notLike(BiFunction<MappingType, T, Expression> funcRef, T value,
                                            SqlSyntax.WordEscape escape, char escapeChar) {
            return Expressions.likePredicate(this, BooleanDualOperator.NOT_LIKE, funcRef.apply(TextType.INSTANCE, value),
                    escape, SQLs.literal(StringType.INSTANCE, escapeChar)
            );
        }

        @Override
        public final <T> OperationExpression mod(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, ExpDualOperator.MOD, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression times(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, ExpDualOperator.TIMES, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression plus(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, ExpDualOperator.PLUS, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression minus(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, ExpDualOperator.MINUS, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression divide(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, ExpDualOperator.DIVIDE, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression bitwiseAnd(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, ExpDualOperator.BITWISE_AND, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression bitwiseOr(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, ExpDualOperator.BITWISE_OR, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression bitwiseXor(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, ExpDualOperator.BITWISE_XOR, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression rightShift(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, ExpDualOperator.RIGHT_SHIFT, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression leftShift(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, ExpDualOperator.LEFT_SHIFT, funcRef.apply(this, value));
        }


        @Override
        public final <T> Expression apply(BiFunction<Expression, Expression, Expression> operator,
                                          BiFunction<Expression, T, Expression> funcRef, T value) {
            final Expression result;
            result = operator.apply(this, funcRef.apply(this, value));
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return result;
        }

        @Override
        public final <M extends SQLWords, T> Expression apply(
                OptionalClauseOperator<M, Expression, Expression> operator, BiFunction<Expression, T, Expression> funcRef,
                T value, M modifier, Expression optionalExp) {
            final Expression result;
            result = operator.apply(this, funcRef.apply(this, value), modifier, optionalExp);
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return result;
        }


        @Override
        public final <M extends SQLWords, T> Expression apply(
                OptionalClauseOperator<M, Expression, Expression> operator, BiFunction<Expression, T, Expression> funcRef,
                T value, M modifier, char escapeChar) {
            final Expression result;
            result = operator.apply(this, funcRef.apply(this, value), modifier, SQLs.literal(StringType.INSTANCE, escapeChar));
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return result;
        }

        @Override
        public final <T> IPredicate test(BiFunction<Expression, Expression, IPredicate> operator,
                                         BiFunction<Expression, T, Expression> funcRef, T value) {
            final IPredicate result;
            result = operator.apply(this, funcRef.apply(this, value));
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return result;
        }

        @Override
        public final <M extends SQLWords, T> IPredicate test(
                OptionalClauseOperator<M, Expression, IPredicate> operator,
                BiFunction<MappingType, T, Expression> funcRef, T value, M modifier, Expression optionalExp) {
            final IPredicate result;
            result = operator.apply(this, funcRef.apply(StringType.INSTANCE, value), modifier, optionalExp);
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return result;
        }

        @Override
        public final <M extends SQLWords, T> IPredicate test(
                OptionalClauseOperator<M, Expression, IPredicate> operator,
                BiFunction<MappingType, T, Expression> funcRef, T value, M modifier, char escapeChar) {
            final IPredicate result;
            result = operator.apply(this, funcRef.apply(StringType.INSTANCE, value),
                    modifier, SQLs.literal(StringType.INSTANCE, escapeChar));
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return result;
        }


    }//OperationSimpleExpression


    static abstract class SqlFunctionExpression extends OperationSimpleExpression implements SQLFunction {

        /**
         * package constructor
         */
        SqlFunctionExpression() {
        }

    }//FunctionExpression


    static abstract class CompoundExpression extends OperationExpression {

        /**
         * package constructor
         */
        CompoundExpression() {
            assert !(this instanceof IPredicate);
        }


    }//CompoundExpression


    /**
     * <p>
     * This class is base class only of below:
     *     <ul>
     *         <li>{@link SingleParamExpression}</li>
     *         <li>{@link SingleLiteralExpression}</li>
     *     </ul>
     * </p>
     *
     * @since 1.0
     */
    static abstract class SingleValueExpression extends OperationSimpleExpression
            implements SqlValueParam.SingleValue {

        final TypeMeta type;

        /**
         * package constructor
         */
        SingleValueExpression(final TypeMeta type) {
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                this.type = type;
            }
        }


    }//SingleValueExpression


    static abstract class PredicateExpression extends OperationExpression implements _Predicate {

        /**
         * package constructor
         */
        PredicateExpression() {
        }


        @Override
        public final TypeMeta typeMeta() {
            return BooleanType.INSTANCE;
        }


    }//PredicateExpression


}
