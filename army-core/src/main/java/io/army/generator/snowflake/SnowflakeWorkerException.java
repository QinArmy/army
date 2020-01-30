package io.army.generator.snowflake;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;


public class SnowflakeWorkerException extends ArmyRuntimeException {

    public SnowflakeWorkerException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    public SnowflakeWorkerException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
