package io.army.struct;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

/**
 * throw when {@link CodeEnum} definition error .
 *
 * @since 1.0
 */
public class CodeEnumException extends ArmyRuntimeException {

    public CodeEnumException() {
        super(ErrorCode.CODE_ENUM_ERROR);
    }


    public CodeEnumException(String format, Object... args) {
        super(ErrorCode.CODE_ENUM_ERROR, format, args);
    }

    public CodeEnumException(Throwable cause, String format, Object... args) {
        super(ErrorCode.CODE_ENUM_ERROR, cause, format, args);
    }
}
