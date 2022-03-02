package io.army.env;

import io.army.ArmyRuntimeException;

public final class BeansException extends ArmyRuntimeException {

    @Deprecated
    BeansException(String format, Object... args) {
        super(format);
    }

}
