package io.army.tx;

/**
 * Exception thrown when create {@code Transaction} or {@code ReactiveTransaction} instance or start transaction
 * error is encountered.
 */
public class CannotCreateTransactionException extends TransactionException {


    public CannotCreateTransactionException(String message) {
        super(message);
    }

}
