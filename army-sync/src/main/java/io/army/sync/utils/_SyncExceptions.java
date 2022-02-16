package io.army.sync.utils;


import io.army.session.DataAccessException;
import io.army.util._Exceptions;

import java.sql.SQLException;

public abstract class _SyncExceptions extends _Exceptions {


    public static DataAccessException wrapDataAccess(SQLException e) {
        return new DataAccessException(e);
    }

}
