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
        StandardSubQueries.assertStandardSubQuery(subQuery);
    }


    public static void assertStandardUpdate(_StandardUpdate update) {
        if (update instanceof _StandardBatchUpdate) {
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

    public static void assertStandardDelete(_StandardDelete delete) {
        if (delete instanceof _StandardBatchDelete) {
            if (!(delete instanceof StandardContextualBatchDelete)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                        , StandardContextualBatchDelete.class.getName()));
            }
        } else if (!(delete instanceof StandardContextualDelete)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                    , StandardContextualDelete.class.getName()));
        }
    }

    public static void standardInsert(_Insert insert) {
        if (insert instanceof _StandardBatchInsert) {
            if (!(insert instanceof StandardBatchInsert)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                        , StandardBatchInsert.class.getName()));
            }
        } else if (!(insert instanceof StandardInsert)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                    , StandardInsert.class.getName()));
        }
    }

    public static void assertStandardSubQueryInsert(_StandardSubQueryInsert insert) {
        if (insert instanceof _StandardChildSubQueryInsert) {
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
