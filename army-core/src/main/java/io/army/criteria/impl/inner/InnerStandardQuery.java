package io.army.criteria.impl.inner;

import io.army.criteria.LockMode;
import io.army.lang.Nullable;

public interface InnerStandardQuery extends InnerQuery {

    @Nullable
    LockMode lockMode();
}
