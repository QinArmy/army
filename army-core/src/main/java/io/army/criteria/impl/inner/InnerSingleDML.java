package io.army.criteria.impl.inner;

import io.army.criteria.IPredicate;
import io.army.meta.TableMeta;

import java.util.List;

@DeveloperForbid
public interface InnerSingleDML extends InnerSQL{

    String tableAlias();

    TableMeta<?> tableMeta();

    List<IPredicate> predicateList();

    int tableIndex();

    int dataSourceIndex();
}
