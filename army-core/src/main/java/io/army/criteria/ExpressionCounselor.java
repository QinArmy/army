package io.army.criteria;

import io.army.meta.FieldMeta;
import io.army.meta.TableMeta;

import java.util.Collection;

/**
 * design for {@link SpecialExpression} and {@link SpecialPredicate}
 * <p>
 * this interface is implemented by implementation of {@link Expression}
 * </p>
 *
 * @see SpecialExpression
 * @see SpecialPredicate
 */
public interface ExpressionCounselor {

    boolean containsField(Collection<FieldMeta<?, ?>> fieldMetas);

    boolean containsFieldOf(TableMeta<?> tableMeta);

    int containsFieldCount(TableMeta<?> tableMeta);

    boolean containsSubQuery();
}
