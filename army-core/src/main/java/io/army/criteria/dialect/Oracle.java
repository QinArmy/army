package io.army.criteria.dialect;

import io.army.criteria.impl.DLS;
import io.army.meta.Field;

/**
 * this class is a  {@link Dialect} implementation and abstract base class of all Oracle Dialect
 * created  on 2018/10/21.
 */
public abstract class Oracle extends DLS implements Dialect {

    protected Oracle() {
    }

    @Override
    public String fieldDefinition(Field<?, ?> field) {
        return null;
    }
}
