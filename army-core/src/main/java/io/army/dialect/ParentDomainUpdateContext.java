package io.army.dialect;

import io.army.meta.ParentTableMeta;

public interface ParentDomainUpdateContext extends DomainUpdateContext {

    @Override
    ParentTableMeta<?> tableMeta();

    boolean needQueryChild();
}
