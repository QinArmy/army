package org.qinarmy.army.criteria;

import org.qinarmy.army.ArmyRuntimeException;
import org.qinarmy.army.ErrorCode;

/**
 * created  on 2018/11/19.
 */
public class MetaException extends ArmyRuntimeException {

    public MetaException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MetaException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public MetaException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
