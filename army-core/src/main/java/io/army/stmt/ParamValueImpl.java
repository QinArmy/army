package io.army.stmt;

import io.army.lang.Nullable;
import io.army.mapping.MappingType;
import io.army.meta.FieldMeta;
import io.army.meta.ParamMeta;
import io.army.util._Assert;

final class ParamValueImpl implements ParamValue {

    private final ParamMeta paramMeta;

    private final Object value;

    ParamValueImpl(ParamMeta paramMeta, @Nullable Object value) {
        _Assert.notNull(paramMeta, "paramMeta required");

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

        MappingType mappingType;
        if (this.paramMeta instanceof FieldMeta) {
            mappingType = ((FieldMeta<?>) this.paramMeta).mappingType();
        } else {
            mappingType = (MappingType) this.paramMeta;
        }
        String valueText;
        if (value == null) {
            valueText = "NULL";
        } else {
            valueText = "";
            //valueText = mappingType.toConstant(null, value);
        }
        return String.format("paramMeta:%s,value:%s"
                , this.paramMeta
                , valueText
        );
    }


}
