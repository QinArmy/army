package io.army.criteria;

import io.army.ErrorCode;

public class TableAliasException extends ArmyCriteriaException {

    public TableAliasException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public TableAliasException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
