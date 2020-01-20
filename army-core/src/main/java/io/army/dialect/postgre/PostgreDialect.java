package io.army.dialect.postgre;

import io.army.dialect.AbstractDialect;
import io.army.dialect.Dialect;
import io.army.dialect.Func;
import io.army.schema.migration.TableDDL;

/**
 * this class is a  {@link Dialect} implementation and abstract base class of all Postgre Dialect
 * created  on 2018/10/21.
 */
abstract class PostgreDialect extends AbstractDialect {

    @Override
    protected TableDDL tableDDL() {
        return null;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public Func func() {
        return null;
    }

    @Override
    public boolean supportZoneId() {
        return false;
    }
}
