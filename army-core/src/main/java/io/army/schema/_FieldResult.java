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

    static Builder builder() {
        return FieldResultImpl.builder();
    }

    interface Builder {

        Builder field(FieldMeta<?, ?> field);

        Builder sqlType(boolean sqlType);

        Builder defaultExp(boolean defaultExp);

        Builder nullable(boolean nullable);

        void comment(boolean comment);

        boolean hasDifference();

        void clear();

        _FieldResult build();

    }


}
