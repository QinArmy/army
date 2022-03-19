package io.army.session;

public class ReadOnlySessionException extends SessionException {

    public ReadOnlySessionException(String message) {
        super(message);
    }

}
