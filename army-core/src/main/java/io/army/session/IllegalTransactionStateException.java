package io.army.session;

/**
 * Exception thrown when operate and transaction status not match, like commit,rollBack.
 */
public class IllegalTransactionStateException extends TransactionException {

    public IllegalTransactionStateException(String message) {
        super(message);
    }

}
