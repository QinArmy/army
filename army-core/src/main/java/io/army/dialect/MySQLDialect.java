package io.army.dialect;

import io.army.criteria.impl.DLS;
import io.army.meta.FieldMeta;

/**
 * this class is a  {@link Dialect} implementation and abstract base class of all MySQL Dialect
 * created  on 2018/10/21.
 */
public class MySQLDialect extends DLS implements Dialect {

    public static final MySQLDialect INSTANCE = new MySQLDialect();

    protected MySQLDialect() {
    }

    @Override
    public String fieldDefinition(FieldMeta<?, ?> field) {
        return null;
    }


}
