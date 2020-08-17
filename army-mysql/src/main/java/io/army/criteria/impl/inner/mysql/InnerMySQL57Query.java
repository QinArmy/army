package io.army.criteria.impl.inner.mysql;

import io.army.criteria.SQLModifier;
import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner.InnerQuery;
import io.army.lang.Nullable;

@DeveloperForbid
public interface InnerMySQL57Query extends InnerQuery {

    @Nullable
    SQLModifier lockMode();

    boolean groupByWithRollUp();
}
