package io.army.session;

public final class SessionClosedException extends SessionException {

    public SessionClosedException(String message) {
        super(message);
    }

    public SessionClosedException(Throwable cause) {
        super(cause);
    }


}
