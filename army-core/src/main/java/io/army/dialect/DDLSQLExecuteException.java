package io.army.dialect;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class DDLSQLExecuteException extends ArmyRuntimeException {


    public DDLSQLExecuteException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
