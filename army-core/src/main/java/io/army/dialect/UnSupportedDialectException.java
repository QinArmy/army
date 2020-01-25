package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class UnSupportedDialectException extends ArmyRuntimeException {

    public UnSupportedDialectException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }
}
