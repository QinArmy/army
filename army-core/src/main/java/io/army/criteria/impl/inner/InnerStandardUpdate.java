package io.army.criteria.impl.inner;

import io.army.meta.TableMeta;

@DeveloperForbid
public interface InnerStandardUpdate extends InnerUpdate {

    TableMeta<?> tableMeta();

    String tableAlias();

}
