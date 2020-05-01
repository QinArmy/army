package io.army.tx;

import io.army.ErrorCode;

/**
 * Exception thrown when session has no transaction.
 */
public class NoSessionTransactionException extends TransactionUsageException {

    public NoSessionTransactionException(String format, Object... args) {
        super(ErrorCode.NO_SESSION_TRANSACTION, format, args);
    }

}
