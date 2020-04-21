package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingType;

public final class FieldParamWrapper implements ParamWrapper {

    static FieldParamWrapper build(FieldMeta<?, ?> fieldMeta) {
        return new FieldParamWrapper(fieldMeta);
    }

    private final FieldMeta<?, ?> fieldMeta;

    private FieldParamWrapper(FieldMeta<?, ?> fieldMeta) {
        this.fieldMeta = fieldMeta;
    }

    public FieldMeta<?, ?> fieldMeta() {
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
