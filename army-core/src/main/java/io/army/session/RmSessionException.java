package io.army.session;

/**
 * @see RmSession
 * @since 1.0
 */
public class RmSessionException extends SessionException {

    public RmSessionException(String message) {
        super(message);
    }

    public RmSessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public RmSessionException(Throwable cause) {
        super(cause);
    }

}
