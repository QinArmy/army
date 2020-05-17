package io.army.aop;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class DomainProxyException extends ArmyRuntimeException {

    public DomainProxyException(String format, Object... args) {
        super(ErrorCode.PROXY_ERROR, format, args);
    }

    public DomainProxyException(Throwable cause, String format, Object... args) {
        super(ErrorCode.PROXY_ERROR, cause, format, args);
    }
}
