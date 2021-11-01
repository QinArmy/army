package io.army;

public class DataAccessException_0 extends ArmyRuntimeException {


    @Deprecated
    public DataAccessException_0(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    @Deprecated
    public DataAccessException_0(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    public DataAccessException_0(String format, Object... args) {
        super(format, args);
    }


}
