package io.army.stmt;

import io.army.lang.Nullable;
import io.army.meta.ParamMeta;

public interface ParamValue {

    ParamMeta paramMeta();

    @Nullable
    Object value();

    static StrictParamValue build(ParamMeta paramMeta, @Nullable Object value) {
        return new ParamValueImpl(paramMeta, value);
    }
}
