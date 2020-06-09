package io.army.tx;

import org.springframework.transaction.TransactionUsageException;

public class ArmyTransactionRollbackOnlyException extends TransactionUsageException {

    public ArmyTransactionRollbackOnlyException(String msg) {
        super(msg);
    }
}
