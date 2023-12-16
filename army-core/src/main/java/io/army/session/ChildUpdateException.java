package io.army.session;

/**
 * @see Session#isRollbackOnly()
 * @since 0.6.0
 */
public final class ChildUpdateException extends DataAccessException {

    public ChildUpdateException(String message) {
        super(message);
    }

    public ChildUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

}
