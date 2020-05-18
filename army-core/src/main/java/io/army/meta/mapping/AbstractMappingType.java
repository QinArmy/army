package io.army.meta.mapping;

public abstract class AbstractMappingType implements MappingMeta {

    @Override
    public final int hashCode() {
        return super.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return this == obj;
    }


    @Override
    public final MappingMeta mappingMeta() {
        return this;
    }

    @Override
    public final String toString() {
        return javaType().getName() + "#" + jdbcType().name();
    }


}
