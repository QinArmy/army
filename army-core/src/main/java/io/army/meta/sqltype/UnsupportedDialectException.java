package io.army.meta.sqltype;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class UnsupportedDialectException extends ArmyRuntimeException {

    private static final long serialVersionUID = -6133111187486800699L;

    public UnsupportedDialectException() {
        super(ErrorCode.UNSUPPORT_DIALECT);
    }

    public UnsupportedDialectException(String format, Object... args) {
        super(ErrorCode.UNSUPPORT_DIALECT, format, args);
    }

    public UnsupportedDialectException(Throwable cause, String format, Object... args) {
        super(ErrorCode.UNSUPPORT_DIALECT, cause, format, args);
    }
}
