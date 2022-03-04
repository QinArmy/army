package io.army.session;

import io.army.SessionException;

public class NotSupportNonVisibleException extends SessionException {

    public NotSupportNonVisibleException(String message) {
        super(message);
    }
}
