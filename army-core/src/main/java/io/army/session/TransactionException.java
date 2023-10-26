package io.army.session;

/**
 * Superclass for all transaction exceptions.
 */
public abstract class TransactionException extends SessionException {


    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

}
