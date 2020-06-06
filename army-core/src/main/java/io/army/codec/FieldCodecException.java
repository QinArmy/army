package io.army.codec;

import io.army.ArmyRuntimeException;
import io.army.ErrorCode;

public class FieldCodecException extends ArmyRuntimeException {

    private static final long serialVersionUID = -5915519433310592529L;

    public static FieldCodecException KeyError(String format, Object... args) {
        return new FieldCodecException(ErrorCode.CODEC_KEY_ERROR, format, args);
    }

    public static FieldCodecException dataError(String format, Object... args) {
        return new FieldCodecException(ErrorCode.CODEC_DATA_ERROR, format, args);
    }

    protected FieldCodecException(ErrorCode errorCode, String format, Object... args) {
        super(errorCode, format, args);
    }

    protected FieldCodecException(ErrorCode errorCode, Throwable cause, String format, Object... args) {
        super(errorCode, cause, format, args);
    }
}
