package io.army.criteria.impl;

import io.army.criteria.ColumnSubQuery;
import io.army.criteria.SubQuery;
import io.army.criteria.impl.inner.*;
import io.army.session.AbstractGenericSession;

public abstract class _CriteriaCounselor {

    _CriteriaCounselor() {
        throw new UnsupportedOperationException();
    }

    public static void assertStandardComposeSelect(_StandardComposeQuery select) {
        if (!(select instanceof ComposeQueries)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", select
                    , ComposeQueries.class.getName()));
        }
    }

    public static void assertStandardComposeSubQuery(_StandardComposeQuery composeQuery) {

        if (!(composeQuery instanceof ComposeQueries)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s or %s.", composeQuery
                    , SubQuery.class.getName()
                    , ColumnSubQuery.class.getName()
            ));
        }
    }

    public static void assertStandardSelect(_StandardSelect select) {
        if (!(select instanceof StandardContextualMultiSelect)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", select
                    , StandardContextualMultiSelect.class.getName()));
        }
    }

    public static void assertStandardSubQuery(_StandardSubQuery subQuery) {
        SubQueries.assertStandardSubQuery(subQuery);
    }


    public static void assertStandardUpdate(_StandardUpdate update) {
        if (update instanceof _StandardBatchUpdate) {
            if (!(update instanceof ContextualBatchUpdate)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", update
                        , ContextualBatchUpdate.class.getName()));
            }
        } else if (!AbstractGenericSession.cacheDomainUpdate(update)
                && !(update instanceof ContextualUpdate)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", update
                    , ContextualUpdate.class.getName()));
        }

    }

    public static void assertStandardDelete(_StandardDelete delete) {
        if (delete instanceof _StandardBatchDelete) {
            if (!(delete instanceof ContextualBatchDelete)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                        , ContextualBatchDelete.class.getName()));
            }
        } else if (!(delete instanceof ContextualDelete)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                    , ContextualDelete.class.getName()));
        }
    }

    public static void standardInsert(_Insert insert) {
        if (!(insert instanceof ContextualValueInsert)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                    , ContextualValueInsert.class.getName()));
        }
    }


}
