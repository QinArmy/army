package io.army;

public class SessionUsageException extends SessionException {

    @Deprecated
    public SessionUsageException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    @Deprecated
    public SessionUsageException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    public SessionUsageException(String format, Object... args) {
        super(ErrorCode.NONE, format, args);
    }

    public SessionUsageException(Throwable cause, String format, Object... args) {
        super(ErrorCode.NONE, cause, format, args);
    }
}
