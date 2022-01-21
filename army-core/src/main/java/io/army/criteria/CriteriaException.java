package io.army.criteria;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

/**
 */
public class CriteriaException extends ArmyRuntimeException {


    @Deprecated
    public CriteriaException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    @Deprecated
    public CriteriaException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    public CriteriaException(String format) {
        super(format);
    }

    public CriteriaException(String format, Throwable cause) {
        super(format, cause);
    }

    public CriteriaException(Throwable cause, String format, Object... args) {
        super(cause, format, args);
    }
}
