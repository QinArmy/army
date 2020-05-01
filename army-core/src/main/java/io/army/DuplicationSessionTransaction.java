package io.army;

import io.army.tx.TransactionException;

public class DuplicationSessionTransaction extends TransactionException {

    public DuplicationSessionTransaction(String format, Object... args) {
        super(ErrorCode.DUPLICATION_SESSION_TRANSACTION, format, args);
    }

}
