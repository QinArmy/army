package io.army;

public class DataAccessException extends ArmyRuntimeException {


    @Deprecated
    public DataAccessException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    @Deprecated
    public DataAccessException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    public DataAccessException(String format, Object... args) {
        super(format, args);
    }
}
