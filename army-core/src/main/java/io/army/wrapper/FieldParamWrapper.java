package io.army.wrapper;

import io.army.meta.FieldMeta;

public interface FieldParamWrapper extends ParamWrapper {

    @Override
    FieldMeta<?, ?> paramMeta();

    /**
     * @throws UnsupportedOperationException always throw.
     */
    @Override
    Object value() throws UnsupportedOperationException;

    static FieldParamWrapper build(FieldMeta<?, ?> fieldMeta) {
        return new FieldParamWrapperImpl(fieldMeta);
    }
}
