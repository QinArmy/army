package io.army.dialect;

import io.army.DataAccessException;
import io.army.ErrorCode;

public class InsertException extends DataAccessException {

    @Deprecated
    public InsertException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public InsertException(String format, Object... args) {
        super(format, args);
    }
}
