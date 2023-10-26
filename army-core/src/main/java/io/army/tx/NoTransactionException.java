package io.army.tx;

/**
 * Exception thrown when session has no transaction.
 */
public final class NoTransactionException extends TransactionUsageException {

    public NoTransactionException(String message) {
        super(message);
    }

}
