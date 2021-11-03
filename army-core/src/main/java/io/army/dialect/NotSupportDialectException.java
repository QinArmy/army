package io.army.dialect;

import io.army.util.ArmyException;

public final class NotSupportDialectException extends ArmyException {

    public NotSupportDialectException(String message) {
        super(message);
    }

    public NotSupportDialectException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportDialectException(Throwable cause) {
        super(cause);
    }

}
