package io.army.session;

import io.army.lang.Nullable;

public class SessionException extends DataAccessException {

    public SessionException(String message) {
        super(message);
    }

    @Deprecated
    public SessionException(Throwable cause, String format, Object... args) {
        super(format, cause);
    }

    public SessionException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

}
