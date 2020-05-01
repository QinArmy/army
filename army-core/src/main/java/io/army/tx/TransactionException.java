package io.army.tx;

import io.army.ErrorCode;
import io.army.SessionException;

/**
 * Superclass for all transaction exceptions.
 */
public abstract class TransactionException extends SessionException {

    public TransactionException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public TransactionException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
