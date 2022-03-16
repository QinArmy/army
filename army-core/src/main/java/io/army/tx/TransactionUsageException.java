package io.army.tx;

import io.army.ErrorCode;

public class TransactionUsageException extends TransactionException {

    @Deprecated
    public TransactionUsageException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    @Deprecated
    public TransactionUsageException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    public TransactionUsageException(String message, Throwable cause) {
        super(message, cause);
    }

}
