package io.army;

public class DuplicationSessionTransaction extends SessionException {

    public DuplicationSessionTransaction(String format, Object... args) {
        super(ErrorCode.DUPLICATION_SESSION_TRANSACTION, format, args);
    }

    public DuplicationSessionTransaction(Throwable cause, String format, Object... args) {
        super(ErrorCode.DUPLICATION_SESSION_TRANSACTION, cause, format, args);
    }
}
