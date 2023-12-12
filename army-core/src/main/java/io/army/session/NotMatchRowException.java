package io.army.session;

/**
 * <p>
 * Throw when not found match row for flush method of session.
 */
public class NotMatchRowException extends SessionException {

    public NotMatchRowException(String message) {
        super(message);
    }


}
