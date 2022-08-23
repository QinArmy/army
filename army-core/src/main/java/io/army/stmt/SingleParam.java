package io.army.stmt;

import io.army.criteria.SQLParam;
import io.army.criteria.SqlValueParam;
import io.army.lang.Nullable;
import io.army.meta.TypeMeta;

public interface SingleParam extends SQLParam, SqlValueParam.SingleValue {

    @Nullable
    Object value();

    static SQLParam build(TypeMeta paramMeta, @Nullable Object value) {
        return SqlParams.single(paramMeta, value);
    }


}
