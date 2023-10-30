package io.army;

import javax.annotation.Nullable;

public class ArmyException extends RuntimeException {


    public ArmyException(String message) {
        super(message);
    }

    public ArmyException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public ArmyException(Throwable cause) {
        super(cause);
    }


}
