package io.army.bean;

import io.army.ArmyException;

public class ObjectAccessException extends ArmyException {

    public ObjectAccessException(String message) {
        super(message);
    }

    public ObjectAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public ObjectAccessException(Throwable cause) {
        super(cause);
    }

}
