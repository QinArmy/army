package io.army.generator;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class GeneratorException extends ArmyRuntimeException {

    public GeneratorException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public GeneratorException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
