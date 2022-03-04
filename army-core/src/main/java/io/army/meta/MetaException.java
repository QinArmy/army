package io.army.meta;

import io.army.ArmyException;

/**
 * throw when {@link io.army.meta.Meta} error.
 */
public final class MetaException extends ArmyException {


    public MetaException(String format) {
        super(format);
    }


    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }




}
