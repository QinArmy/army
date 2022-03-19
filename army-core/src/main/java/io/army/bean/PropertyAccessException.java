package io.army.bean;

public class PropertyAccessException extends ObjectAccessException {

    private final Class<?> beanClass;

    private final String propertyName;

    public PropertyAccessException(String message, Class<?> beanClass, String propertyName) {
        super(message);
        this.beanClass = beanClass;
        this.propertyName = propertyName;
    }

    public PropertyAccessException(String message, Throwable cause, Class<?> beanClass, String propertyName) {
        super(message, cause);
        this.beanClass = beanClass;
        this.propertyName = propertyName;
    }

    public PropertyAccessException(Throwable cause, Class<?> beanClass, String propertyName) {
        super(cause);
        this.beanClass = beanClass;
        this.propertyName = propertyName;
    }
}
