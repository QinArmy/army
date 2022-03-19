package io.army.session;

import io.army.ErrorCode;

public class SessionUsageException extends SessionException {

    @Deprecated
    public SessionUsageException(ErrorCode errorCode, String format, Object... args) {
        super(format);
    }

    @Deprecated
    public SessionUsageException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(cause, format, args);
    }

    public SessionUsageException(String format, Object... args) {
        super(format);
    }


}
