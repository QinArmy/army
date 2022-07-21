package io.army.stmt;

import java.util.Collection;
import java.util.List;

public interface MultiParam extends SqlParam {

    List<?> valueList();


    static MultiParam build(NamedMultiParam param, Collection<?> values) {
        return SqlParams.multi(param, values);
    }

}
