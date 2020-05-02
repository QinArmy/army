package io.army;

public class UnKnownTypeException extends ArmyRuntimeException {

    public UnKnownTypeException(Object type) {
        super(ErrorCode.UNKNOWN_TYPE, "unknown type[%s]", type);
    }
}
