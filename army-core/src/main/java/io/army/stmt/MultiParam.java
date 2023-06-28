package io.army.stmt;

import io.army.criteria.NamedParam;
import io.army.criteria.SQLParam;
import io.army.criteria.SqlValueParam;

import java.util.Collection;
import java.util.List;

public interface MultiParam extends SQLParam, SqlValueParam.MultiValue {

    List<?> valueList();


    static MultiParam build(NamedParam.NamedRow param, Collection<?> values) {
        return SqlParams.multi(param, values);
    }

}
