package org.qinarmy.army.criteria;

import org.qinarmy.army.ArmyRuntimeException;
import org.qinarmy.army.ErrorCode;

/**
 * created  on 2018/11/25.
 */
public class CriteriaException extends ArmyRuntimeException {

    public CriteriaException(ErrorCode errorCode) {
        super(errorCode);
    }

    public CriteriaException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public CriteriaException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
