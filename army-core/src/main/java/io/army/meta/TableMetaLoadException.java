package io.army.meta;

import io.army.ArmyException;

import javax.annotation.Nullable;

public final class TableMetaLoadException extends ArmyException {

    public TableMetaLoadException(String message) {
        super(message);
    }

    public TableMetaLoadException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public TableMetaLoadException(Throwable cause) {
        super(cause);
    }
}
