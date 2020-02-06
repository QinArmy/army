package io.army.dialect;

import io.army.lang.Nullable;
import io.army.meta.mapping.MappingType;
import io.army.util.Assert;

final class ParamWrapperImpl implements ParamWrapper {

    private final MappingType mappingType;

    private final Object value;

    ParamWrapperImpl(MappingType mappingType, @Nullable Object value) {
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
        String valueText;
        if (value == null) {
            valueText = "NULL";
        } else {
            valueText = mappingType.nonNullTextValue(value);
        }
        return String.format("mappingType:%s,value:%s"
                , mappingType.getClass().getName()
                , valueText
        );
    }
}
