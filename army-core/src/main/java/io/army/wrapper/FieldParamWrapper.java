package io.army.wrapper;

import io.army.meta.FieldMeta;

/**
 * place holder for batch sql .
 *
 */
public interface FieldParamWrapper extends ParamWrapper {

    @Override
    FieldMeta<?, ?> paramMeta();

    /**
     * @throws UnsupportedOperationException always throw.
     */
    @Override
    Object value() throws UnsupportedOperationException;

}
