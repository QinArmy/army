package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class DialectNotMatchException extends ArmyRuntimeException {


    public DialectNotMatchException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public DialectNotMatchException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
