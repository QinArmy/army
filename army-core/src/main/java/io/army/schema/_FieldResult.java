package io.army.schema;

import io.army.meta.FieldMeta;

/**
 * @see _TableResult
 */
public interface _FieldResult {

    FieldMeta<?, ?> field();

    boolean sqlType();

    boolean defaultValue();

    boolean nullable();

    boolean comment();

}
