package io.army.sqltype;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public final class UnsupportedSQLDataTypeException extends ArmyRuntimeException {

    public UnsupportedSQLDataTypeException(String format, Object... args) {
        super(ErrorCode.UNSUPPORTED_SQL_DATA_TYPE, format, args);
    }
}
