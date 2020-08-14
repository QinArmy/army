package io.army.meta;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

/**
 * throw when {@link io.army.meta.Meta} error.
 */
public final class MetaException extends ArmyRuntimeException {


    private static final long serialVersionUID = -1570931082845046499L;

    public MetaException(String format, Object... args) {
        super(ErrorCode.META_ERROR, format, args);
    }

    public MetaException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public MetaException(Throwable cause, String format, Object... args) {
        super(ErrorCode.META_ERROR, cause, format, args);
    }

    public MetaException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
