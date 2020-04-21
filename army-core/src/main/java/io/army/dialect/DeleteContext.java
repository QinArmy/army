package io.army.dialect;

import io.army.criteria.SQLContext;
import io.army.criteria.impl.inner.InnerDelete;

public interface DeleteContext extends SQLContext {

    InnerDelete innerDelete();


}
