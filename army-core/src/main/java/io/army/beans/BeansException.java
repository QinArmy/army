package io.army.beans;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class BeansException extends ArmyRuntimeException {

    public BeansException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public BeansException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
