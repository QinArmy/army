package io.army.tx;

import io.army.ArmyAccessException;
import io.army.ErrorCode;

public abstract class TransactionException extends ArmyAccessException {

    public TransactionException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public TransactionException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
