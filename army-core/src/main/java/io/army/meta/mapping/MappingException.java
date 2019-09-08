package io.army.meta.mapping;

import io.army.ErrorCode;
import io.army.criteria.MetaException;

public class MappingException extends MetaException {

    private static final long serialVersionUID = 7434337281792541599L;

    public MappingException(ErrorCode errorCode) {
        super(errorCode);
    }

    public MappingException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public MappingException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
