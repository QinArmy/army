package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._BatchSingleUpdate;
import io.army.criteria.impl.inner._StandardBatchDelete;
import io.army.criteria.impl.inner._StandardComposeQuery;
import io.army.criteria.impl.inner._StandardSubQuery;

public abstract class _CriteriaCounselor {

    _CriteriaCounselor() {
        throw new UnsupportedOperationException();
    }

    public static void assertStandardComposeSelect(_StandardComposeQuery select) {

    }

    public static void assertStandardComposeSubQuery(_StandardComposeQuery composeQuery) {

    }

    public static void assertStandardSelect(Select select) {
    }

    public static void assertStandardSubQuery(_StandardSubQuery subQuery) {
        //
    }


    public static void assertStandardUpdate(Update update) {
        if (update instanceof _BatchSingleUpdate) {
            if (!(update instanceof StandardBatchUpdate)) {
                String m = String.format("%s isn't instance of %s"
                        , update.getClass().getName(), StandardBatchUpdate.class.getName());
                throw new CriteriaException(m);
            }
        } else if (!(update instanceof StandardUpdate)) {
            String m = String.format("%s isn't instance of %s"
                    , update.getClass().getName(), StandardUpdate.class.getName());
            throw new CriteriaException(m);
        }

    }

    public static void assertStandardDelete(Delete delete) {
        if (delete instanceof _StandardBatchDelete) {
            if (!(delete instanceof StandardBatchDelete)) {
                throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                        , StandardBatchDelete.class.getName()));
            }
        } else if (!(delete instanceof StandardDelete)) {
            throw new IllegalArgumentException(String.format("%s isn't instance of %s", delete
                    , StandardDelete.class.getName()));
        }
    }

    public static void assertStandardInsert(Insert insert) {
        if (!(insert instanceof StandardValueInsert)) {
            String m = String.format("%s isn't instance of %s", insert, StandardValueInsert.class.getName());
            throw new CriteriaException(m);
        }
    }

    public static void assertCet(Cte cte) {

    }


}
