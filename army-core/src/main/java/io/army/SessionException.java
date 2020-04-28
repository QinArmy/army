package io.army;

public abstract class SessionException extends ArmyAccessException {

    public SessionException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public SessionException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
