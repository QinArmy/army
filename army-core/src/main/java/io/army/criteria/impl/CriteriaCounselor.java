package io.army.criteria.impl;

import io.army.boot.BootCounselor;
import io.army.criteria.ColumnSubQuery;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.*;

public abstract class CriteriaCounselor {

    CriteriaCounselor() {
        throw new UnsupportedOperationException();
    }

    public static void assertStandardComposeSelect(InnerStandardComposeQuery select) {
        if (!(select instanceof ComposeSelects)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", select
                    , ComposeSelects.class.getName()));
        }
    }

    public static void assertStandardComposeSubQuery(InnerStandardComposeQuery composeQuery) {
        if (composeQuery instanceof ColumnSubQuery) {
            if (!(composeQuery instanceof ComposeColumnSubQueries)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", composeQuery
                        , ComposeColumnSubQueries.class.getName()));
            }
        } else if (composeQuery instanceof SubQuery) {
            if (!(composeQuery instanceof ComposeSubQueries)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", composeQuery
                        , ComposeSubQueries.class.getName()));
            }
        } else {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s or %s.", composeQuery
                    , SubQuery.class.getName()
                    , ColumnSubQuery.class.getName()
            ));
        }

    }

    public static void assertStandardSelect(InnerStandardSelect select) {
        if (!(select instanceof StandardContextualMultiSelect)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", select
                    , StandardContextualMultiSelect.class.getName()));
        }
    }

    public static void assertStandardSubQuery(InnerStandardSubQuery subQuery) {
        if (subQuery instanceof ColumnSubQuery) {
            if (!(subQuery instanceof ColumnSubQueryAdaptor)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", subQuery
                        , ColumnSubQueryAdaptor.class.getName()));
            }
        } else if (subQuery instanceof SubQuery) {
            if (!(subQuery instanceof StandardSubQueryMultiSelect)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", subQuery
                        , StandardSubQueryMultiSelect.class.getName()));
            }
        } else {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", subQuery
                    , SubQuery.class.getName()));
        }
    }


    public static void assertStandardUpdate(InnerStandardUpdate update) {
        if (!(update instanceof StandardContextualSingleUpdate)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", update
                    , StandardContextualSingleUpdate.class.getName()));
        }
    }

    public static void assertStandardDomainUpdate(InnerStandardDomainUpdate update) {
        if (!(update instanceof StandardContextualUpdate)
                && !BootCounselor.cacheDomainUpdate(update)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of army", update));
        }
    }

    public static void assertStandardDelete(InnerStandardDelete delete) {
        if (delete instanceof InnerStandardDomainDelete) {
            throw new IllegalArgumentException(String.format("%s isn't instance illegal.", delete));
        } else if (!(delete instanceof StandardContextualSingleDelete)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                    , StandardContextualSingleDelete.class.getName()));
        }
    }

    public static void assertStandardDomainDelete(InnerStandardDomainDelete delete) {
        if (!(delete instanceof StandardContextualSingleDelete.StandardContextualDomainDelete)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                    , StandardContextualSingleDelete.StandardContextualDomainDelete.class.getName()));
        }
    }

    public static void assertStandardInsert(InnerStandardInsert insert) {
        if (!(insert instanceof StandardInsert)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                    , StandardInsert.class.getName()));
        }
    }

    public static void assertStandardBatchInsert(InnerStandardBatchInsert insert) {
        if (!(insert instanceof StandardBatchInsert)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                    , StandardBatchInsert.class.getName()));
        }
    }

    public static void assertStandardSubQueryInsert(InnerStandardSubQueryInsert insert) {
        if (!(insert instanceof StandardContextualSubQueryInsert)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                    , StandardContextualSubQueryInsert.class.getName()));
        }
    }


}
