package io.army.dialect;

import io.army.criteria.impl.inner.InnerSelect;

public interface SelectContext extends ClauseSQLContext {

    InnerSelect innerSelect();
}
