package io.army;

public class SessionFactoryException extends ArmyRuntimeException {

    @Deprecated
    public SessionFactoryException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    @Deprecated
    public SessionFactoryException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    public SessionFactoryException(String format, Object... args) {
        super(format, args);
    }

    public SessionFactoryException(Throwable cause, String format, Object... args) {
        super(cause, format, args);
    }
}
