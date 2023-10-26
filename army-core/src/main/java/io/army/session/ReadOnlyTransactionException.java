package io.army.session;


public class ReadOnlyTransactionException extends TransactionUsageException {

    public ReadOnlyTransactionException(String message) {
        super(message);
    }
}
