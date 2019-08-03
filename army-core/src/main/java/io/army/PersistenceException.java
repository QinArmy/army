package io.army;

public class PersistenceException extends ArmyRuntimeException {

    public PersistenceException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PersistenceException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public PersistenceException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
