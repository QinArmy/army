package io.army.dialect;

import io.army.meta.FieldMeta;
import io.army.meta.mapping.MappingType;

public interface FieldParamWrapper extends ParamWrapper {

    FieldMeta<?, ?> fieldMeta();

    /**
     * @throws UnsupportedOperationException always throw.
     */
    @Override
    MappingType mappingType() throws UnsupportedOperationException;

    /**
     * @throws UnsupportedOperationException always throw.
     */
    @Override
    Object value() throws UnsupportedOperationException;

    static FieldParamWrapper build(FieldMeta<?, ?> fieldMeta) {
        return new FieldParamWrapperImpl(fieldMeta);
    }
}
