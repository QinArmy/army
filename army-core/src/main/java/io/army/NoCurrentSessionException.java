package io.army;

public class NoCurrentSessionException extends SessionException {

    public NoCurrentSessionException(String format, Object... args) {
        super(ErrorCode.NO_CURRENT_SESSION, format, args);
    }

    public NoCurrentSessionException(Throwable cause, String format, Object... args) {
        super(ErrorCode.NO_CURRENT_SESSION, cause, format, args);
    }
}
