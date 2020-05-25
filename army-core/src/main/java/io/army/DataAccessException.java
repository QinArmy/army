package io.army;

public class DataAccessException extends ArmyRuntimeException {


    public DataAccessException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public DataAccessException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
