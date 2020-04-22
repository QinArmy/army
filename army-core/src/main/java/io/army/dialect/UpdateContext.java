package io.army.dialect;

import io.army.criteria.impl.inner.InnerUpdate;

public interface UpdateContext extends ClauseSQLContext {

    InnerUpdate innerUpdate();

}
