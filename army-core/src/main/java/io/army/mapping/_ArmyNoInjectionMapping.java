package io.army.mapping;

public abstract class _ArmyNoInjectionMapping extends _ArmyBuildInMapping {


    protected _ArmyNoInjectionMapping() {
        final Class<?> thisClass = this.getClass();
        if (thisClass == StringType.class ||
                thisClass == BinaryType.class) {
            throw new IllegalStateException("sub class error.");
        }
    }


}
