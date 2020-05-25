package io.army;

public class LockTimeoutException extends DataAccessException {

    public LockTimeoutException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public LockTimeoutException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
