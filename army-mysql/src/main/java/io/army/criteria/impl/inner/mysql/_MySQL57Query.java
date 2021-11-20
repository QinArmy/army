package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner._Query;
import io.army.lang.Nullable;

public interface _MySQL57Query extends _Query {

    @Nullable
    SQLModifier lockMode();

    boolean groupByWithRollUp();
}
