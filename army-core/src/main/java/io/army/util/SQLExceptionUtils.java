package io.army.util;

import io.army.ArmyAccessException;
import io.army.ErrorCode;

import java.sql.SQLException;

public abstract class SQLExceptionUtils {

    protected SQLExceptionUtils() {
        throw new UnsupportedOperationException();
    }

    public static ArmyAccessException convert(SQLException e, String sql) {
        return new ArmyAccessException(ErrorCode.ACCESS_ERROR, e
                , "army set param occur error ,sql[%s]", sql);
    }
}
