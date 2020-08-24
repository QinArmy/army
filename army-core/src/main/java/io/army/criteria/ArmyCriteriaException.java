package io.army.criteria;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class ArmyCriteriaException extends ArmyRuntimeException {

    public ArmyCriteriaException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public ArmyCriteriaException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    public ArmyCriteriaException(String format, Object... args) {
        super(ErrorCode.NONE, format, args);
    }

}
