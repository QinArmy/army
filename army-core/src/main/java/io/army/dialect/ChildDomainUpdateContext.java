package io.army.dialect;

import io.army.meta.ChildTableMeta;

public interface ChildDomainUpdateContext extends DomainUpdateContext {

    @Override
    ChildTableMeta<?> tableMeta();

}
