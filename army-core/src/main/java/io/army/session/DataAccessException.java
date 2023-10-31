package io.army.session;

/**
 * <p>Throw(or emit) by {@link Session} or {@link Cursor} when access database occur.
 * <p>Following
 * <ul>
 *     <li>{@link DriverException}</li>
 *     <li>{@link OptimisticLockException}</li>
 *     <li>{@link ChildUpdateException}</li>
 * </ul>
 * are  important sub-class
 *
 * @see DriverException
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
