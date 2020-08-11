package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public final class NotSupportDialectException extends ArmyRuntimeException {

    public NotSupportDialectException(String format, Object... args) {
        super(ErrorCode.NOT_SUPPORT_DIALECT, format, args);
    }

    public NotSupportDialectException(Throwable cause, String format, Object... args) {
        super(ErrorCode.NOT_SUPPORT_DIALECT, cause, format, args);
    }
}
