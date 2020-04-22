package io.army.criteria.impl.inner;

import io.army.meta.TableMeta;

@DeveloperForbid
public interface InnerStandardDelete extends InnerDelete {

    TableMeta<?> tableMeta();

    String tableAlias();

}
