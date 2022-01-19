package io.army.criteria.impl;

import io.army.criteria.Expression;
import io.army.criteria.GenericField;
import io.army.criteria.IPredicate;
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
abstract class OperationField<T extends IDomain, E> extends OperationExpression<E> implements GenericField<T, E> {


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
    public final Expression<E> modNamed() {
        return this.mod(SQLs.namedParam(this));
    }

    @Override
    public final Expression<E> plusNamed() {
        return this.plus(SQLs.namedParam(this));
    }

    @Override
    public final Expression<E> minusNamed() {
        return this.minus(SQLs.namedParam(this));
    }

    @Override
    public final Expression<E> multiplyNamed() {
        return this.multiply(SQLs.namedParam(this));
    }

    @Override
    public final Expression<E> divideNamed() {
        return this.divide(SQLs.namedParam(this));
    }

    @Override
    public final Expression<E> bitwiseAndNamed() {
        return this.bitwiseAnd(SQLs.namedParam(this));
    }

    @Override
    public final Expression<E> bitwiseOrNamed() {
        return this.bitwiseOr(SQLs.namedParam(this));
    }

    @Override
    public final Expression<E> xorNamed() {
        return this.xor(SQLs.namedParam(this));
    }

    @Override
    public final Expression<E> rightShiftNamed() {
        return this.rightShift(SQLs.namedParam(this));
    }

    @Override
    public final Expression<E> leftShiftNamed() {
        return this.leftShift(SQLs.namedParam(this));
    }

    @Override
    public final IPredicate likeNamed() {
        return this.like(SQLs.namedParam(this));
    }

    @Override
    public final IPredicate notLikeNamed() {
        return this.notLike(SQLs.namedParam(this));
    }


}
