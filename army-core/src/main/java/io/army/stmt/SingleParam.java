package io.army.stmt;

import io.army.criteria.SqlParam;
import io.army.criteria.SqlValueParam;
import io.army.lang.Nullable;
import io.army.meta.ParamMeta;

public interface SingleParam extends SqlParam, SqlValueParam.SingleValue {

    @Nullable
    Object value();

    static SqlParam build(ParamMeta paramMeta, @Nullable Object value) {
        return SqlParams.single(paramMeta, value);
    }


}
