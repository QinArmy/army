package io.army.modelgen;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

final class MappingException extends ArmyRuntimeException {

    public MappingException(String format, Object... args) {
        super(ErrorCode.MAPPING_ERROR, format, args);
    }

    public MappingException(Throwable cause, String format, Object... args) {
        super(ErrorCode.MAPPING_ERROR, cause, format, args);
    }
}
