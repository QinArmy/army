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
 *         <li>{@link  CriteriaContexts.DerivedSelection}</li>
 *         <li>{@link  CriteriaContexts.RefDerivedField}</li>
 *     </ul>
 * </p>
 */
abstract class OperationDataField extends OperationExpression.OperationSimpleExpression implements DataField,
        _Selection {

    @Override
    public final OperationPredicate equal(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, BooleanDualOperator.EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate less(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, BooleanDualOperator.LESS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate lessEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, BooleanDualOperator.LESS_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate great(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, BooleanDualOperator.GREAT, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate greatEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, BooleanDualOperator.GREAT_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate notEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, BooleanDualOperator.NOT_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate like(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, BooleanDualOperator.LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate notLike(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, BooleanDualOperator.NOT_LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate in(TeNamedOperator<DataField> namedOperator, int size) {
        return Expressions.dualPredicate(this, BooleanDualOperator.IN, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final OperationPredicate notIn(TeNamedOperator<DataField> namedOperator, int size) {
        return Expressions.dualPredicate(this, BooleanDualOperator.NOT_IN, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final OperationExpression mod(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, ExpDualOperator.MOD, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression plus(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, ExpDualOperator.PLUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression minus(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, ExpDualOperator.MINUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression times(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, ExpDualOperator.TIMES, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression divide(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, ExpDualOperator.DIVIDE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression bitwiseAnd(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, ExpDualOperator.BITWISE_AND, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression bitwiseOr(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, ExpDualOperator.BITWISE_OR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression xor(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, ExpDualOperator.BITWISE_XOR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression rightShift(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, ExpDualOperator.RIGHT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression leftShift(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, ExpDualOperator.LEFT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }



}
