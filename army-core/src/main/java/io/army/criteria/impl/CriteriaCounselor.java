package io.army.criteria.impl;

import io.army.criteria.impl.inner.*;

public abstract class CriteriaCounselor {

    CriteriaCounselor() {
        throw new UnsupportedOperationException();
    }

    public static void assertSafe(InnerStandardSingleUpdate update) {
        if (!(update instanceof StandardContextualSingleUpdate)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", update
                    , StandardContextualSingleUpdate.class.getName()));
        }
    }

    public static void assertSafe(InnerStandardDomainUpdate update) {
        if (!(update instanceof StandardContextualDomainUpdate)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", update
                    , StandardContextualDomainUpdate.class.getName()));
        }
    }

    public static void assertInsert(InnerStandardInsert insert) {
        if (!(insert instanceof StandardInsert)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                    , StandardInsert.class.getName()));
        }
    }

    public static void assertInsert(InnerStandardBatchInsert insert) {
        if (!(insert instanceof StandardBatchInsert)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                    , StandardBatchInsert.class.getName()));
        }
    }

    public static void assertInsert(InnerStandardSubQueryInsert insert) {
        if (!(insert instanceof StandardContextualSubQueryInsert)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                    , StandardContextualSubQueryInsert.class.getName()));
        }
    }


}
