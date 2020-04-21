package io.army.dialect;

import io.army.criteria.SQLContext;
import io.army.criteria.impl.inner.InnerUpdate;

public interface UpdateContext extends SQLContext {

    InnerUpdate innerUpdate();

}
