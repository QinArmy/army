package io.army;

import io.army.session.DataAccessException;

public abstract class SessionException extends DataAccessException {

    public SessionException(String message) {
        super(message);
    }

    @Deprecated
    public SessionException(Throwable cause, String format, Object... args) {
        super(format, cause);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }

}
