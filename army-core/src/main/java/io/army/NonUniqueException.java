package io.army;


public final class NonUniqueException extends ArmyAccessException {

    public NonUniqueException(String format, Object... args) {
        super(ErrorCode.CRITERIA_ERROR, format, args);
    }

    public NonUniqueException(Throwable cause, String format, Object... args) {
        super(ErrorCode.CRITERIA_ERROR, cause, format, args);
    }
}
