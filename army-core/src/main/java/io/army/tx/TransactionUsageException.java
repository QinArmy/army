package io.army.tx;

import io.army.ErrorCode;

public class TransactionUsageException extends TransactionException {

    public TransactionUsageException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public TransactionUsageException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
