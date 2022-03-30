package io.army.tx;

import io.army.session.SessionException;

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
