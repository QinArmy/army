package io.army.criteria;

import io.army.meta.FieldExp;

/**
 * <p>
 * left {@link FieldExp} and right {@link FieldExp} has same {@link io.army.meta.TableMeta}
 * </p>
 */
public interface FieldPairDualPredicate extends IPredicate {

    FieldExp<?, ?> left();

    DualOperator operator();

    FieldExp<?, ?> right();

}
