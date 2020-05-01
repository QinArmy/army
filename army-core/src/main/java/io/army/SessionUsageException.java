package io.army;

public class SessionUsageException extends SessionException {

    public SessionUsageException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public SessionUsageException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
