package io.army.jdbc;

import io.army.session.DataAccessException;

import java.sql.SQLException;

abstract class JdbcExceptions {

    private JdbcExceptions() {
        throw new UnsupportedOperationException();
    }


    public static DataAccessException wrap(SQLException e) {
        return new DataAccessException(e);
    }


}
