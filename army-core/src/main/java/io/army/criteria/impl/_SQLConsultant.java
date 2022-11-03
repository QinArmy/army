package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._Cte;
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
        if (!(query instanceof StandardQueries) && !(query instanceof StandardUnionQueries)) {
            String m = String.format("%s isn't instance of %s or %s"
                    , query.getClass().getName(), StandardQueries.class.getName()
                    , StandardUnionQueries.class.getName());
            throw new CriteriaException(m);
        }
    }


    public static void assertStandardUpdate(Update update) {
        if (!(update instanceof StandardUpdates)) {
            throw instanceNotMatch(update, StandardUpdates.class);
        }

    }

    public static void assertStandardDelete(Delete delete) {
        if (!(delete instanceof StandardDeletes)) {
            throw instanceNotMatch(delete, StandardDeletes.class);
        }
    }

    public static void assertStandardInsert(final Insert insert) {
        if (insert instanceof _Insert._DomainInsert) {
            if (!(insert instanceof StandardInserts.DomainsInsertStatement)) {
                throw instanceNotMatch(insert, StandardInserts.DomainsInsertStatement.class);
            }
        } else if (insert instanceof _Insert._ValuesInsert) {
            if (!(insert instanceof StandardInserts.ValueInsertStatement)) {
                throw instanceNotMatch(insert, StandardInserts.ValueInsertStatement.class);
            }
        } else if (insert instanceof _Insert._QueryInsert) {
            if (!(insert instanceof StandardInserts.QueryInsertStatement)) {
                throw instanceNotMatch(insert, StandardInserts.QueryInsertStatement.class);
            }
        } else {
            throw new CriteriaException("Not standard insert statement");
        }

    }

    public static void assertStandardNestedItems(@Nullable NestedItems nestedItems) {
        if (!(nestedItems instanceof StandardNestedJoins)) {
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

    static CriteriaException illegalCteImpl(_Cte cte) {
        return new CriteriaException(String.format("Illegal Cte %s", cte));
    }


}
