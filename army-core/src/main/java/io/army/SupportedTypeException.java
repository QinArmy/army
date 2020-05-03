package io.army;

public class SupportedTypeException extends ArmyRuntimeException {

    public SupportedTypeException(Object type, String supporter) {
        super(ErrorCode.NOT_SUPPORTED_TYP, "Type[%s] supported by %s", type, supporter);
    }
}
