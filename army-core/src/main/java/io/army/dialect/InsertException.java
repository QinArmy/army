package io.army.dialect;

import io.army.DataAccessException_0;
import io.army.ErrorCode;

public class InsertException extends DataAccessException_0 {

    @Deprecated
    public InsertException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public InsertException(String format, Object... args) {
        super(format, args);
    }
}
