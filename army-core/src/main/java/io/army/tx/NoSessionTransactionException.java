package io.army.tx;

/**
 * Exception thrown when session has no transaction.
 */
public class NoSessionTransactionException extends TransactionUsageException {

    public NoSessionTransactionException(String message) {
        super(message);
    }

}
