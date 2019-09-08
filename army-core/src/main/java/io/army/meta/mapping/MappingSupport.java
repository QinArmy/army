package io.army.meta.mapping;

import io.army.dialect.Dialect;
import io.army.meta.sqltype.mysql.UnsupportedDialect;

public abstract class MappingSupport {


    public UnsupportedDialect unsupportedDialect(Dialect dialect) {
        return new UnsupportedDialect("%s un support dialect[%s]",
                this.getClass().getName(), dialect);
    }

    public IllegalArgumentException databaseValueError(Object databaseValue) {
        return new IllegalArgumentException(String.format(
                "databaseValue[%s] couldn't convert to %s", databaseValue, getClass()));
    }
}
