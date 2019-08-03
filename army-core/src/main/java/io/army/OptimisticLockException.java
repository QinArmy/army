package io.army;


public class OptimisticLockException extends ArmyRuntimeException {

    public OptimisticLockException(ErrorCode errorCode) {
        super(errorCode);
    }

    public OptimisticLockException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public OptimisticLockException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
