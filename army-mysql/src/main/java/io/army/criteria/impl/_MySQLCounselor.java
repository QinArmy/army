package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner._SingleDelete;
import io.army.criteria.impl.inner._SingleUpdate;
import io.army.criteria.impl.inner._UnionRowSet;

public abstract class _MySQLCounselor extends _SQLCounselor {


    public static void assertUpdate(final Update update) {
        if (update instanceof _SingleUpdate) {
            if (!(update instanceof MySQLSingleUpdate)) {
                throw instanceNotMatch(update, MySQLSingleUpdate.class);
            }
        } else if (!(update instanceof MySQLMultiUpdate)) {
            throw instanceNotMatch(update, MySQLMultiUpdate.class);
        }

    }

    public static void assertDelete(final Delete delete) {
        if (delete instanceof _SingleDelete) {
            if (!(delete instanceof MySQLSingleDelete)) {
                throw instanceNotMatch(delete, MySQLSingleDelete.class);
            }
        } else if (!(delete instanceof MySQLMultiDelete)) {
            throw instanceNotMatch(delete, MySQLMultiDelete.class);
        }
    }

    public static void assertRowSet(final RowSet rowSet) {
        if (rowSet instanceof _UnionRowSet) {
            if (!(rowSet instanceof MySQL80UnionQuery)) {
                throw instanceNotMatch(rowSet, MySQL80UnionQuery.class);
            }
        } else if (!(rowSet instanceof MySQL80SimpleQuery)) {
            throw instanceNotMatch(rowSet, MySQL80SimpleQuery.class);
        }

    }

    public static void assertHint(Hint hint) {
        if (!(hint instanceof MySQLHints)) {
            throw MySQLUtils.illegalHint(hint);
        }
    }

    public static void assertWindow(Window window) {
        if (!SimpleWindow.isStandardWindow(window)) {
            throw new CriteriaException("Illegal window.");
        }
    }

    public static void assertMySQLCte(final Cte cte) {
        if (!(cte instanceof SQLs.CteImpl || cte instanceof CriteriaContexts.RefCte)) {
            throw new CriteriaException("Illegal Cte");
        }
        final SubStatement subStatement;
        subStatement = cte.subStatement();
        if (subStatement instanceof _UnionRowSet) {
            if (!(subStatement instanceof MySQL80UnionQuery || subStatement instanceof StandardUnionQuery)) {
                throw new CriteriaException("Illegal sub query");
            }
        } else if (!(subStatement instanceof MySQL80SimpleQuery || subStatement instanceof StandardSimpleQuery)) {
            throw new CriteriaException("Illegal sub query");
        }
    }

    public static void assertNestedItems(final NestedItems nestedItems) {
        if (!(nestedItems instanceof MySQLNestedItems || nestedItems instanceof StandardNestedItems)) {
            throw new CriteriaException(String.format("Illegal %s", NestedItems.class.getName()));
        }

    }


}
