package io.army.codec;

import io.army.ErrorCode;

public class FieldCodecReturnException extends FieldCodecException {

    private static final long serialVersionUID = -209094116631768507L;

    public FieldCodecReturnException(String format, Object... args) {
        super(ErrorCode.ERROR_CODEC_RETURN, format, args);
    }
}
