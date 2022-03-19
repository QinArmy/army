package io.army.session;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;
import io.army.lang.Nullable;

public class SessionFactoryException extends ArmyRuntimeException {

    @Deprecated
    public SessionFactoryException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    @Deprecated
    public SessionFactoryException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    public SessionFactoryException(String message) {
        super(message);
    }

    public SessionFactoryException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public SessionFactoryException(Throwable cause, String format, Object... args) {
        super(cause, format, args);
    }
}