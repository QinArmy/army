package io.army.wrapper;

import io.army.meta.FieldMeta;

final class FieldParamWrapperImpl implements FieldParamWrapper {

    private final FieldMeta<?, ?> fieldMeta;

    FieldParamWrapperImpl(FieldMeta<?, ?> fieldMeta) {
        this.fieldMeta = fieldMeta;
    }


    @Override
    public final FieldMeta<?, ?> paramMeta() {
        return this.fieldMeta;
    }

    @Override
    public final Object value() {
        throw new UnsupportedOperationException();
    }
}
