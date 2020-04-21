package io.army.criteria.impl.inner;

import io.army.meta.TableMeta;

@DeveloperForbid
public interface InnerDomainDML {

    TableMeta<?> tableMeta();

    String tableAlias();
}
