package io.army.generator;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class GeneratorException extends ArmyRuntimeException {

    private static final long serialVersionUID = 3690450509486176645L;

    public GeneratorException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public GeneratorException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
