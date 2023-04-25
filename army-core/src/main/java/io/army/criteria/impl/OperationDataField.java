package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.Expression;
import io.army.criteria.impl.inner._Selection;
import io.army.function.TeNamedOperator;

import java.util.function.BiFunction;

/**
 * <p>
 * This class is a implementation of {@link DataField},and This class is base class of below:
 *     <ul>
 *         <li>{@link TableFieldMeta}</li>
 *         <li>{@link QualifiedFieldImpl}</li>
 *         <li>{@link  CriteriaContexts.ImmutableDerivedField}</li>
 *         <li>{@link  CriteriaContexts.MutableDerivedField}</li>
 *     </ul>
 * </p>
 */
abstract class OperationDataField extends OperationExpression.OperationSimpleExpression implements DataField,
        _Selection {

    @Override
    public final OperationPredicate equal(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate less(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate lessEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LESS_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate great(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate greatEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.GREATER_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate notEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate like(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate notLike(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate in(TeNamedOperator<DataField> namedOperator, int size) {
        return Expressions.dualPredicate(this, DualBooleanOperator.IN, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final OperationPredicate notIn(TeNamedOperator<DataField> namedOperator, int size) {
        return Expressions.dualPredicate(this, DualBooleanOperator.NOT_IN, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final OperationExpression mod(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.MOD, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression plus(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.PLUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression minus(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.MINUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression times(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.TIMES, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression divide(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.DIVIDE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression bitwiseAnd(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_AND, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression bitwiseOr(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_OR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression xor(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.BITWISE_XOR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression rightShift(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.RIGHT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression leftShift(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualExpOperator.LEFT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }



}
