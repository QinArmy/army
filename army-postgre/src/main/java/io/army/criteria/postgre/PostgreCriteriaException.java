package io.army.criteria.postgre;

import io.army.ErrorCode;
import io.army.criteria.CriteriaException;

public class PostgreCriteriaException extends CriteriaException {

    public PostgreCriteriaException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public PostgreCriteriaException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
