package io.army.tx;


/**
 * Exception thrown when a general transaction system error is encountered,
 * like on commit or rollback.
 */
public final class TransactionSystemException extends TransactionException {


    public TransactionSystemException(String message, Throwable cause) {
        super(message, cause);
    }

}
