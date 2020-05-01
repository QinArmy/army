package io.army.tx;

import io.army.ErrorCode;

/**
 * Exception to be thrown when a transaction has timed out.
 */
public class TransactionTimeOutException extends TransactionException {

    public TransactionTimeOutException(String format, Object... args) {
        super(ErrorCode.TRANSACTION_TIME_OUT, format, args);
    }

}
