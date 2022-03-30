package io.army.tx;


public class ReadOnlyTransactionException extends TransactionUsageException {

    public ReadOnlyTransactionException(String message) {
        super(message);
    }
}
