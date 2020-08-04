package io.army.boot.migratioin;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public final class SchemaExtractException extends ArmyRuntimeException {


    public SchemaExtractException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
