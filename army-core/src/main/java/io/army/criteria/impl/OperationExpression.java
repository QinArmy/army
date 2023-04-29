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
import io.army.meta.TypeMeta;
import io.army.util._StringUtils;

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
        return this instanceof SqlValueParam.SingleAnonymousValue
                && ((SqlValueParam.SingleAnonymousValue) this).value() == null;
    }

    @Override
    public final OperationPredicate equal(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.EQUAL, operand);
    }


    @Override
    public final OperationPredicate equalAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final OperationPredicate equalSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate less(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS, operand);
    }


    @Override
    public final OperationPredicate lessAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate lessSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, QueryOperator.SOME, subQuery);
    }

    @Override
    public final OperationPredicate lessAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate lessEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS_EQUAL, operand);
    }


    @Override
    public final OperationPredicate lessEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate lessEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate lessEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.LESS_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate greater(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER, operand);
    }


    @Override
    public final OperationPredicate greaterAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate greaterSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate greaterAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate greaterEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER_EQUAL, operand);
    }


    @Override
    public final OperationPredicate greaterEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, QueryOperator.ANY, subQuery);
    }

    @Override
    public final OperationPredicate greaterEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, QueryOperator.SOME, subQuery);
    }


    @Override
    public final OperationPredicate greaterEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.GREATER_EQUAL, QueryOperator.ALL, subQuery);
    }


    @Override
    public final OperationPredicate notEqual(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_EQUAL, operand);
    }


    @Override
    public final OperationPredicate notEqualAny(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, QueryOperator.ANY, subQuery);
    }


    @Override
    public final OperationPredicate notEqualSome(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, QueryOperator.SOME, subQuery);
    }

    @Override
    public final OperationPredicate notEqualAll(SubQuery subQuery) {
        return Expressions.compareQueryPredicate(this, DualBooleanOperator.NOT_EQUAL, QueryOperator.ALL, subQuery);
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
        return Expressions.dualPredicate(this, DualBooleanOperator.IN, operand);
    }

    @Override
    public final OperationPredicate in(SubQuery subQuery) {
        return Expressions.inOperator(this, DualBooleanOperator.IN, subQuery);
    }

    @Override
    public final OperationPredicate notIn(Expression operand) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_IN, operand);
    }

    @Override
    public final OperationPredicate notIn(SubQuery subQuery) {
        return Expressions.inOperator(this, DualBooleanOperator.NOT_IN, subQuery);
    }

    @Override
    public final OperationPredicate like(Expression pattern) {
        return Expressions.likePredicate(this, DualBooleanOperator.LIKE, pattern, SQLs.ESCAPE, null);
    }


    @Override
    public final IPredicate like(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        return Expressions.likePredicate(this, DualBooleanOperator.LIKE, pattern,
                escape, SQLs.literal(StringType.INSTANCE, escapeChar)
        );
    }

    @Override
    public final IPredicate like(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(this, DualBooleanOperator.LIKE, pattern, escape, escapeChar);
    }


    @Override
    public final OperationPredicate notLike(Expression pattern) {
        return Expressions.likePredicate(this, DualBooleanOperator.NOT_LIKE, pattern, SQLs.ESCAPE, null);
    }


    @Override
    public final IPredicate notLike(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        return Expressions.likePredicate(this, DualBooleanOperator.NOT_LIKE, pattern,
                escape, SQLs.literal(StringType.INSTANCE, escapeChar)
        );
    }

    @Override
    public final IPredicate notLike(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        return Expressions.likePredicate(this, DualBooleanOperator.NOT_LIKE, pattern, escape, escapeChar);
    }

    @Override
    public final OperationExpression mod(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.MOD, operand);
    }


    @Override
    public final OperationExpression times(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.TIMES, operand);
    }


    @Override
    public final OperationExpression plus(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.PLUS, operand);
    }


    @Override
    public final OperationExpression minus(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.MINUS, operand);
    }


    @Override
    public final OperationExpression divide(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.DIVIDE, operand);
    }


    @Override
    public final OperationExpression bitwiseAnd(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_AND, operand);
    }


    @Override
    public final OperationExpression bitwiseOr(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_OR, operand);
    }

    @Override
    public final OperationExpression bitwiseXor(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_XOR, operand);
    }


    @Override
    public final OperationExpression rightShift(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.RIGHT_SHIFT, operand);
    }

    @Override
    public final OperationExpression leftShift(Expression operand) {
        return Expressions.dualExp(this, DualExpOperator.LEFT_SHIFT, operand);
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
            implements SimpleExpression, ArmySimpleExpression {

        /**
         * package constructor
         */
        OperationSimpleExpression() {

        }

        @Override
        public final <T> OperationPredicate equal(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.EQUAL, funcRef.apply(this, value));
        }


        @Override
        public final <T> OperationPredicate less(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.LESS, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationPredicate lessEqual(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.LESS_EQUAL, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationPredicate greater(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.GREATER, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationPredicate greaterEqual(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.GREATER_EQUAL, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationPredicate notEqual(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.NOT_EQUAL, funcRef.apply(this, value));
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
            return Expressions.dualPredicate(this, DualBooleanOperator.IN, funcRef.apply(this, value));
        }

        @Override
        public final <T extends Collection<?>> OperationPredicate notIn(BiFunction<Expression, T, Expression> funcRef,
                                                                        T value) {
            return Expressions.dualPredicate(this, DualBooleanOperator.NOT_IN, funcRef.apply(this, value));
        }

        @Override
        public final OperationPredicate in(TeNamedOperator<Expression> funcRef, String paramName, int size) {
            return Expressions.dualPredicate(this, DualBooleanOperator.IN, funcRef.apply(this, paramName, size));
        }

        @Override
        public final OperationPredicate notIn(TeNamedOperator<Expression> funcRef, String paramName, int size) {
            return Expressions.dualPredicate(this, DualBooleanOperator.NOT_IN, funcRef.apply(this, paramName, size));
        }

        @Override
        public final <T> OperationPredicate like(BiFunction<MappingType, T, Expression> funcRef, T value) {
            return Expressions.likePredicate(this, DualBooleanOperator.LIKE, funcRef.apply(TextType.INSTANCE, value),
                    SQLs.ESCAPE, null);
        }


        @Override
        public final <T> IPredicate like(BiFunction<MappingType, T, Expression> funcRef, T value,
                                         SqlSyntax.WordEscape escape, char escapeChar) {
            return Expressions.likePredicate(this, DualBooleanOperator.LIKE, funcRef.apply(TextType.INSTANCE, value),
                    escape, SQLs.literal(CharacterType.INSTANCE, escapeChar)
            );
        }


        @Override
        public final <T> OperationPredicate notLike(BiFunction<MappingType, T, Expression> funcRef, T value) {
            return Expressions.likePredicate(this, DualBooleanOperator.NOT_LIKE, funcRef.apply(TextType.INSTANCE, value),
                    SQLs.ESCAPE, null);
        }

        @Override
        public final <T> IPredicate notLike(BiFunction<MappingType, T, Expression> funcRef, T value,
                                            SqlSyntax.WordEscape escape, char escapeChar) {
            return Expressions.likePredicate(this, DualBooleanOperator.NOT_LIKE, funcRef.apply(TextType.INSTANCE, value),
                    escape, SQLs.literal(CharacterType.INSTANCE, escapeChar)
            );
        }

        @Override
        public final <T> OperationExpression mod(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.MOD, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression times(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.TIMES, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression plus(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.PLUS, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression minus(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.MINUS, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression divide(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.DIVIDE, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression bitwiseAnd(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.BITWISE_AND, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression bitwiseOr(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.BITWISE_OR, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression bitwiseXor(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.BITWISE_XOR, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression rightShift(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.RIGHT_SHIFT, funcRef.apply(this, value));
        }

        @Override
        public final <T> OperationExpression leftShift(BiFunction<Expression, T, Expression> funcRef, T value) {
            return Expressions.dualExp(this, DualExpOperator.LEFT_SHIFT, funcRef.apply(this, value));
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
            return this.apply(operator, funcRef, value, modifier, SQLs.literal(CharacterType.INSTANCE, escapeChar));
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
            result = operator.apply(this, funcRef.apply(TextType.INSTANCE, value), modifier, optionalExp);
            if (result == null) {
                throw ContextStack.clearStackAndNullPointer();
            }
            return result;
        }

        @Override
        public final <M extends SQLWords, T> IPredicate test(
                OptionalClauseOperator<M, Expression, IPredicate> operator,
                BiFunction<MappingType, T, Expression> funcRef, T value, M modifier, char escapeChar) {
            return this.test(operator, funcRef, value, modifier, SQLs.literal(CharacterType.INSTANCE, escapeChar));
        }


    }//OperationSimpleExpression


    static abstract class SqlFunctionExpression extends OperationSimpleExpression
            implements SQLFunction, TypeInfer.DelayTypeInfer {

        /**
         * package constructor
         */
        SqlFunctionExpression() {
        }

        /**
         * @return sql function couldn't return {@link TableField},void to codec {@link TableField}
         */
        @Override
        public abstract MappingType typeMeta();


    }//FunctionExpression


    static abstract class CompoundExpression extends OperationExpression {

        /**
         * package constructor
         */
        CompoundExpression() {
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
            return StringType.INSTANCE;
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
