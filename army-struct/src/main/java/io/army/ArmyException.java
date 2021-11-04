package io.army;

public class ArmyException extends RuntimeException {


    public ArmyException(String message) {
        super(message);
    }

    public ArmyException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArmyException(Throwable cause) {
        super(cause);
    }


}
