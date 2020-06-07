package io.army.codec;

import io.army.meta.FieldMeta;

public interface CodecContext {

    int encodeFailCount(FieldMeta<?, ?> fieldMeta, Object keyTag);

    void encodeFailIncrement(FieldMeta<?, ?> fieldMeta, Object keyTag);

    int decodeFailCount(FieldMeta<?, ?> fieldMeta, Object keyTag);

    void decodeFailIncrement(FieldMeta<?, ?> fieldMeta, Object keyTag);

    StatementType statementType();

}
