package io.army.bean;

import io.army.ErrorCode;

public class FatalBeanException extends BeansException {

    public FatalBeanException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public FatalBeanException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
