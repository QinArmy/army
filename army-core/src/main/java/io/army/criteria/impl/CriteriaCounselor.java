package io.army.criteria.impl;

import io.army.criteria.impl.inner.*;

public abstract class CriteriaCounselor {

    CriteriaCounselor() {
        throw new UnsupportedOperationException();
    }

    public static void assertStandardSelect(InnerStandardSelect select) {
        if (!(select instanceof StandardContextualMultiSelect)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", select
                    , StandardContextualMultiSelect.class.getName()));
        }
    }

    public static void assertStandardUpdate(InnerStandardUpdate update) {
        if (update instanceof InnerStandardDomainUpdate) {
            if (!(update instanceof StandardContextualDomainUpdate)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", update
                        , StandardContextualDomainUpdate.class.getName()));
            }
        } else if (!(update instanceof StandardContextualSingleUpdate)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", update
                    , StandardContextualSingleUpdate.class.getName()));
        }
    }

    public static void assertStandardDelete(InnerStandardDelete delete) {
        if (!(delete instanceof StandardContextualSingleDelete)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                    , StandardContextualSingleDelete.class.getName()));
        } else if (!(delete instanceof StandardContextualSingleDelete.StandardContextualDomainDelete)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                    , StandardContextualSingleDelete.StandardContextualDomainDelete.class.getName()));
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
