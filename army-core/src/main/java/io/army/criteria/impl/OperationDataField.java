package io.army.criteria.impl;

import io.army.criteria.DataField;
import io.army.criteria.Expression;
import io.army.criteria.Item;
import io.army.criteria.TypeInfer;
import io.army.criteria.impl.inner._Selection;
import io.army.function.TeNamedOperator;

import java.util.function.BiFunction;
import java.util.function.Function;

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
abstract class OperationDataField<I extends Item> extends OperationExpression<I> implements DataField, _Selection {

    OperationDataField(Function<TypeInfer, I> function) {
        super(function);
    }


    @Override
    public final OperationPredicate<I> equal(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualOperator.EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate<I> less(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualOperator.LESS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate<I> lessEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualOperator.LESS_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate<I> great(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualOperator.GREAT, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate<I> greatEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualOperator.GREAT_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate<I> notEqual(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualOperator.NOT_EQUAL, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate<I> like(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualOperator.LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate<I> notLike(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualPredicate(this, DualOperator.NOT_LIKE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationPredicate<I> in(TeNamedOperator<DataField> namedOperator, int size) {
        return Expressions.dualPredicate(this, DualOperator.IN, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final OperationPredicate<I> notIn(TeNamedOperator<DataField> namedOperator, int size) {
        return Expressions.dualPredicate(this, DualOperator.NOT_IN, namedOperator.apply(this, this.fieldName(), size));
    }

    @Override
    public final OperationExpression<I> mod(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualOperator.MOD, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression<I> plus(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualOperator.PLUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression<I> minus(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualOperator.MINUS, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression<I> times(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualOperator.TIMES, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression<I> divide(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualOperator.DIVIDE, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression<I> bitwiseAnd(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualOperator.BITWISE_AND, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression<I> bitwiseOr(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualOperator.BITWISE_OR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression<I> xor(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualOperator.XOR, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression<I> rightShift(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualOperator.RIGHT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationExpression<I> leftShift(BiFunction<DataField, String, Expression> namedOperator) {
        return Expressions.dualExp(this, DualOperator.LEFT_SHIFT, namedOperator.apply(this, this.fieldName()));
    }

    @Override
    public final OperationDataField<I> bracket() {
        //return this ,don't create new instance
        return this;
    }


}
