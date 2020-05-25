package io.army.dialect;

import io.army.DataAccessException;
import io.army.ErrorCode;

public class InsertException extends DataAccessException {

    public InsertException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public InsertException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
