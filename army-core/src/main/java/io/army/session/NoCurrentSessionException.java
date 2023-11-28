package io.army.session;

public final class NoCurrentSessionException extends SessionException {

    public NoCurrentSessionException(String format) {
        super(format);
    }

}
