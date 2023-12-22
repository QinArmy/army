package io.army.generator.snowflake;

import io.army.ArmyException;

import javax.annotation.Nullable;


public final class SnowflakeWorkerException extends ArmyException {

    public SnowflakeWorkerException(String message) {
        super(message);
    }

    public SnowflakeWorkerException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public SnowflakeWorkerException(Throwable cause) {
        super(cause);
    }

}
