package io.army.dialect;

import io.army.meta.ParentTableMeta;

public interface ParentDomainDeleteContext extends DomainDeleteContext {

    @Override
    ParentTableMeta<?> tableMeta();


}
