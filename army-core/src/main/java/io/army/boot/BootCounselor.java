package io.army.boot;

import io.army.criteria.impl.inner.InnerStandardUpdate;

public abstract class BootCounselor {

    protected BootCounselor() {
        throw new UnsupportedOperationException();
    }

    public static boolean cacheDomainUpdate(InnerStandardUpdate update) {
        return update instanceof CacheDomainUpdate;
    }
}
