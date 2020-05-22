package io.army.criteria;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

/**
 */
public class CriteriaException extends ArmyRuntimeException {


    public CriteriaException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public CriteriaException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
