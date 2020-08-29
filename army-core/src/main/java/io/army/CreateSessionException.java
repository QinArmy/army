package io.army;

public class CreateSessionException extends SessionException {

    public CreateSessionException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public CreateSessionException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }

    public CreateSessionException(String format, Object... args) {
        super(ErrorCode.NONE, format, args);
    }
}
