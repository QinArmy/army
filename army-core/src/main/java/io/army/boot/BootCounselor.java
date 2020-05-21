package io.army.boot;

public abstract class BootCounselor {

    protected BootCounselor() {
        throw new UnsupportedOperationException();
    }

    public static boolean cacheDomainUpdate(InnerStandardDomainUpdate update) {
        return update instanceof CacheDomainUpdate;
    }
}
