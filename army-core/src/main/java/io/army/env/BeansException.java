package io.army.env;

import io.army.ArmyRuntimeException;

public final class BeansException extends ArmyRuntimeException {

    BeansException(String format, Object... args) {
        super(format, args);
    }

}
