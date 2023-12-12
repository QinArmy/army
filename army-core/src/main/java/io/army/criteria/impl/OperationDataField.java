package io.army.criteria.impl;

import io.army.criteria.CompoundExpression;
import io.army.criteria.CompoundPredicate;
import io.army.criteria.Expression;
import io.army.criteria.SqlField;
import io.army.criteria.impl.inner._Selection;
import io.army.function.TeNamedOperator;

import java.util.function.BiFunction;

/**
 * <p>
 * This class is a implementation of {@link SqlField},and This class is base class of below:
 * <ul>
 *     <li>{@link TableFieldMeta}</li>
 *     <li>{@link QualifiedFieldImpl}</li>
 *     <li>{@link  CriteriaContexts.ImmutableDerivedField}</li>
 *     <li>{@code   CriteriaContexts.MutableDerivedField}</li>
 * </ul>
 */
abstract class OperationDataField extends OperationExpression.OperationSimpleExpression implements SqlField,
        _Selection {

    @Override
    public final CompoundPredicate equal(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate less(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate lessEqual(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate great(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate greatEqual(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate notEqual(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate like(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate notLike(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundPredicate in(TeNamedOperator<SqlField> namedOperator, int size) {
        return Expressions.inPredicate(this, false, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final CompoundPredicate notIn(TeNamedOperator<SqlField> namedOperator, int size) {
        return Expressions.inPredicate(this, true, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final CompoundExpression mod(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.MOD, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression plus(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.PLUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression minus(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.MINUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression times(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.TIMES, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression divide(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.DIVIDE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression bitwiseAnd(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_AND, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression bitwiseOr(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_OR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression xor(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_XOR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression rightShift(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.RIGHT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final CompoundExpression leftShift(BiFunction<SqlField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.LEFT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }



}
