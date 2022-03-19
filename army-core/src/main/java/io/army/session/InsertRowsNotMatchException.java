package io.army.session;

import io.army.ErrorCode;
import io.army.dialect.InsertException;

public class InsertRowsNotMatchException extends InsertException {

    private static final long serialVersionUID = -8442826627346137923L;

    public InsertRowsNotMatchException(String format, Object... args) {
        super(ErrorCode.INSERT_COUNT_NOT_MATCH, format, args);
    }


}
