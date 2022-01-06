package io.army.criteria.impl.inner;

import io.army.criteria.LockMode;
import io.army.lang.Nullable;

public interface _StandardQuery extends _Query {

    @Nullable
    LockMode lockMode();

}
