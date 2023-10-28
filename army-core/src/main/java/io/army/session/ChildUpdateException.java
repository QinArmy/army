package io.army.session;

public class ChildUpdateException extends DataAccessException {

    public ChildUpdateException(String message) {
        super(message);
    }

    public ChildUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

}
