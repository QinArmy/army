package io.army.tx;

import io.army.SessionException;
import io.army.session.GenericSession;

/**
 * throw when try close {@link GenericSession} ,but {@link GenericTransaction} not close.
 */
public class TransactionNotCloseException extends SessionException {

    public TransactionNotCloseException(String format, Object... args) {
        super(format);
    }
}
