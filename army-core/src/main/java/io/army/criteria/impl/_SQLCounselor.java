package io.army.criteria.impl;

import io.army.criteria.*;

public abstract class _SQLCounselor {

    _SQLCounselor() {
        throw new UnsupportedOperationException();
    }


    public static void assertStandardQuery(Query query) {
        if (!(query instanceof StandardSimpleQuery) && !(query instanceof StandardUnionQuery)) {
            String m = String.format("%s isn't instance of %s or %s"
                    , query.getClass().getName(), StandardSimpleQuery.class.getName()
                    , StandardUnionQuery.class.getName());
            throw new CriteriaException(m);
        }
    }


    public static void assertStandardUpdate(Update update) {
        if (!(update instanceof StandardUpdate)) {
            String m = String.format("%s isn't instance of %s"
                    , update.getClass().getName(), StandardUpdate.class.getName());
            throw new CriteriaException(m);
        }

    }

    public static void assertStandardDelete(Delete delete) {
        if (!(delete instanceof StandardDelete)) {
            throw new CriteriaException(String.format("%s isn't instance of %s", delete.getClass().getName()
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
