package io.army.dialect;

import io.army.criteria.RowSet;
import io.army.criteria.impl.inner._PartRowSet;

public interface _ParensRowSet extends _PartRowSet {

    RowSet innerRowSet();


}
