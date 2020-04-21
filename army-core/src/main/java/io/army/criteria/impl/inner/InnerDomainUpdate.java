package io.army.criteria.impl.inner;

import io.army.meta.TableMeta;

@DeveloperForbid
public interface InnerDomainUpdate extends InnerUpdate {

    TableMeta<?> tableMata();

    String tableAlias();
}
