package io.army;

public class ReadOnlySessionException extends SessionException {

    public ReadOnlySessionException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public ReadOnlySessionException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
