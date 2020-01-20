package io.army.schema;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class SchemaInfoException extends ArmyRuntimeException {

    public SchemaInfoException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public SchemaInfoException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
