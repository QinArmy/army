package io.army;

public class SessionCloseFailureException extends SessionException {

    public SessionCloseFailureException(Throwable cause, String format, Object... args) {
        super(ErrorCode.CLOSE_CONN_ERROR, cause, format, args);
    }
}
