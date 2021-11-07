package io.army.meta;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class TableMetaLoadException extends ArmyRuntimeException {

    @Deprecated
    public TableMetaLoadException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    @Deprecated
    public TableMetaLoadException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    public TableMetaLoadException(String format) {
        super(format);
    }

    public TableMetaLoadException(String format, Throwable e) {
        super(e, format);
    }
}
