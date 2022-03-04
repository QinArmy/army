package io.army;

public class SessionCloseFailureException extends SessionException {

    public SessionCloseFailureException(Throwable cause, String format, Object... args) {
        super(cause, format, args);
    }
}
