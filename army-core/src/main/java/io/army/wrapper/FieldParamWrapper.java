package io.army.wrapper;

import io.army.criteria.Insert;
import io.army.criteria.Visible;
import io.army.meta.FieldMeta;

/**
 * place holder for batch sql .
 *
 * @see io.army.dialect.Dialect#batchInsert(Insert, Visible)
 */
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
