package io.army.sync;

import io.army.ErrorCode;
import io.army.SessionException;

import java.util.Collections;
import java.util.Map;

public final class TmSessionCloseException extends SessionException {

    private final Map<Integer, SessionException> rmSessionExceptionMap;

    public TmSessionCloseException(Map<Integer, SessionException> rmSessionExceptionMap
            , String format, Object... args) {
        super(ErrorCode.SESSION_CLOSE_ERROR, format, args);
        this.rmSessionExceptionMap = Collections.unmodifiableMap(rmSessionExceptionMap);
    }


    /**
     * @return a unmodifiable map
     */
    public Map<Integer, SessionException> getRmSessionExceptionMap() {
        return rmSessionExceptionMap;
    }
}
