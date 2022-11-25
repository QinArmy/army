package io.army.bean;


public class InvalidPropertyException extends ObjectAccessException {


    private final Class<?> beanClass;

    private final String propertyName;

    public InvalidPropertyException(String message, Class<?> beanClass, String propertyName) {
        super(message);
        this.beanClass = beanClass;
        this.propertyName = propertyName;
    }

    public InvalidPropertyException(String message, Class<?> beanClass, String propertyName, Throwable cause) {
        super(message, cause);
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
