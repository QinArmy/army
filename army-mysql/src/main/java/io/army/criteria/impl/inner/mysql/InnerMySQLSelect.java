package io.army.criteria.impl.inner.mysql;

import io.army.criteria.Expression;
import io.army.criteria.impl.inner.DeveloperForbid;
import io.army.criteria.impl.inner.InnerQuery;
import io.army.criteria.impl.inner.InnerSelect;
import io.army.criteria.impl.inner.InnerSpecialSelect;
import io.army.lang.Nullable;

@DeveloperForbid
public interface InnerMySQLSelect extends InnerSpecialSelect {

    boolean withRollUp();

    @Nullable
    Expression<?> procedure();
}
