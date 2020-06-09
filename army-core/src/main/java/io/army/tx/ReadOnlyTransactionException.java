package io.army.tx;

import io.army.ErrorCode;

public class ReadOnlyTransactionException extends TransactionUsageException {

    public ReadOnlyTransactionException(String format, Object... args) {
        super(ErrorCode.READ_ONLY_TRANSACTION, format, args);
    }
}
