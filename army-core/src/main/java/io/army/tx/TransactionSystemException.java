package io.army.tx;

import io.army.ErrorCode;

/**
 * Exception thrown when a general transaction system error is encountered,
 * like on commit or rollback.
 */
@SuppressWarnings("serial")
public class TransactionSystemException extends TransactionException {

    public TransactionSystemException(Throwable cause, String format, Object... args) {
        super(ErrorCode.TRANSACTION_SYSTEM_ERROR, cause, format, args);
    }

}
