package io.army.tx;

import io.army.ErrorCode;
import io.army.SessionException;

/**
 * throw when try close {@link io.army.GenericSession} ,but {@link GenericTransaction} not close.
 */
public class TransactionNotCloseException extends SessionException {

    public TransactionNotCloseException(String format, Object... args) {
        super(ErrorCode.TRANSACTION_NOT_CLOSE, format, args);
    }
}
