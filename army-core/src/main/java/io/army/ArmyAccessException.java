package io.army;

public class ArmyAccessException extends ArmyRuntimeException {


    public ArmyAccessException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public ArmyAccessException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
