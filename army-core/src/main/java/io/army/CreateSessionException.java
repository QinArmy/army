package io.army;

public class CreateSessionException extends SessionException {



    public CreateSessionException(String format, Object... args) {
        super(format);
    }
}
