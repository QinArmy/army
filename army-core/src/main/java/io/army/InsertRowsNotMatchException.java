package io.army;

public class InsertRowsNotMatchException extends SessionException {

    public InsertRowsNotMatchException(String format, Object... args) {
        super(ErrorCode.INSERT_COUNT_NOT_MATCH, format, args);
    }


}
