package io.army.session;

/**
 * <p>This exception representing the exception that database driver throw or emit.
 * <p>Throw or emit when database driver occur error.
 *
 * @since 1.0
 */
public class DataAccessException extends SessionException {

    public DataAccessException(String message) {
        super(message);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }


}
