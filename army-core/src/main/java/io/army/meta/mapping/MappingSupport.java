package io.army.meta.mapping;

import io.army.dialect.Dialect;
import io.army.meta.sqltype.mysql.UnsupportedDialect;

import java.sql.SQLException;

public abstract class MappingSupport {


    protected UnsupportedDialect unsupportedDialect(Dialect dialect) {
        return new UnsupportedDialect("%s un support dialect[%s]",
                this.getClass().getName(), dialect);
    }

    protected IllegalArgumentException databaseValueError(Object databaseValue) {
        return new IllegalArgumentException(String.format(
                "databaseValue[%s] couldn't convert to %s", databaseValue, getClass()));
    }

    protected SQLException convertToJavaException(Object databaseValue, Class<?> javaType) throws SQLException {
        throw new SQLException(String.format("databaseValue[%s] cannot convert to java type[%s]",
                databaseValue,
                javaType.getName()
        ));
    }


}
