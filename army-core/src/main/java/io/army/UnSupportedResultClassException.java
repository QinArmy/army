package io.army;

public final class UnSupportedResultClassException extends ArmyRuntimeException {

    private final Class<?> resultClass;

    @Deprecated
    public UnSupportedResultClassException(Class<?> resultClass, String format, Object... args) {
        super(format);
        this.resultClass = resultClass;
    }


    public Class<?> getResultClass() {
        return this.resultClass;
    }
}
