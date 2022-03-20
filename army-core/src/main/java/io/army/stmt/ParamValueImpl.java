package io.army.stmt;

import io.army.lang.Nullable;
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


}
