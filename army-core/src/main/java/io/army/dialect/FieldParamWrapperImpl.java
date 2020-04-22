package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingType;

final class FieldParamWrapperImpl implements FieldParamWrapper {

    private final FieldMeta<?, ?> fieldMeta;

    FieldParamWrapperImpl(FieldMeta<?, ?> fieldMeta) {
        this.fieldMeta = fieldMeta;
    }

    public final FieldMeta<?, ?> fieldMeta() {
        return this.fieldMeta;
    }

    @Override
    public final MappingType mappingType() {
        throw new UnsupportedOperationException();
    }

    @Override
    public final Object value() {
        throw new UnsupportedOperationException();
    }
}
