package io.army.codec;

import io.army.ArmyException;

import javax.annotation.Nullable;

public final class FieldCodecException extends ArmyException {
    public FieldCodecException(String message) {
        super(message);
    }

    public FieldCodecException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public FieldCodecException(Throwable cause) {
        super(cause);
    }
}
