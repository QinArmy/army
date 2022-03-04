package io.army.session;

import io.army.SessionException;

public class ChildDmlNoTractionException extends SessionException {

    public ChildDmlNoTractionException(String message) {
        super(message);
    }

}
