package io.army.session;

/**
 * Exception to be thrown when a transaction has timed out.
 */
public class TransactionTimeOutException extends TransactionException {

    public TransactionTimeOutException(String message) {
        super(message);
    }

}