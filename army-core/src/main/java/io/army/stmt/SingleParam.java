package io.army.stmt;

import io.army.criteria.SQLParam;
import io.army.criteria.SqlValueParam;
import io.army.meta.TypeMeta;

import javax.annotation.Nullable;

public interface SingleParam extends SQLParam, SqlValueParam.SingleValue {

    @Nullable
    Object value();

    static SingleParam build(TypeMeta paramMeta, @Nullable Object value) {
        return SqlParams.single(paramMeta, value);
    }


}
