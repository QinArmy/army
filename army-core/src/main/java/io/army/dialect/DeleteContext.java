package io.army.dialect;

import io.army.criteria.impl.inner.InnerDelete;

public interface DeleteContext extends ClauseSQLContext {

    InnerDelete innerDelete();


}
