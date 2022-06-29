package io.army.criteria.impl.inner;

import io.army.criteria.RowSet;
import io.army.lang.Nullable;

public interface _RowSetInsert extends _Insert {

    RowSet rowSet();

    @Nullable
    RowSet childRowSet();


}
