package io.army;

public class PessimisticLockException extends ArmyRuntimeException {

    public PessimisticLockException(ErrorCode errorCode) {
        super(errorCode);
    }

    public PessimisticLockException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public PessimisticLockException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
