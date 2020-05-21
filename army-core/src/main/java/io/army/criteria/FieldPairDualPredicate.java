package io.army.criteria;

import io.army.meta.FieldExp;

/**
 * <p>
 * left {@link FieldExp} and right {@link FieldExp} has same {@link io.army.meta.TableMeta}.
 * </p>
 * <p>
 * design for child domain update, see {@code io.army.dialect.StandardUpdateContext}
 * </p>
 */
public interface FieldPairDualPredicate extends SpecialPredicate {

    FieldExp<?, ?> left();

    DualOperator operator();

    FieldExp<?, ?> right();

}
