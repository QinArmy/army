package io.army.sharding;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public final class RouteCreateException extends ArmyRuntimeException {

    public RouteCreateException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public RouteCreateException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
