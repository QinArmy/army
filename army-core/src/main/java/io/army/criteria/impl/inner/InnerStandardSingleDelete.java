package io.army.criteria.impl.inner;

import io.army.meta.TableMeta;

@DeveloperForbid
public interface InnerStandardSingleDelete extends InnerDelete {

    TableMeta<?> tableMeta();

}
