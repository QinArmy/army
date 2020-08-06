package io.army.tx.sync;

import io.army.ErrorCode;
import io.army.tx.TransactionUsageException;

/**
 * throw when {@link org.springframework.transaction.annotation.Isolation} not expected.
 *
 * @see TransactionalUtils
 */
public class TransactionIsolationException extends TransactionUsageException {


    public TransactionIsolationException(String format, Object... args) {
        super(ErrorCode.ISOLATION_ERROR, format, args);
    }

    public TransactionIsolationException(Throwable cause, String format, Object... args) {
        super(ErrorCode.ISOLATION_ERROR, cause, format, args);
    }
}
