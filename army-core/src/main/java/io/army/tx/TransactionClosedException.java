package io.army.tx;

import io.army.ErrorCode;

public class TransactionClosedException extends TransactionUsageException {

    public TransactionClosedException(String format, Object... args) {
        super(ErrorCode.TRANSACTION_CLOSED, format, args);
    }

}
