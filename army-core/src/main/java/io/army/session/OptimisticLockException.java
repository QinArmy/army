package io.army.session;


import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public final class OptimisticLockException extends ArmyRuntimeException {


    public OptimisticLockException(String format, Object... args) {
        super(ErrorCode.OPTIMISTIC_LOCK, format, args);
    }

}
