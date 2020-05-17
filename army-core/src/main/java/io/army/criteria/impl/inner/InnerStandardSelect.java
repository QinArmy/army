package io.army.criteria.impl.inner;

import io.army.criteria.LockMode;
import io.army.lang.Nullable;

@DeveloperForbid
public interface InnerStandardSelect extends InnerSelect, InnerQuery {

    @Nullable
    LockMode lockMode();
}
