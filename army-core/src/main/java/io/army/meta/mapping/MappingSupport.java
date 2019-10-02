package io.army.meta.mapping;

import java.sql.SQLException;

public abstract class MappingSupport {


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
