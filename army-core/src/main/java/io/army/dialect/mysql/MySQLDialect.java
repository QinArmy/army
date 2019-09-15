package io.army.dialect.mysql;

import io.army.dialect.AbstractDialect;
import io.army.dialect.Dialect;

/**
 * this class is a  {@link Dialect} implementation and abstract base class of all MySQL Dialect
 * created  on 2018/10/21.
 */
public abstract class MySQLDialect extends AbstractDialect implements MySQLFunc {


    @Override
    protected abstract MySQLFunc func();

    @Override
    public final boolean supportZoneId() {
        return false;
    }


}
