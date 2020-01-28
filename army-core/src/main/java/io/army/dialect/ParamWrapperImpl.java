package io.army.dialect;

import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

final class ParamWrapperImpl implements ParamWrapper {

    private final MappingType mappingType;

    private final Object value;

    ParamWrapperImpl(MappingType mappingType, Object value) {
        Assert.notNull(mappingType, "mappingType required");

        this.mappingType = mappingType;
        this.value = value;
    }

    @Override
    public MappingType mappingType() {
        return mappingType;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public String toString() {
        return String.format("mappingType:%s,value:%s"
                , mappingType.getClass().getName()
                , value
        );
    }
}
