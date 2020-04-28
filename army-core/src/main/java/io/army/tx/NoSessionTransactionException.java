package io.army.tx;

import io.army.ErrorCode;

public class NoSessionTransactionException extends TransactionException {

    public NoSessionTransactionException(String format, Object... args) {
        super(ErrorCode.NO_SESSION_TRANSACTION, format, args);
    }

    public NoSessionTransactionException(Throwable cause, String format, Object... args) {
        super(ErrorCode.NO_SESSION_TRANSACTION, cause, format, args);
    }
}
