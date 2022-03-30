package io.army.session;

import io.army.tx.GenericTransaction;

/**
 * throw when try close {@link GenericSession} ,but {@link GenericTransaction} not close.
 */
public class TransactionNotCloseException extends SessionException {

    public TransactionNotCloseException(String format, Object... args) {
        super(format);
    }
}
