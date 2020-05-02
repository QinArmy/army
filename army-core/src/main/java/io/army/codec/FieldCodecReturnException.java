package io.army.codec;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class FieldCodecReturnException extends ArmyRuntimeException {

    public FieldCodecReturnException(String format, Object... args) {
        super(ErrorCode.ERROR_CODEC_RETURN, format, args);
    }
}
