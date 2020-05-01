package io.army.tx;

import io.army.ErrorCode;

/**
 * Exception thrown when operate and transaction status not match, like commit,rollBack.
 */
public class IllegalTransactionStateException extends TransactionException {

    public IllegalTransactionStateException(String format, Object... args) {
        super(ErrorCode.TRANSACTION_STATUS_ERROR, format, args);
    }

}
