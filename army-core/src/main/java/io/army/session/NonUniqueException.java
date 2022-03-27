package io.army.session;


import io.army.DataAccessException_0;
import io.army.ErrorCode;

public final class NonUniqueException extends DataAccessException_0 {

    public NonUniqueException(String format, Object... args) {
        super(ErrorCode.CRITERIA_ERROR, format, args);
    }

    public NonUniqueException(Throwable cause, String format, Object... args) {
        super(ErrorCode.CRITERIA_ERROR, cause, format, args);
    }
}