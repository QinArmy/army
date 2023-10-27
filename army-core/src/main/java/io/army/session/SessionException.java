package io.army.session;

import io.army.ArmyException;

public class SessionException extends ArmyException {

    public SessionException(String message) {
        super(message);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }

    public SessionException(Throwable cause) {
        super(cause);
    }

}
