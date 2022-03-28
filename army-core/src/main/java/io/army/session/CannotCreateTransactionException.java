package io.army.session;

import io.army.lang.Nullable;

/**
 * Exception thrown when create {@code Transaction} or {@code ReactiveTransaction} instance or start transaction
 * error is encountered.
 */
public class CannotCreateTransactionException extends SessionException {


    public CannotCreateTransactionException(String message) {
        super(message);
    }

    public CannotCreateTransactionException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

}
