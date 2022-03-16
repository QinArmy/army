package io.army.tx;

import io.army.ErrorCode;
import io.army.SessionException;

/**
 * Superclass for all transaction exceptions.
 */
public abstract class TransactionException extends SessionException {

    @Deprecated
    public TransactionException(ErrorCode errorCode, String format, Object... args) {
        super(format);
    }

    @Deprecated
    public TransactionException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(format);
    }

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
