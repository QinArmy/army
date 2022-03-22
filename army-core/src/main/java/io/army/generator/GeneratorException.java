package io.army.generator;

import io.army.ArmyException;
import io.army.lang.Nullable;

public class GeneratorException extends ArmyException {


    public GeneratorException(String message) {
        super(message);
    }

    public GeneratorException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }


}
