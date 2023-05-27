package io.army.criteria.impl;


import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.impl.inner._Predicate;
import io.army.criteria.standard.SQLFunction;
import io.army.dialect._Constant;
import io.army.dialect._SqlContext;
import io.army.function.OptionalClauseOperator;
import io.army.function.TeNamedOperator;
import io.army.lang.Nullable;
import io.army.mapping.*;
import io.army.mapping.optional.JsonPathType;
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

import java.util.Collection;
import java.util.function.BiFunction;

/**
 * this class is base class of most implementation of {@link Expression}
 *
 * @since 1.0
 */
abstract class OperationExpression implements FunctionArg.SingleFunctionArg, ArmyExpression {


    /**
     * <p>
     * Private constructor
     * </p>
     *
     * @see OperationSimpleExpression#OperationSimpleExpression()
     * @see OperationCompoundExpression#OperationCompoundExpression()
     * @see PredicateExpression#PredicateExpression()
     */
    private OperationExpression() {
    }


    @Override
    public final boolean isNullValue() {
        return this instanceof SqlValueParam.SingleAnonymousValue
                && ((SqlValueParam.SingleAnonymousValue) this).value() == null;
    }

    @Override
    public final CompoundPredicate equal(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.EQUAL, operand);
    }


    @Override
    public final CompoundPredicate equalAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final CompoundPredicate equalSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final CompoundPredicate less(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS, operand);
    }


    @Override
    public final CompoundPredicate lessAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, QueryOperator.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate lessSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, QueryOperator.SOME, subQuery);
    }

    @Override
    public final CompoundPredicate lessAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, QueryOperator.ALL, subQuery);
    }


    @Override
    public final CompoundPredicate lessEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS_EQUAL, operand);
    }


    @Override
    public final CompoundPredicate lessEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate lessEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final CompoundPredicate lessEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final CompoundPredicate greater(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER, operand);
    }


    @Override
    public final CompoundPredicate greaterAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, QueryOperator.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate greaterSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, QueryOperator.SOME, subQuery);
    }


    @Override
    public final CompoundPredicate greaterAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, QueryOperator.ALL, subQuery);
    }


    @Override
    public final CompoundPredicate greaterEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER_EQUAL, operand);
    }


    @Override
    public final CompoundPredicate greaterEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final CompoundPredicate greaterEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final CompoundPredicate greaterEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final CompoundPredicate notEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_EQUAL, operand);
    }


    @Override
    public final CompoundPredicate notEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final CompoundPredicate notEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, QueryOperator.SOME, subQuery);
    }

    @Override
    public final CompoundPredicate notEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, QueryOperator.ALL, subQuery);
    }

    @Override
    public final CompoundPredicate between(Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(this, false, null, first, second);
    }


    @Override
    public final CompoundPredicate notBetween(Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(this, true, null, first, second);
    }

    @Override
    public final CompoundPredicate between(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(this, false, modifier, first, second);
    }

    @Override
    public final CompoundPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        return Expressions.betweenPredicate(this, true, modifier, first, second);
    }

    @Override
    public final CompoundPredicate is(SQLsSyntax.BooleanTestWord operand) {
        return Expressions.booleanTestPredicate(this, false, operand);
    }

    @Override
    public final CompoundPredicate isNot(SQLsSyntax.BooleanTestWord operand) {
        return Expressions.booleanTestPredicate(this, true, operand);
    }

    @Override
    public final CompoundPredicate isNull() {
        return Expressions.booleanTestPredicate(this, false, SQLs.NULL);
    }

    @Override
    public final CompoundPredicate isNotNull() {
        return Expressions.booleanTestPredicate(this, true, SQLs.NULL);
    }

    @Override
    public final CompoundPredicate is(SQLsSyntax.IsComparisonWord operator, Expression operand) {
        return Expressions.isComparisonPredicate(this, false, operator, operand);
    }

    @Override
    public final CompoundPredicate isNot(SQLsSyntax.IsComparisonWord operator, Expression operand) {
        return Expressions.isComparisonPredicate(this, true, operator, operand);
    }


    @Override
    public final CompoundPredicate in(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.IN, operand);
    }

    @Override
    public final CompoundPredicate in(SubQuery subQuery) {
        return Expressions.inOperator(this, DualBooleanOperator.IN, subQuery);
    }

    @Override
    public final CompoundPredicate notIn(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_IN, operand);
    }

    @Override
    public final CompoundPredicate notIn(SubQuery subQuery) {
        return Expressions.inOperator(this, DualBooleanOperator.NOT_IN, subQuery);
    }

    @Override
    public final CompoundPredicate like(Expression pattern) {
        return Expressions.likePredicate(this, DualBooleanOperator.LIKE, pattern, SQLs.ESCAPE, null);
    }


    @Override
    public final CompoundPredicate like(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        return Expressions.likePredicate(this, DualBooleanOperator.LIKE, pattern,
                escape, SQLs.literal(NoCastTextType.INSTANCE, escapeChar)
        );
    }

    @Override
    public final CompoundPredicate like(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(this, DualBooleanOperator.LIKE, pattern, escape, escapeChar);
    }


    @Override
    public final CompoundPredicate notLike(Expression pattern) {
        return Expressions.likePredicate(this, DualBooleanOperator.NOT_LIKE, pattern, SQLs.ESCAPE, null);
    }


    @Override
    public final CompoundPredicate notLike(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        return Expressions.likePredicate(this, DualBooleanOperator.NOT_LIKE, pattern,
                escape, SQLs.literal(StringType.INSTANCE, escapeChar)
        );
    }

    @Override
    public final CompoundPredicate notLike(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(this, DualBooleanOperator.NOT_LIKE, pattern, escape, escapeChar);
    }

    @Override
    public final CompoundExpression mod(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.MOD, operand);
    }


    @Override
    public final CompoundExpression times(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.TIMES, operand);
    }


    @Override
    public final CompoundExpression plus(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.PLUS, operand);
    }


    @Override
    public final CompoundExpression minus(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.MINUS, operand);
    }


    @Override
    public final CompoundExpression divide(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.DIVIDE, operand);
    }


    @Override
    public final CompoundExpression bitwiseAnd(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_AND, operand);
    }


    @Override
    public final CompoundExpression bitwiseOr(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_OR, operand);
    }

    @Override
    public final CompoundExpression bitwiseXor(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_XOR, operand);
    }


    @Override
    public final CompoundExpression rightShift(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.RIGHT_SHIFT, operand);
    }

    @Override
    public final CompoundExpression leftShift(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.LEFT_SHIFT, operand);
    }

    @Override
    public final CompoundExpression apply(BiFunction<Expression, Expression, CompoundExpression> operator, Expression operand) {
        final CompoundExpression result;
        result = operator.apply(this, operand);
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <M extends SQLWords> CompoundExpression apply(OptionalClauseOperator<M, Expression, CompoundExpression> operator,
                                                               Expression right, M modifier, Expression optionalExp) {
        final CompoundExpression result;
        result = operator.apply(this, right, modifier, optionalExp);
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <M extends SQLWords> CompoundExpression apply(OptionalClauseOperator<M, Expression, CompoundExpression> operator,
                                                               Expression right, M modifier, char escapeChar) {
        final CompoundExpression result;
        result = operator.apply(this, right, modifier, SQLs.literal(StringType.INSTANCE, escapeChar));
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }


    @Override
    public final CompoundPredicate test(BiFunction<Expression, Expression, CompoundPredicate> operator, Expression operand) {
        final CompoundPredicate result;
        result = operator.apply(this, operand);
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }


    @Override
    public final <M extends SQLWords> CompoundPredicate test(
            OptionalClauseOperator<M, Expression, CompoundPredicate> operator, Expression right, M modifier,
            Expression optionalExp) {
        final CompoundPredicate result;
        result = operator.apply(this, right, modifier, optionalExp);
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }

    @Override
    public final <M extends SQLWords> CompoundPredicate test(
            OptionalClauseOperator<M, Expression, CompoundPredicate> operator, Expression right, M modifier, char escapeChar) {
        final CompoundPredicate result;
        result = operator.apply(this, right, modifier, SQLs.literal(StringType.INSTANCE, escapeChar));
        if (result == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return result;
    }


    @Override
    public final Expression mapTo(final @Nullable TypeMeta typeMeta) {
        if (typeMeta == null) {
            throw ContextStack.clearStackAndNullPointer();
        }
        return Expressions.castExp(this, typeMeta);
    }

    @Override
    public final Selection as(String selectionAlas) {
        if (this instanceof CriteriaContexts.SelectionReference) {
            String m = String.format("the reference of %s don't support as() method.", Selection.class.getName());
            throw ContextStack.clearStackAndCriteriaError(m);
        }
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

    static OperationExpression bracketExp(final @Nullable Expression expression) {
        final OperationExpression bracket;
        if (!(expression instanceof OperationExpression)) {
            throw NonOperationExpression.nonOperationExpression(expression);
        } else if (expression instanceof OperationSimpleExpression) {
            bracket = (OperationSimpleExpression) expression;
        } else {
            bracket = new BracketsExpression((ArmyExpression) expression);
        }
        return bracket;
    }

    /**
     * @see SQLs#NULL
     */
    static SqlSyntax.WordNull nullWord() {
        return NullWord.INSTANCE;
    }


    static abstract class OperationSimpleExpression extends OperationExpression
            implements SimpleExpression,
            ArmySimpleExpression,
            Statement._ArrayExpOperator,
            Statement._JsonExpOperator {

        /**
         * package constructor
         */
        OperationSimpleExpression() {

        }

        @Override
        public final <T> CompoundPredicate equal(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.EQUAL, funcRef.apply(this, value));
        }


        @Override
        public final <T> CompoundPredicate less(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.LESS, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundPredicate lessEqual(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.LESS_EQUAL, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundPredicate greater(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.GREATER, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundPredicate greaterEqual(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.GREATER_EQUAL, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundPredicate notEqual(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.NOT_EQUAL, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundPredicate between(BiFunction<Expression, T, Expression> funcRef, T first,
                                                   SQLs.WordAnd and, T second) {
            return Expressions.betweenPredicate(this, false, null, funcRef.apply(this, first), funcRef.apply(this, second));
        }

        @Override
        public final <T> CompoundPredicate notBetween(BiFunction<Expression, T, Expression> funcRef, T first,
                                                      SQLs.WordAnd and, T second) {
            return Expressions.betweenPredicate(this, true, null, funcRef.apply(this, first), funcRef.apply(this, second));
        }

        @Override
        public final <T> CompoundPredicate between(@Nullable SQLsSyntax.BetweenModifier modifier,
                                                   BiFunction<Expression, T, Expression> funcRef, T first,
                                                   SQLsSyntax.WordAnd and, T second) {
            return Expressions.betweenPredicate(this, false, modifier, funcRef.apply(this, first), funcRef.apply(this, second));
        }

        @Override
        public final <T> CompoundPredicate notBetween(@Nullable SQLsSyntax.BetweenModifier modifier,
                                                      BiFunction<Expression, T, Expression> funcRef, T first,
                                                      SQLsSyntax.WordAnd and, T second) {
            return Expressions.betweenPredicate(this, true, modifier, funcRef.apply(this, first), funcRef.apply(this, second));
        }

        @Override
        public final <T> CompoundPredicate is(SQLsSyntax.IsComparisonWord operator,
                                              BiFunction<Expression, T, Expression> funcRef, @Nullable T value) {
            return Expressions.isComparisonPredicate(this, false, operator, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundPredicate isNot(SQLsSyntax.IsComparisonWord operator,
                                                 BiFunction<Expression, T, Expression> funcRef, @Nullable T value) {
            return Expressions.isComparisonPredicate(this, true, operator, funcRef.apply(this, value));
        }

        @Override
        public final <T extends Collection<?>> CompoundPredicate in(BiFunction<Expression, T, Expression> funcRef,
                                                                    T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.IN, funcRef.apply(this, value));
        }

        @Override
        public final <T extends Collection<?>> CompoundPredicate notIn(BiFunction<Expression, T, Expression> funcRef,
                                                                       T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.NOT_IN, funcRef.apply(this, value));
        }

        @Override
        public final CompoundPredicate in(TeNamedOperator<Expression> funcRef, String paramName, int size) {
            return Expressions.dualPredicate(this, DualBooleanOperator.IN, funcRef.apply(this, paramName, size));
        }

        @Override
        public final CompoundPredicate notIn(TeNamedOperator<Expression> funcRef, String paramName, int size) {
            return Expressions.dualPredicate(this, DualBooleanOperator.NOT_IN, funcRef.apply(this, paramName, size));
        }

        @Override
        public final <T> CompoundPredicate like(BiFunction<MappingType, T, Expression> funcRef, T value) {
            return Expressions.likePredicate(this, DualBooleanOperator.LIKE, funcRef.apply(NoCastTextType.INSTANCE, value),
                    SQLs.ESCAPE, null);
        }


        @Override
        public final <T> CompoundPredicate like(BiFunction<MappingType, T, Expression> funcRef, T value,
                                                SqlSyntax.WordEscape escape, char escapeChar) {
            return Expressions.likePredicate(this, DualBooleanOperator.LIKE, funcRef.apply(NoCastTextType.INSTANCE, value),
                    escape, SQLs.literal(NoCastTextType.INSTANCE, escapeChar)
            );
        }


        @Override
        public final <T> CompoundPredicate notLike(BiFunction<MappingType, T, Expression> funcRef, T value) {
            return Expressions.likePredicate(this, DualBooleanOperator.NOT_LIKE, funcRef.apply(NoCastTextType.INSTANCE, value),
                    SQLs.ESCAPE, null);
        }

        @Override
        public final <T> CompoundPredicate notLike(BiFunction<MappingType, T, Expression> funcRef, T value,
                                                   SqlSyntax.WordEscape escape, char escapeChar) {
            return Expressions.likePredicate(this, DualBooleanOperator.NOT_LIKE, funcRef.apply(NoCastTextType.INSTANCE, value),
                    escape, SQLs.literal(NoCastTextType.INSTANCE, escapeChar)
            );
        }

        @Override
        public final <T> CompoundExpression mod(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.MOD, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundExpression times(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.TIMES, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundExpression plus(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.PLUS, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundExpression minus(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.MINUS, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundExpression divide(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.DIVIDE, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundExpression bitwiseAnd(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.BITWISE_AND, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundExpression bitwiseOr(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.BITWISE_OR, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundExpression bitwiseXor(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.BITWISE_XOR, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundExpression rightShift(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.RIGHT_SHIFT, funcRef.apply(this, value));
        }

        @Override
        public final <T> CompoundExpression leftShift(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.LEFT_SHIFT, funcRef.apply(this, value));
        }


        @Override
        public final <T> CompoundExpression apply(BiFunction<Expression, Expression, CompoundExpression> operator,
                                                  BiFunction<Expression, T, Expression> funcRef, T value) {
            final CompoundExpression result;
            result = operator.apply(this, funcRef.apply(this, value));
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return result;
        }

        @Override
        public final <M extends SQLWords, T> CompoundExpression apply(
                OptionalClauseOperator<M, Expression, CompoundExpression> operator, BiFunction<Expression, T, Expression> funcRef,
                T value, M modifier, Expression optionalExp) {
            final CompoundExpression result;
            result = operator.apply(this, funcRef.apply(this, value), modifier, optionalExp);
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return result;
        }


        @Override
        public final <M extends SQLWords, T> CompoundExpression apply(
                OptionalClauseOperator<M, Expression, CompoundExpression> operator, BiFunction<Expression, T, Expression> funcRef,
                T value, M modifier, char escapeChar) {
            return this.apply(operator, funcRef, value, modifier, SQLs.literal(NoCastTextType.INSTANCE, escapeChar));
        }

        @Override
        public final <T> CompoundPredicate test(BiFunction<Expression, Expression, CompoundPredicate> operator,
                                                BiFunction<Expression, T, Expression> funcRef, T value) {
            final CompoundPredicate result;
            result = operator.apply(this, funcRef.apply(this, value));
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return result;
        }

        @Override
        public final <M extends SQLWords, T> CompoundPredicate test(
                OptionalClauseOperator<M, Expression, CompoundPredicate> operator,
                BiFunction<MappingType, T, Expression> funcRef, T value, M modifier, Expression optionalExp) {
            final CompoundPredicate result;
            result = operator.apply(this, funcRef.apply(NoCastTextType.INSTANCE, value), modifier, optionalExp);
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return result;
        }

        @Override
        public final <M extends SQLWords, T> CompoundPredicate test(
                OptionalClauseOperator<M, Expression, CompoundPredicate> operator,
                BiFunction<MappingType, T, Expression> funcRef, T value, M modifier, char escapeChar) {
            return this.test(operator, funcRef, value, modifier, SQLs.literal(NoCastTextType.INSTANCE, escapeChar));
        }

        /*-------------------below array operator method -------------------*/

        @Override
        public final SimpleExpression atElement(int index) {
            return Expressions.arrayElementExp(this, index);
        }

        @Override
        public final SimpleExpression atElement(int index1, int index2) {
            return Expressions.arrayElementExp(this, index1, index2);
        }

        @Override
        public final SimpleExpression atElement(int index1, int index2, int index3, int... restIndex) {
            return Expressions.arrayElementExp(this, index1, index2, index3, restIndex);
        }

        @Override
        public final <T> SimpleExpression atElement(BiFunction<MappingType, T, Expression> funcRef, T value) {
            return Expressions.arrayElementExp(this, funcRef.apply(NoCastIntegerType.INSTANCE, value));
        }

        @Override
        public final <T> SimpleExpression atElement(BiFunction<MappingType, T, Expression> funcRef, T value1, T value2) {
            return Expressions.arrayElementExp(this, funcRef.apply(NoCastIntegerType.INSTANCE, value1),
                    funcRef.apply(NoCastIntegerType.INSTANCE, value2)
            );
        }

        @Override
        public final <T> SimpleExpression atElement(BiFunction<MappingType, T, Expression> funcRef, T value1, T value2, T value3) {
            return Expressions.arrayElementExp(this, funcRef.apply(NoCastIntegerType.INSTANCE, value1),
                    funcRef.apply(NoCastIntegerType.INSTANCE, value2),
                    funcRef.apply(NoCastIntegerType.INSTANCE, value3),
                    new Expression[0]
            );
        }

        @Override
        public final <T, U> SimpleExpression atElement(BiFunction<MappingType, T, Expression> funcRef1, T value1, BiFunction<MappingType, U, Expression> funcRef2, U value2) {
            return Expressions.arrayElementExp(this, funcRef1.apply(NoCastIntegerType.INSTANCE, value1),
                    funcRef2.apply(NoCastIntegerType.INSTANCE, value2)
            );
        }

        @Override
        public final <T, U, V> SimpleExpression atElement(BiFunction<MappingType, T, Expression> funcRef1, T value1, BiFunction<MappingType, U, Expression> funcRef2, U value2, BiFunction<MappingType, V, Expression> funcRef3, V value3) {
            return Expressions.arrayElementExp(this, funcRef1.apply(NoCastIntegerType.INSTANCE, value1),
                    funcRef2.apply(NoCastIntegerType.INSTANCE, value2),
                    funcRef3.apply(NoCastIntegerType.INSTANCE, value3),
                    new Expression[0]
            );
        }

        @Override
        public final SimpleExpression atElement(Expression index) {
            return Expressions.arrayElementExp(this, index);
        }

        @Override
        public final SimpleExpression atElement(Expression index1, Expression index2) {
            return Expressions.arrayElementExp(this, index1, index2);
        }

        @Override
        public final SimpleExpression atElement(Expression index1, Expression index2, Expression index3, Expression... restIndex) {
            return Expressions.arrayElementExp(this, index1, index2, index3, restIndex);
        }

        @Override
        public final ArrayExpression atArray(int index) {
            return Expressions.arrayElementArrayExp(this, index);
        }

        @Override
        public final ArrayExpression atArray(int index1, int index2) {
            return Expressions.arrayElementArrayExp(this, index1, index2);
        }

        @Override
        public final ArrayExpression atArray(int index1, int index2, int index3, int... restIndex) {
            return Expressions.arrayElementArrayExp(this, index1, index2, index3, restIndex);
        }

        @Override
        public final ArrayExpression atArray(ArraySubscript index) {
            return Expressions.arrayElementArrayExp(this, index);
        }

        @Override
        public final ArrayExpression atArray(ArraySubscript index1, ArraySubscript index2) {
            return Expressions.arrayElementArrayExp(this, index1, index2);
        }

        @Override
        public final ArrayExpression atArray(ArraySubscript index1, ArraySubscript index2, ArraySubscript index3, ArraySubscript... restIndex) {
            return Expressions.arrayElementArrayExp(this, index1, index2, index3, restIndex);
        }

        @Override
        public final <T> ArrayExpression atArray(BiFunction<MappingType, T, Expression> funcRef, T value) {
            return Expressions.arrayElementArrayExp(this, funcRef.apply(NoCastIntegerType.INSTANCE, value));
        }

        @Override
        public final <T> ArrayExpression atArray(BiFunction<MappingType, T, Expression> funcRef, T value1, T value2) {
            return Expressions.arrayElementArrayExp(this, funcRef.apply(NoCastIntegerType.INSTANCE, value1),
                    funcRef.apply(NoCastIntegerType.INSTANCE, value2)
            );
        }

        @Override
        public final <T> ArrayExpression atArray(BiFunction<MappingType, T, Expression> funcRef, T value1, T value2, T value3) {
            return Expressions.arrayElementArrayExp(this, funcRef.apply(NoCastIntegerType.INSTANCE, value1),
                    funcRef.apply(NoCastIntegerType.INSTANCE, value2),
                    funcRef.apply(NoCastIntegerType.INSTANCE, value3),
                    new ArraySubscript[0]
            );
        }

        @Override
        public final <T, U> ArrayExpression atArray(BiFunction<MappingType, T, Expression> funcRef1, T value1, BiFunction<MappingType, U, Expression> funcRef2, U value2) {
            return Expressions.arrayElementArrayExp(this, funcRef1.apply(NoCastIntegerType.INSTANCE, value1),
                    funcRef2.apply(NoCastIntegerType.INSTANCE, value2)
            );
        }

        @Override
        public final <T, U, V> ArrayExpression atArray(BiFunction<MappingType, T, Expression> funcRef1, T value1, BiFunction<MappingType, U, Expression> funcRef2, U value2, BiFunction<MappingType, V, Expression> funcRef3, V value3) {
            return Expressions.arrayElementArrayExp(this, funcRef1.apply(NoCastIntegerType.INSTANCE, value1),
                    funcRef2.apply(NoCastIntegerType.INSTANCE, value2),
                    funcRef3.apply(NoCastIntegerType.INSTANCE, value3),
                    new ArraySubscript[0]
            );
        }



        /*-------------------below json operator method -------------------*/

        @Override
        public final JsonExpression arrayElement(int index) {
            return Expressions.jsonArrayElement(this, index);
        }

        @Override
        public final JsonExpression objectAttr(String keyName) {
            return Expressions.jsonObjectAttr(this, keyName);
        }

        @Override
        public final JsonExpression atPath(String jsonPath) {
            return Expressions.jsonPathExtract(this, jsonPath);
        }

        @Override
        public final JsonExpression atPath(Expression jsonPath) {
            return Expressions.jsonPathExtract(this, jsonPath);
        }

        @Override
        public final <T> JsonExpression atPath(BiFunction<MappingType, T, Expression> funcRef, T jsonPath) {
            return Expressions.jsonPathExtract(this, funcRef.apply(JsonPathType.INSTANCE, jsonPath));
        }


    }//OperationSimpleExpression


    static abstract class SqlFunctionExpression extends OperationSimpleExpression
            implements SQLFunction, TypeInfer.DelayTypeInfer {

        final String name;

        /**
         * package constructor
         */
        SqlFunctionExpression(String name) {
            this.name = name;
        }

        @Override
        public final String name() {
            return this.name;
        }

        /**
         * @return sql function couldn't return {@link TableField},void to codec {@link TableField}
         */
        @Override
        public abstract MappingType typeMeta();


    }//FunctionExpression


    static abstract class OperationCompoundExpression extends OperationExpression implements CompoundExpression {

        /**
         * package constructor
         */
        OperationCompoundExpression() {
        }

        /**
         * @return compound expression couldn't return {@link TableField},void to codec {@link TableField}
         */
        @Override
        public abstract MappingType typeMeta();


    }//CompoundExpression


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


    static final class BracketsExpression extends OperationExpression implements ArmySimpleExpression {

        private final ArmyExpression expression;

        /**
         * <p>
         * <strong>Private constructor</strong>
         * </p>
         *
         * @see #bracketExp(Expression)
         */
        private BracketsExpression(ArmyExpression expression) {
            this.expression = expression;
        }

        @Override
        public TypeMeta typeMeta() {
            return this.expression.typeMeta();
        }


        @Override
        public void appendSql(final _SqlContext context) {
            final StringBuilder builder = context.sqlBuilder()
                    .append(_Constant.SPACE_LEFT_PAREN);

            this.expression.appendSql(context);

            builder.append(_Constant.SPACE_RIGHT_PAREN);
        }


        @Override
        public int hashCode() {
            return this.expression.hashCode();
        }

        @Override
        public boolean equals(final Object obj) {
            final boolean match;
            if (obj == this) {
                match = true;
            } else if (obj instanceof BracketsExpression) {
                match = ((BracketsExpression) obj).expression.equals(this.expression);
            } else {
                match = false;
            }
            return match;
        }

        @Override
        public String toString() {
            return _StringUtils.builder()
                    .append(_Constant.SPACE_LEFT_PAREN)
                    .append(this.expression)
                    .append(_Constant.SPACE_RIGHT_PAREN)
                    .toString();
        }


    }//BracketsExpression


    /**
     * <p>
     * This class representing sql {@code NULL} key word.
     * </p>
     *
     * @see SQLs#NULL
     */
    private static final class NullWord extends OperationExpression
            implements SqlValueParam.SingleAnonymousValue,
            ArmySimpleExpression,
            SqlSyntax.WordNull,
            SqlSyntax.ArmyKeyWord {

        private static final NullWord INSTANCE = new NullWord();


        private NullWord() {
        }

        @Override
        public String spaceRender() {
            return _Constant.SPACE_NULL;
        }

        @Override
        public void appendSql(_SqlContext context) {
            context.sqlBuilder().append(_Constant.SPACE_NULL);
        }

        @Override
        public TypeMeta typeMeta() {
            return NoCastTextType.INSTANCE;
        }

        @Override
        public Object value() {
            //always null
            return null;
        }

        @Override
        public String toString() {
            return _Constant.SPACE_NULL;
        }


    }// NullWord


}
