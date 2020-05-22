package io.army.criteria;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

/**
 * throw when {@link io.army.meta.Meta} error.
 */
public class MetaException extends ArmyRuntimeException {

    private static final long serialVersionUID = -7919928531735229285L;

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
