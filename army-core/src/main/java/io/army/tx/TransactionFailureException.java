package io.army.tx;

/**
 * Exception thrown when commit method or rollback method failure because system error is encountered.
 */
public class TransactionFailureException extends TransactionSystemException {

    @Deprecated
    public TransactionFailureException(Throwable cause, String format, Object... args) {
        super(cause, format, args);
    }

    public TransactionFailureException(String message) {
        super(message);
    }

    public TransactionFailureException(String message, Throwable cause) {
        super(message, cause);
    }
}
