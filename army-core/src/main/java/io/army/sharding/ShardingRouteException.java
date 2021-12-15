package io.army.sharding;

import io.army.ArmyRuntimeException;

public class ShardingRouteException extends ArmyRuntimeException {

    public ShardingRouteException(String format) {
        super(format);
    }

    public ShardingRouteException(String message, Throwable cause) {
        super(message, cause);
    }

}
