package io.army.dialect;

import io.army.criteria.SQLContext;
import io.army.criteria.impl.inner.InnerUpdate;
import io.army.meta.TableMeta;

public interface UpdateContext extends SQLContext {

    InnerUpdate innerUpdate();

    TableMeta<?> tableMeta();

    String tableAlias();

}
