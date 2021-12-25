package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;

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
        if (!(select instanceof StandardSelect)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", select
                    , StandardSelect.class.getName()));
        }
    }

    public static void assertStandardSubQuery(_StandardSubQuery subQuery) {
        SubQueries.assertStandardSubQuery(subQuery);
    }


    public static void assertStandardUpdate(Update update) {
        if (update instanceof _BatchSingleUpdate) {
            if (!(update instanceof ContextualBatchUpdate)) {
                String m = String.format("%s isn't instance of %s"
                        , update.getClass().getName(), ContextualBatchUpdate.class.getName());
                throw new CriteriaException(m);
            }
        } else if (!(update instanceof ContextualUpdate)) {
            String m = String.format("%s isn't instance of %s"
                    , update.getClass().getName(), ContextualUpdate.class.getName());
            throw new CriteriaException(m);
        }

    }

    public static void assertStandardDelete(Delete delete) {
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

    public static void assertStandardInsert(Insert insert) {
        if (!(insert instanceof ContextualValueInsert)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", insert
                    , ContextualValueInsert.class.getName()));
        }
    }


}
