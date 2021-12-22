package io.army.mapping;

public abstract class _ArmyNoInjectionMapping extends AbstractMappingType {


    protected _ArmyNoInjectionMapping() {
        final Class<?> thisClass = this.getClass();
        if (!thisClass.getName().startsWith("io.army.mapping")) {
            String m = String.format("Non army class couldn't extend %s .", thisClass.getName());
            throw new UnsupportedOperationException(m);
        }
        if (thisClass == StringType.class ||
                thisClass == ByteArrayType.class) {
            throw new IllegalStateException("sub class error.");
        }

    }


}
