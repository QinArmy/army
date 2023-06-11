package io.army.jdbc;

import io.army.ArmyException;
import io.army.session.DataAccessException;
import io.army.util._Exceptions;

import java.sql.SQLException;

abstract class JdbcExceptions {

    private JdbcExceptions() {
        throw new UnsupportedOperationException();
    }


    public static ArmyException wrap(final Throwable error) {
        final ArmyException e;
        if (error instanceof SQLException) {
            e = new DataAccessException(error);
        } else if (error instanceof ArmyException) {
            e = (ArmyException) error;
        } else {
            e = _Exceptions.unknownError(error.getMessage(), error);
        }
        return e;
    }


}
