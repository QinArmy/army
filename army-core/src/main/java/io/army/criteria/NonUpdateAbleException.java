package io.army.criteria;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class NonUpdateAbleException extends ArmyCriteriaException {

    public NonUpdateAbleException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public NonUpdateAbleException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
