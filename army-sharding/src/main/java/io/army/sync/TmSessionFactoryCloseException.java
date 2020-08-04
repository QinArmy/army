package io.army.sync;

import io.army.ErrorCode;
import io.army.SessionFactoryException;

import java.util.Collections;
import java.util.Map;

public final class TmSessionFactoryCloseException extends SessionFactoryException {

    private final Map<Integer, SessionFactoryException> rmSessionFactoryExceptionMap;

    public TmSessionFactoryCloseException(Map<Integer, SessionFactoryException> rmSessionFactoryExceptionMap
            , String format, Object... args) {
        super(ErrorCode.SESSION_FACTORY_CLOSE_ERROR, format, args);
        this.rmSessionFactoryExceptionMap = Collections.unmodifiableMap(rmSessionFactoryExceptionMap);
    }


    /**
     * @return a unmodifiable map
     */
    public Map<Integer, SessionFactoryException> getRmSessionFactoryExceptionMap() {
        return rmSessionFactoryExceptionMap;
    }
}
