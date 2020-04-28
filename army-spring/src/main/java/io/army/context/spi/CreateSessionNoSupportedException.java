package io.army.context.spi;

import io.army.ArmyAccessException;
import io.army.ErrorCode;

public class CreateSessionNoSupportedException extends ArmyAccessException {

    public CreateSessionNoSupportedException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public CreateSessionNoSupportedException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
