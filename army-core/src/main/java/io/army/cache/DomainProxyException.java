package io.army.cache;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class DomainProxyException extends ArmyRuntimeException {

    private static final long serialVersionUID = -8854889299783019810L;

    public DomainProxyException(String format, Object... args) {
        super(ErrorCode.PROXY_ERROR, format, args);
    }

    public DomainProxyException(Throwable cause, String format, Object... args) {
        super(ErrorCode.PROXY_ERROR, cause, format, args);
    }
}
