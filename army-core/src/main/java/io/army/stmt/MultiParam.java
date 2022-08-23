package io.army.stmt;

import io.army.criteria.NamedParam;
import io.army.criteria.SQLParam;

import java.util.Collection;
import java.util.List;

public interface MultiParam extends SQLParam {

    List<?> valueList();


    static MultiParam build(NamedParam.NamedMulti param, Collection<?> values) {
        return SqlParams.multi(param, values);
    }

}
