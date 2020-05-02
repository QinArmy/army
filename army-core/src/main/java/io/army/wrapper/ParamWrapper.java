package io.army.wrapper;

import io.army.lang.Nullable;
import io.army.meta.ParamMeta;

public interface ParamWrapper {

    ParamMeta paramMeta();

    @Nullable
    Object value();

    static ParamWrapper build(ParamMeta paramMeta, @Nullable Object value) {
        return new ParamWrapperImpl(paramMeta, value);
    }
}
