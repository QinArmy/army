package io.army.dialect;

import io.army.criteria.impl.inner.InnerDomainUpdate;

public interface DomainUpdateContext extends UpdateContext {

    @Override
    InnerDomainUpdate innerUpdate();


}
