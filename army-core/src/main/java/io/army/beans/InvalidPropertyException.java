package io.army.beans;

import io.army.ErrorCode;

public class InvalidPropertyException extends FatalBeanException {


    private final Class<?> beanClass;

    private final String propertyName;

     InvalidPropertyException(ErrorCode errorCode, String propertyName,
                              Class<?> beanClass, String format, Object... args) {
         super(errorCode, format, args);
         this.beanClass = beanClass;
         this.propertyName = propertyName;
     }

    InvalidPropertyException(ErrorCode errorCode, Throwable cause, String propertyName,
                             Class<?> beanClass, String format, Object... args) {
        super(errorCode, cause, format, args);
        this.beanClass = beanClass;
        this.propertyName = propertyName;
    }

    InvalidPropertyException(String propertyName,
                             Class<?> beanClass, String format, Object... args) {
        super(ErrorCode.NONE, format, args);
        this.beanClass = beanClass;
        this.propertyName = propertyName;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public String getPropertyName() {
        return propertyName;
    }
}
