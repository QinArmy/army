package io.army;

public class LockTimeoutException extends PersistenceException {

    public LockTimeoutException(ErrorCode errorCode) {
        super(errorCode);
    }

    public LockTimeoutException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public LockTimeoutException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
