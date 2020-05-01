package io.army.tx;

import io.army.ErrorCode;

/**
 * Exception thrown when create {@code Transaction} or {@code ReactiveTransaction} instance or start transaction
 * error is encountered.
 */
public class CannotCreateTransactionException extends TransactionException {


    public CannotCreateTransactionException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    public CannotCreateTransactionException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }
}
