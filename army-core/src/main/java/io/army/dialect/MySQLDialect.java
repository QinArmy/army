package io.army.dialect;

import io.army.criteria.impl.DLS;
import io.army.meta.Field;

/**
 * this class is a  {@link Dialect} implementation and abstract base class of all MySQL Dialect
 * created  on 2018/10/21.
 */
public abstract class MySQLDialect extends DLS implements Dialect {


    @Override
    public String fieldDefinition(Field<?, ?> field) {
        return null;
    }


}
