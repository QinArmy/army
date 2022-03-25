package io.army.tx;

import io.army.session.SessionException;

/**
 * Exception thrown when create {@code Transaction} or {@code ReactiveTransaction} instance or start transaction
 * error is encountered.
 */
public class CannotCreateTransactionException extends SessionException {


    public CannotCreateTransactionException(String format) {
        super(format);
    }

    public CannotCreateTransactionException(String message, Throwable cause) {
        super(message, cause);
    }

}
