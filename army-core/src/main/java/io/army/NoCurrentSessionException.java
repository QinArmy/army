package io.army;

public class NoCurrentSessionException extends SessionException {

    public NoCurrentSessionException(String format, Object... args) {
        super(format);
    }

}
