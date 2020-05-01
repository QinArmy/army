package io.army.tx;

/**
 * Exception thrown when commit method or rollback method failure because system error is encountered.
 */
public class TransactionFailureException extends TransactionSystemException {

    public TransactionFailureException(Throwable cause, String format, Object... args) {
        super(cause, format, args);
    }
}
