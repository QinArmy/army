package io.army.criteria.impl.inner;

import io.army.meta.TableMeta;

@DeveloperForbid
public interface InnerSingleDML extends InnerSQL{

    String tableAlias();

    TableMeta<?> tableMeta();

    int databaseIndex();

    int tableIndex();
}
