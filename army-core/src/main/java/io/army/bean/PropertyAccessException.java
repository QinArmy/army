package io.army.bean;

import io.army.ErrorCode;

public class PropertyAccessException extends BeansException {

    PropertyAccessException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    PropertyAccessException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    PropertyAccessException(String format, Object... args) {
        super(ErrorCode.NONE, format, args);
    }
}
