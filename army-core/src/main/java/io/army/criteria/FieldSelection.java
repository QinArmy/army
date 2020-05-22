package io.army.criteria;

import io.army.meta.FieldMeta;

/**
 * see {@code io.army.criteria.impl.FieldSelectionImpl}
 *
 * @see FieldMeta
 * @see LogicalField
 */
public interface FieldSelection extends Selection {

    FieldMeta<?, ?> fieldMeta();

}
