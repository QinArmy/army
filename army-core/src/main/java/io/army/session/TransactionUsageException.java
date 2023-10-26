package io.army.session;

public class TransactionUsageException extends TransactionException {


    public TransactionUsageException(String message) {
        super(message);
    }

    public TransactionUsageException(String message, Throwable cause) {
        super(message, cause);
    }

}
