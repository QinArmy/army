package io.army.criteria;

import io.army.meta.FieldMeta;

/**
 * <p>
 * see {@code io.army.boot.TmSessionImpl}
 * </p>
 */
public interface FieldValueEqualPredicate extends FieldValuePredicate {

    @Override
    FieldMeta<?, ?> fieldExp();
}
