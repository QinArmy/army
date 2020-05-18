package io.army.boot;

import io.army.criteria.impl.inner.InnerStandardDomainUpdate;

public abstract class BootCounselor {

    protected BootCounselor() {
        throw new UnsupportedOperationException();
    }

    public static boolean cacheDomainUpdate(InnerStandardDomainUpdate update) {
        return update instanceof CacheDomainUpdate;
    }
}
