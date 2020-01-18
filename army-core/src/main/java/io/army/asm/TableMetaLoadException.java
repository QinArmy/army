package io.army.asm;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class TableMetaLoadException extends ArmyRuntimeException {


    public TableMetaLoadException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public TableMetaLoadException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
