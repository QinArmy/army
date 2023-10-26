package io.army.session;

import io.army.ArmyException;

public class SessionFactoryException extends ArmyException {


    public SessionFactoryException(String message) {
        super(message);
    }

    public SessionFactoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionFactoryException(Throwable cause) {
        super(cause);
    }


}
