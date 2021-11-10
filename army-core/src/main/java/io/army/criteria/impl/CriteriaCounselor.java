package io.army.criteria.impl;

import io.army.criteria.ColumnSubQuery;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.*;
import io.army.session.AbstractGenericSession;

public abstract class CriteriaCounselor {

    CriteriaCounselor() {
        throw new UnsupportedOperationException();
    }

    public static void assertStandardComposeSelect(InnerStandardComposeQuery select) {
        if (!(select instanceof ComposeQueries)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", select
                    , ComposeQueries.class.getName()));
        }
    }

    public static void assertStandardComposeSubQuery(InnerStandardComposeQuery composeQuery) {

        if (!(composeQuery instanceof ComposeQueries)) {
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
        StandardSubQueries.assertStandardSubQuery(subQuery);
    }


    public static void assertStandardUpdate(InnerStandardUpdate update) {
        if (update instanceof InnerStandardBatchUpdate) {
            if (!(update instanceof StandardContextualBatchUpdate)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", update
                        , StandardContextualBatchUpdate.class.getName()));
            }
        } else if (!AbstractGenericSession.cacheDomainUpdate(update)
                && !(update instanceof StandardContextualUpdate)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", update
                    , StandardContextualUpdate.class.getName()));
        }

    }

    public static void assertStandardDelete(InnerStandardDelete delete) {
        if (delete instanceof InnerStandardBatchDelete) {
            if (!(delete instanceof StandardContextualBatchDelete)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                        , StandardContextualBatchDelete.class.getName()));
            }
        } else if (!(delete instanceof StandardContextualDelete)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                    , StandardContextualDelete.class.getName()));
        }
    }

    public static void assertStandardInsert(InnerStandardInsert insert) {
        if (insert instanceof InnerStandardBatchInsert) {
            if (!(insert instanceof StandardBatchInsert)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                        , StandardBatchInsert.class.getName()));
            }
        } else if (!(insert instanceof StandardInsert)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                    , StandardInsert.class.getName()));
        }
    }

    public static void assertStandardSubQueryInsert(InnerStandardSubQueryInsert insert) {
        if (insert instanceof InnerStandardChildSubQueryInsert) {
            if (!(insert instanceof StandardContextualChildSubQueryInsert)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                        , StandardContextualChildSubQueryInsert.class.getName()));
            }
        } else if (!(insert instanceof StandardContextualSubQueryInsert)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                    , StandardContextualSubQueryInsert.class.getName()));
        }
    }


}
