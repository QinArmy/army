package io.army.session;

import io.army.SessionException;

public class SessionClosedException extends SessionException {

    public SessionClosedException(String message) {
        super(message);
    }

}
