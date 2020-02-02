package io.army.boot;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class FieldValuesCreateException extends ArmyRuntimeException {

    public FieldValuesCreateException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public FieldValuesCreateException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
