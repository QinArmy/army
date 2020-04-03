package io.army.criteria.postgre;

import io.army.ErrorCode;

public class PostgreWithQueryException extends PostgreCriteriaException {

    public PostgreWithQueryException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public PostgreWithQueryException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
