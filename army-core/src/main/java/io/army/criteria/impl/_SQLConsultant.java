package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Insert;
import io.army.dialect.Database;
import io.army.lang.Nullable;
import io.army.util._ClassUtils;

public abstract class _SQLConsultant {
    // consultant

    _SQLConsultant() {
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

    public static void assertStandardInsert(final Insert insert) {
        if (insert instanceof _Insert._DomainInsert || insert instanceof _Insert._ValuesInsert) {
            if (!(insert instanceof StandardInserts.StandardValuesSyntaxStatement)) {
                throw instanceNotMatch(insert, StandardInserts.StandardValuesSyntaxStatement.class);
            }
        } else if (!(insert instanceof StandardInserts.StandardQueryInsertStatement)) {
            throw instanceNotMatch(insert, StandardInserts.StandardQueryInsertStatement.class);
        }

    }

    public static void assertStandardNestedItems(@Nullable NestedItems nestedItems) {
        if (!(nestedItems instanceof StandardNestedItems)) {
            throw illegalNestedItems(nestedItems, null);
        }
    }

    protected static CriteriaException instanceNotMatch(Statement statement, Class<?> statementClass) {
        String m = String.format("%s isn't instance of %s"
                , _ClassUtils.safeClassName(statement), statementClass.getName());
        throw new CriteriaException(m);
    }


    static CriteriaException illegalNestedItems(@Nullable NestedItems nestedItem, @Nullable Database database) {
        String m = String.format("Illegal %s %s for %s"
                , NestedItems.class.getName()
                , _ClassUtils.safeClassName(nestedItem)
                , database == null ? "standard" : database);
        throw new CriteriaException(m);
    }


}
