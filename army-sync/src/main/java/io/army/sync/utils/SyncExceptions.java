package io.army.sync.utils;


import io.army.session.DataAccessException;
import io.army.util.Exceptions;

import java.sql.SQLException;

public abstract class SyncExceptions  extends Exceptions {


    public static DataAccessException wrapDataAccess(SQLException e){
        return new DataAccessException(e);
    }

}
