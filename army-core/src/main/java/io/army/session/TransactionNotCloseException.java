package io.army.session;

import io.army.tx.Transaction;

/**
 * throw when try close {@link GenericSession} ,but {@link Transaction} not close.
 */
public class TransactionNotCloseException extends SessionException {

    public TransactionNotCloseException(String format, Object... args) {
        super(format);
    }
}
