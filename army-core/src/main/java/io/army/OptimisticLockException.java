package io.army;


public class OptimisticLockException extends ArmyRuntimeException {


    public OptimisticLockException(String format, Object... args) {
        super(ErrorCode.OPTIMISTIC_LOCK, format, args);
    }

}
