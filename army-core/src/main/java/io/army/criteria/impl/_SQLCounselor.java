package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.util._ClassUtils;

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
            throw instanceNotMatch(update, StandardUpdate.class);
        }

    }

    public static void assertStandardDelete(Delete delete) {
        if (!(delete instanceof StandardDelete)) {
            throw instanceNotMatch(delete, StandardDelete.class);
        }
    }

    public static void assertStandardInsert(Insert insert) {
        if (!(insert instanceof StandardValueInsert)) {
            throw instanceNotMatch(insert, StandardValueInsert.class);
        }
    }

    protected static CriteriaException instanceNotMatch(Statement statement, Class<?> statementClass) {
        String m = String.format("%s isn't instance of %s"
                , _ClassUtils.safeClassName(statement), statementClass.getName());
        throw new CriteriaException(m);
    }


}
