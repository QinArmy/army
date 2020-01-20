package io.army.schema.migration;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class SchemaExtractException extends ArmyRuntimeException {


    public SchemaExtractException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
