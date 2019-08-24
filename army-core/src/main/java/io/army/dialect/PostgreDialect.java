package io.army.dialect;

import io.army.meta.Field;

/**
 * this class is a  {@link Dialect} implementation and abstract base class of all Postgre Dialect
 * created  on 2018/10/21.
 */
public abstract class PostgreDialect extends AbstractDialect {

    @Override
    public String fieldDefinition(Field<?, ?> field) {
        return null;
    }
}
