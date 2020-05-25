package io.army.util;

import io.army.DataAccessException;
import io.army.ErrorCode;

import java.sql.SQLException;

public abstract class SQLExceptionUtils {

    protected SQLExceptionUtils() {
        throw new UnsupportedOperationException();
    }

    public static DataAccessException convert(SQLException e, String sql) {
        return new DataAccessException(ErrorCode.ACCESS_ERROR, e
                , "army set param occur error ,sql[%s]", sql);
    }
}
