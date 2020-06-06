package io.army.codec;

import io.army.meta.FieldMeta;

public interface CodecContext {

    int failCount(FieldMeta<?, ?> fieldMeta, Object keyTag);

    void failIncrement(FieldMeta<?, ?> fieldMeta, Object keyTag);

}
