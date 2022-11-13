package io.army.mapping;

public abstract class _ArmyNoInjectionMapping extends _ArmyInnerMapping {


    protected _ArmyNoInjectionMapping() {
        final Class<?> thisClass = this.getClass();
        if (thisClass == StringType.class ||
                thisClass == PrimitiveByteArrayType.class) {
            throw new IllegalStateException("sub class error.");
        }

    }


}
