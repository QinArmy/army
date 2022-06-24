package io.army.criteria.impl.inner;

import io.army.criteria.SubQuery;
import io.army.lang.Nullable;

public interface _SubQueryInsert extends _Insert {

    SubQuery subQuery();

    @Nullable
    SubQuery childSubQuery();


}
