package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.criteria.standard.SQLFunction;
import io.army.function.OptionalClauseOperator;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;

import java.util.function.BiFunction;

/**
 * <p>
 * This class representing non-operation expression
 * </p>
 */
abstract class NonOperationExpression implements ArmyExpression {


    NonOperationExpression() {
    }


    @Override
    public final boolean isNullValue() {
        final boolean nullable;
        if (this instanceof SqlValueParam.SingleAnonymousValue) {
            nullable = ((SqlValueParam.SingleAnonymousValue) this).value() == null;
        } else {
            nullable = false;
        }
        return nullable;
    }

    @Override
    public final CompoundPredicate equal(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate equalAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate equalSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate less(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate lessAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate lessSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate lessAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate lessEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate lessEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate lessEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate lessEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greater(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate greaterAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greaterSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greaterAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greaterEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate greaterEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greaterEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate greaterEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate notEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate between(Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notBetween(Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate between(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate is(SQLsSyntax.BooleanTestWord operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate isNot(SQLsSyntax.BooleanTestWord operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate is(SQLsSyntax.IsComparisonWord operator, Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate isNot(SQLsSyntax.IsComparisonWord operator, Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate isNull() {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate isNotNull() {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate in(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate in(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notIn(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notIn(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate like(Expression pattern) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate like(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate like(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notLike(Expression pattern) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundPredicate notLike(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate notLike(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression mod(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression times(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression plus(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression minus(Expression minuend) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression divide(Expression divisor) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression bitwiseAnd(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundExpression bitwiseOr(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundExpression bitwiseXor(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundExpression rightShift(Expression bitNumber) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundExpression leftShift(Expression bitNumber) {
        throw unsupportedOperation(this);
    }


    @Override
    public final CompoundExpression apply(BiFunction<Expression, Expression, CompoundExpression> operator, Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final <M extends SQLWords> CompoundExpression apply(OptionalClauseOperator<M, Expression, CompoundExpression> operator, Expression right, M modifier, Expression optionalExp) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords> CompoundExpression apply(OptionalClauseOperator<M, Expression, CompoundExpression> operator, Expression right, M modifier, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final CompoundPredicate test(BiFunction<Expression, Expression, CompoundPredicate> operator, Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords> CompoundPredicate test(OptionalClauseOperator<M, Expression, CompoundPredicate> operator, Expression right, M modifier, Expression optionalExp) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords> CompoundPredicate test(OptionalClauseOperator<M, Expression, CompoundPredicate> operator, Expression right, M modifier, char escapeChar) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationExpression mapTo(TypeMeta typeMeta) {
        throw unsupportedOperation(this);
    }


    @Override
    public final Selection as(String selectionAlas) {
        throw unsupportedOperation(this);
    }

    @Override
    public final SortItem asSortItem() {
        throw unsupportedOperation(this);
    }

    @Override
    public final SortItem asc() {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SQLs.ASC, null);
    }

    @Override
    public final SortItem desc() {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SQLs.DESC, null);
    }

    @Override
    public final SortItem ascSpace(@Nullable Statement.NullsFirstLast firstLast) {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SQLs.ASC, firstLast);
    }

    @Override
    public final SortItem descSpace(@Nullable Statement.NullsFirstLast firstLast) {
        if (!(this instanceof CriteriaContexts.SelectionReference)) {
            throw unsupportedOperation(this);
        }
        return ArmySortItems.create(this, SQLs.DESC, firstLast);
    }

    String operationErrorMessage() {
        return String.format("%s don't support any operation.", this.getClass().getName());
    }


    static CriteriaException unsupportedOperation(NonOperationExpression expression) {
        String m;
        if (expression instanceof MultiValueExpression) {
            m = String.format("%s support only IN(NOT IN) operator.", expression.getClass().getName());
        } else {
            m = expression.operationErrorMessage();
        }
        return ContextStack.clearStackAndCriteriaError(m);
    }

    static RuntimeException nonOperationExpression(final @Nullable Expression expression) {
        final RuntimeException e;
        if (expression == null) {
            e = ContextStack.clearStackAndNullPointer();
        } else if (expression instanceof NonOperationExpression) {
            e = unsupportedOperation((NonOperationExpression) expression);
        } else {
            String m = String.format("%s isn't army expression", expression.getClass().getName());
            e = ContextStack.clearStackAndCriteriaError(m);
        }
        return e;
    }


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
    static abstract class MultiValueExpression extends NonOperationExpression
            implements SqlValueParam.MultiValue, FunctionArg {


    }//MultiValueExpression

    static abstract class NonOperationFunction extends NonOperationExpression implements SQLFunction {

        final String name;

        NonOperationFunction(String name) {
            this.name = name;
        }

        @Override
        public final String name() {
            return this.name;
        }

    }//NonOperationFunction



}
