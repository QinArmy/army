package io.army.context.spi;

import io.army.DataAccessException;
import io.army.ErrorCode;

public class CreateSessionNoSupportedException extends DataAccessException {

    public CreateSessionNoSupportedException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public CreateSessionNoSupportedException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
