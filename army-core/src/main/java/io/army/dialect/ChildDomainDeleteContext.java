package io.army.dialect;

import io.army.meta.ChildTableMeta;

public interface ChildDomainDeleteContext extends DomainDeleteContext {

    @Override
    ChildTableMeta<?> tableMeta();


}
