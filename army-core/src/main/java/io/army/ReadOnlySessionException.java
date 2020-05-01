package io.army;

public class ReadOnlySessionException extends SessionException {

    public ReadOnlySessionException(String format, Object... args) {
        super(ErrorCode.READ_ONLY_SESSION, format, args);
    }

}
