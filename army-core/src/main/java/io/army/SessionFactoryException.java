package io.army;

public class SessionFactoryException extends ArmyRuntimeException {

    public SessionFactoryException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public SessionFactoryException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
