package io.army.session;

public class NoCurrentSessionException extends SessionException {

    public NoCurrentSessionException(String format, Object... args) {
        super(format);
    }

}
