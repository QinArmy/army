package io.army.wrapper;

import io.army.lang.Nullable;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.meta.mapping.MappingMeta;
import io.army.util.Assert;

final class ParamWrapperImpl implements ParamWrapper {

    private final ParamMeta paramMeta;

    private final Object value;

    ParamWrapperImpl(ParamMeta paramMeta, @Nullable Object value) {
        Assert.notNull(paramMeta, "paramMeta required");

        this.paramMeta = paramMeta;
        this.value = value;
    }

    @Override
    public ParamMeta paramMeta() {
        return paramMeta;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public String toString() {

        MappingMeta mappingMeta;
        if (this.paramMeta instanceof FieldMeta) {
            mappingMeta = ((FieldMeta<?, ?>) this.paramMeta).mappingMeta();
        } else {
            mappingMeta = (MappingMeta) this.paramMeta;
        }
        String valueText;
        if (value == null) {
            valueText = "NULL";
        } else {
            valueText = mappingMeta.toConstant(null, value);
        }
        return String.format("paramMeta:%s,value:%s"
                , this.paramMeta
                , valueText
        );
    }
}
