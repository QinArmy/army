package io.army;

public class InsertNotMatchException extends SessionException {

    public InsertNotMatchException(String format, Object... args) {
        super(ErrorCode.INSERT_COUNT_NOT_MATCH, format, args);
    }


}
