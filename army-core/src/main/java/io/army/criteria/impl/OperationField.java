package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.IPredicate;
import io.army.criteria.TableField;
import io.army.domain.IDomain;

/**
 * <p>
 * This class is base class of the implementation of below:
 *     <ul>
 *         <li>{@link io.army.meta.FieldMeta}</li>
 *         <li>{@link io.army.criteria.QualifiedField}</li>
 *     </ul>
 * </p>
 */
abstract class OperationField<T extends IDomain> extends OperationExpression implements TableField<T> {


    @Override
    public final IPredicate equalNamed() {
        return this.equal(SQLs.namedParam(this));
    }

    @Override
    public final IPredicate lessThanNamed() {
        return this.lessThan(SQLs.namedParam(this));
    }

    @Override
    public final IPredicate lessEqualNamed() {
        return this.lessEqual(SQLs.namedParam(this));
    }

    @Override
    public final IPredicate greatThanNamed() {
        return this.greatThan(SQLs.namedParam(this));
    }

    @Override
    public final IPredicate greatEqualNamed() {
        return this.greatEqual(SQLs.namedParam(this));
    }

    @Override
    public final IPredicate notEqualNamed() {
        return this.notEqual(SQLs.namedParam(this));
    }

    @Override
    public final IPredicate likeNamed() {
        return this.like(SQLs.namedParam(this));
    }

    @Override
    public final IPredicate notLikeNamed() {
        return this.notLike(SQLs.namedParam(this));
    }

    @Override
    public final IPredicate inNamed() {
        return DualPredicate.create(this, DualOperator.IN, SQLs.namedParam(this));
    }

    @Override
    public final IPredicate notInNamed() {
        return DualPredicate.create(this, DualOperator.NOT_IN, SQLs.namedParam(this));
    }

    @Override
    public final Expression modNamed() {
        return this.mod(SQLs.namedParam(this));
    }

    @Override
    public final Expression plusNamed() {
        return this.plus(SQLs.namedParam(this));
    }

    @Override
    public final Expression minusNamed() {
        return this.minus(SQLs.namedParam(this));
    }

    @Override
    public final Expression multiplyNamed() {
        return this.multiply(SQLs.namedParam(this));
    }

    @Override
    public final Expression divideNamed() {
        return this.divide(SQLs.namedParam(this));
    }

    @Override
    public final Expression bitwiseAndNamed() {
        return this.bitwiseAnd(SQLs.namedParam(this));
    }

    @Override
    public final Expression bitwiseOrNamed() {
        return this.bitwiseOr(SQLs.namedParam(this));
    }

    @Override
    public final Expression xorNamed() {
        return this.xor(SQLs.namedParam(this));
    }

    @Override
    public final Expression rightShiftNamed() {
        return this.rightShift(SQLs.namedParam(this));
    }

    @Override
    public final Expression leftShiftNamed() {
        return this.leftShift(SQLs.namedParam(this));
    }


}
