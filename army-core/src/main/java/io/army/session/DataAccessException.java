package io.army.session;

import io.army.ArmyException;
import io.army.lang.Nullable;

public class DataAccessException extends ArmyException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }


}
