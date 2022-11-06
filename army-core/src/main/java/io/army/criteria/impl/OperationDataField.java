package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.function.TeNamedOperator;

import java.util.function.BiFunction;


abstract class OperationDataField extends OperationExpression implements DataField {

    OperationDataField() {
    }


    @Override
    public final IPredicate equal(BiFunction<DataField, String, Expression> namedOperator) {
        return DualPredicate.create(this, DualOperator.EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final IPredicate less(BiFunction<DataField, String, Expression> namedOperator) {
        return DualPredicate.create(this, DualOperator.LESS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final IPredicate lessEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return DualPredicate.create(this, DualOperator.LESS_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final IPredicate great(BiFunction<DataField, String, Expression> namedOperator) {
        return DualPredicate.create(this, DualOperator.GREAT, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final IPredicate greatEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return DualPredicate.create(this, DualOperator.GREAT_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final IPredicate notEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return DualPredicate.create(this, DualOperator.NOT_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final IPredicate like(BiFunction<DataField, String, Expression> namedOperator) {
        return DualPredicate.create(this, DualOperator.LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final IPredicate notLike(BiFunction<DataField, String, Expression> namedOperator) {
        return DualPredicate.create(this, DualOperator.NOT_LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final IPredicate in(TeNamedOperator<DataField, String, Integer> namedOperator, int size) {
        return DualPredicate.create(this, DualOperator.IN, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final IPredicate notIn(TeNamedOperator<DataField, String, Integer> namedOperator, int size) {
        return DualPredicate.create(this, DualOperator.NOT_IN, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final Expression mod(BiFunction<DataField, String, Expression> namedOperator) {
        return DualExpression.create(this, DualOperator.MOD, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final Expression plus(BiFunction<DataField, String, Expression> namedOperator) {
        return DualExpression.create(this, DualOperator.PLUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final Expression minus(BiFunction<DataField, String, Expression> namedOperator) {
        return DualExpression.create(this, DualOperator.MINUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final Expression times(BiFunction<DataField, String, Expression> namedOperator) {
        return DualExpression.create(this, DualOperator.TIMES, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final Expression divide(BiFunction<DataField, String, Expression> namedOperator) {
        return DualExpression.create(this, DualOperator.DIVIDE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final Expression bitwiseAnd(BiFunction<DataField, String, Expression> namedOperator) {
        return DualExpression.create(this, DualOperator.BITWISE_AND, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final Expression bitwiseOr(BiFunction<DataField, String, Expression> namedOperator) {
        return DualExpression.create(this, DualOperator.BITWISE_OR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final Expression xor(BiFunction<DataField, String, Expression> namedOperator) {
        return DualExpression.create(this, DualOperator.XOR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final Expression rightShift(BiFunction<DataField, String, Expression> namedOperator) {
        return DualExpression.create(this, DualOperator.RIGHT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final Expression leftShift(BiFunction<DataField, String, Expression> namedOperator) {
        return DualExpression.create(this, DualOperator.LEFT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }


}
