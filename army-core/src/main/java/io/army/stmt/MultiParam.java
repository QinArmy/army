package io.army.stmt;

import io.army.criteria.NamedParam;
import io.army.criteria.SqlParam;

import java.util.Collection;
import java.util.List;

public interface MultiParam extends SqlParam {

    List<?> valueList();


    static MultiParam build(NamedParam.NamedMulti param, Collection<?> values) {
        return SqlParams.multi(param, values);
    }

}
