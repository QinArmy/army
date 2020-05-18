package io.army.cache;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class SessionCacheException extends ArmyRuntimeException {

    public SessionCacheException(String format, Object... args) {
        super(ErrorCode.CACHE_ERROR, format, args);
    }

    public SessionCacheException(Throwable cause, String format, Object... args) {
        super(ErrorCode.CACHE_ERROR, cause, format, args);
    }

}
