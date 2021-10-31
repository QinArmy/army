package io.army.stmt;

import io.army.meta.FieldMeta;

/**
 * place holder for batch sql .
 *
 */
public interface FieldParamValue extends ParamValue {

    @Override
    FieldMeta<?, ?> paramMeta();

    /**
     * @throws UnsupportedOperationException always throw.
     */
    @Override
    Object value() throws UnsupportedOperationException;

}
