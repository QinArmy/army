package io.army.criteria.impl.inner;

import io.army.criteria.SQLWords;
import io.army.lang.Nullable;

public interface _StandardQuery extends _Query {

    @Nullable
    SQLWords lockMode();

}
