package io.army.session;

public class ChildUpdateException extends SessionException {

    public ChildUpdateException(String message) {
        super(message);
    }

    public ChildUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

}
