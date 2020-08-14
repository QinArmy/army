package io.army.generator.snowflake;

import io.army.ArmyRuntimeException;


public class SnowflakeWorkerException extends ArmyRuntimeException {


    public SnowflakeWorkerException(String format, Object... args) {
        super(format, args);
    }
}
