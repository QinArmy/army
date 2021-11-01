package io.army.session;

import io.army.util.ArmyException;

public class DataAccessException extends ArmyException {

    DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }


}
