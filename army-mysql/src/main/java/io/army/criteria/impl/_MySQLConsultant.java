package io.army.criteria.impl;

import io.army.criteria.*;
import io.army.criteria.impl.inner.*;
import io.army.criteria.mysql.MySQLDqlValues;
import io.army.criteria.mysql.MySQLLoad;
import io.army.criteria.mysql.MySQLReplace;
import io.army.dialect.Database;
import io.army.util._ClassUtils;

public abstract class _MySQLConsultant extends _SQLConsultant {


    /**
     * <p>
     * Assert insert is MySQL dialect {@link  Insert} statement.
     * </p>
     */
    public static void assertInsert(final Insert insert) {
        if (insert instanceof _Insert._DomainInsert || insert instanceof _Insert._ValuesInsert) {
            if (!(insert instanceof MySQLInserts.MySQLValueSyntaxStatement)) {
                throw instanceNotMatch(insert, MySQLInserts.MySQLValueSyntaxStatement.class);
            }
        } else if (insert instanceof _Insert._AssignmentInsert) {
            if (!(insert instanceof MySQLInserts.AssignmentsInsertStatement)) {
                throw instanceNotMatch(insert, MySQLInserts.AssignmentsInsertStatement.class);
            }
        } else if (insert instanceof _Insert._QueryInsert) {
            if (!(insert instanceof MySQLInserts.MySQLQueryInsertStatement)) {
                throw instanceNotMatch(insert, MySQLInserts.MySQLQueryInsertStatement.class);
            }
        } else {
            throw new CriteriaException("Not MySQL dialect insert statement.");
        }

    }


    /**
     * <p>
     * Assert insert is MySQL dialect {@link  Insert} statement.
     * </p>
     */
    public static void assertReplace(final MySQLReplace replace) {
        if (replace instanceof _Insert._DomainInsert) {
            if (!(replace instanceof MySQLReplaces.DomainReplaceStatement)) {
                throw instanceNotMatch(replace, MySQLReplaces.DomainReplaceStatement.class);
            }
        } else if (replace instanceof _Insert._ValuesInsert) {
            if (!(replace instanceof MySQLReplaces.ValuesReplaceStatement)) {
                throw instanceNotMatch(replace, MySQLReplaces.ValuesReplaceStatement.class);
            }
        } else if (replace instanceof _Insert._AssignmentInsert) {
            if (!(replace instanceof MySQLReplaces.AssignmentsReplaceStatement)) {
                throw instanceNotMatch(replace, MySQLReplaces.AssignmentsReplaceStatement.class);
            }
        } else if (replace instanceof _Insert._QueryInsert) {
            if (!(replace instanceof MySQLReplaces.QueryReplaceStatement)) {
                throw instanceNotMatch(replace, MySQLReplaces.QueryReplaceStatement.class);
            }
        } else {
            throw new CriteriaException("Not MySQL dialect replace statement.");
        }

    }


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

    public static void assertQuery(final Query rowSet) {
        if (rowSet instanceof _UnionRowSet) {
            if (!(rowSet instanceof MySQL80UnionQuery)) {
                throw instanceNotMatch(rowSet, MySQL80UnionQuery.class);
            }
        } else if (!(rowSet instanceof MySQL80SimpleQuery)) {
            throw instanceNotMatch(rowSet, MySQL80SimpleQuery.class);
        }

    }

    public static void assertValues(final MySQLDqlValues values) {
        if (values instanceof _UnionRowSet) {
            if (!(values instanceof MySQLUnionValues)) {
                throw instanceNotMatch(values, MySQLUnionValues.class);
            }
        } else if (!(values instanceof MySQLSimpleValues)) {
            throw instanceNotMatch(values, MySQLSimpleValues.class);
        }
    }

    public static void assertMySQLLoad(final MySQLLoad load) {
        if (!(load instanceof MySQLLoads.MySQLLoadDataStatement)) {
            throw instanceNotMatch(load, MySQLLoads.MySQLLoadDataStatement.class);
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

    public static void assertMySQLCte(final _Cte cte) {
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
            throw illegalNestedItems(nestedItems, Database.MySQL);
        }

    }

    public static void assertJsonTable(final TabularItem jsonTable) {
        if (!(jsonTable instanceof MySQLFunctions.JsonTable)) {
            String m = String.format("%s isn't instance of %s", _ClassUtils.safeClassName(jsonTable)
                    , MySQLFunctions.JsonTable.class);
            throw new CriteriaException(m);
        }

    }


}
