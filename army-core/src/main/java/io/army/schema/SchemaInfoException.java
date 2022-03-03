package io.army.schema;

import io.army.ArmyException;

public class SchemaInfoException extends ArmyException {

    public SchemaInfoException(String message) {
        super(message);
    }

    public SchemaInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}
