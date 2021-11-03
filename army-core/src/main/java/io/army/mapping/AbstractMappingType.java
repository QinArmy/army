package io.army.mapping;

import io.army.meta.ServerMeta;

public abstract class AbstractMappingType implements MappingType {

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }

    @Override
    public final MappingType mappingMeta() {
        return this;
    }


    protected final IllegalArgumentException notSupportConvertBeforeBind(final Object nonNull) {
        String m = String.format("Not support convert %s for %s bind.", nonNull, javaType().getName());
        return new IllegalArgumentException(m);
    }

    protected final IllegalArgumentException notSupportConvertAfterGet(final Object nonNull) {
        String m = String.format("Not support convert from %s to %s.", nonNull, javaType().getName());
        return new IllegalArgumentException(m);
    }


    protected static NoMappingException noMappingError(Class<?> javaType, ServerMeta serverMeta) {
        String m = String.format("No mapping from java type[%s] to Server[%s]", javaType.getName(), serverMeta);
        return new NoMappingException(m);
    }


}
