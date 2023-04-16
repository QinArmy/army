package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.dialect.SubQuery;
import io.army.function.OptionalClauseOperator;
import io.army.function.TeNamedOperator;
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
        if (this instanceof SqlValueParam.SingleNonNamedValue) {
            nullable = ((SqlValueParam.SingleNonNamedValue) this).value() == null;
        } else {
            nullable = false;
        }
        return nullable;
    }

    @Override
    public final IPredicate equal(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate equalAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate equalSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate less(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate lessAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate lessSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate lessAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate lessEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate lessEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate lessEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate lessEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate great(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate greatAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate greatSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate greatAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate greatEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate greatEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate greatEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate greatEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notEqual(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate notEqualAny(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notEqualSome(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notEqualAll(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate between(Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notBetween(Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }


    @Override
    public final IPredicate between(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }


    @Override
    public final IPredicate notBetween(@Nullable SQLs.BetweenModifier modifier, Expression first, SQLs.WordAnd and, Expression second) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate is(SQLsSyntax.BooleanTestWord operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate isNot(SQLsSyntax.BooleanTestWord operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final IPredicate is(SQLsSyntax.IsComparisonWord operator, Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final IPredicate isNot(SQLsSyntax.IsComparisonWord operator, Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate isNull() {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate isNotNull() {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate in(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate in(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate in(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notIn(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notIn(SubQuery subQuery) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationPredicate notIn(TeNamedOperator<Expression> namedOperator, String paramName, int size) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate like(Expression pattern) {
        throw unsupportedOperation(this);
    }


    @Override
    public final IPredicate like(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final IPredicate like(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationPredicate notLike(Expression pattern) {
        throw unsupportedOperation(this);
    }


    @Override
    public final IPredicate notLike(Expression pattern, SQLs.WordEscape escape, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final IPredicate notLike(Expression pattern, SQLs.WordEscape escape, Expression escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression mod(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression times(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression plus(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression minus(Expression minuend) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression divide(Expression divisor) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression bitwiseAnd(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationExpression bitwiseOr(Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final OperationExpression bitwiseXor(Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationExpression rightShift(Expression bitNumber) {
        throw unsupportedOperation(this);
    }


    @Override
    public final OperationExpression leftShift(Expression bitNumber) {
        throw unsupportedOperation(this);
    }


    @Override
    public final Expression apply(BiFunction<Expression, Expression, Expression> operator, Expression operand) {
        throw unsupportedOperation(this);
    }


    @Override
    public final <M extends SQLWords> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, Expression right, M modifier, Expression optionalExp) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords> Expression apply(OptionalClauseOperator<M, Expression, Expression> operator, Expression right, M modifier, char escapeChar) {
        throw unsupportedOperation(this);
    }

    @Override
    public final IPredicate test(BiFunction<Expression, Expression, IPredicate> operator, Expression operand) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, Expression right, M modifier, Expression optionalExp) {
        throw unsupportedOperation(this);
    }

    @Override
    public final <M extends SQLWords> IPredicate test(OptionalClauseOperator<M, Expression, IPredicate> operator, Expression right, M modifier, char escapeChar) {
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
        throw unsupportedOperation(this);
    }

    @Override
    public final SortItem desc() {
        throw unsupportedOperation(this);
    }

    @Override
    public final SortItem ascSpace(@Nullable Statement.NullsFirstLast firstLast) {
        throw unsupportedOperation(this);
    }

    @Override
    public final SortItem descSpace(@Nullable Statement.NullsFirstLast firstLast) {
        throw unsupportedOperation(this);
    }

    static abstract class NonSelectionExpression extends NonOperationExpression {

        NonSelectionExpression() {
        }

    }//NonSelectionExpression


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
    static abstract class MultiValueExpression extends NonSelectionExpression
            implements SqlValueParam.MultiValue, FunctionArg {

        final TypeMeta type;

        /**
         * package constructor
         */
        MultiValueExpression(final TypeMeta type) {
            if (type instanceof QualifiedField) {
                this.type = ((QualifiedField<?>) type).fieldMeta();
            } else {
                this.type = type;
            }
        }


    }//MultiValueExpression

    static CriteriaException unsupportedOperation(NonOperationExpression expression) {
        String m;
        if (expression instanceof MultiValueExpression) {
            m = String.format("%s support only IN(NOT IN) operator.", expression.getClass().getName());
        } else {
            m = String.format("%s don't support any operation.", expression.getClass().getName());
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

}
