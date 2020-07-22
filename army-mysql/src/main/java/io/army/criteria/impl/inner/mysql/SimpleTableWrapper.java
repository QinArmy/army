package io.army.criteria.impl.inner.mysql;

import io.army.meta.TableMeta;

public interface SimpleTableWrapper {

    TableMeta<?> tableMeta();

    int databaseIndex();

    int tableIndex();
}
