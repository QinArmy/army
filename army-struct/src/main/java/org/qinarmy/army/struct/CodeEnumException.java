package org.qinarmy.army.struct;

import org.qinarmy.army.ArmyRuntimeException;
import org.qinarmy.army.ErrorCode;

/**
 * throw when {@link CodeEnum} definition error .
 * created  on 2019-02-24.
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
