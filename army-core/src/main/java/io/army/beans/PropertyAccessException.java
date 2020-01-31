package io.army.beans;

import io.army.ErrorCode;

public class PropertyAccessException extends BeansException {

    public PropertyAccessException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public PropertyAccessException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
