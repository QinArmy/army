package io.army.sharding;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class ShardingRouteException extends ArmyRuntimeException {

    public ShardingRouteException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public ShardingRouteException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
