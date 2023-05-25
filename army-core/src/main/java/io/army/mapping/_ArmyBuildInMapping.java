package io.army.mapping;


import java.lang.ref.SoftReference;

public abstract class _ArmyBuildInMapping extends MappingType {

    protected _ArmyBuildInMapping() {
        final Class<?> thisClass = this.getClass();
        if (!thisClass.getName().startsWith("io.army.mapping.")) {
            String m = String.format("Non army class couldn't extend %s .", thisClass.getName());
            throw new UnsupportedOperationException(m);
        }

    }


    protected static final class InstanceRef<T> extends SoftReference<T> {

        public InstanceRef(T referent) {
            super(referent);
        }

    }//InstanceRef


}
