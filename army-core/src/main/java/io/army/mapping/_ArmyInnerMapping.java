package io.army.mapping;

public abstract class _ArmyInnerMapping extends AbstractMappingType {

    protected _ArmyInnerMapping() {
        final Class<?> thisClass = this.getClass();
        if (!thisClass.getName().startsWith("io.army.mapping.")) {
            String m = String.format("Non army class couldn't extend %s .", thisClass.getName());
            throw new UnsupportedOperationException(m);
        }


    }


}