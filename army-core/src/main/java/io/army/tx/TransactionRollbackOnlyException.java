package io.army.tx;

import io.army.ErrorCode;

public final class TransactionRollbackOnlyException extends TransactionUsageException {

    public TransactionRollbackOnlyException(String format, Object... args) {
        super(ErrorCode.TRANSACTION_ROLLBACK_ONLY, format, args);
    }

}
