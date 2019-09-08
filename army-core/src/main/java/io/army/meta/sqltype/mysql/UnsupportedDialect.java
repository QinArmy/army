package io.army.meta.sqltype.mysql;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class UnsupportedDialect extends ArmyRuntimeException {

    public UnsupportedDialect() {
        super(ErrorCode.UNSUPPORT_DIALECT);
    }

    public UnsupportedDialect(String format, Object... args) {
        super(ErrorCode.UNSUPPORT_DIALECT, format, args);
    }

    public UnsupportedDialect(Throwable cause, String format, Object... args) {
        super(ErrorCode.UNSUPPORT_DIALECT, cause, format, args);
    }
}
